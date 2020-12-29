package org.recap.controller;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class MessageRestControllerUT extends BaseTestCaseUT {

    @Mock
    MessageRestController messageRestController;

    String institutionCode = "{institutionCode : PUL}";
    String institutionCodeJson = "{\"PUL\":\"PUL\"}";
    String imslocation = "{imslocation : PU}";
    @Before
    public void setup(){
        ReflectionTestUtils.setField(messageRestController, "institution", institutionCode);
        ReflectionTestUtils.setField(messageRestController, "imsLocation", imslocation);
    }

    /*@Test
    public void getValue(){
        String jString = "{\"PUL\": \"PUL\"}";
        JSONObject jsonObject = new JSONObject(jString.toString());
        String institutionCode = "PUL";
        ReflectionTestUtils.setField(messageRestController, "institution", institutionCodeJson);
        Mockito.when(messageRestController.getValue(jsonObject.toString())).thenCallRealMethod();
        Map<String, Object> result = messageRestController.getValue("PUL");
        assertNotNull(result);
    }*/
    @Test
    public void getInsData(){
        Mockito.when(messageRestController.getInsData()).thenCallRealMethod();
        Map<String, Object> result = messageRestController.getInsData();
        assertNotNull(result);
    }

    @Test
    public void getLocationData(){
        Mockito.when(messageRestController.getLocationData()).thenCallRealMethod();
        Map<String, Object> result = messageRestController.getLocationData();
        assertNotNull(result);
    }
}
