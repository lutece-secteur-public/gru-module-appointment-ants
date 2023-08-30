/*
 * Copyright (c) 2002-2022, City of Paris
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
import fr.paris.lutece.plugins.appointment.modules.ants.pojo.PreDemandeStatusEnum;
import fr.paris.lutece.plugins.appointment.modules.ants.pojo.PredemandePOJO;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


public class PreDemandeValidationService {

	private static final String PROPERTY_ENDPOINT_STATUS = AppPropertiesService.getProperty("ants.api.opt.get.status");
	private static final String PROPERTY_API_OPT_AUTH_TOKEN_KEY = AppPropertiesService.getProperty("ants.auth.token");
	private static final String PROPERTY_API_OPT_AUTH_TOKEN_VALUE = AppPropertiesService
			.getProperty("ants.api.opt.auth.token");

	public static boolean processPreDemandeCodes(List<String> codes) throws IOException {
		String response = getPreDemandeStatusAndAppointments(codes);

		if (StringUtils.isNotBlank(response)) {
			Map<String, PredemandePOJO> responseAsMap = getStatusResponseAsMap(response);

			for (Map.Entry<String, PredemandePOJO> entry : responseAsMap.entrySet()) {
				PredemandePOJO predemande = entry.getValue();

				List<PredemandePOJO.Appointment> appointments = predemande.getAppointments();

				if (CollectionUtils.isNotEmpty(appointments) || !PreDemandeStatusEnum.validated
						.equals(PreDemandeStatusEnum.valueOf(predemande.getStatus()))) {
					return false;
				}
			}
		}
		return true;
	}

	public static Map<String, PredemandePOJO> getStatusResponseAsMap(String response) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(response);

		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
		Map<String, PredemandePOJO> resultMap = new HashMap<>();

		while (fieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> entry = fieldsIterator.next();
			String fieldName = entry.getKey();
			JsonNode field = entry.getValue();

			PredemandePOJO predemande = mapper.readerFor(PredemandePOJO.class).readValue(field.toString());
			resultMap.put(fieldName, predemande);
		}

		return resultMap;
	}

	private static String getPreDemandeStatusAndAppointments(List<String> codes) {
		StringBuilder urlBuilder = new StringBuilder(PROPERTY_ENDPOINT_STATUS);

		for (String code : codes) {
			urlBuilder.append("application_ids=").append(code).append("&");
		}

		// Remove the trailing "&"
		String endpointAPI = urlBuilder.toString();
		endpointAPI = endpointAPI.substring(0, endpointAPI.length() - 1);

		HttpAccess httpAccess = new HttpAccess();

		try {
			Map<String, String> headers = new HashMap<>();
			headers.put(PROPERTY_API_OPT_AUTH_TOKEN_KEY, PROPERTY_API_OPT_AUTH_TOKEN_VALUE);
			return httpAccess.doGet(endpointAPI, null, null, headers);
		} catch (HttpAccessException ex) {
			AppLogService.error("Error calling API {}", endpointAPI, ex);
		}

		return null;
	}
}
