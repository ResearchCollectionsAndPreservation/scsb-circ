package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.DebugInfo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CancelHoldResponseUT extends BaseTestCaseUT {

    @Test
    public void testCancelHoldResponse() {
        CancelHoldResponse cancelHoldResponse = new CancelHoldResponse();
        cancelHoldResponse.setCount(1);
        cancelHoldResponse.setStatusCode(1);
        cancelHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        assertNotNull(cancelHoldResponse.getCount());
        assertNotNull(cancelHoldResponse.getStatusCode());
        assertNotNull(cancelHoldResponse.getDebugInfo());
    }
}
