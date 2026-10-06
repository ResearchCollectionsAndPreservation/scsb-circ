package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.DebugInfo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestHoldResponseUT extends BaseTestCaseUT {

    @Test
    public void testRestHoldResponse() {
        RestHoldResponse restHoldResponse = new RestHoldResponse();
        restHoldResponse.setCount(1);
        restHoldResponse.setStatusCode(1);
        restHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        assertNotNull(restHoldResponse.getCount());
        assertNotNull(restHoldResponse.getStatusCode());
        assertNotNull(restHoldResponse.getDebugInfo());
    }
}
