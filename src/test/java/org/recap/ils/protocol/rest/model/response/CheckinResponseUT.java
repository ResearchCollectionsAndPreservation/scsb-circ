package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.CheckinData;
import org.recap.ils.protocol.rest.model.DebugInfo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class CheckinResponseUT extends BaseTestCaseUT {

    @Test
    public void testCheckinResponse() {
        CheckinResponse checkinResponse = getCheckinResponse();
        assertNotNull(checkinResponse.getCount());
        assertNotNull(checkinResponse.getStatusCode());
        assertNotNull(checkinResponse.getDebugInfo());
        assertNotNull(checkinResponse.getData());
    }

    private CheckinResponse getCheckinResponse() {
        CheckinResponse checkinResponse = new CheckinResponse();
        checkinResponse.setCount(1);
        checkinResponse.setStatusCode(213);
        checkinResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        checkinResponse.setData(new CheckinData());
        return checkinResponse;
    }
}
