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
package fr.paris.lutece.plugins.appointment.modules.ants.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fr.paris.lutece.plugins.appointment.modules.ants.common.RequestParameters;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.url.UrlItem;

public class PredemandeCodeUtils
{
	private static final String PROPERTY_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME_KEY = "ants.session.attribute.name";

	private static final String CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME =
			AppPropertiesService.getProperty( PROPERTY_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME_KEY );
	private PredemandeCodeUtils()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get a List of all the predemande codes entered by the user
	 *
	 * @param request
	 * 				Current HTTP Request
	 * @param prefixPredemandeCode
	 * 				The prefix used in the request's parameters to identify a predemande code
	 * @param totalNumberPersons
	 * 				Number of people attending the appointment
	 * @return
	 * 				The predemande codes as a List of Strings
	 */
	public static List<String> getPredemandeCodeList( HttpServletRequest request, String prefixPredemandeCode, int totalNumberPersons )
	{
		List<String> predemandeCodeList = new ArrayList<>( );
		String inputIdValue = null;
		String predemandeCode = null;

		for ( int i = 1; i <= totalNumberPersons; i++ )
		{
			inputIdValue = prefixPredemandeCode.concat( String.valueOf( i ) );
			predemandeCode = request.getParameter( inputIdValue );

			if( predemandeCode != null )
			{
				predemandeCodeList.add( predemandeCode );
			}
		}
		return predemandeCodeList;
	}


	/**
	 * Build the URL used to redirect user, with specific parameters
	 *
	 * @param request
	 * 				Current HTTP Request
	 * @param xPageName
	 * 				Name of the XPage to display
	 * @param viewName
	 * 				Name of the view to display in the XPage
	 * @param reqParams
	 * 				request parameters used in the URL
	 * @return
	 * 				The redirection URL as a String
	 */
	public static String constructRedirectionUrl(HttpServletRequest request, String xPageName, String viewName, RequestParameters reqParams)
	{

		UrlItem url = new UrlItem("Portal.jsp");

		url.addParameter(MVCUtils.PARAMETER_PAGE, xPageName);
		url.addParameter(MVCUtils.PARAMETER_VIEW, viewName);

		if (StringUtils.isNoneBlank(reqParams.getFormIdParameter()))
		{
			url.addParameter(reqParams.getFormIdParameter(), request.getParameter(reqParams.getFormIdParameter()));
		}

		if (StringUtils.isNoneBlank(reqParams.getDateTimeParameter()) && StringUtils.isNoneBlank(reqParams.getDateTimeValue()))
		{
			url.addParameter(reqParams.getDateTimeParameter(), reqParams.getDateTimeValue());
		}

		if (StringUtils.isNoneBlank(reqParams.getNbPlacesToTakeParameter()) && StringUtils.isNoneBlank(reqParams.getNbPlacesToTakeValue()) )
		{
			url.addParameter(reqParams.getNbPlacesToTakeParameter(), reqParams.getNbPlacesToTakeValue());
		}

		if (StringUtils.isNoneBlank(reqParams.getCategoryParameter()) && StringUtils.isNoneBlank(reqParams.getCategoryValue()))
		{
			url.addParameter(reqParams.getCategoryParameter(), reqParams.getCategoryValue());
		}

		if (StringUtils.isNoneBlank(reqParams.getAnchorParameter()) && StringUtils.isNoneBlank(reqParams.getAnchorValue()))
		{
			url.addParameter(reqParams.getAnchorParameter(), reqParams.getAnchorValue());
		}
		return url.getUrl();
	}


	/**
	 * Add the predemande codes in a session's attributes. The codes will be put together as a single String,
	 * where their values are separated by a specific symbol.
	 *
	 * @param session                 HttpSession where the codes will be added
	 * @param predemandeCodeValueList List of all the predemande codes
	 * @param codeValuesSeparator     String used to separate each code value
	 */
	public static void insertPredemandeCodesInSession(HttpSession session,
													  List<String> predemandeCodeValueList, String codeValuesSeparator, String sessionAttributeName )
	{
		if (sessionAttributeName == null)
		{
			return;
		}

		String strPredemandeCodes = String.join( codeValuesSeparator, predemandeCodeValueList );

		if ( StringUtils.isNotBlank( strPredemandeCodes ) )
		{
			session.setAttribute( sessionAttributeName, strPredemandeCodes );
		}
	}
	
	/**
	 * Get the total amount of predemande codes entered by the user
	 * @param session
	 * 				HttpSession where the codes are saved
	 * @param codeValuesSeparator
	 * 				String used to separate each code value
	 * @return
	 * 				The amount of predemande codes
	 */
	public static int getAmountPredemandeCodesInSession( HttpSession session, String codeValuesSeparator )
	{
		String predemandeCodesAsString = ( String ) session.getAttribute( CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME );
		String[] predemandeCodesList = predemandeCodesAsString.split( codeValuesSeparator );
		
		return predemandeCodesList.length;
	}

}
