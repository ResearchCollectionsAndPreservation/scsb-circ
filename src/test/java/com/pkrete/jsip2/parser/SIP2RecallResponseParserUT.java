package com.pkrete.jsip2.parser;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.SIP2MessageResponse;
import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;

public class SIP2RecallResponseParserUT extends BaseTestCase {
    @Test
    public void testParse() {
        SIP2MessageResponse SIP2MessageResponse = null;
        SIP2RecallResponseParser SIP2RecallResponseParser = new SIP2RecallResponseParser();
        try {
            SIP2MessageResponse = SIP2RecallResponseParser.parse("test");
        } catch (InvalidSIP2ResponseValueException e) {
            e.printStackTrace();
        } catch (InvalidSIP2ResponseException e) {
            e.printStackTrace();
        }
        assertNotNull(SIP2MessageResponse);
    }

}
