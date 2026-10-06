package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.DebugInfo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
