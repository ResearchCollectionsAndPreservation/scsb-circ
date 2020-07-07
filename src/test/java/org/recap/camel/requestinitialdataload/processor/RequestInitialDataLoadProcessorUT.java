package org.recap.camel.requestinitialdataload.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.camel.requestinitialdataload.RequestDataLoadCSVRecord;

import java.util.*;

import static org.junit.Assert.assertTrue;

public class RequestInitialDataLoadProcessorUT extends BaseTestCase {

    RequestInitialDataLoadProcessor requestInitialDataLoadProcessor;

    @Test
    public void testBefore() {
        requestInitialDataLoadProcessor = new RequestInitialDataLoadProcessor("CUL");
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = new RequestDataLoadCSVRecord();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord1 = new RequestDataLoadCSVRecord();
        requestDataLoadCSVRecord.setBarcode("332456456456745");
        requestDataLoadCSVRecord.setCustomerCode("PB");
        requestDataLoadCSVRecord.setDeliveryMethod("Test");
        requestDataLoadCSVRecord.setCreatedDate(new Date().toString());
        requestDataLoadCSVRecord.setLastUpdatedDate(new Date().toString());
        requestDataLoadCSVRecord.setPatronId("0000000");
        requestDataLoadCSVRecord.setStopCode("AD");
        requestDataLoadCSVRecord.setEmail("hemalatha.s@htcindia.com");
        requestDataLoadCSVRecord1.setBarcode("332456456456745");
        requestDataLoadCSVRecord1.setCustomerCode("PB");
        requestDataLoadCSVRecord1.setDeliveryMethod("Test");
        requestDataLoadCSVRecord1.setCreatedDate(new Date().toString());
        requestDataLoadCSVRecord1.setLastUpdatedDate(new Date().toString());
        requestDataLoadCSVRecord1.setPatronId("0000000");
        requestDataLoadCSVRecord1.setStopCode("AD");
        requestDataLoadCSVRecord1.setEmail("hemalatha.s@htcindia.com");
        List<RequestDataLoadCSVRecord> requestDataLoadCSVRecordList = new ArrayList<>();
        requestDataLoadCSVRecordList.add(requestDataLoadCSVRecord);
        requestDataLoadCSVRecordList.add(requestDataLoadCSVRecord1);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("directoryName", "CUL");
        ex.getIn().setBody(requestDataLoadCSVRecordList);
        ex.setProperty("CamelSplitComplete",true);
        try {
            Set<String> data = new HashSet<>();
            data.add("332456456456745");
            data.add("332456456456742");
            requestInitialDataLoadProcessor.setBarcodeSet(data);
            requestInitialDataLoadProcessor.getBarcodeSet();
            requestInitialDataLoadProcessor.processInput(ex);
            ex.getIn().setHeader("directoryName", "PUL");
            requestInitialDataLoadProcessor.processInput(ex);
            ex.getIn().setHeader("directoryName", "NYPLL");
            requestInitialDataLoadProcessor.processInput(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
}
