package org.recap.request;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.util.PropertyUtil;

import static org.junit.Assert.assertTrue;

/**
 * Created by sudhishk on 19/1/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailServiceUT {

    @InjectMocks
    EmailService emailService;

    @Mock
    private ProducerTemplate producer;

    @Mock
    private PropertyUtil propertyUtil;

    @Test
    public void testRecalEmail() {
        emailService.sendEmail(RecapCommonConstants.NYPL, "NYPLTST67891", "RECAP", "A history of the Burmah Oil Company", "NoPatron", RecapCommonConstants.NYPL,"");
        emailService.sendEmail(RecapCommonConstants.COLUMBIA, "CULTST42345", "RECAP", "Changing contours of Asian agriculture", "RECAPTST01", RecapCommonConstants.COLUMBIA,"");
        emailService.sendEmail(RecapCommonConstants.PRINCETON, "PULTST54323", "RECAP","1863 laws of war", "45678912", RecapCommonConstants.PRINCETON,"");
        emailService.sendEmail(RecapCommonConstants.PRINCETON, "PULTST54323", "RECAP","Message", "45678912", RecapConstants.GFA,"");
        emailService.sendEmail("", "PULTST54323", "RECAP","Message", "45678912", RecapConstants.DELETED_MAIL_TO,"");
        emailService.sendEmail("", "PULTST54323", "RECAP","Message", "45678912", "","");
        emailService.sendBulkRequestEmail("12","TestFirstBulkRequest","TestFirstBulkRequest","PROCESSED","Test","");
    }
}
