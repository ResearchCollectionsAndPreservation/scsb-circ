package org.recap.camel.accessionReconciliation;

import org.apache.camel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.camel.accessionreconciliation.AccessionReconciliationEmailService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by akulak on 25/5/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessionReconciliationEmailServiceUT {
    @InjectMocks
    AccessionReconciliationEmailService accessionReconciliationEmailService;
    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    Header dataheader;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    EmailPayLoad emailPayLoad;
    @Test
    public void testEmailIdTo() throws Exception{
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        String emailAddress = "test@mail.com";
        String ccEmailAddress = "testcc@mail.com";
        String institution = "PUL";
        AccessionReconciliationEmailService accessionReconciliationEmailService = new AccessionReconciliationEmailService(institution,producerTemplate);
        ReflectionTestUtils.setField(accessionReconciliationEmailService,"pulEmailTo",emailAddress);
        ReflectionTestUtils.setField(accessionReconciliationEmailService,"pulEmailCC",ccEmailAddress);
        accessionReconciliationEmailService.emailIdTo(institution, emailPayLoad);
        assertEquals(emailAddress,emailPayLoad.getTo());
        assertEquals(ccEmailAddress,emailPayLoad.getCc());
    }
    @Test
    public void processInput(){
        AccessionReconciliationEmailService accessionReconciliationEmailService1 = new AccessionReconciliationEmailService("NYPL",producerTemplate);
        message.setHeader("CamelFileNameProduced",dataheader);
//        Mockito.when(exchange.getIn()).thenReturn(message);
  //      Mockito.doNothing().when(producerTemplate).sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.EMAIL_BODY_FOR, "AccessionReconcilation");
//        accessionReconciliationEmailService1.processInput(exchange);
    }

}
