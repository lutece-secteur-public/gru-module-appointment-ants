package fr.paris.lutece.plugins.appointment.modules.ants.service;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.portal.service.util.AppLogService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;



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

	    public static boolean processPreDemandeCodes(List<String> codes) {

			for (String code : codes)
			{
				if (!VerificationPredemandeCodePatternService.isMatchesPattern(code))
				{
					return false;
				}
			}

			String jsonResponse = getPreDemandeStatusAndAppointments(codes);

			if (jsonResponse != null)
			{
				String status;
				JSONArray appointmentsArray;
				JSONObject jsonObject = new JSONObject(jsonResponse);

				for (String key : jsonObject.keySet())
				{
					JSONObject innerObject = jsonObject.getJSONObject(key);
					status = innerObject.getString("status");
					appointmentsArray = innerObject.getJSONArray("appointments");

					PreDemandeStatus statusEnum = PreDemandeStatus.valueOf(status);
					if (statusEnum != PreDemandeStatus.validated || appointmentsArray.length() > 0)
					{
						return false;
					}
				}
			}
			return true;
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
