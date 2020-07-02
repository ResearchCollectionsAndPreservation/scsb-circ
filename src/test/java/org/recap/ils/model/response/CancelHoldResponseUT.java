package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.response.CancelHoldResponse;

import static org.junit.Assert.assertTrue;

public class CancelHoldResponseUT extends BaseTestCase {
    CancelHoldResponse cancelHoldResponse;

    @Test
    public void testCancelHoldResponse(){
        cancelHoldResponse = new CancelHoldResponse();
        cancelHoldResponse.getCount();
        cancelHoldResponse.getStatusCode();
        cancelHoldResponse.getDebugInfo();
        assertTrue(true);
    }
}
