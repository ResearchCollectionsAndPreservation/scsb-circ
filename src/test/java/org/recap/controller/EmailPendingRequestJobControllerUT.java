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
import org.recap.RecapConstants;
import org.recap.service.ActiveMqQueuesInfo;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class EmailPendingRequestJobControllerUT {

    @Mock
    private ActiveMqQueuesInfo activemqQueuesInfo;

    @InjectMocks
    EmailPendingRequestJobController emailPendingRequestJobController;

    @Mock
    CommonUtil commonUtil;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    private PropertyUtil propertyUtil;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(emailPendingRequestJobController, "pendingRequestLimit", 10);
    }
    @Test
    public void sendEmailForPendingRequest() throws Exception{
        Mockito.when(commonUtil.findAllImsLocationCodeExceptUN()).thenReturn(Arrays.asList("HD"));
        Mockito.when(activemqQueuesInfo.getActivemqQueuesInfo("las" + "HD" + RecapConstants.OUTGOING_QUEUE_SUFFIX)).thenReturn(20);
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey("HD", "las.email.assist.to")).thenReturn("test@gmail.com");
        String result = emailPendingRequestJobController.sendEmailForPendingRequest();
        assertNotNull(result);
        assertEquals("Success",result);
    }
}
