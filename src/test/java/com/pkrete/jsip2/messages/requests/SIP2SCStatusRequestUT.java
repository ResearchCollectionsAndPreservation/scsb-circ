package com.pkrete.jsip2.messages.requests;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNull;

public class SIP2SCStatusRequestUT extends BaseTestCase {
    @Mock
    SIP2SCStatusRequest mockSIP2SCStatusRequest;

    @Test
    public void testSIP2RecallRequest() {
        Mockito.when(mockSIP2SCStatusRequest.getData()).thenReturn(null);
        assertNull(null);
    }
}
