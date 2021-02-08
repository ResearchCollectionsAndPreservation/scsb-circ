package com.pkrete.jsip2.parser;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.responses.SIP2ItemInformationResponse;
import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class SIP2ItemInformationResponseParserUT extends BaseTestCaseUT {
    @Test
    public void testParse() {
        SIP2ItemInformationResponse SIP2ItemInformationResponse = null;
        SIP2ItemInformationResponseParser sip2ItemInformationResponseParser = new SIP2ItemInformationResponseParser();
        try {
            SIP2ItemInformationResponse = sip2ItemInformationResponseParser.parse("011010101" + new Date().toString() + "CFAHCJCMABAJBGBH");
        } catch (InvalidSIP2ResponseValueException e) {
            e.printStackTrace();
        } catch (InvalidSIP2ResponseException e) {
            e.printStackTrace();
        }
        assertNotNull(SIP2ItemInformationResponse);
    }
}
