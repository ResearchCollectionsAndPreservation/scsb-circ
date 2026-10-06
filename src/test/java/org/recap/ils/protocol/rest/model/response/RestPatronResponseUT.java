package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.DebugInfo;
import org.recap.ils.protocol.rest.model.RestPatronData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestPatronResponseUT extends BaseTestCaseUT {
    @Test
    public void testRestPatronResponse() {
        RestPatronResponse restPatronResponse = new RestPatronResponse();
        DebugInfo debugInfo = new DebugInfo();
        List<DebugInfo> debugInfolist = new ArrayList<>();
        debugInfolist.add(debugInfo);
        restPatronResponse.setCount(1);
        restPatronResponse.setStatusCode(200);
        RestPatronData restPatronData = new RestPatronData();
        List<RestPatronData> restPatronDatalist = new ArrayList<>();
        restPatronDatalist.add(restPatronData);
        restPatronResponse.setDebugInfo(debugInfolist);
        restPatronResponse.setData(restPatronDatalist);

        assertNotNull(restPatronResponse.getCount());
        assertNotNull(restPatronResponse.getStatusCode());
        assertNotNull(restPatronResponse.getDebugInfo());
        assertNotNull(restPatronResponse.getData());
    }
}
