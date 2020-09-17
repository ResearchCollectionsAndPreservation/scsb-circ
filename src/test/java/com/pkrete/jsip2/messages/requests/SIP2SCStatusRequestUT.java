package com.pkrete.jsip2.messages.requests;

import com.pkrete.jsip2.variables.StatusCode;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SIP2SCStatusRequestUT{

    @Test
    public void testSIP2RecallRequest() {
        SIP2SCStatusRequest sip2SCStatusRequest = new SIP2SCStatusRequest(StatusCode.OK);
        sip2SCStatusRequest.setMaxPrintWidth("10");
        sip2SCStatusRequest.getMaxPrintWidth();
        String result = sip2SCStatusRequest.getData();
        assertNotNull(result);
    }
    @Test
    public void testSIP2RecallRequest2() {
        SIP2SCStatusRequest sip2SCStatusRequest = new SIP2SCStatusRequest(StatusCode.OK,"50");
        sip2SCStatusRequest.setErrorDetectionEnabled(true);
        String result = sip2SCStatusRequest.getData();
        assertNotNull(result);
    }
    @Test
    public void testSIP2RecallRequest3() {
        SIP2SCStatusRequest sip2SCStatusRequest = new SIP2SCStatusRequest();
        sip2SCStatusRequest.setStatusCode(StatusCode.OK);
        sip2SCStatusRequest.getStatusCode();
        String result = sip2SCStatusRequest.getData();
        assertNotNull(result);
    }
}
