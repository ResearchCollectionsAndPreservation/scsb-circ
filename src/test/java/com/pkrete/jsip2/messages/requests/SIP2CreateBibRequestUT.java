package com.pkrete.jsip2.messages.requests;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;

public class SIP2CreateBibRequestUT {

    @Test
    public void testgetData() {
        SIP2CreateBibRequest sip2CreateBibRequest = new SIP2CreateBibRequest("test", "test", "test", "1234");
        String result = sip2CreateBibRequest.getData();
        assertNotNull(result);
    }
    @Test
    public void testgetData2() {
        SIP2CreateBibRequest sip2CreateBibRequest = new SIP2CreateBibRequest("test", "test", "test");
        sip2CreateBibRequest.setErrorDetectionEnabled(true);
        String result = sip2CreateBibRequest.getData();
        assertNotNull(result);
    }
}
