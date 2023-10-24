package fr.paris.lutece.plugins.appointment.modules.ants.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class PredemandeCodeUtilsTest extends LuteceTestCase {

    private static final String CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME = "APPOINTMENT_CODE_PREDEMANDE";

    private MockHttpServletRequest request;
    private MockHttpSession session;

    public void setUp() {
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
        request.setSession(session);
    }


    @Test
    public void testGetPredemandeCodeList() {
        request.addParameter("predemande_code_1", "TEST000001");
        request.addParameter("predemande_code_2", "TEST000002");
        int totalNumberPersons = 2;

        List<String> predemandeCodeList = PredemandeCodeUtils.getPredemandeCodeList(request, "predemande_code_", totalNumberPersons);

        assertEquals(2, predemandeCodeList.size());
        assertEquals("TEST000001", predemandeCodeList.get(0));
        assertEquals("TEST000002", predemandeCodeList.get(1));
    }

    @Test
    public void testInsertPredemandeCodesInSession() {

        List<String> predemandeCodeList = new ArrayList<>();
        predemandeCodeList.add("TEST000001");
        predemandeCodeList.add("TEST000002");

        PredemandeCodeUtils.insertPredemandeCodesInSession(session, predemandeCodeList, ",", CONSTANT_PREDEMANDE_CODE_LIST_SESSION_ATTRIBUTE_NAME);
        assertEquals("TEST000001,TEST000002", session.getAttribute("APPOINTMENT_CODE_PREDEMANDE"));
    }

}