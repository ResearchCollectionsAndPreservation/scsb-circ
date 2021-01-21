package org.recap.callable;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ils.RestProtocolConnector;
import org.recap.ils.model.nypl.JobData;
import org.recap.ils.model.nypl.response.JobResponse;

import static org.junit.Assert.assertNotNull;

public class RestJobResponsePollingCallableUT extends BaseTestCaseUT {

    @InjectMocks
    RestJobResponsePollingCallable restJobResponsePollingCallable;
    @Mock
    RestProtocolConnector restProtocolConnector;
    @Test
    public void call() throws Exception {
        Integer pollingTimeInterval = 10000;
        String jobId = "1";
        JobResponse jobResponse = new JobResponse();
        JobData jobData = new JobData();
        jobData.setFinished(true);
        jobResponse.setData(jobData);
        RestJobResponsePollingCallable restJobResponsePollingCallable = new RestJobResponsePollingCallable(jobId,pollingTimeInterval,restProtocolConnector);
        Mockito.when(restProtocolConnector.queryForJob(jobId)).thenReturn(jobResponse);
        JobResponse response = restJobResponsePollingCallable.call();
        assertNotNull(response);
    }
}
