package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.service.ActiveMqQueuesInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class EmailPendingRequestJobControllerUT {

    @Mock
    private ActiveMqQueuesInfo activemqQueuesInfo;

    @InjectMocks
    EmailPendingRequestJobController emailPendingRequestJobController;

    @Mock
    ProducerTemplate producerTemplate;
    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(emailPendingRequestJobController, "pendingRequestLimit", 10);
    }
    @Test
    public void sendEmailForPendingRequest() throws Exception{
        Mockito.when(activemqQueuesInfo.getActivemqQueuesInfo("lasOutgoingQ")).thenReturn(20);
        String result = emailPendingRequestJobController.sendEmailForPendingRequest();
        assertNotNull(result);
        assertEquals("Success",result);
    }
}
