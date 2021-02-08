package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.response.NYPLHoldResponse;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class NYPLHoldResponseUT extends BaseTestCaseUT {

    @Test
    public void testNYPLHoldResponse() {
        NYPLHoldResponse nyplHoldResponse = new NYPLHoldResponse();
        nyplHoldResponse.setCount(1);
        nyplHoldResponse.setStatusCode(1);
        nyplHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        assertNotNull(nyplHoldResponse.getCount());
        assertNotNull(nyplHoldResponse.getStatusCode());
        assertNotNull(nyplHoldResponse.getDebugInfo());
    }
}
