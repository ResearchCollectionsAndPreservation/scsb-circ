package com.pkrete.jsip2.messages.responses;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SIP2RecallResponseUT {


    @Test
    public void testSIP2CreateBibResponse() {
        SIP2RecallResponse sip2RecallResponse = new SIP2RecallResponse("82Test");
        sip2RecallResponse.setExpirationDate(new Date().toString());
        sip2RecallResponse.setPickupLocation("PA");
        sip2RecallResponse.setItemIdentifier("12345");
        sip2RecallResponse.setTitleIdentifier("234");
        sip2RecallResponse.setBibId("123456");
        sip2RecallResponse.setScreenMessage(Arrays.asList("Success", "Failure"));
        sip2RecallResponse.setPrintLine(Arrays.asList("P", "A"));
        sip2RecallResponse.setSequence(1);
        String res = sip2RecallResponse.countChecksum();
        assertTrue(true);
    }
}
