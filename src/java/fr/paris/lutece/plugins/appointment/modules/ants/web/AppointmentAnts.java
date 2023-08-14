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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.plugins.appointment.modules.ants.service.GetPredemandeCodeList;
import fr.paris.lutece.plugins.appointment.modules.ants.service.PreDemandeValidationService;


@Controller( xpageName = "appointmentants", pageTitleI18nKey = "module.appointment.ants.pageTitle", pagePathI18nKey = "module.appointment.ants.pagePathLabel" )
public class AppointmentAnts extends MVCApplication
{
    private static final String TEMPLATE_PREDEMANDEFORM = "skin/plugins/appointment/modules/ants/predemandeForm.html";
    private static final String VIEW_PREDEMANDEFORM = "predemandeForm";
    private static final String ACTION_PRE_SEARCH = "presearch";
    private static final String PARAMETER_TOTAL_PERSONS_INPUT_VALUE = "totalPersonsInput";
    private static final String PROPERTY_ID_PREDEMANDE_CODE_SUFFIX= "predemande_code_";
    private static final String PARAMETER_NB_SLOTS = "nb_consecutive_slots";
    private static final String PARAMETER_CATEGORIE = "category";
    private static final String CATEGORIE_TITRES = "titres";
    private static final String PARAMETER_ROLE = "role";
    private static final String ROLE_NONE = "none";
    private static final String EXCLUDE_ROLE = AppPropertiesService.getProperty( "appointment-ants.exclude.role", "none" );
    private static final String PROPERTY_ERROR_MESSAGE = "ants.display.fieldsErrorMessage";

    /**
     * Returns the content of the page preDemandeForm.
     *
     * @param request
     *            The HTTP request
     * @return The view
     * @throws AccessDeniedException
     */
    @View( value = VIEW_PREDEMANDEFORM, defaultView = true )
    public XPage viewPreDemandeForm( HttpServletRequest request )
    {
        return getXPage( TEMPLATE_PREDEMANDEFORM, request.getLocale( ));
    }

    /**
     * Redirects to main carto pview
     *
     * @param request
     *            The HTTP request
     * @return The view
     */
    @Action( value = ACTION_PRE_SEARCH )
    public XPage presearch( HttpServletRequest request )
    {
    	int nbSlots = Integer.parseInt(request.getParameter(PARAMETER_TOTAL_PERSONS_INPUT_VALUE));
    	String fieldsErrorMessage = AppPropertiesService.getProperty(PROPERTY_ERROR_MESSAGE);
    	
        List<String> predemandeCodeValueList = new ArrayList<>();
        List<String> predemandeCodeKeyList = GetPredemandeCodeList.getKeyPredemandeCodeList(PROPERTY_ID_PREDEMANDE_CODE_SUFFIX, nbSlots);

    	for(String codeKey : predemandeCodeKeyList) {
    		String predemandeCode = request.getParameter(codeKey);
    		predemandeCodeValueList.add(predemandeCode);
    	}
    	
        boolean isAllCodesNotValid =  PreDemandeValidationService.processPreDemandeCodes(predemandeCodeValueList);

        String role = ROLE_NONE;

        if(!isAllCodesNotValid)
        {
            role = EXCLUDE_ROLE;
            addError(fieldsErrorMessage);
            redirectView(request, VIEW_PREDEMANDEFORM);
        }

        UrlItem url = new UrlItem( "Portal.jsp" );
        url.addParameter( MVCUtils.PARAMETER_PAGE, "appointmentsearch" );
        url.addParameter( MVCUtils.PARAMETER_VIEW, "search" );
        url.addParameter( PARAMETER_NB_SLOTS, nbSlots );
        url.addParameter( PARAMETER_ROLE, role );
        url.addParameter( PARAMETER_CATEGORIE, CATEGORIE_TITRES );

        return redirect( request, url.getUrl( ) );
    }
    
}
