package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.response.CheckinResponse;
import org.recap.ils.model.nypl.response.NYPLHoldResponse;

import static org.junit.Assert.assertTrue;

public class CheckinResponseUT extends BaseTestCase{
    CheckinResponse checkinResponse;
    @Test
    public void testCheckinResponse(){
        checkinResponse = new CheckinResponse();
        checkinResponse.getCount();
        checkinResponse.getStatusCode();
        checkinResponse.getDebugInfo();
        assertTrue(true);
    }
}
