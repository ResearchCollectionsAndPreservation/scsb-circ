package org.recap.mqconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.RequestInformation;
import org.recap.request.BulkItemRequestProcessService;
import org.recap.request.BulkItemRequestService;
import org.recap.request.ItemEDDRequestService;
import org.recap.request.ItemRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by hemalathas on 14/3/17.
 */

public class RequestItemQueueConsumerUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(RequestItemQueueConsumer.class);

    @Mock
    RequestItemQueueConsumer requestItemQueueConsumer;

    @InjectMocks
    RequestItemQueueConsumer mockedRequestItemQueueConsumer;

    @Mock
    BulkItemRequestProcessService bulkItemRequestProcessService;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    BulkItemRequestService bulkItemRequestService;

    @Mock
    ItemRequestService itemRequestService;

    @Mock
    ItemEDDRequestService itemEDDRequestService;

    @Mock
    ObjectMapper om;

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
        Mockito.when(requestItemQueueConsumer.getBulkItemRequestProcessService()).thenReturn(bulkItemRequestProcessService);
        Mockito.when(requestItemQueueConsumer.getBulkItemRequestService()).thenReturn(bulkItemRequestService);
        Mockito.when(requestItemQueueConsumer.getItemEDDRequestService()).thenReturn(itemEDDRequestService);
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.when(requestItemQueueConsumer.getObjectMapper()).thenReturn(om);
    }
    @Test
    public void testRequestItemOnMessage() throws IOException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getObjectMapper()).thenReturn(om);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(requestItemQueueConsumer.getObjectMapper().readValue(body, ItemRequestInformation.class)).thenReturn(itemRequestInformation);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).requestItemOnMessage(body,exchange);
        requestItemQueueConsumer.requestItemOnMessage(body,exchange);
    }

    @Test
    public void testRequestItemEDDOnMessage() throws IOException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getObjectMapper()).thenReturn(om);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.when(requestItemQueueConsumer.getItemEDDRequestService()).thenReturn(itemEDDRequestService);
        Mockito.when(requestItemQueueConsumer.getObjectMapper().readValue(body, ItemRequestInformation.class)).thenReturn(itemRequestInformation);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).requestItemEDDOnMessage(body,exchange);
        requestItemQueueConsumer.requestItemEDDOnMessage(body,exchange);
    }

    @Test
    public void testRequestItemBorrowDirectOnMessage() throws IOException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getObjectMapper()).thenReturn(om);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(requestItemQueueConsumer.getObjectMapper().readValue(body, ItemRequestInformation.class)).thenReturn(itemRequestInformation);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).requestItemBorrowDirectOnMessage(body,exchange);
        requestItemQueueConsumer.requestItemBorrowDirectOnMessage(body,exchange);
    }

    @Test
    public void testRequestItemRecallOnMessage() throws IOException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getObjectMapper()).thenReturn(om);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(requestItemQueueConsumer.getObjectMapper().readValue(body, ItemRequestInformation.class)).thenReturn(itemRequestInformation);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).requestItemRecallOnMessage(body,exchange);
        requestItemQueueConsumer.requestItemRecallOnMessage(body,exchange);
    }

    @Test
    public void testRequestTopicOnMessage() throws Exception{
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        JSONObject jsonObject = new JSONObject();
        String body = jsonObject.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).requestTopicOnMessage(body);
        requestItemQueueConsumer.requestTopicOnMessage(body);
    }

    @Test
    public void testEDDTopicOnMessage() throws Exception{
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).eddTopicOnMessage(body);
        requestItemQueueConsumer.eddTopicOnMessage(body);
    }

    @Test
    public void testRecallTopicOnMessage() throws Exception{
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).recallTopicOnMessage(body);
        requestItemQueueConsumer.recallTopicOnMessage(body);
    }

    @Test
    public void testLasOutgoingQOnCompletion(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).lasOutgoingQOnCompletion(body);
        requestItemQueueConsumer.lasOutgoingQOnCompletion(body);
    }

    @Test
    public void testLasIngoingQOnCompletion(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).lasIngoingQOnCompletion(body);
        requestItemQueueConsumer.lasIngoingQOnCompletion(body);
    }
    @Test
    public void scsbOutgoingQOnCompletion(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).scsbOutgoingQOnCompletion(body);
        requestItemQueueConsumer.scsbOutgoingQOnCompletion(body);
    }


    @Test
    public void lasResponseRetrievalOnMessage(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).lasResponseRetrievalOnMessage(body);
        requestItemQueueConsumer.lasResponseRetrievalOnMessage(body);
    }

    @Test
    public void testLasResponseEDDOnMessage(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).lasResponseEDDOnMessage(body);
        requestItemQueueConsumer.lasResponseEDDOnMessage(body);
    }

    @Test
    public void testLasResponsePWIOnMessage(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).lasResponsePWIOnMessage(body);
        requestItemQueueConsumer.lasResponsePWIOnMessage(body);
    }

    @Test
    public void testLasResponsePWDOnMessage(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        String body = itemRequestInformation.toString();
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).lasResponsePWDOnMessage(body);
        requestItemQueueConsumer.lasResponsePWDOnMessage(body);
    }
    @Test
    public void bulkRequestItemOnMessage() throws Exception{
        String body = "12345";
        Mockito.when(requestItemQueueConsumer.getBulkItemRequestService()).thenReturn(bulkItemRequestService);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).bulkRequestItemOnMessage(body,exchange);
        requestItemQueueConsumer.bulkRequestItemOnMessage(body,exchange);
    }
    @Test
    public void bulkRequestProcessItemOnMessage() throws Exception{
        message.setHeader(RecapCommonConstants.BULK_REQUEST_ID,1);
        message.setBody("BULK REQUEST");
        exchange.setIn(message);
        String body = "12345";
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(requestItemQueueConsumer.getBulkItemRequestProcessService()).thenReturn(bulkItemRequestProcessService);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).bulkRequestProcessItemOnMessage(body,exchange);
        requestItemQueueConsumer.bulkRequestProcessItemOnMessage(body,exchange);
    }
    @Test
    public  void requestItemLasStatusCheckOnMessage() throws Exception{
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestId(1);
        itemInformationResponse.setBibID("1");
        RequestInformation requestInformation = new RequestInformation();
        requestInformation.setItemRequestInfo(itemRequestInformation);
        requestInformation.setItemResponseInformation(itemInformationResponse);
        JSONObject jsonObject = new JSONObject();
        String body = jsonObject.toString();
        Mockito.when(requestItemQueueConsumer.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).requestItemLasStatusCheckOnMessage(body,exchange);
        requestItemQueueConsumer.requestItemLasStatusCheckOnMessage(body,exchange);
    }
    @Test
    public  void requestItemLasStatusCheckOnMessageException() throws Exception{
        String body = "12345";
        Mockito.when(requestItemQueueConsumer.getLogger()).thenReturn(logger);
        Mockito.doCallRealMethod().when(requestItemQueueConsumer).requestItemLasStatusCheckOnMessage(body,exchange);
        requestItemQueueConsumer.requestItemLasStatusCheckOnMessage(body,exchange);
    }
    @Test
    public void checkGetters(){
        mockedRequestItemQueueConsumer.getBulkItemRequestProcessService();
        mockedRequestItemQueueConsumer.getBulkItemRequestService();
        mockedRequestItemQueueConsumer.getItemEDDRequestService();
        mockedRequestItemQueueConsumer.getItemRequestService();
        mockedRequestItemQueueConsumer.getInstitutionCode();
        mockedRequestItemQueueConsumer.getLogger();
        mockedRequestItemQueueConsumer.getObjectMapper();
        RequestItemQueueConsumer requestItemQueueConsumer = new RequestItemQueueConsumer(itemRequestService,itemEDDRequestService);
        requestItemQueueConsumer.getLogger();
    }
}