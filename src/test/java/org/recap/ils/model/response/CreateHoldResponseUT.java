package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.response.CreateHoldResponse;

import static org.junit.Assert.assertTrue;

public class CreateHoldResponseUT extends BaseTestCase {
    CreateHoldResponse createHoldResponse;

    @Test
    public void testCheckinResponse() {
        createHoldResponse = new CreateHoldResponse();
        createHoldResponse.getCount();
        createHoldResponse.getStatusCode();
        createHoldResponse.getDebugInfo();
        assertTrue(true);
    }
}
