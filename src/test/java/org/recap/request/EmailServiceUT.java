package org.recap.request;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

/**
 * Created by sudhishk on 19/1/17.
 */
public class EmailServiceUT extends BaseTestCase {

    @Autowired
    EmailService emailService;

    @Test
    public void testRecalEmail() {
        emailService.sendEmail(RecapCommonConstants.NYPL, "NYPLTST67891", "A history of the Burmah Oil Company", "NoPatron", RecapCommonConstants.NYPL,"");
        emailService.sendEmail(RecapCommonConstants.COLUMBIA, "CULTST42345", "Changing contours of Asian agriculture", "RECAPTST01", RecapCommonConstants.COLUMBIA,"");
        emailService.sendEmail(RecapCommonConstants.PRINCETON, "PULTST54323", "1863 laws of war", "45678912", RecapCommonConstants.PRINCETON,"");
        emailService.sendEmail(RecapCommonConstants.PRINCETON, "PULTST54323", "Message", "45678912", RecapConstants.GFA,"");

        emailService.sendEmail("A history of the Burmah Oil Company","RECAPTST01",RecapCommonConstants.NYPL,"");
        emailService.sendEmail("NYPLTST67891",RecapCommonConstants.NYPL,"");
        emailService.sendEmail("NYPLTST67891",RecapCommonConstants.COLUMBIA,"");
        emailService.sendEmail("NYPLTST67891",RecapCommonConstants.PRINCETON,"");
        emailService.sendEmail("NYPLTST67891","GFA","");

        emailService.sendBulkRequestEmail("12","TestFirstBulkRequest","TestFirstBulkRequest","PROCESSED","Test","");
        assertTrue(true);
    }
}
