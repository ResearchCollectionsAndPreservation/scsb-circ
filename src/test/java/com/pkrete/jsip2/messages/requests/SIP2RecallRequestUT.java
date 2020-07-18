package com.pkrete.jsip2.messages.requests;

import com.pkrete.jsip2.variables.HoldMode;
import org.apache.camel.spi.AsEndpointUri;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class SIP2RecallRequestUT extends BaseTestCase {



    @Test
    public void testSIP2RecallRequest() {
        SIP2RecallRequest SIP2RecallRequestnew = new SIP2RecallRequest("test", "test", "Test", "1234");
        String result = SIP2RecallRequestnew.getData();
        assertNotNull(result);
    }
}

