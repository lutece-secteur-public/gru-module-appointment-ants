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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class PredemandeResponse
{

    @JsonProperty( "status" )
    private String _status;

    @JsonProperty( "appointments" )
    private List<Appointment> _appointments;

    // Getter & setters
    public String getStatus( )
    {
        return _status;
    }

    public void setStatus( String status )
    {
        this._status = status;
    }

    public List<Appointment> getAppointments( )
    {
        return _appointments;
    }

    public void setAppointments( List<Appointment> appointments )
    {
        this._appointments = appointments;
    }

    public static class Appointment
    {
        @JsonProperty( "management_url" )
        private String _managementUrl;

        @JsonProperty( "meeting_point" )
        private String _meetingPoint;

        @JsonProperty( "appointment_date" )
        private String _appointmentDate;

        @JsonProperty( "editor_comment" )
        private String _editorComment;

        // Getter & setters
        public String getManagementUrl( )
        {
            return _managementUrl;
        }

        public String getMeetingPoint( )
        {
            return _meetingPoint;
        }

        public String getAppointmentDate( )
        {
            return _appointmentDate;
        }

        public String getEditorComment( )
        {
            return _editorComment;
        }

        public void setManagementUrl( String managementUrl )
        {
            this._managementUrl = managementUrl;
        }

        public void setMeetingPoint( String meetingPoint )
        {
            this._meetingPoint = meetingPoint;
        }

        public void setAppointmentDate( String appointmentDate )
        {
            this._appointmentDate = appointmentDate;
        }

        public void setEditorComment( String editorComment )
        {
            this._editorComment = editorComment;
        }
    }

}
