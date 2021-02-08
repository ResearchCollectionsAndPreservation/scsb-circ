package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.response.CancelHoldResponse;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

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
