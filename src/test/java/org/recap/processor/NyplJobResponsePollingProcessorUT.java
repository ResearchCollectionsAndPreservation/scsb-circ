package org.recap.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.callable.NyplJobResponsePollingCallable;
import org.recap.ils.NyplApiServiceConnector;
import org.recap.ils.model.nypl.response.JobResponse;
import org.recap.ils.service.NyplApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Mock
    ExecutorService executor;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(nyplJobResponsePollingProcessor, "pollingMaxTimeOut", 30);
        ReflectionTestUtils.setField(nyplJobResponsePollingProcessor, "pollingTimeInterval", 1000);
    }

    @Test
    public void testPollNyplRequestItemJobResponse() throws Exception {
        JobResponse response = nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse("1");
        assertNotNull(response);
    }

}