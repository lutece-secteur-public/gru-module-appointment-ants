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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.accesscontrol.business.AccessController;
import fr.paris.lutece.plugins.accesscontrol.business.config.IAccessControllerConfigDAO;
import fr.paris.lutece.plugins.accesscontrol.service.AbstractPersistentAccessControllerType;
import fr.paris.lutece.plugins.accesscontrol.service.IAccessControllerType;
import fr.paris.lutece.plugins.appointment.modules.ants.business.accesscontrol.config.SlotsNumberAccessControllerConfig;
import fr.paris.lutece.plugins.appointment.modules.ants.business.accesscontrol.config.SlotsNumberAccessControllerConfigDAO;
import fr.paris.lutece.plugins.appointment.modules.ants.utils.PredemandeCodeUtils;
import fr.paris.lutece.portal.business.accesscontrol.AccessControlSessionData;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

public class SlotsNumberAccessControllerType extends AbstractPersistentAccessControllerType<SlotsNumberAccessControllerConfig> implements IAccessControllerType
{
    public static final String BEAN_NAME = "accesscontrol.slotsNumberAccessControllerType";
    private static final String TITLE_KEY = "module.appointment.ants.accesscontrol.slotsNumberAccessController.name";

    private static final String TEMPLATE_CONFIG = "/admin/plugins/appointment/modules/ants/accesscontrol/config/slots_number_controller_config.html";
    private static final String TEMPLATE_CONTROLLER = "skin/plugins/appointment/modules/ants/accesscontrol/controller/slots_number_controller_template.html";

    private static final String MARK_CONFIG = "config";

    private static final String PARAMETER_SLOTS_NUMBER = "slots_number_id";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_ERROR_MESSAGE = "error_message";

    @Inject
    @Named( SlotsNumberAccessControllerConfigDAO.BEAN_NAME )
    private IAccessControllerConfigDAO<SlotsNumberAccessControllerConfig> _dao;
    
    /**
     * Get the template of the page used to configure an Access Controller
     */
    @Override
    public String getControllerConfigForm( HttpServletRequest request, Locale locale, AccessController controller )
    {
    	SlotsNumberAccessControllerConfig config = _dao.load( controller.getId( ) );
        if ( config == null )
        {
            config = new SlotsNumberAccessControllerConfig( );
            config.setIdAccessController( controller.getId( ) );
            _dao.insert( config );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        addPersistentDataToModel( locale, config, model );

        return AppTemplateService.getTemplate( TEMPLATE_CONFIG, locale, model ).getHtml( );
    }

    /**
     * Get the template of the page displayed to the user when the Access Controller is triggered
     */
    @Override
    public String getControllerForm( HttpServletRequest request, Locale locale, AccessController controller )
    {
    	SlotsNumberAccessControllerConfig config = _dao.load( controller.getId( ) );
        Map<String, Object> model = new HashMap<>( );
        //String nNbSlotsToTake = request.getParameter( PARAMETER_SLOTS_NUMBER );
        model.put( MARK_CONFIG, config );

        return AppTemplateService.getTemplate( TEMPLATE_CONTROLLER, locale, model ).getHtml( );
    }

    @Override
    public void saveControllerConfig( HttpServletRequest request, Locale locale, AccessController controller )
    {
    	SlotsNumberAccessControllerConfig config = _dao.load( controller.getId( ) );
        config.setComment( request.getParameter( PARAMETER_COMMENT ) );
        config.setErrorMessage( request.getParameter( PARAMETER_ERROR_MESSAGE ) );

        saveDataHandlerConfig( request, config );
        _dao.store( config );
    }

    @Override
    public String validate( HttpServletRequest request, AccessController controller )
    {
    	SlotsNumberAccessControllerConfig config = _dao.load( controller.getId( ) );

    	/* Value entered by the user in the Controller View */
    	String slotsNumber = request.getParameter( PARAMETER_SLOTS_NUMBER );

    	HttpSession session = request.getSession( );

    	if( session == null )
    	{
    		return null;
    	}

    	//getDataFromSession( request, controller.getId( ), 1, "APPOINTMENT_FORM" );

    	int nbAntsApplications = PredemandeCodeUtils.getAmountPredemandeCodesInSession( session, "," );

    	if ( StringUtils.isEmpty( slotsNumber ) )
    	{
    		return config.getErrorMessage( );
    	}

    	int nbSlots = Integer.parseInt( slotsNumber );

    	// If there are more slots taken than ANTS Application numbers
    	if ( nbSlots > nbAntsApplications )
    	{
    		return config.getErrorMessage( );
    	}
    	return null;
    }

    @Override
    public void persistDataToSession( AccessControlSessionData sessionData, HttpServletRequest request, Locale locale, int idController )
    {
        String nNbSlotsToTake = request.getParameter( PARAMETER_SLOTS_NUMBER );
        sessionData.addPersistentParam( idController, nNbSlotsToTake );
    }
    
    /*public Serializable getDataFromSession( HttpServletRequest request, int idController, int idResource, String resourceType )
    {
    	try {
    	AccessControlSessionData sessionData =
    			(AccessControlSessionData) request.getSession( ).getAttribute( AccessControlSessionData.getSessionKey( idResource, resourceType ) );

    	Map<Integer, Serializable> sessionDataMap = sessionData.getPersistentData( );

    	return sessionDataMap.get( idController );
    	} catch( Exception e )
    	{
    		return null;
    	}
    }*/
    
    @Override
    protected SlotsNumberAccessControllerConfig loadConfig( int idConfig )
    {
        return _dao.load( idConfig );
    }
    
    @Override
    public void deleteConfig( int idController )
    {
        _dao.delete( idController );
    }
    
    @Override
    public String getBeanName( )
    {
        return BEAN_NAME;
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( TITLE_KEY, locale );
    }

    @Override
    public boolean hasConfig( )
    {
        return true;
    }
}
