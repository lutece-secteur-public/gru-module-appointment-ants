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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class PredemandeCodeUtilsTest extends LuteceTestCase
{

    private static final String CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME = "APPOINTMENT_CODE_PREDEMANDE";

    private MockHttpServletRequest request;
    private MockHttpSession session;

    protected void setUp( ) throws Exception
    {
        super.setUp( );
        request = new MockHttpServletRequest( );
        session = new MockHttpSession( );
        request.setSession( session );
    }

    public void testGetPredemandeCodeList( )
    {
        request.addParameter( "predemande_code_1", "TEST000001" );
        request.addParameter( "predemande_code_2", "TEST000002" );
        int totalNumberPersons = 2;

        List<String> predemandeCodeList = PredemandeCodeUtils.getPredemandeCodeList( request, "predemande_code_", totalNumberPersons );

        assertEquals( 2, predemandeCodeList.size( ) );
        assertEquals( "TEST000001", predemandeCodeList.get( 0 ) );
        assertEquals( "TEST000002", predemandeCodeList.get( 1 ) );
    }

    public void testInsertPredemandeCodesInSession( )
    {

        List<String> predemandeCodeList = new ArrayList<>( );
        predemandeCodeList.add( "TEST000001" );
        predemandeCodeList.add( "TEST000002" );

        PredemandeCodeUtils.insertPredemandeCodesInSession( session, predemandeCodeList, ",", CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME );
        assertEquals( "TEST000001,TEST000002", session.getAttribute( "APPOINTMENT_CODE_PREDEMANDE" ) );
    }

}
