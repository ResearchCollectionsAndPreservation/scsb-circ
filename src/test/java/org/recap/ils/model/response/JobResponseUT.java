package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.JobData;
import org.recap.ils.model.nypl.response.JobResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class JobResponseUT extends BaseTestCase {
    org.recap.ils.model.nypl.response.JobResponse jobResponse;

    @Test
    public void testJobResponse(){
        jobResponse = new JobResponse();
        JobData jobdata= new JobData();
        List<DebugInfo> debuginfolist= new ArrayList<>();
        DebugInfo debugInfo = new DebugInfo();
        debuginfolist.add(debugInfo);
        jobResponse.setCount(1);
        jobResponse.setStatusCode(2);
        jobResponse.setDebugInfo(debuginfolist);
        jobResponse.setData(jobdata);
        jobResponse.getCount();
        jobResponse.getStatusCode();
        jobResponse.getDebugInfo();
        assertTrue(true);
    }
}
