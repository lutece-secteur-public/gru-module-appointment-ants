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
import fr.paris.lutece.portal.service.message.CustomSiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Specializes;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@ApplicationScoped
@Specializes
public class AntsAccessControlServiceProvider extends AccessControlServiceProvider
{

    public static final String SITE_MESSAGE_JSP = "jsp/site/SiteMessage.jsp";

    @Override
    public XPage redirectToAccessControlXPage( HttpServletRequest request, int idResource, String resourceType, int idAccessControl )
    {
        AccessController controller = AccessControllerHome.findByPrimaryKey( idAccessControl );
        IAccessControllerType controllerType = CDI.current( ).select( IAccessControllerType.class, NamedLiteral.of( controller.getType( ) ) ).get( );

        // If the form uses a standard AccessController
        if ( !( controllerType instanceof SlotsNumberAccessControllerType ) )
        {
            // Redirect to the standard implementation of the AccessControl XPage
            return super.redirectToAccessControlXPage( request, idResource, resourceType, idAccessControl );
        }
        // If the form uses the specific ANTS access controller, we override the main execution
        else
        {
            return validateController( request, (SlotsNumberAccessControllerType) controllerType, controller );
        }
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
        SiteMessage message = new CustomSiteMessage( "Erreur de validation : ", validationResult, null, SiteMessage.TYPE_ERROR, SiteMessage.TYPE_BUTTON_BACK,
                null );
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
}
