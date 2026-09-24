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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.appointment.modules.ants.common.RequestParameters;
import fr.paris.lutece.plugins.appointment.modules.ants.service.PreDemandeValidationService;
import fr.paris.lutece.plugins.appointment.modules.ants.utils.PredemandeCodeUtils;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.xpages.XPage;

@RequestScoped
@Named( "appointment-ants.xpage.appointmentants" )
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
    private static final String MARKER_MAX_PLACES_TO_TAKE = "max_places_to_take";
    private static final int MAX_PLACES_TO_TAKE = 6;

    private static final String PROPERTY_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME_KEY = "ants.session.attribute.name";
    private static final String CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME = AppPropertiesService
            .getProperty( PROPERTY_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME_KEY );

    @Inject
    private Models _models;

    /**
     * Returns the content of the page preDemandeForm, filled with the values of the request that have the expected format.
     *
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_PREDEMANDEFORM, defaultView = true )
    public XPage viewPreDemandeForm( HttpServletRequest request )
    {
        int nNbPlacesToTake = getNbPlacesToTake( request );
        String strDateTime = getDateTime( request );
        String strIdForm = getIdForm( request );

        if ( strDateTime != null )
        {
            _models.put( MARKER_STARTING_DATE_TIME, strDateTime );
        }
        if ( strIdForm != null )
        {
            _models.put( MARKER_ID_FORM, strIdForm );
        }
        _models.put( MARKER_NB_PLACES_TO_TAKE, nNbPlacesToTake );
        _models.put( MARKER_MAX_PLACES_TO_TAKE, MAX_PLACES_TO_TAKE );
        _models.put( MARKER_LIST_ANTS_CODES, PredemandeCodeUtils.getPredemandeCodeList( request, PROPERTY_ID_PREDEMANDE_CODE_PREFIX, nNbPlacesToTake )
                .stream( ).map( code -> PredemandeCodeUtils.isValidCode( code ) ? code : StringUtils.EMPTY ).toList( ) );

        return getXPage( TEMPLATE_PREDEMANDEFORM, request.getLocale( ) );
    }

    /**
     * Checks the pre-demand codes against the ANTS API, then redirects to the booking of the chosen slot (user coming
     * from the ANTS web site) or to the appointment search.
     *
     * @param request
     *            The HTTP request
     * @return The view
     * @throws IOException
     *             If the redirection fails
     */
    @Action( value = ACTION_PRE_SEARCH )
    public XPage presearch( HttpServletRequest request ) throws IOException
    {
        int nNbPlacesToTake = getNbPlacesToTake( request );
        String strDateTime = getDateTime( request );
        String strIdForm = getIdForm( request );

        List<String> predemandeCodeList = PredemandeCodeUtils.getPredemandeCodeList( request, PROPERTY_ID_PREDEMANDE_CODE_PREFIX, nNbPlacesToTake );

        Map<String, String> additionalParameters = new HashMap<>( );
        additionalParameters.put( PARAMETER_NUMBER_OF_PLACES_TO_TAKE, String.valueOf( nNbPlacesToTake ) );
        if ( strDateTime != null )
        {
            additionalParameters.put( PARAMETER_DATE_TIME, strDateTime );
        }
        if ( strIdForm != null )
        {
            additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
        }
        for ( int i = 0; i < predemandeCodeList.size( ); i++ )
        {
            if ( PredemandeCodeUtils.isValidCode( predemandeCodeList.get( i ) ) )
            {
                additionalParameters.put( PROPERTY_ID_PREDEMANDE_CODE_PREFIX + ( i + 1 ), predemandeCodeList.get( i ) );
            }
        }

        if ( predemandeCodeList.size( ) != nNbPlacesToTake || !predemandeCodeList.stream( ).allMatch( PredemandeCodeUtils::isValidCode ) )
        {
            addError( PROPERTY_ERROR_MESSAGE, request.getLocale( ) );
            return redirect( request, VIEW_PREDEMANDEFORM, additionalParameters );
        }

        if ( !PredemandeCodeUtils.hasUniqueValues( predemandeCodeList ) )
        {
            addError( PROPERTY_PREDEMANDE_CODES_NOT_UNIQUE_ERROR_MESSAGE, request.getLocale( ) );
            return redirect( request, VIEW_PREDEMANDEFORM, additionalParameters );
        }

        if ( !PreDemandeValidationService.checkPredemandeCodesValidationAndAppointments( predemandeCodeList ) )
        {
            addError( PROPERTY_ERROR_MESSAGE, request.getLocale( ) );
            return redirect( request, VIEW_PREDEMANDEFORM, additionalParameters );
        }

        PredemandeCodeUtils.insertPredemandeCodesInSession( request.getSession( true ), predemandeCodeList, ",",
                CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME );

        RequestParameters params = new RequestParameters( );
        params.setNbPlacesToTakeValue( String.valueOf( nNbPlacesToTake ) );

        if ( strDateTime != null )
        {
            params.setFormIdParameter( PARAMETER_ID_FORM );
            params.setDateTimeParameter( PARAMETER_DATE_TIME );
            params.setDateTimeValue( strDateTime );
            params.setNbPlacesToTakeParameter( PARAMETER_NUMBER_OF_PLACES_TO_TAKE );
            params.setAnchorParameter( PARAMETER_ANCHOR );
            params.setAnchorValue( STEP_3 );
            return redirect( request,
                    PredemandeCodeUtils.constructRedirectionUrl( request, APPOINTMENT_PLUGIN_XPAGE_NAME, APPOINTMENT_PLUGIN_APPOINTMENTFORM_VIEW_NAME, params ) );
        }

        params.setCategoryParameter( PARAMETER_CATEGORIE );
        params.setCategoryValue( PARAMETER_CATEGORIE_TITRES );
        params.setNbPlacesToTakeParameter( PARAMETER_NB_CONSECUTIVE_SLOTS );
        return redirect( request,
                PredemandeCodeUtils.constructRedirectionUrl( request, APPOINTMENTSEARCH_PLUGIN_XPAGE_NAME, APPOINTMENTSEARCH_PLUGIN_SEARCH_VIEW_NAME, params ) );
    }

    /**
     * Get the number of people of the request, between 1 and the maximum; 1 when the request carries no valid number.
     *
     * @param request
     *            The HTTP request
     * @return The number of people
     */
    private static int getNbPlacesToTake( HttpServletRequest request )
    {
        int nNbPlaces = NumberUtils.toInt( request.getParameter( PARAMETER_NUMBER_OF_PLACES_TO_TAKE ), 1 );

        return ( nNbPlaces >= 1 && nNbPlaces <= MAX_PLACES_TO_TAKE ) ? nNbPlaces : 1;
    }

    /**
     * Get the starting date and time of the slot chosen on the ANTS web site.
     *
     * @param request
     *            The HTTP request
     * @return The date and time in the ISO format, or null when the request carries none or an invalid one
     */
    private static String getDateTime( HttpServletRequest request )
    {
        String strDateTime = request.getParameter( PARAMETER_DATE_TIME );

        if ( StringUtils.isBlank( strDateTime ) )
        {
            return null;
        }
        try
        {
            return LocalDateTime.parse( strDateTime ).toString( );
        }
        catch( DateTimeParseException e )
        {
            return null;
        }
    }

    /**
     * Get the id of the appointment form chosen on the ANTS web site.
     *
     * @param request
     *            The HTTP request
     * @return The id, or null when the request carries none or a non numeric one
     */
    private static String getIdForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        return StringUtils.isNumeric( strIdForm ) ? strIdForm : null;
    }
}
