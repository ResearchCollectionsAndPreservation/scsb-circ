package org.recap.request.service;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.ScsbCommonConstants;
import org.recap.request.service.EmailService;
import org.recap.util.PropertyUtil;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

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
        emailService.sendEmail(ScsbCommonConstants.NYPL, "NYPLTST67891", "RECAP", "A history of the Burmah Oil Company", "NoPatron", ScsbCommonConstants.NYPL,"");
        emailService.sendEmail(ScsbCommonConstants.COLUMBIA, "CULTST42345", "RECAP", "Changing contours of Asian agriculture", "RECAPTST01", ScsbCommonConstants.COLUMBIA,"");
        emailService.sendEmail(ScsbCommonConstants.PRINCETON, "PULTST54323", "RECAP","1863 laws of war", "45678912", ScsbCommonConstants.PRINCETON,"");
        emailService.sendEmail(ScsbCommonConstants.PRINCETON, "PULTST54323", "RECAP","Message", "45678912", ScsbConstants.GFA,"");
        emailService.sendEmail("", "PULTST54323", "RECAP","Message", "45678912", ScsbConstants.DELETED_MAIL_TO,"");
        emailService.sendEmail("", "PULTST54323", "RECAP","Message", "45678912", "","");
        emailService.sendBulkRequestEmail("12","TestFirstBulkRequest","TestFirstBulkRequest","PROCESSED","Test","");
    }

    @Test
    public void sendLASExceptionEmail(){
        String customerCode = "PA";
        String itemBarcode = "243533";
        String messageDisplay = "success";
        String patronBarcode = "3456784";
        String toInstitution = "PUL";
        String subject = "test";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn("test@gmail.com");
        emailService.sendLASExceptionEmail(customerCode,itemBarcode,messageDisplay,patronBarcode,toInstitution,subject);

    }
}
