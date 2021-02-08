package com.pkrete.jsip2.parser;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.SIP2MessageResponse;
import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class SIP2CreateBibResponseParserUT extends BaseTestCaseUT {
    @Test
    public void testCreatebibParser() {
        SIP2MessageResponse sIP2MessageResponse = null;
        String createBibEsipResponse = "821MJ8967832|MA12040035|AY|";
        SIP2CreateBibResponseParser sip2CreateBibResponseParser = new SIP2CreateBibResponseParser();
        try {
            sIP2MessageResponse = sip2CreateBibResponseParser.parse(createBibEsipResponse);
        } catch (InvalidSIP2ResponseValueException e) {
            e.printStackTrace();
        } catch (InvalidSIP2ResponseException e) {
            e.printStackTrace();
        }
        assertNotNull(sIP2MessageResponse);
    }

  /*  @Test
    public void testCreatebibParserException() {
        String createBibEsipResponse = "011";
        SIP2MessageResponse sIP2MessageResponse = null;
        SIP2CreateBibResponseParser sip2CreateBibResponseParser = new SIP2CreateBibResponseParser();
        try {
            sIP2MessageResponse = sip2CreateBibResponseParser.parse(createBibEsipResponse);
        } catch (InvalidSIP2ResponseValueException e) {
            e.printStackTrace();
        } catch (InvalidSIP2ResponseException e) {
            e.printStackTrace();
        }
        assertNotNull(sIP2MessageResponse);
    }*/

}
