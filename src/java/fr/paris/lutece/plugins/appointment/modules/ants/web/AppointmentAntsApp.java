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
package fr.paris.lutece.plugins.appointment.modules.ants.web;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.modules.ants.common.RequestParameters;
import fr.paris.lutece.plugins.appointment.modules.ants.service.PreDemandeValidationService;
import fr.paris.lutece.plugins.appointment.modules.ants.utils.PredemandeCodeUtils;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

@Controller( xpageName = AppointmentAntsApp.XPAGE_NAME, pageTitleI18nKey = "module.appointment.ants.pageTitle", pagePathI18nKey = "module.appointment.ants.pagePathLabel" )
public class AppointmentAntsApp extends MVCApplication
{
    private static final long serialVersionUID = 1L;

    /**
     * The name of the XPage
     */
    public static final String XPAGE_NAME = "appointmentants";

    /**
     * The name of external XPages
     */
    protected static final String APPOINTMENT_PLUGIN_XPAGE_NAME = "appointment";
    protected static final String APPOINTMENTSEARCH_PLUGIN_XPAGE_NAME = "appointmentsearch";

    /**
     * The name of external views
     */
    protected static final String APPOINTMENT_PLUGIN_APPOINTMENTFORM_VIEW_NAME = "getViewAppointmentForm";
    protected static final String APPOINTMENTSEARCH_PLUGIN_SEARCH_VIEW_NAME = "appointmentsearch";

    // TEMPLATES
    private static final String TEMPLATE_PREDEMANDEFORM = "skin/plugins/appointment/modules/ants/predemandeForm.html";

    // VIEWS
    public static final String VIEW_PREDEMANDEFORM = "predemandeForm";

    // ACTIONS
    private static final String ACTION_PRE_SEARCH = "presearch";

    // PROPERTIES
    private static final String PROPERTY_ID_PREDEMANDE_CODE_PREFIX = "predemande_code_";
    private static final String PROPERTY_ERROR_MESSAGE = "module.appointment.ants.display.fieldsErrorMessage";
    private static final String PROPERTY_PREDEMANDE_CODES_NOT_UNIQUE_ERROR_MESSAGE = "module.appointment.ants.display.predemandeCodesNotUniqueErrorMessage";

    // PARAMETERS
    private static final String PARAMETER_CATEGORIE = "category";
    private static final String PARAMETER_CATEGORIE_TITRES = "titres";
    private static final String PARAMETER_DATE_TIME = "starting_date_time";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_NB_CONSECUTIVE_SLOTS = "nb_consecutive_slots";
    private static final String PARAMETER_NUMBER_OF_PLACES_TO_TAKE = "nbPlacesToTake";
    private static final String PARAMETER_ANCHOR = "anchor";
    private static final String STEP_3 = "#step3";

    // MARKERS
    private static final String MARKER_STARTING_DATE_TIME = "starting_date_time";
    private static final String MARKER_ID_FORM = "id_form";
    private static final String MARKER_NB_PLACES_TO_TAKE = "nbPlacesToTake";
    private static final String MARKER_LIST_ANTS_CODES = "list_ants_codes";

    private static final String PROPERTY_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME_KEY = "ants.session.attribute.name";
    private static final String CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME = AppPropertiesService
            .getProperty( PROPERTY_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME_KEY );

