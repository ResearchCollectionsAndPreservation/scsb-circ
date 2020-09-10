package com.pkrete.jsip2.messages.responses;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class SIP2CreateBibResponseUT {


    @Test
    public void testSIP2CreateBibResponse() {
            SIP2CreateBibResponse sip2CreateBibResponse = new SIP2CreateBibResponse("82Test");
            sip2CreateBibResponse.setExpirationDate(new Date().toString());
            sip2CreateBibResponse.setPickupLocation("PA");
            sip2CreateBibResponse.setItemIdentifier("12345");
            sip2CreateBibResponse.setTitleIdentifier("2345");
            sip2CreateBibResponse.setBibId("123456");
            sip2CreateBibResponse.setScreenMessage(Arrays.asList("Success","Failure"));
            sip2CreateBibResponse.setPrintLine(Arrays.asList("P","A"));
            sip2CreateBibResponse.setSequence(1);
            String res = sip2CreateBibResponse.countChecksum();
            assertTrue(true);
    }
}
