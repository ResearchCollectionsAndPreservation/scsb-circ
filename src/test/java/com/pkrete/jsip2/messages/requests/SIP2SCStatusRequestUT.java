package com.pkrete.jsip2.messages.requests;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SIP2SCStatusRequestUT extends BaseTestCase {

    @Test
    public void testSIP2RecallRequest() {
        SIP2SCStatusRequest sip2SCStatusRequest = new SIP2SCStatusRequest();
        String result = sip2SCStatusRequest.getData();
        assertNotNull(result);
    }
}
