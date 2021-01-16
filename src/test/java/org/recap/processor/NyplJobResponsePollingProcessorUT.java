package org.recap.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ils.NyplApiServiceConnector;
import org.recap.ils.model.nypl.JobData;
import org.recap.ils.model.nypl.response.JobResponse;
import org.recap.ils.service.NyplApiResponseUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by hemalathas on 21/2/17.
 */
public class NyplJobResponsePollingProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    NyplJobResponsePollingProcessor nyplJobResponsePollingProcessor;

    @Mock
    NyplApiResponseUtil nyplApiResponseUtil;

    @Mock
    NyplApiServiceConnector nyplApiServiceConnector;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(nyplJobResponsePollingProcessor, "pollingMaxTimeOut", 30);
        ReflectionTestUtils.setField(nyplJobResponsePollingProcessor, "pollingTimeInterval", 1000);
    }

    @Test
    public void testPollNyplRequestItemJobResponse() throws Exception {
        JobResponse jobResponse = getJobResponse();
        Mockito.when(nyplApiServiceConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.when(nyplApiResponseUtil.getJobStatusMessage(any())).thenReturn("Success");
        JobResponse response = nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse("1");
        assertNotNull(response);
    }
    @Test
    public void testPollNyplRequestItemJobResponseException() throws Exception {
        JobResponse jobResponse = getJobResponse();
        Mockito.when(nyplApiServiceConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.doThrow(new NullPointerException()).when(nyplApiResponseUtil).getJobStatusMessage(any());
        JobResponse response = nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse("1");
        assertNotNull(response);
    }
    @Test
    public void testPollNyplRequestItemJobResponseInterruptedException() throws Exception {
        JobResponse jobResponse = getJobResponse();
        Mockito.when(nyplApiServiceConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.doThrow(new InterruptedException()).when(nyplApiResponseUtil).getJobStatusMessage(any());
        JobResponse response = nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse("1");
        assertNotNull(response);
    }
    @Test
    public void testPollNyplRequestItemJobResponseTimeoutException() throws Exception {
        JobResponse jobResponse = getJobResponse();
        Mockito.when(nyplApiServiceConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.doThrow(new TimeoutException()).when(nyplApiResponseUtil).getJobStatusMessage(any());
        JobResponse response = nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse("1");
        assertNotNull(response);
    }
    @Test
    public void testPollNyplRequestItemJobResponseExecutionException() throws Exception {
        JobResponse response = nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse("1");
        assertNotNull(response);
    }

    private JobResponse getJobResponse() {
        JobResponse jobResponse = new JobResponse();
        JobData jobData = new JobData();
        jobData.setFinished(true);
        jobResponse.setData(jobData);
        return jobResponse;
    }

}