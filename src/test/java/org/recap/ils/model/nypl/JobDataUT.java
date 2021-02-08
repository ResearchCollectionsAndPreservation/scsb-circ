package org.recap.ils.model.nypl;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 3/4/17.
 */
public class JobDataUT extends BaseTestCaseUT {

    @Test
    public void testJobData() {
        JobData jobData = new JobData();
        jobData.setId("1");
        jobData.setStarted(true);
        jobData.setFinished(true);
        jobData.setSuccess(true);
        jobData.setNotices(Arrays.asList(new Notice()));
        jobData.setSuccessRedirectUrl("/test");
        jobData.setStartCallbackUrl("/test");
        jobData.setSuccessCallbackUrl("/test");
        jobData.setFailureCallbackUrl("/test");
        jobData.setUpdateCallbackUrl("/test");

        assertNotNull(jobData.getId());
        assertNotNull(jobData.getStarted());
        assertNotNull(jobData.getFinished());
        assertNotNull(jobData.getSuccess());
        assertNotNull(jobData.getNotices());
        assertNotNull(jobData.getSuccessRedirectUrl());
        assertNotNull(jobData.getStartCallbackUrl());
        assertNotNull(jobData.getSuccessCallbackUrl());
        assertNotNull(jobData.getFailureCallbackUrl());
        assertNotNull(jobData.getUpdateCallbackUrl());


    }

}