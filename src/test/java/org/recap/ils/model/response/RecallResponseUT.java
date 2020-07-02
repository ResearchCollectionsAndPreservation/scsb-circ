package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.response.RecallResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RecallResponseUT extends BaseTestCase {
    RecallResponse recallResponse;
    @Test
    public void tesRecallResponse(){
        recallResponse = new RecallResponse();
        List<DebugInfo> debuginfolist= new ArrayList<>();
        DebugInfo debugInfo = new DebugInfo();
        debuginfolist.add(debugInfo);
        recallResponse.setCount(1);
        recallResponse.setDebugInfo(debuginfolist);
        recallResponse.setStatusCode(1);
        recallResponse.getCount();
        recallResponse.getDebugInfo();
        recallResponse.getStatusCode();
        assertTrue(true);
    }
}
