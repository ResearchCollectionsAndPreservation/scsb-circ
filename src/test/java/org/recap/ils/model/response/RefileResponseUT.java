package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.response.RefileResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class RefileResponseUT extends BaseTestCaseUT {

    @Test
    public void tesRecallResponse() {
        RefileResponse refileResponse = new RefileResponse();
        List<DebugInfo> debuginfolist = new ArrayList<>();
        DebugInfo debugInfo = new DebugInfo();
        debuginfolist.add(debugInfo);
        refileResponse.setCount(1);
        refileResponse.setDebugInfo(debuginfolist);
        refileResponse.setStatusCode(1);

        assertNotNull(refileResponse.getCount());
        assertNotNull(refileResponse.getStatusCode());
        assertNotNull(refileResponse.getDebugInfo());
    }
}
