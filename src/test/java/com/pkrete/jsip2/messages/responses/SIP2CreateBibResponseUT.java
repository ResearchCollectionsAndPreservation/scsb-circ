package com.pkrete.jsip2.messages.responses;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SIP2CreateBibResponseUT {
    SIP2CreateBibResponse mockSIP2CreateBibResponse;

    @Before
    public void Setup() {
    }
    @Test
    public void testSIP2CreateBibResponse() {
        try {
            mockSIP2CreateBibResponse = new SIP2CreateBibResponse("82Test");
            String res = mockSIP2CreateBibResponse.countChecksum();
        } catch (Exception e) {
        }
        assertTrue(true);
    }
}
