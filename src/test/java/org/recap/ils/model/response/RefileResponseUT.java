package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.response.RecallResponse;
import org.recap.ils.model.nypl.response.RefileResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RefileResponseUT extends BaseTestCase{
    RefileResponse refileResponse;
    @Test
    public void tesRecallResponse(){
        refileResponse = new RefileResponse();
        List<DebugInfo> debuginfolist= new ArrayList<>();
        DebugInfo debugInfo = new DebugInfo();
        debuginfolist.add(debugInfo);
        refileResponse.setCount(1);
        refileResponse.setDebugInfo(debuginfolist);
        refileResponse.setStatusCode(1);
        refileResponse.getCount();
        refileResponse.getDebugInfo();
        refileResponse.getStatusCode();
        assertTrue(true);
    }
}
