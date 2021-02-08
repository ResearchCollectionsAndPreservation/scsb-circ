package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.response.CreateHoldResponse;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class CreateHoldResponseUT extends BaseTestCaseUT {

    @Test
    public void testCheckinResponse() {
        CreateHoldResponse createHoldResponse = new CreateHoldResponse();
        createHoldResponse.setCount(1);
        createHoldResponse.setStatusCode(1);
        createHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        createHoldResponse.getCount();
        createHoldResponse.getStatusCode();
        createHoldResponse.getDebugInfo();
        assertNotNull(createHoldResponse.getCount());
        assertNotNull(createHoldResponse.getStatusCode());
        assertNotNull(createHoldResponse.getDebugInfo());
    }
}
