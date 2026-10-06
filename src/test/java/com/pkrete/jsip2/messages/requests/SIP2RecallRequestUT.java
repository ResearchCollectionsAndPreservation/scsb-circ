package com.pkrete.jsip2.messages.requests;

import com.pkrete.jsip2.variables.HoldMode;
import com.pkrete.jsip2.variables.HoldType;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SIP2RecallRequestUT {


    @Test
    public void testSIP2RecallRequest() {
        SIP2RecallRequest SIP2RecallRequestnew = new SIP2RecallRequest("test", "test", "Test", "1234");
        String result = SIP2RecallRequestnew.getData();
        assertNotNull(result);
    }

    @Test
    public void testSIP2RecallRequest2() {
        SIP2RecallRequest SIP2RecallRequestnew = new SIP2RecallRequest("test", "test");
        SIP2RecallRequestnew.setHoldMode(HoldMode.ADD);
        SIP2RecallRequestnew.setHoldType(HoldType.ANY_COPY);
        SIP2RecallRequestnew.getHoldMode();
        SIP2RecallRequestnew.getHoldType();
        SIP2RecallRequestnew.setExpirationDate(new Date().toString());
        SIP2RecallRequestnew.setPickupLocation("PA");
        SIP2RecallRequestnew.setPatronPassword("test");
        SIP2RecallRequestnew.setUseFeeAcknowledged(true);
        SIP2RecallRequestnew.setErrorDetectionEnabled(true);
        String result = SIP2RecallRequestnew.getData();
        assertNotNull(result);
    }
}

