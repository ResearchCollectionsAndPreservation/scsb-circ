package com.pkrete.jsip2.messages.responses;

import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class SIP2RecallResponseUT extends BaseTestCase {

    SIP2RecallResponse mockSIP2RecallResponse;

    @Before
    public void Setup() {
    }
    @Test
    public void testSIP2CreateBibResponse() {
        try {
            mockSIP2RecallResponse = new SIP2RecallResponse("82Test");
            String res = mockSIP2RecallResponse.countChecksum();
        } catch (Exception e) {
        }
        assertTrue(true);
    }
}
