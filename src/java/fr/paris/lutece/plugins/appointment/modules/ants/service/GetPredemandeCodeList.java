package fr.paris.lutece.plugins.appointment.modules.ants.service;

import java.util.ArrayList;
import java.util.List;

public class GetPredemandeCodeList {

	public static List<String> getKeyPredemandeCodeList(String idSuffixPredemandeCode, int totalNumberPersons)
	{
		List<String> predemandeCodeKeyList = new ArrayList<>(); 
		for (int i = 1; i <= totalNumberPersons; i++) {
            String idOfInputKey = idSuffixPredemandeCode.concat(String.valueOf(i)); 
            predemandeCodeKeyList.add(idOfInputKey);
        }
		return predemandeCodeKeyList;
	}

}
