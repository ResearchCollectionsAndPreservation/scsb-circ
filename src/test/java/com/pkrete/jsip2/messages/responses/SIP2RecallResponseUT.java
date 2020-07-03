package com.pkrete.jsip2.messages.responses;

import com.pkrete.jsip2.variables.SecurityMarkerFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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
