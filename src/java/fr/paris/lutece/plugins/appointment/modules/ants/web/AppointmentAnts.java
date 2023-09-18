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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.plugins.appointment.modules.ants.service.PreDemandeValidationService;
import fr.paris.lutece.plugins.appointment.modules.ants.utils.PredemandeCodeUtils;

@Controller(xpageName = AppointmentAnts.XPAGE_NAME, pageTitleI18nKey = "module.appointment.ants.pageTitle", pagePathI18nKey = "module.appointment.ants.pagePathLabel")
public class AppointmentAnts extends MVCApplication {
	/**
	 * The name of the XPage
	 */
	protected static final String XPAGE_NAME = "appointmentants";

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
	private static final String VIEW_PREDEMANDEFORM = "predemandeForm";

	// ACTIONS
	private static final String ACTION_PRE_SEARCH = "presearch";

	// PROPERTIES
	private static final String PROPERTY_ID_PREDEMANDE_CODE_SUFFIX = "predemande_code_";
	private static final String PROPERTY_ERROR_MESSAGE = "ants.display.fieldsErrorMessage";


	// PARAMETERS
	private static final String PARAMETER_CATEGORIE = "category";
	private static final String PARAMETER_CATEGORIE_TITRES = "titres";
	private static final String PARAMETER_DATE_TIME = "starting_date_time";
	private static final String PARAMETER_PROPERTY_ID_FORM = "id_form";
	private static final String PARAMETER_NB_CONSECUTIVE_SLOTS = "nb_consecutive_slots";
	private static final String PARAMETER_PLACES_TAKED_NOMBER = "nbPlacesToTake";
	private static final String PARAMETER_PLACES_TAKED_NOMBER_VALUE = "nbPlacesToTake";
	private static final String PARAMETER_ANCHOR = "anchor";
	private static final String PARAMETER_ANCHOR_VALUE = "#step3";

	// MARKERS
	private static final String MARKER_STRATING_DATE_TIME = "starting_date_time";
	private static final String MARKER_ID_FORM = "id_form";
	private static final String MARKER_NB_PLACES_TO_TAKE = "nbPlacesToTake";

	/**
	 * Returns the content of the page preDemandeForm.
	 *
	 * @param request The HTTP request
	 * @return The view
	 * @throws AccessDeniedException
	 */
	@View(value = VIEW_PREDEMANDEFORM, defaultView = true)
	public XPage viewPreDemandeForm(HttpServletRequest request) {
		String dateTime = request.getParameter(PARAMETER_DATE_TIME);
		String idForm = request.getParameter(PARAMETER_PROPERTY_ID_FORM);
		String nbPlacesToTake = request.getParameter(PARAMETER_PLACES_TAKED_NOMBER_VALUE);

		Map<String, Object> model = getModel();
		model.put(MARKER_STRATING_DATE_TIME, dateTime);
		model.put(MARKER_ID_FORM, idForm);
		model.put(MARKER_NB_PLACES_TO_TAKE, nbPlacesToTake);

		return getXPage(TEMPLATE_PREDEMANDEFORM, request.getLocale(), model);
	}

	/**
	 * Redirects to main carto pview
	 *
	 * @param request The HTTP request
	 * @return The view
	 */
	@Action(value = ACTION_PRE_SEARCH)
	public XPage presearch(HttpServletRequest request) throws IOException {
		Integer nbPlacesToTake = Integer.parseInt(request.getParameter(PARAMETER_PLACES_TAKED_NOMBER_VALUE));
		String fieldsErrorMessage = AppPropertiesService.getProperty(PROPERTY_ERROR_MESSAGE);
		String dateTime = request.getParameter(PARAMETER_DATE_TIME);

		List<String> predemandeCodeList = PredemandeCodeUtils.getPredemandeCodeList(request,
				PROPERTY_ID_PREDEMANDE_CODE_SUFFIX, nbPlacesToTake);

		PredemandeCodeUtils.insertCodesPredemandeOnSession(request, predemandeCodeList);

		XPage redirectionXpage = new XPage();
		String url = null;

		boolean isAllCodesNotValid = PreDemandeValidationService.processPreDemandeCodes(predemandeCodeList);

		if (StringUtils.isNotBlank(dateTime)) {
			if (!isAllCodesNotValid) {
				addError(fieldsErrorMessage);
				url = PredemandeCodeUtils.constructRedirectionUrl(request, predemandeCodeList, null, null,
						XPAGE_NAME, VIEW_PREDEMANDEFORM, PARAMETER_PROPERTY_ID_FORM, PARAMETER_DATE_TIME, dateTime,
						PARAMETER_PLACES_TAKED_NOMBER, PARAMETER_PLACES_TAKED_NOMBER_VALUE, null, null);
				redirectionXpage = redirect(request, url);
			} else {
				url = PredemandeCodeUtils.constructRedirectionUrl(request, predemandeCodeList, null, null,
						APPOINTMENT_PLUGIN_XPAGE_NAME, APPOINTMENT_PLUGIN_APPOINTMENTFORM_VIEW_NAME,
						PARAMETER_PROPERTY_ID_FORM, PARAMETER_DATE_TIME, dateTime, PARAMETER_PLACES_TAKED_NOMBER,
						PARAMETER_PLACES_TAKED_NOMBER_VALUE, PARAMETER_ANCHOR, PARAMETER_ANCHOR_VALUE);
				redirectionXpage = redirect(request, url);
			}

		} else {
			if (!isAllCodesNotValid) {
				addError(fieldsErrorMessage);
				redirectionXpage = redirectView(request, VIEW_PREDEMANDEFORM);
			} else {
				url = PredemandeCodeUtils.constructRedirectionUrl(request, predemandeCodeList, PARAMETER_CATEGORIE,
						PARAMETER_CATEGORIE_TITRES, APPOINTMENTSEARCH_PLUGIN_XPAGE_NAME,
						APPOINTMENTSEARCH_PLUGIN_SEARCH_VIEW_NAME, null, null, null, PARAMETER_NB_CONSECUTIVE_SLOTS,
						PARAMETER_PLACES_TAKED_NOMBER_VALUE, null, null);
				redirectionXpage = redirect(request, url);
			}
		}

		return redirectionXpage;
	}

}
