package fr.paris.lutece.plugins.appointment.modules.ants.pojo;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class PredemandePOJO {

	@JsonProperty( "status" )
	private String status;

	@JsonProperty( "appointments" )
	private List<Appointment> appointments;
	
	//Getter & setters
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}
	
	public class Appointment {
	   @JsonProperty("management_url")
	   private String managementUrl;

	   @JsonProperty("meeting_point")
	   private String meetingPoint;

	   @JsonProperty("appointment_date")
	   private String appointmentDate;

	   //Getter & setters
	   public String getManagementUrl() {
			return managementUrl;
	   }
	
	   public String getMeetingPoint() {
			return meetingPoint;
		}
	
		public String getAppointmentDate() {
			return appointmentDate;
		}

		public void setManagementUrl(String managementUrl) {
			this.managementUrl = managementUrl;
		}
		
		public void setMeetingPoint(String meetingPoint) {
			this.meetingPoint = meetingPoint;
		}
		
		public void setAppointmentDate(String appointmentDate) {
			this.appointmentDate = appointmentDate;
		}
	 }
}