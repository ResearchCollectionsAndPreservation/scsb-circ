package com.pkrete.jsip2.messages.requests;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNull;

public class SIP2CreateBibRequestUT extends BaseTestCase {
    @Mock
    SIP2CreateBibRequest mockSIP2CreateBibRequest;

    @Test
    public void testgetData() {
        SIP2CreateBibRequest sip2CreateBibRequest = new SIP2CreateBibRequest("test", "test", "test");
        SIP2CreateBibRequest sip2CreateBibRequestnew = new SIP2CreateBibRequest("test", "test", "test", "1234");
        Mockito.when(mockSIP2CreateBibRequest.getData()).thenReturn(null);
        assertNull(null);
    }
}
