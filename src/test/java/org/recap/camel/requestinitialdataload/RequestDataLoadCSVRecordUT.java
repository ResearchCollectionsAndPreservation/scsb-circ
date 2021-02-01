package org.recap.camel.requestinitialdataload;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 13/7/17.
 */
public class RequestDataLoadCSVRecordUT extends BaseTestCaseUT {

    @Test
    public void testRequestDataLoadCSVRecord() {
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = new RequestDataLoadCSVRecord();
        requestDataLoadCSVRecord.setBarcode("332456456456745");
        requestDataLoadCSVRecord.setCustomerCode("PB");
        requestDataLoadCSVRecord.setDeliveryMethod("Test");
        requestDataLoadCSVRecord.setCreatedDate(new Date().toString());
        requestDataLoadCSVRecord.setLastUpdatedDate(new Date().toString());
        requestDataLoadCSVRecord.setPatronId("0000000");
        requestDataLoadCSVRecord.setStopCode("AD");
        requestDataLoadCSVRecord.setEmail("hemalatha.s@htcindia.com");
        RequestDataLoadErrorCSVRecord requestDataLoadErrorCSVRecord = new RequestDataLoadErrorCSVRecord();
        requestDataLoadErrorCSVRecord.setBarcodes("3324545545344575");
        assertNotNull(requestDataLoadCSVRecord.getBarcode());
        assertNotNull(requestDataLoadCSVRecord.getDeliveryMethod());
        assertNotNull(requestDataLoadCSVRecord.getCreatedDate());
        assertNotNull(requestDataLoadCSVRecord.getLastUpdatedDate());
        assertNotNull(requestDataLoadCSVRecord.getPatronId());
        assertNotNull(requestDataLoadCSVRecord.getStopCode());
        assertNotNull(requestDataLoadCSVRecord.getEmail());
        assertNotNull(requestDataLoadCSVRecord.getCustomerCode());
        assertNotNull(requestDataLoadErrorCSVRecord.getBarcodes());
    }

}