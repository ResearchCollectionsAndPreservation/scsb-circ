package org.recap.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

public class AWSClientConfigUT extends BaseTestCaseUT {

    private static String testKey = "jsknljcsl";
    private static String testSecretKey = "soiadkl:uc";

    @InjectMocks
    AWSClientConfig awsClientConfig;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(awsClientConfig, "awsAccessKey", testKey);
        ReflectionTestUtils.setField(awsClientConfig, "awsAccessSecretKey", testSecretKey);
    }

    @Test
    public void getAwsClient() {
        awsClientConfig.getAwsClient();
    }
}
