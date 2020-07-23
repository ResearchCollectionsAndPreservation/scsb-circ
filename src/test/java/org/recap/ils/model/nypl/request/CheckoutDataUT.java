package org.recap.ils.model.nypl.request;

import org.junit.Test;
import org.recap.ils.model.nypl.CheckoutData;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class CheckoutDataUT {

    @Test
    public  void getCheckoutData(){
        CheckoutData checkoutData = new CheckoutData();
        checkoutData.setCreatedDate(new Date().toString());
        checkoutData.setId(1);
        checkoutData.setJobId("1");
        checkoutData.setItemBarcode("123456");
        checkoutData.setDesiredDateDue(new Date().toString());
        checkoutData.setProcessed(true);
        checkoutData.setSuccess(true);
        checkoutData.setUpdatedDate(new Date());
        checkoutData.setPatronBarcode("123456");

        assertNotNull(checkoutData.getCreatedDate());
        assertNotNull(checkoutData.getDesiredDateDue());
        assertNotNull(checkoutData.getId());
        assertNotNull(checkoutData.getItemBarcode());
        assertNotNull(checkoutData.getPatronBarcode());
        assertNotNull(checkoutData.getJobId());
        assertNotNull(checkoutData.getProcessed());
        assertNotNull(checkoutData.getSuccess());
        assertNotNull(checkoutData.getUpdatedDate());
    }
}
