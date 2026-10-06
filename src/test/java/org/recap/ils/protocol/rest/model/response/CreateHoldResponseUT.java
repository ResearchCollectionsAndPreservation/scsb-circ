package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.DebugInfo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
