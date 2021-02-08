package org.recap.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;

public class ActiveMqQueuesInfoUT extends BaseTestCaseUT {
    @InjectMocks
    ActiveMqQueuesInfo activeMqQueuesInfo;

    String activeMqApiUrl = "test/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=";
    String searchAttribute = "/QueueSize";
    String serviceUrl = "http://test:8161";
    String activemqCredentials = "admin:admin";

    @Before
    public void setup() {
        ReflectionTestUtils.setField(activeMqQueuesInfo, "activeMqApiUrl", activeMqApiUrl);
        ReflectionTestUtils.setField(activeMqQueuesInfo, "searchAttribute", searchAttribute);
        ReflectionTestUtils.setField(activeMqQueuesInfo, "serviceUrl", serviceUrl);
        ReflectionTestUtils.setField(activeMqQueuesInfo, "activemqCredentials", activemqCredentials);
    }

    @Test
    public void testActiveMqQueuesInfo() {
        Integer value = activeMqQueuesInfo.getActivemqQueuesInfo("recap");
        String res = activeMqQueuesInfo.getEncodedActivemqCredentials();
        assertNotNull(res);
    }
}
