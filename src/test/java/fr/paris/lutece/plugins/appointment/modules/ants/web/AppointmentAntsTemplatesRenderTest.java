/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.modules.ants.business.accesscontrol.config.SlotsNumberAccessControllerConfig;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders the front office templates of the module with the HTML auto-escaping and the macros of the core (LUT-33153).
 */
public class AppointmentAntsTemplatesRenderTest extends LuteceTestCase
{
    private static final String TEMPLATE_PREDEMANDEFORM = "skin/plugins/appointment/modules/ants/predemandeForm.html";
    private static final String TEMPLATE_CONTROLLER = "skin/plugins/appointment/modules/ants/accesscontrol/controller/slots_number_controller_template.html";
    private static final String CODE = "ABCDE12345";

    /**
     * Renders the pre-demand form for two people out of six.
     *
     * @return the HTML
     */
    private static String renderPredemandeForm( )
    {
        Map<String, Object> error = new HashMap<>( );
        error.put( "message", "Code <b>invalide</b>" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "nbPlacesToTake", 2 );
        model.put( "max_places_to_take", 6 );
        model.put( "list_ants_codes", List.of( CODE, "" ) );
        model.put( "starting_date_time", "2026-11-14T10:00" );
        model.put( "id_form", "3" );
        model.put( "errors", List.of( error ) );

        return AppTemplateService.getTemplate( TEMPLATE_PREDEMANDEFORM, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * Finds the input element of a field.
     *
     * @param strHtml
     *            the HTML
     * @param strId
     *            the id of the field
     * @return the input element
     */
    private static String input( String strHtml, String strId )
    {
        Matcher matcher = Pattern.compile( "<input[^>]*\\bid=\"" + strId + "\"[^>]*>" ).matcher( strHtml );
        assertTrue( matcher.find( ), strId + " not found in " + strHtml );

        return matcher.group( );
    }

    /**
     * The form renders: the code fields beyond the number of people are disabled, the others hold their code, and the
     * submit button carries the name of the action.
     */
    @Test
    public void testPredemandeFormRenders( )
    {
        String strHtml = renderPredemandeForm( );

        String strCode1 = input( strHtml, "predemande_code_1" );
        assertTrue( strCode1.contains( "value=\"" + CODE + "\"" ), strCode1 );
        assertFalse( strCode1.contains( " disabled" ), strCode1 );
        assertTrue( strCode1.contains( " required" ), strCode1 );
        assertFalse( input( strHtml, "predemande_code_2" ).contains( " disabled" ), strHtml );
        String strCode3 = input( strHtml, "predemande_code_3" );
        assertTrue( strCode3.contains( " disabled" ), strCode3 );
        assertFalse( strCode3.contains( " required" ), strCode3 );
        assertTrue( input( strHtml, "predemande_code_6" ).contains( " disabled" ), strHtml );
        assertTrue( input( strHtml, "nbPlacesToTake" ).contains( "value=\"2\"" ), strHtml );
        assertTrue( input( strHtml, "starting_date_time" ).contains( "value=\"2026-11-14T10:00\"" ), strHtml );
        assertTrue( Pattern.compile( "<button[^>]*name=\"action_presearch\"" ).matcher( strHtml ).find( ), strHtml );
    }

    /**
     * The error messages are written as text, and the numbers of the script are written as JavaScript numbers.
     */
    @Test
    public void testPredemandeFormEscapesTextAndScriptValues( )
    {
        String strHtml = renderPredemandeForm( );

        assertTrue( strHtml.contains( "Code &lt;b&gt;invalide&lt;/b&gt;" ), strHtml );
        assertFalse( strHtml.contains( "<b>invalide" ), strHtml );
        assertTrue( strHtml.contains( "|| 1, 1 ), 6 );" ), strHtml );
        assertTrue( strHtml.contains( "i <= 6; i++" ), strHtml );
        assertFalse( strHtml.contains( "#i18n{module.appointment.ants.predemandeserial.label_1}" ), strHtml );
    }

    /**
     * Each code field is described by its help text and, after a failed check, by the error messages, and is marked
     * invalid; the link to the ANTS web site says that it opens a new window.
     */
    @Test
    public void testPredemandeFormTiesHelpAndErrorsToFields( )
    {
        String strHtml = renderPredemandeForm( );

        String strCode1 = input( strHtml, "predemande_code_1" );
        assertTrue( strCode1.contains( "aria-describedby=\"predemande_errors predemande_help_1\"" ), strCode1 );
        assertTrue( strCode1.contains( "aria-invalid=\"true\"" ), strCode1 );
        assertTrue( strHtml.contains( "id=\"predemande_errors\" tabindex=\"-1\"" ), strHtml );
        assertTrue( strHtml.contains( "id=\"predemande_help_1\"" ), strHtml );
        assertTrue( Pattern.compile( "<a href=\"https://ants.gouv.fr/\"[^>]*target=\"_blank\"[^>]*>.*?<span class=\"visually-hidden\">", Pattern.DOTALL )
                .matcher( strHtml ).find( ), strHtml );
    }

    /**
     * The comment of the slots number controller, rich text entered in the back office editor, is written as HTML.
     */
    @Test
    public void testControllerCommentIsWrittenAsHtml( )
    {
        SlotsNumberAccessControllerConfig config = new SlotsNumberAccessControllerConfig( );
        config.setComment( "<p>Bring <strong>two</strong> documents</p>" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "config", config );

        String strHtml = AppTemplateService.getTemplate( TEMPLATE_CONTROLLER, Locale.FRENCH, model ).getHtml( );

        assertTrue( strHtml.contains( "<strong>two</strong>" ), strHtml );
        assertFalse( strHtml.contains( "&lt;strong&gt;" ), strHtml );
    }
}
