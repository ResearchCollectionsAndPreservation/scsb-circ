package org.recap.controller;

import org.apache.camel.ProducerTemplate;
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

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class EmailPendingRequestJobControllerUT {

    @Mock
    private ActiveMqQueuesInfo activemqQueuesInfo;


    private static String queueName = "lasOutgoingQ";
    @InjectMocks
    EmailPendingRequestJobController emailPendingRequestJobController;

    @Mock
    ProducerTemplate producerTemplate;

    @Test
    public void sendEmailForPendingRequest() throws Exception{
       // Mockito.when(activemqQueuesInfo.getActivemqQueuesInfo(queueName)).thenReturn(1);
//        Mockito.when(emailPendingRequestJobController.sendEmailForPendingRequest()).thenCallRealMethod();
    //    String result = emailPendingRequestJobController.sendEmailForPendingRequest();
       // assertNotNull(result);
    }
}
