/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.modules.ants.service.accesscontrol;

import fr.paris.lutece.plugins.accesscontrol.business.AccessController;
import fr.paris.lutece.plugins.accesscontrol.business.AccessControllerHome;
import fr.paris.lutece.plugins.accesscontrol.service.AccessControlServiceProvider;
import fr.paris.lutece.plugins.accesscontrol.service.IAccessControllerType;
import fr.paris.lutece.plugins.appointment.modules.ants.utils.PredemandeCodeUtils;
import fr.paris.lutece.portal.service.message.CustomSiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.CdiHelper;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Specializes;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.text.StringEscapeUtils;

import freemarker.core.TemplateHTMLOutputModel;

@ApplicationScoped
@Specializes
public class AntsAccessControlServiceProvider extends AccessControlServiceProvider
{

    public static final String SITE_MESSAGE_JSP = "jsp/site/SiteMessage.jsp";

    @Inject
    @Any
    private Instance<IAccessControllerType> _controllerTypes;

    @Override
    public XPage redirectToAccessControlXPage( HttpServletRequest request, int idResource, String resourceType, int idAccessControl )
    {
        // idAccessControl identifies the access control, not one of its controllers
        List<AccessController> listControllers = AccessControllerHome.getAccessControllersListByAccessControlId( idAccessControl );
        List<AccessController> listSlotsControllers = new ArrayList<>( );
        SlotsNumberAccessControllerType slotsControllerType = null;

        for ( AccessController controller : listControllers )
        {
            IAccessControllerType controllerType = CdiHelper.resolve( _controllerTypes, controller.getType( ) );
            if ( controllerType instanceof SlotsNumberAccessControllerType )
            {
                slotsControllerType = (SlotsNumberAccessControllerType) controllerType;
                listSlotsControllers.add( controller );
            }
        }

        // If the access control uses a standard AccessController
        if ( listSlotsControllers.isEmpty( ) || listSlotsControllers.size( ) != listControllers.size( ) )
        {
            // Redirect to the standard implementation of the AccessControl XPage
            return super.redirectToAccessControlXPage( request, idResource, resourceType, idAccessControl );
        }

        // If the access control only uses the specific ANTS access controller, we override the main execution
        for ( AccessController controller : listSlotsControllers )
        {
            XPage page = validateController( request, slotsControllerType, controller );
            if ( page != null )
            {
                return page;
            }
        }

        return null;
    }

    /**
     * Removes the access control data of a resource, once its booking is done; when the access control of the resource
     * holds the ANTS slots number controller, the pre-demand codes of the session are consumed too: a new booking needs a
     * new pre-demand check.
     */
    @Override
    public void deleteSessionDataForResource( HttpServletRequest request, int idResource, String resourceType )
    {
        super.deleteSessionDataForResource( request, idResource, resourceType );
        int idAccessControl = findAccessControlForResource( idResource, resourceType );
        if ( idAccessControl > 0 && usesSlotsController( idAccessControl ) )
        {
            PredemandeCodeUtils.removePredemandeCodesFromSession( request.getSession( ) );
        }
    }

    /**
     * Tells whether an access control holds the ANTS slots number controller.
     *
     * @param idAccessControl
     *            the id of the access control
     * @return true if one of its controllers is the ANTS slots number controller
     */
    private boolean usesSlotsController( int idAccessControl )
    {
        for ( AccessController controller : AccessControllerHome.getAccessControllersListByAccessControlId( idAccessControl ) )
        {
            if ( CdiHelper.resolve( _controllerTypes, controller.getType( ) ) instanceof SlotsNumberAccessControllerType )
            {
                return true;
            }
        }
        return false;
    }

    public XPage validateController( HttpServletRequest request, SlotsNumberAccessControllerType controllerType, AccessController controller )
    {
        String validationResult = controllerType.validate( request, controller );

        if ( validationResult == null )
        {
            return null;
        }

        return getErrorPage( request, validationResult );
    }

    private XPage getErrorPage( HttpServletRequest request, String validationResult )
    {
        MVCUtils.getLogger( ).error( "Validation error: {}", validationResult );
        HttpServletResponse response = LocalVariables.getResponse( );
        SiteMessage message = new TextSiteMessage( "Erreur de validation : ", validationResult );
        HttpSession session = request.getSession( true );
        session.setAttribute( "LUTECE_PORTAL_MESSAGE", message );
        String strTarget = AppPathService.getBaseUrl( request ) + SITE_MESSAGE_JSP;
        try
        {
            response.sendRedirect( strTarget );
        }
        catch( IOException e )
        {
            MVCUtils.getLogger( ).error( "Unable to redirect : {} : {}", strTarget, e.getMessage( ), e );
        }

        return new XPage( );
    }

    /**
     * Site message whose text, the error message of the controller, is written as text.
     */
    static class TextSiteMessage extends CustomSiteMessage
    {
        private static final long serialVersionUID = 1L;

        TextSiteMessage( String strTitle, String strText )
        {
            super( strTitle, strText, null, SiteMessage.TYPE_ERROR, SiteMessage.TYPE_BUTTON_BACK, null );
        }

        /**
         * Returns the text of the message written as text.
         *
         * @param locale
         *            The current locale
         * @return The escaped text as markup
         */
        @Override
        public TemplateHTMLOutputModel getMarkup( Locale locale )
        {
            String strText = getText( locale );

            return HtmlMarkup.of( strText == null ? "" : StringEscapeUtils.escapeHtml4( strText ) );
        }
    }
}
