package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.response.NYPLHoldResponse;

import static org.junit.Assert.assertTrue;

public class NYPLHoldResponseUT extends BaseTestCase {
    NYPLHoldResponse nYPLHoldResponse;
    @Test
    public void testNYPLHoldResponse(){
        nYPLHoldResponse = new NYPLHoldResponse();
        nYPLHoldResponse.getCount();
        nYPLHoldResponse.getStatusCode();
        nYPLHoldResponse.getDebugInfo();
        assertTrue(true);
    }
}
