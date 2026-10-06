package org.recap.ils.protocol.rest.callable;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ils.connector.RestProtocolConnector;
import org.recap.ils.protocol.rest.model.JobData;
import org.recap.ils.protocol.rest.model.response.JobResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        RestJobResponsePollingCallable restJobResponsePollingCallable = new RestJobResponsePollingCallable(jobId, pollingTimeInterval, restProtocolConnector);
        Mockito.when(restProtocolConnector.queryForJob(jobId)).thenReturn(jobResponse);
        JobResponse response = restJobResponsePollingCallable.call();
        assertNotNull(response);
    }
}
