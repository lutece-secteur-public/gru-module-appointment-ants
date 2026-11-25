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
package fr.paris.lutece.plugins.appointment.modules.ants.service;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.modules.ants.web.PredemandeResponse;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Answers of the ANTS status API, checked without any HTTP call.
 */
public class PreDemandeValidationServiceTest extends LuteceTestCase
{
    /**
     * A validated pre-demand whose answer has no "appointments" key (Jackson leaves the list null) is a pre-demand without
     * appointment, not a NullPointerException.
     */
    @Test
    public void testValidatedAnswerWithoutAppointmentsIsAccepted( )
    {
        PredemandeResponse response = new PredemandeResponse( );
        response.setStatus( "validated" );

        assertTrue( PreDemandeValidationService.areValidatedWithoutAppointment( Map.of( "AB12345678", response ), List.of( "AB12345678" ) ) );
    }

    /**
     * A pre-demand already used for an appointment is refused.
     */
    @Test
    public void testAnswerWithAnAppointmentIsRefused( )
    {
        PredemandeResponse response = new PredemandeResponse( );
        response.setStatus( "validated" );
        response.setAppointments( List.of( new PredemandeResponse.Appointment( ) ) );

        assertFalse( PreDemandeValidationService.areValidatedWithoutAppointment( Map.of( "AB12345678", response ), List.of( "AB12345678" ) ) );
    }

    /**
     * A pre-demand that is not validated is refused, with or without the "appointments" key.
     */
    @Test
    public void testAnswerNotValidatedIsRefused( )
    {
        PredemandeResponse response = new PredemandeResponse( );
        response.setStatus( "unknown" );

        assertFalse( PreDemandeValidationService.areValidatedWithoutAppointment( Map.of( "AB12345678", response ), List.of( "AB12345678" ) ) );
    }
}
