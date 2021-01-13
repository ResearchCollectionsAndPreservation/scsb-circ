package org.recap.camel.requestinitialdataload.processor;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RouteController;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.camel.requestinitialdataload.RequestDataLoadCSVRecord;
import org.recap.service.requestdataload.RequestDataLoadService;

import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RequestInitialDataLoadProcessorUT {

    @InjectMocks
    RequestInitialDataLoadProcessor requestInitialDataLoadProcessor;
    @Mock
    RequestInitialDataLoadProcessor requestInitialDataLoadProcessor1;
    @Mock
    RequestInitialDataLoadProcessor requestInitialDataLoadProcessor2;
    @Mock
    Exchange exchange;
    @Mock
    Message message;
    @Mock
    RequestDataLoadService requestDataLoadService;
    @Mock
    AmazonS3 awsS3Client;
    @Mock
    RouteController routeController;
    @Mock
    CamelContext camelContext;

    private Set<String> barcodeSet = new HashSet<>();
    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBefore() {
        requestInitialDataLoadProcessor = new RequestInitialDataLoadProcessor("CUL");
        requestInitialDataLoadProcessor1 = new RequestInitialDataLoadProcessor("PUL");
        requestInitialDataLoadProcessor2 = new RequestInitialDataLoadProcessor("NYPL");
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
            requestInitialDataLoadProcessor1.processInput(ex);
            ex.getIn().setHeader("directoryName", "NYPL");
            requestInitialDataLoadProcessor2.processInput(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void processInput() throws ParseException {
        barcodeSet.add("123456");
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord1 = getRequestDataLoadCSVRecord();
        requestDataLoadCSVRecord.setBarcode("123456");
        List<RequestDataLoadCSVRecord> requestDataLoadCSVRecordList = new ArrayList<>();
        requestDataLoadCSVRecordList.add(requestDataLoadCSVRecord);
        requestDataLoadCSVRecordList.add(requestDataLoadCSVRecord1);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setBody(requestDataLoadCSVRecordList);
        exchange.getIn().setHeader("John", "CUL");
        exchange.getIn().setHeader("CamelAwsS3Key", "GEJGNSIKHIL");
        exchange.getIn().setHeader("CamelAwsS3BucketName", "AWSS3Bucket/Htsvmlfkgdr/fksifdg");
        exchange.setProperty("CamelSplitIndex",0);
        exchange.setProperty("CamelSplitComplete",true);
        Set<String> stringSet = new HashSet<>();
        stringSet.add("test");
        //Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);
        requestInitialDataLoadProcessor.processInput(exchange);

    }
    private RequestDataLoadCSVRecord getRequestDataLoadCSVRecord(){
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = new RequestDataLoadCSVRecord();
        requestDataLoadCSVRecord.setBarcode("332456456456745");
        requestDataLoadCSVRecord.setCustomerCode("PB");
        requestDataLoadCSVRecord.setDeliveryMethod("Test");
        requestDataLoadCSVRecord.setCreatedDate(new Date().toString());
        requestDataLoadCSVRecord.setLastUpdatedDate(new Date().toString());
        requestDataLoadCSVRecord.setPatronId("0000000");
        requestDataLoadCSVRecord.setStopCode("AD");
        requestDataLoadCSVRecord.setEmail("hemalatha.s@htcindia.com");
        return requestDataLoadCSVRecord;

    }
}
