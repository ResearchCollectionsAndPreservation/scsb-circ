package org.recap.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.las.AbstractLASImsLocationConnector;
import org.recap.las.GFALasService;
import org.recap.las.LASImsLocationConnectorFactory;
import org.recap.las.model.GFALasStatusCheckRequest;
import org.recap.las.model.GFALasStatusCheckResponse;
import org.recap.las.model.GFALasStatusDsItem;
import org.recap.las.model.GFALasStatusTtItem;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

public class LasHeartBeatCheckPollingProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    LasHeartBeatCheckPollingProcessor lasHeartBeatCheckPollingProcessor;

    @Mock
    private GFALasService gfaLasService;

    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Mock
    AbstractLASImsLocationConnector abstractLASImsLocationConnector;

    @Before
    public void setup() {
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn("1000");
    }

    @Test
    public void pollLasHeartBeatResponse() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "PUL");
        exchange.getIn().setBody(itemRequestInformation);
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = getGFALasStatusCheckResponse();
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.heartBeatCheck(any(GFALasStatusCheckRequest.class))).thenReturn(gfaLasStatusCheckResponse);
        lasHeartBeatCheckPollingProcessor.pollLasHeartBeatResponse(exchange);
    }

    @Test
    public void pollLasHeartBeatResponseWithoutImsLocation() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setImsLocationCode(null);
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "PUL");
        exchange.getIn().setBody(itemRequestInformation);
        lasHeartBeatCheckPollingProcessor.pollLasHeartBeatResponse(exchange);
    }

    @Test
    public void pollLasHeartBeatResponseException() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "PUL");
        exchange.getIn().setBody(itemRequestInformation);
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = getGFALasStatusCheckResponse();
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.heartBeatCheck(any(GFALasStatusCheckRequest.class))).thenReturn(gfaLasStatusCheckResponse);
        Mockito.doThrow(new NullPointerException()).when(producerTemplate).sendBodyAndHeader(RecapConstants.LAS_OUTGOING_QUEUE_PREFIX + itemRequestInformation.getImsLocationCode() + RecapConstants.OUTGOING_QUEUE_SUFFIX, itemRequestInformation, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
        lasHeartBeatCheckPollingProcessor.pollLasHeartBeatResponse(exchange);
    }

    @Test
    public void pollLasHeartBeatResponseInterruptedException() throws Exception {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "PUL");
        exchange.getIn().setBody(itemRequestInformation);
        lasHeartBeatCheckPollingProcessor.pollLasHeartBeatResponse(exchange);
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setUsername("Discovery");
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setCallNumber("X");
        itemRequestInformation.setAuthor("John");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setExpirationDate("30-03-2017 00:00:00");
        itemRequestInformation.setCustomerCode("PB");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setRequestType("RETRIEVAL");
        itemRequestInformation.setImsLocationCode("PA");
        return itemRequestInformation;
    }

    private GFALasStatusCheckResponse getGFALasStatusCheckResponse() {
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = new GFALasStatusCheckResponse();
        GFALasStatusDsItem gfaLasStatusDsItem = new GFALasStatusDsItem();
        GFALasStatusTtItem gfaLasStatusTtItem = new GFALasStatusTtItem();
        gfaLasStatusTtItem.setImsLocationCode("1");
        gfaLasStatusTtItem.setSuccess(Boolean.TRUE.toString());
        gfaLasStatusTtItem.setScreenMessage("SUCCESS");
        gfaLasStatusDsItem.setTtitem(Arrays.asList(gfaLasStatusTtItem));
        gfaLasStatusCheckResponse.setDsitem(gfaLasStatusDsItem);
        return gfaLasStatusCheckResponse;
    }
}
