package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.response.RecallResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class RecallResponseUT extends BaseTestCaseUT {

    @Test
    public void tesRecallResponse() {
        RecallResponse recallResponse = new RecallResponse();
        List<DebugInfo> debuginfolist = new ArrayList<>();
        DebugInfo debugInfo = new DebugInfo();
        debuginfolist.add(debugInfo);
        recallResponse.setCount(1);
        recallResponse.setDebugInfo(debuginfolist);
        recallResponse.setStatusCode(1);
        assertNotNull(recallResponse.getCount());
        assertNotNull(recallResponse.getStatusCode());
        assertNotNull(recallResponse.getDebugInfo());
    }
}
