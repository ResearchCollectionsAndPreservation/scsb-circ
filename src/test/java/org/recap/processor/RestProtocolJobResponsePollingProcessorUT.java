package org.recap.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ils.RestProtocolConnector;
import org.recap.ils.model.nypl.JobData;
import org.recap.ils.model.nypl.response.JobResponse;
import org.recap.ils.service.RestApiResponseUtil;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class RestProtocolJobResponsePollingProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    RestProtocolJobResponsePollingProcessor restProtocolJobResponsePollingProcessor;

    @Mock
    RestProtocolConnector restProtocolConnector;

    @Mock
    RestApiResponseUtil restApiResponseUtil;

    @Mock
    PropertyUtil propertyUtil;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(restProtocolJobResponsePollingProcessor, "pollingMaxTimeOut", 30);
        ReflectionTestUtils.setField(restProtocolJobResponsePollingProcessor, "pollingTimeInterval", 1000);
    }

    @Test
    public void pollRestApiRequestItemJobResponse() throws Exception {
        String jobId = "1";
        String institution ="NYPL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.max.timeout")).thenReturn("30");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.time.interval")).thenReturn("1000");
        JobResponse jobResponse = getJobResponse();
        Mockito.when(restProtocolConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.when(restApiResponseUtil.getJobStatusMessage(any())).thenReturn("Success");
        JobResponse response = restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(jobId,institution);
        assertNotNull(response);
    }
    @Test
    public void pollRestApiRequestItemJobResponseNullPointerException() throws Exception {
        String jobId = "1";
        String institution ="NYPL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.max.timeout")).thenReturn("30");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.time.interval")).thenReturn("1000");
        JobResponse jobResponse = getJobResponse();
        Mockito.when(restProtocolConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.doThrow(new NullPointerException()).when(restApiResponseUtil).getJobStatusMessage(any());
        JobResponse response = restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(jobId,institution);
        assertNotNull(response);
    }
    @Test
    public void pollRestApiRequestItemJobResponseTimeoutException() throws Exception {
        String jobId = "1";
        String institution ="NYPL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.max.timeout")).thenReturn("30");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.time.interval")).thenReturn("1000");
        JobResponse jobResponse = getJobResponse();
        Mockito.when(restProtocolConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.doThrow(new TimeoutException()).when(restApiResponseUtil).getJobStatusMessage(any());
        JobResponse response = restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(jobId,institution);
        assertNotNull(response);
    }
    @Test
    public void pollRestApiRequestItemJobResponseInterruptedException() throws Exception {
        String jobId = "1";
        String institution ="NYPL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.max.timeout")).thenReturn("30");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.time.interval")).thenReturn("1000");
        JobResponse jobResponse = getJobResponse();
        Mockito.when(restProtocolConnector.queryForJob(any())).thenReturn(jobResponse);
        Mockito.doThrow(new InterruptedException()).when(restApiResponseUtil).getJobStatusMessage(any());
        JobResponse response = restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(jobId,institution);
        assertNotNull(response);
    }
    @Test
    public void pollRestApiRequestItemJobResponseExecutionException() throws Exception {
        String jobId = "1";
        String institution ="NYPL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.max.timeout")).thenReturn("30");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.time.interval")).thenReturn("1000");
        JobResponse response = restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(jobId,institution);
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
