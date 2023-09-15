package fr.paris.lutece.plugins.appointment.modules.ants.service;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.plugins.appointment.modules.ants.pojo.PredemandePOJO;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;


public class PreDemandeValidationService {

	private static final String PROPERTY_ENDPOINT_STATUS = AppPropertiesService.getProperty("ants.api.opt.get.status");
	private static final String PROPERTY_API_OPT_AUTH_TOKEN_KEY = AppPropertiesService.getProperty("ants.auth.token");
	private static final String PROPERTY_API_OPT_AUTH_TOKEN_VALUE = AppPropertiesService.getProperty("ants.api.opt.auth.token");
	
		public enum PreDemandeStatus
		{
		   brouillon,  // N/A
		   consumed,   // « consumed »
		   validated,  // « validated »
		   declared,   // « declared »
		   unknown,    // « unknown »
		   expired,    // « expired »
		   purge       // N/A
		 }

	    public static boolean processPreDemandeCodes(List<String> codes) throws IOException 
	    {
			String response = getPreDemandeStatusAndAppointments(codes);
			
			if (response != null)
			{
				Map<String, PredemandePOJO> responseAsMap = getStatusResponseAsMap(response);
				
				for (Map.Entry<String, PredemandePOJO> entry : responseAsMap.entrySet()) 
				{ 
		          PredemandePOJO predemande = entry.getValue();
		           
		          List<PredemandePOJO.Appointment> appointments = predemande.getAppointments();
		            
		          if (!appointments.isEmpty() || !PreDemandeStatus.valueOf(predemande.getStatus()).equals(PreDemandeStatus.validated))
		          {
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

	    private static String getPreDemandeStatusAndAppointments(List<String> codes) 
	    {
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
	            return httpAccess.doGet(endpointAPI,null , null, headers);
	        }
	        catch (HttpAccessException ex) {
	            AppLogService.error("Error calling API {}", endpointAPI, ex);
	        }

	        return null;
	    }
}
