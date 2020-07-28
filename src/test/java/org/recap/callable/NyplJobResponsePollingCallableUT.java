package org.recap.callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ils.NyplApiServiceConnector;
import org.recap.ils.model.nypl.JobData;
import org.recap.ils.model.nypl.response.JobResponse;

@RunWith(MockitoJUnitRunner.class)
public class NyplJobResponsePollingCallableUT {

    @InjectMocks
    NyplJobResponsePollingCallable nyplJobResponsePollingCallable;

    @Mock
    NyplApiServiceConnector nyplApiServiceConnector;

    @Test
    public void call() throws Exception {
        String jobId = "1";
        Integer pollingTimeInterval =10;
        JobResponse jobResponse = new JobResponse();
        JobData jobData = new JobData();
        jobData.setFinished(true);
        jobResponse.setData(jobData);
        nyplJobResponsePollingCallable = new NyplJobResponsePollingCallable(jobId,pollingTimeInterval,nyplApiServiceConnector);
        Mockito.when(nyplApiServiceConnector.queryForJob(jobId)).thenReturn(jobResponse);
        nyplJobResponsePollingCallable.call();
    }
}
