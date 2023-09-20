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

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.plugins.appointment.modules.ants.web.PreDemandeStatusEnum;
import fr.paris.lutece.plugins.appointment.modules.ants.web.PredemandeResponse;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * This class provides methods for processing and validating predemande codes.
 */
public class PreDemandeValidationService
{

	private static final String PROPERTY_ENDPOINT_STATUS = AppPropertiesService.getProperty("ants.api.opt.get.status");
	private static final String PROPERTY_API_OPT_AUTH_TOKEN_KEY = AppPropertiesService.getProperty("ants.auth.token");
	private static final String PROPERTY_API_OPT_AUTH_TOKEN_VALUE = AppPropertiesService.getProperty("ants.api.opt.auth.token");
	private static final String PROPERTY_ID_APPLICATION_PARAMETER = AppPropertiesService.getProperty("ants.ids_application.parameters");

	private PreDemandeValidationService()
	{
	}

	/**
	 * Processes a list of predemande codes to check their status and appointments.
	 *
	 * @param codes
	 * 				The list of predemande codes to process.
	 * @return
	 * 				True if all predemande codes are validated and have appointments; otherwise, false.
	 * @throws IOException If there is an error while processing the predemande codes.
	 */
	public static boolean processPreDemandeCodes(List<String> codes) throws IOException {
		String response = getPreDemandeStatusAndAppointments(codes);

		if (StringUtils.isNotBlank(response)) {
			Map<String, PredemandeResponse> responseAsMap = getStatusResponseAsMap(response);

			for (Map.Entry<String, PredemandeResponse> entry : responseAsMap.entrySet()) {
				PredemandeResponse predemande = entry.getValue();

				List<PredemandeResponse.Appointment> appointments = predemande.getAppointments();

				if (CollectionUtils.isNotEmpty(appointments) || !PreDemandeStatusEnum.validated
						.equals(PreDemandeStatusEnum.valueOf(predemande.getStatus()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Converts the JSON response to a map of predemande status objects.
	 *
	 * @param response
	 * 				The JSON response from the API.
	 * @return
	 * 				A map of predemande status objects.
	 * @throws IOException If there is an error parsing the JSON response.
	 */
	public static Map<String, PredemandeResponse> getStatusResponseAsMap(String response) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(response);

		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
		Map<String, PredemandeResponse> resultMap = new HashMap<>();

		while (fieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> entry = fieldsIterator.next();
			String fieldName = entry.getKey();
			JsonNode field = entry.getValue();

			PredemandeResponse predemande = mapper.readerFor(PredemandeResponse.class).readValue(field.toString());
			resultMap.put(fieldName, predemande);
		}

		return resultMap;
	}


	/**
	 * Retrieves the pre-demande status and appointments from the API.
	 *
	 * @param codes
	 * 			The list of predemande codes to retrieve.
	 * @return The JSON response from the API.
	 */
	private static String getPreDemandeStatusAndAppointments(List<String> codes) {
		UrlItem urlItem = new UrlItem(PROPERTY_ENDPOINT_STATUS);
		for (String code : codes)
		{
			urlItem.addParameter(PROPERTY_ID_APPLICATION_PARAMETER, code);
		}
		String strUrlItem = urlItem.toString();
		HttpAccess httpAccess = new HttpAccess();

		try
		{
			Map<String, String> headers = new HashMap<>();
			headers.put(PROPERTY_API_OPT_AUTH_TOKEN_KEY, PROPERTY_API_OPT_AUTH_TOKEN_VALUE);
			return httpAccess.doGet(strUrlItem, null, null, headers);
		}
		catch (HttpAccessException ex)
		{
			AppLogService.error("Error calling API {}", strUrlItem, ex);
		}

		return null;
	}
}
