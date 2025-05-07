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

package fr.paris.lutece.plugins.appointment.modules.ants.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.appointment.modules.ants.utils.HttpCallsUtils;
import fr.paris.lutece.plugins.appointment.modules.ants.web.PreDemandeStatusEnum;
import fr.paris.lutece.plugins.appointment.modules.ants.web.PredemandeResponse;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.html.EncodingService;

/**
 * This class provides methods for processing and validating predemande codes.
 */
public class PreDemandeValidationService
{

    private static final String PROPERTY_ENDPOINT_STATUS = AppPropertiesService.getProperty( "ants.api.opt.get.status" );
    private static final String PROPERTY_API_OPT_AUTH_TOKEN_KEY = AppPropertiesService.getProperty( "ants.auth.token" );
    private static final String PROPERTY_API_OPT_AUTH_TOKEN_VALUE = String.valueOf( DatastoreService.getDataValue( "module.appointment.ants.site_property.token" ,"") );
    private static final String PROPERTY_ID_APPLICATION_PARAMETER = AppPropertiesService.getProperty( "ants.ids_application.parameters" );
    private static final String PROPERTY_MEETING_POINT_ID_PARAMETER = AppPropertiesService.getProperty( "ants.meeting_point_id.parameters" );
    private static final String PROPERTY_MEETING_POINT_ID_DEFAULT_VALUE = AppPropertiesService.getProperty( "ants.meeting_point_id.defaultValue" );

    // Timeout properties specific to the ANTS HTTP calls
    private static final String PROPERTY_SOCKET_TIMEOUT = "ants.api.socketTimeout";
    private static final String PROPERTY_CONNECTION_TIMEOUT = "ants.api.connectionTimeout";

    private PreDemandeValidationService( )
    {
    }

    /**
     * Processes a list of predemande codes to check their status and appointments.
     *
     * @param codes
     *            The list of predemande codes to process.
     * @return True if all predemande codes are validated and have appointments; otherwise, false.
     * @throws IOException
     *             If there is an error while processing the predemande codes.
     */
    public static boolean checkPredemandeCodesValidationAndAppointments( List<String> codes ) throws IOException
    {
        Map<String, PredemandeResponse> responseMap = getPreDemandeStatusAndAppointments( codes );

        if ( responseMap.isEmpty( ) )
        {
            return false;
        }

        for ( PredemandeResponse predemande : responseMap.values( ) )
        {
            List<PredemandeResponse.Appointment> appointments = predemande.getAppointments( );

            if ( !appointments.isEmpty( ) || !PreDemandeStatusEnum.VALIDATED.name( ).equalsIgnoreCase( predemande.getStatus( ) ) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieves the pre-demande status and appointments from the API.
     *
     * @param codes
     *            The list of predemande codes to retrieve.
     * @return The JSON response from the API.
     * @throws IOException
     *             If there is an error while processing the predemande codes.
     */
    private static Map<String, PredemandeResponse> getPreDemandeStatusAndAppointments( List<String> codes ) throws IOException
    {
        UrlItem urlItem;
        String apiUrl = null;

        if ( !codes.isEmpty( ) )
        {
            urlItem = new UrlItem( PROPERTY_ENDPOINT_STATUS + PROPERTY_ID_APPLICATION_PARAMETER + "=" + codes.get( 0 ) );
            for ( int i = 1; i < codes.size( ); i++ )
            {
                urlItem.addParameter( PROPERTY_ID_APPLICATION_PARAMETER, codes.get( i ) );
            }
            // Add the "meeting_point_id" parameter and its value in the URL
            String strMeetingPointId = EncodingService.encodeUrl( PROPERTY_MEETING_POINT_ID_DEFAULT_VALUE ).replace( "+", "%20" );
            urlItem.addParameter( PROPERTY_MEETING_POINT_ID_PARAMETER, strMeetingPointId );

            apiUrl = urlItem.toString( );
        }

        // Create a new HttpAccess object with specific timeout values, if any was set,
        // otherwise the default configuration will be used
        HttpAccess httpAccess = HttpCallsUtils.getHttpAccessTimeoutFromProperties( PROPERTY_SOCKET_TIMEOUT, PROPERTY_CONNECTION_TIMEOUT );

        Map<String, String> headers = new HashMap<>( );
        headers.put( PROPERTY_API_OPT_AUTH_TOKEN_KEY, PROPERTY_API_OPT_AUTH_TOKEN_VALUE );

        try
        {
            String jsonResponse = httpAccess.doGet( apiUrl, null, null, headers );
            ObjectMapper mapper = new ObjectMapper( );
            TypeReference<HashMap<String, PredemandeResponse>> typeRef = new TypeReference<HashMap<String, PredemandeResponse>>( )
            {
            };
            return mapper.readValue( jsonResponse, typeRef );
        }
        catch( HttpAccessException | IOException ex )
        {
            AppLogService.error( "Error calling API {}", apiUrl, ex );
            return Collections.emptyMap( );
        }
    }
}
