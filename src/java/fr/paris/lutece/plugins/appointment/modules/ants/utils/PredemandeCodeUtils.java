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

package fr.paris.lutece.plugins.appointment.modules.ants.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.url.UrlItem;

public class PredemandeCodeUtils {
	
	private static final String PROPERTY_SESSION_ATTRIBUTE_NAME =AppPropertiesService.getProperty("ants.session.attribute.name");

	public static List<String> getPredemandeCodeList(HttpServletRequest request, String idSuffixPredemandeCode, int totalNumberPersons)
	{
		List<String> predemandeCodeList = new ArrayList<>(); 
		String inputIdValue = null;
		String predemandeCode = null;
		
		for (int i = 1; i <= totalNumberPersons; i++) {
			inputIdValue = idSuffixPredemandeCode.concat(String.valueOf(i)); 
            predemandeCode = request.getParameter(inputIdValue);
            predemandeCodeList.add(predemandeCode);
        }
		
		return predemandeCodeList;
	}
	
	public static String constructRedirectionUrl(HttpServletRequest request, List<String> predemandeCodeValueList,
			String category, String categoryParams, String xPageName, String viewName, String idForm, String dateTime,
			String dateTimeValue, String nbPlacesToTake,String nbPlacesToTakeValue, String anchor, String anchorValue) {

		UrlItem url = new UrlItem("Portal.jsp");

		url.addParameter(MVCUtils.PARAMETER_PAGE, xPageName);
		url.addParameter(MVCUtils.PARAMETER_VIEW, viewName);

		if (StringUtils.isNoneBlank(idForm)) {
			url.addParameter(idForm, request.getParameter(idForm));
		}
		
		if (StringUtils.isNoneBlank(dateTime) && StringUtils.isNoneBlank(dateTimeValue)) {
			url.addParameter(dateTime, dateTimeValue);
		}
		
		if (StringUtils.isNoneBlank(nbPlacesToTake) && StringUtils.isNoneBlank(nbPlacesToTakeValue) ) {
			url.addParameter(nbPlacesToTake, request.getParameter(nbPlacesToTakeValue));
		}
		
		if (StringUtils.isNoneBlank(category) && StringUtils.isNoneBlank(categoryParams)) {
			url.addParameter(category, categoryParams);
		}
		
		if (StringUtils.isNoneBlank(anchor) && StringUtils.isNoneBlank(anchorValue)) {
			url.addParameter(anchor, anchorValue);
		}
		return url.getUrl();

	}
	
	public static void insertCodesPredemandeOnSession(HttpServletRequest request,
			List<String> predemandeCodeValueList) 
	{

		String strPredemandeCodes = String.join(",", predemandeCodeValueList);
		HttpSession session = request.getSession(true);
		
		if (StringUtils.isNotBlank(strPredemandeCodes)) 
		{	
			session.removeAttribute(PROPERTY_SESSION_ATTRIBUTE_NAME);
			session.setAttribute(PROPERTY_SESSION_ATTRIBUTE_NAME, strPredemandeCodes);
		}
	}
}
