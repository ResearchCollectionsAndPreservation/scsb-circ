package com.pkrete.jsip2.messages.requests;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class SIP2RecallRequestUT extends BaseTestCase {

    SIP2RecallRequest mockSIP2RecallRequest;

    @Test
    public void testSIP2RecallRequest() {
        mockSIP2RecallRequest = new SIP2RecallRequest("test", "test");
        SIP2RecallRequest SIP2RecallRequestnew = new SIP2RecallRequest("test", "test", "Test", "1234");
        mockSIP2RecallRequest.getData();
        assertTrue(true);
    }
}