    /**
     * Returns the content of the page preDemandeForm.
     *
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_PREDEMANDEFORM, defaultView = true )
    public XPage viewPreDemandeForm( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );

        String dateTime = request.getParameter( PARAMETER_DATE_TIME );
        String idForm = request.getParameter( PARAMETER_ID_FORM );
        String nbPlacesToTake = request.getParameter( PARAMETER_NUMBER_OF_PLACES_TO_TAKE );
        List<String> predemandeCodeList = null;

        if ( dateTime != null )
        {
            model.put( MARKER_STARTING_DATE_TIME, dateTime );
        }

        if ( idForm != null )
        {
            model.put( MARKER_ID_FORM, idForm );
        }

        if ( nbPlacesToTake != null )
        {
            model.put( MARKER_NB_PLACES_TO_TAKE, nbPlacesToTake );

            // Try to retrieve the predemande codes, if any was previously entered
            predemandeCodeList = PredemandeCodeUtils.getPredemandeCodeList( request, PROPERTY_ID_PREDEMANDE_CODE_PREFIX, Integer.parseInt( nbPlacesToTake ) );
        }
        if ( predemandeCodeList != null && !predemandeCodeList.isEmpty( ) )
        {
            model.put( MARKER_LIST_ANTS_CODES, predemandeCodeList );
        }
        return getXPage( TEMPLATE_PREDEMANDEFORM, request.getLocale( ), model );
    }

    /**
     * Redirects to main carto pview
     *
     * @param request
     *            The HTTP request
     * @return The view
     */
    @Action( value = ACTION_PRE_SEARCH )
    public XPage presearch( HttpServletRequest request ) throws IOException
    {
        // The value of these parameters is retrieved when the user is coming from the ANTS' website
        String nbPlacesToTake = request.getParameter( PARAMETER_NUMBER_OF_PLACES_TO_TAKE );
        String dateTime = request.getParameter( PARAMETER_DATE_TIME );
        String idForm = request.getParameter( PARAMETER_ID_FORM );

        List<String> predemandeCodeList = PredemandeCodeUtils.getPredemandeCodeList( request, PROPERTY_ID_PREDEMANDE_CODE_PREFIX,
                Integer.parseInt( nbPlacesToTake ) );

        Map<String, String> additionalParameters = new Hashtable<>( );
        additionalParameters.put( PARAMETER_NUMBER_OF_PLACES_TO_TAKE, nbPlacesToTake );
        additionalParameters.put( PARAMETER_DATE_TIME, dateTime );
        additionalParameters.put( PARAMETER_ID_FORM, idForm );

        // Add the predemande code in the parameters, so they can be retrieved later if necessary
        for ( int i = 1; i <= Integer.parseInt( nbPlacesToTake ); i++ )
        {
            String inputIdValue = PROPERTY_ID_PREDEMANDE_CODE_PREFIX.concat( String.valueOf( i ) );
            additionalParameters.put( inputIdValue, request.getParameter( inputIdValue ) );
        }

        // If there's a date in the parameters, then the current user is coming from the ANTS' website
        boolean isUserFromAnts = StringUtils.isNotBlank( dateTime );

        // Check if the predemande codes are unique. If they aren't, then display an error on the page
        if ( !PredemandeCodeUtils.hasUniqueValues( predemandeCodeList ) )
        {
            addError( PROPERTY_PREDEMANDE_CODES_NOT_UNIQUE_ERROR_MESSAGE, request.getLocale( ) );
            return redirect( request, VIEW_PREDEMANDEFORM, additionalParameters );
        }

        // Check if all the ANTS codes are valid
        boolean isAllCodesValid = PreDemandeValidationService.checkPredemandeCodesValidationAndAppointments( predemandeCodeList );

        // If one ore more codes are not valid, display an error message to the user
        if ( !isAllCodesValid )
        {
            addError( PROPERTY_ERROR_MESSAGE, request.getLocale( ) );
            return redirect( request, VIEW_PREDEMANDEFORM, additionalParameters );
        }
        else
        {
            // Save the predemande codes in the current session
            HttpSession session = request.getSession( true );
            PredemandeCodeUtils.insertPredemandeCodesInSession( session, predemandeCodeList, ",", CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME );

            RequestParameters params = new RequestParameters( );
            params.setNbPlacesToTakeValue( String.valueOf( nbPlacesToTake ) );
            String url;

            // Codes are valid and the user comes from the ANTS web site
            if ( isUserFromAnts )
            {
                params.setFormIdParameter( PARAMETER_ID_FORM );
                params.setDateTimeParameter( PARAMETER_DATE_TIME );
                params.setDateTimeValue( dateTime );
                params.setNbPlacesToTakeParameter( PARAMETER_NUMBER_OF_PLACES_TO_TAKE );
                params.setAnchorParameter( PARAMETER_ANCHOR );
                params.setAnchorValue( STEP_3 );
                url = PredemandeCodeUtils.constructRedirectionUrl( request, APPOINTMENT_PLUGIN_XPAGE_NAME, APPOINTMENT_PLUGIN_APPOINTMENTFORM_VIEW_NAME,
                        params );

                return redirect( request, url );
            }
            else
            {
                // Codes are valid and the user does not come from the ANTS web site
                params.setCategoryParameter( PARAMETER_CATEGORIE );
                params.setCategoryValue( PARAMETER_CATEGORIE_TITRES );
                params.setNbPlacesToTakeParameter( PARAMETER_NB_CONSECUTIVE_SLOTS );
                url = PredemandeCodeUtils.constructRedirectionUrl( request, APPOINTMENTSEARCH_PLUGIN_XPAGE_NAME, APPOINTMENTSEARCH_PLUGIN_SEARCH_VIEW_NAME,
                        params );
                return redirect( request, url );
            }
        }
    }
}
