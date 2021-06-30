package org.recap.ims.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RouteController;
import org.apache.camel.support.DefaultExchange;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.response.ItemInformationResponse;
import org.recap.ims.connector.AbstractLASImsLocationConnector;
import org.recap.ims.util.GFALasServiceUtil;
import org.recap.ims.connector.factory.LASImsLocationConnectorFactory;
import org.recap.ims.model.*;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.ScsbLasItemStatusCheckModel;
import org.recap.model.gfa.Ttitem;
import org.recap.model.jpa.*;
import org.recap.ims.processor.LasItemStatusCheckPollingProcessor;
import org.recap.model.search.SearchResultRow;
import org.recap.repository.jpa.*;
import org.recap.request.service.ItemRequestService;
import org.recap.util.CommonUtil;
import org.recap.request.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

public class GFALasServiceUT extends BaseTestCaseUT{

    @InjectMocks
    @Spy
    GFALasService gfaLasService;

    @Mock
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Mock
    AbstractLASImsLocationConnector abstractLASImsLocationConnector;

    @Mock
    private ProducerTemplate producer;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RouteController routeController;

    @Mock
    CamelContext camelContext;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    private LasItemStatusCheckPollingProcessor lasItemStatusCheckPollingProcessor;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    RestTemplate restTemplate = new RestTemplate();

    @Mock
    SimpleClientHttpRequestFactory simpleClientHttpRequestFactory;

    @Mock
    private CommonUtil commonUtil;

    @Mock
    private GFALasServiceUtil gfaLasServiceUtil;

    @Mock
    private PropertyUtil propertyUtil;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
    }

    @Test
    public void isUseQueueLasCall(){
        String imsLocationCode = "test";
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        gfaLasService.isUseQueueLasCall(imsLocationCode);
    }

    @Test
    public void executeRetrieveOrder() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setImsLocationCode("test");
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderWithoutImsLocationCode() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setImsLocationCode("");
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderForSCHONREFILEWO() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setImsLocationCode("test");
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("SCH ON REFILE WO:");
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderException() throws Exception {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderGFAItemStatusIN() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("VER ON REFILE WO:");
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }
    @Test
    public void executeRetrieveOrderGFAItemStatusSCHONREFILEWOWithoutUseQueueLasCall() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(false);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("VER ON REFILE WO:");
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any(GFAItemStatusCheckRequest.class))).thenReturn(gfaItemStatusCheckResponse);
        Mockito.when(abstractLASImsLocationConnector.itemRetrieval(any(GFARetrieveItemRequest.class))).thenReturn(getGfaRetrieveItemResponse());
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.FALSE.toString());
        ItemInformationResponse response1 = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        Mockito.when(abstractLASImsLocationConnector.itemRetrieval(any(GFARetrieveItemRequest.class))).thenReturn(gfaRetrieveItemResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderGFAItemStatusSCHONREFILEWOWithUseQueueLasCall() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(false);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("VER ON REFILE WO:");
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any(GFAItemStatusCheckRequest.class))).thenReturn(gfaItemStatusCheckResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderGFAItemStatusSCHONREFILEWOInnerException() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(false);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("VER ON REFILE WO:");
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any(GFAItemStatusCheckRequest.class))).thenReturn(gfaItemStatusCheckResponse);
        Mockito.when(abstractLASImsLocationConnector.itemRetrieval(any(GFARetrieveItemRequest.class))).thenThrow(new RestClientException("Bad Request"));
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.FALSE.toString());
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderGFAItemStatusINForEDDWithUseQueueLasCall() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setImsLocationCode("PA");
        itemRequestInfo.setRequestType("EDD");
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("VER ON REFILE WO:");
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderGFAItemStatusINForEDDWithoutUseQueueLasCall() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestType("EDD");
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        gfaEddItemResponse.setSuccess(false);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("VER ON REFILE WO:");
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.FALSE.toString());
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any(GFAItemStatusCheckRequest.class))).thenReturn(gfaItemStatusCheckResponse);
        Mockito.when(abstractLASImsLocationConnector.itemEDDRetrieval(any(GFARetrieveEDDItemRequest.class))).thenReturn(gfaEddItemResponse);
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        Mockito.when(abstractLASImsLocationConnector.itemEDDRetrieval(any(GFARetrieveEDDItemRequest.class))).thenReturn(getGFAEddItemResponse());
        ItemInformationResponse response1 = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response1);
    }

    @Test
    public void executeRetrieveOrderGFAItemStatusINWithoutUseQueueLasCall() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatus001 = new GFAItemStatus();
        gfaItemStatus001.setItemBarCode(itemRequestInfo.getItemBarcodes().get(0));
        List<GFAItemStatus> gfaItemStatuses = new ArrayList<>();
        gfaItemStatuses.add(gfaItemStatus001);
        gfaItemStatusCheckRequest.setItemStatus(gfaItemStatuses);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).setItemStatus("VER ON REFILE WO:");
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void executeRetrieveOrderWithoutGFAItemStatusResponse() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        RequestStatusEntity requestStatusEntity = getRequestItemEntity().getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbConstants.REQUEST_STATUS_LAS_ITEM_STATUS_PENDING)).thenReturn(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.findRequestItemById(any())).thenReturn(getRequestItemEntity());
        ItemInformationResponse response = gfaLasService.executeRetrieveOrder(itemRequestInfo, itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void gfaItemRequestProcessorForRetriveal() {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(itemRequestInfo);
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        Mockito.when(abstractLASImsLocationConnector.itemRetrieval(any(GFARetrieveItemRequest.class))).thenReturn(gfaRetrieveItemResponse);
        ItemInformationResponse response = gfaLasService.gfaItemRequestProcessor(ex);
        assertNotNull(response);
    }
    @Test
    public void gfaItemRequestProcessorForRetrivealWithFailureResponse() {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(itemRequestInfo);
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(false);
        Mockito.when(abstractLASImsLocationConnector.itemRetrieval(any(GFARetrieveItemRequest.class))).thenReturn(gfaRetrieveItemResponse);
        ItemInformationResponse response = gfaLasService.gfaItemRequestProcessor(ex);
        assertNotNull(response);
    }
    @Test
    public void gfaItemRequestProcessorRetrivealException() {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestId(null);
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(itemRequestInfo);
        ItemInformationResponse response = gfaLasService.gfaItemRequestProcessor(ex);
        assertNotNull(response);
    }

    @Test
    public void gfaItemRequestProcessorForEDD() throws JsonProcessingException {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestType("EDD");
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(itemRequestInfo);
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(itemRequestInfo.getImsLocationCode()).itemEDDRetrieval(any(GFARetrieveEDDItemRequest.class))).thenReturn(gfaEddItemResponse);
        ItemInformationResponse response = gfaLasService.gfaItemRequestProcessor(ex);
        assertNotNull(response);
    }
    @Test
    public void gfaItemRequestProcessorForEDDException() {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setRequestType("EDD");
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(itemRequestInfo);
        ItemInformationResponse response = gfaLasService.gfaItemRequestProcessor(ex);
        assertNotNull(response);
    }
    @Test
    public void gfaItemRequestProcessorForEDDWithFailureResponse() {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestType("EDD");
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(itemRequestInfo);
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        gfaEddItemResponse.setSuccess(false);
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(itemRequestInfo.getImsLocationCode()).itemEDDRetrieval(any(GFARetrieveEDDItemRequest.class))).thenReturn(gfaEddItemResponse);
        ItemInformationResponse response = gfaLasService.gfaItemRequestProcessor(ex);
        assertNotNull(response);
    }

    @Test
    public void callItemEDDRetrievable(){
        ItemRequestInformation itemRequestInformation = null;
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemInformationResponse response= gfaLasService.callItemEDDRetrievable(itemRequestInformation,itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void processLASRetrieveResponse() throws Exception {
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        JSONObject object = new JSONObject(gfaRetrieveItemResponse);
        String body = object.toString();
        Mockito.when(ReflectionTestUtils.invokeMethod(gfaLasServiceUtil, "getLASRetrieveResponse", gfaRetrieveItemResponse)).thenReturn(gfaRetrieveItemResponse);
        ItemInformationResponse itemInformationResponse = gfaLasService.processLASRetrieveResponse(body);
        assertNotNull(itemInformationResponse);
    }

    @Test
    public void processLASRetrieveResponseFailure() throws Exception {
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(false);
        JSONObject object = new JSONObject(gfaRetrieveItemResponse);
        String body = object.toString();
        Mockito.when(ReflectionTestUtils.invokeMethod(gfaLasServiceUtil, "getLASRetrieveResponse", gfaRetrieveItemResponse)).thenReturn(gfaRetrieveItemResponse);
        ItemInformationResponse itemInformationResponse = gfaLasService.processLASRetrieveResponse(body);
        assertNotNull(itemInformationResponse);
    }

    @Test
    public void processLASRetrieveResponseException() throws Exception {
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        JSONObject object = new JSONObject(gfaRetrieveItemResponse);
        String body = object.toString();
        ItemInformationResponse itemInformationResponse = gfaLasService.processLASRetrieveResponse(body);
        assertNotNull(itemInformationResponse);
    }

    @Test
    public void processLASEDDRetrieveResponse() throws JsonProcessingException {
        GFAEddItemResponse gfaEddItemResponse = getGfaEddItemResponse();
        gfaEddItemResponse.getDsitem().getTtitem().get(0).setErrorCode("");
        JSONObject object = new JSONObject(gfaEddItemResponse);
        String body = object.toString();
        gfaLasService.processLASEDDRetrieveResponse(body);
    }

    @Test
    public void processLASEDDRetrieveResponseFailure() throws JsonProcessingException {
        GFAEddItemResponse gfaEddItemResponse = getGfaEddItemResponse();
        JSONObject object = new JSONObject(gfaEddItemResponse);
        String body = object.toString();
        gfaLasService.processLASEDDRetrieveResponse(body);
    }

    @Test
    public void processLASEDDRetrieveResponseException() throws JsonProcessingException {
        JSONObject object = new JSONObject();
        String body = object.toString();
        gfaLasService.processLASEDDRetrieveResponse(body);
    }

    @Test
    public void startPolling() {
        String barcode = "135621";
        String imsLocationCode = "PUL";
        CamelContext ctx = new DefaultCamelContext();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        Mockito.when(lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse(any(), any(), any())).thenReturn(gfaItemStatusCheckResponse);
        gfaLasService.startPolling(barcode, imsLocationCode);
    }
    @Test
    public void startPollingException(){
        String barcode = "135621";
        String imsLocationCode = "PUL";
        Mockito.doThrow(new NullPointerException()).when(lasItemStatusCheckPollingProcessor).pollLasItemStatusJobResponse(any(),any(),any());
        gfaLasService.startPolling(barcode,imsLocationCode);
    }
    @Test
    public void getGfaItemStatusInUpperCase(){
        String gfaItemStatus = "test:test";
        String result = gfaLasService.getGfaItemStatusInUpperCase(gfaItemStatus);
        assertNotNull(result);
    }
    @Test
    public void getGfaItemStatus(){
        String gfaItemStatus = "test";
        String result = gfaLasService.getGfaItemStatusInUpperCase(gfaItemStatus);
        assertNotNull(result);
    }
    @Test
    public void buildRequestInfoAndReplaceToLAS(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        Mockito.when(commonUtil.getImsLocationCodeByItemBarcode(requestItemEntity.getItemEntity().getBarcode())).thenReturn("HD");
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        String result = gfaLasService.buildRequestInfoAndReplaceToLAS(requestItemEntity);
        assertNotNull(result);
    }
    @Test
    public void buildRequestInfoAndReplaceToLASException(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        Mockito.when(commonUtil.getImsLocationCodeByItemBarcode(requestItemEntity.getItemEntity().getBarcode())).thenReturn("HD");
        Mockito.doThrow(new NullPointerException()).when(commonUtil).checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean());
        String result = gfaLasService.buildRequestInfoAndReplaceToLAS(requestItemEntity);
        assertNotNull(result);
    }
    @Test
    public void buildRequestInfoAndReplaceToLASForRETRIEVAL(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        requestItemEntity.getRequestTypeEntity().setRequestTypeCode("RETRIEVAL");
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        Mockito.when(commonUtil.getImsLocationCodeByItemBarcode(any())).thenReturn("test");
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbConstants.LAS_REFILE_REQUEST_PLACED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        String result = gfaLasService.buildRequestInfoAndReplaceToLAS(requestItemEntity);
        assertNotNull(result);
    }
    @Test
    public void callGfaItemStatus(){
        String itemBarcode ="1346673";
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        Mockito.when(commonUtil.getImsLocationCodeByItemBarcode(any())).thenReturn("test");
        String gfaItemStatusValue = gfaLasService.callGfaItemStatus(itemBarcode);
        assertNotNull(gfaItemStatusValue);
    }

    @Test
    public void getGFAItemStatusCheckResponseByBarcodesAndImsLocationList(){
        List<ScsbLasItemStatusCheckModel> itemsStatusCheckModel = new ArrayList<>();
        ScsbLasItemStatusCheckModel scsbLasItemStatusCheckModel = new ScsbLasItemStatusCheckModel();
        scsbLasItemStatusCheckModel.setItemStatus("Complete");
        scsbLasItemStatusCheckModel.setItemBarcode("23567");
        scsbLasItemStatusCheckModel.setImsLocation("PUL");
        itemsStatusCheckModel.add(scsbLasItemStatusCheckModel);
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any(GFAItemStatusCheckRequest.class))).thenReturn(getGfaItemStatusCheckResponse());
        gfaLasService.getGFAItemStatusCheckResponseByBarcodesAndImsLocationList(itemsStatusCheckModel);
    }

    @Test
    public void buildGFAEddItemRequest(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        searchResultRow.setTitle("TEST");
        Mockito.doNothing().when(itemRequestServiceUtil).setEddInfoToGfaRequest(any(), any());
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        ReflectionTestUtils.invokeMethod(gfaLasService,"buildGFAEddItemRequest",requestItemEntity);
    }
    @Test
    public void buildGFARetrieveItemRequest(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        ReflectionTestUtils.invokeMethod(gfaLasService,"buildGFARetrieveItemRequest",requestItemEntity);
    }

    @Test
    public void checkGetters(){
        gfaLasService.getRestTemplate();
    }
    private GFAPwiRequest getGFAPwiRequest() {
        GFAPwiRequest gfaPwiRequest = new GFAPwiRequest();
        GFAPwiDsItemRequest gfaPwiDsItemRequest = new GFAPwiDsItemRequest();
        GFAPwiTtItemRequest gfaPwiTtItemRequest = new GFAPwiTtItemRequest();
        gfaPwiTtItemRequest.setCustomerCode("AR");
        gfaPwiTtItemRequest.setItemBarcode("AR00051608");
        gfaPwiDsItemRequest.setTtitem(Arrays.asList(gfaPwiTtItemRequest));
        gfaPwiRequest.setDsitem(gfaPwiDsItemRequest);
        return gfaPwiRequest;
    }

    private GFAPwiResponse getGfaPwiResponse() {
        GFAPwiResponse gfaPwiResponse  = new GFAPwiResponse();
        GFAPwiDsItemResponse gfaPwiDsItemResponse = new GFAPwiDsItemResponse();
        GFAPwiTtItemResponse gfaPwiTtItemResponse = new GFAPwiTtItemResponse();
        gfaPwiTtItemResponse.setCustomerCode("CA");
        gfaPwiTtItemResponse.setErrorNote("ErrorNote");
        gfaPwiDsItemResponse.setTtitem(Arrays.asList(gfaPwiTtItemResponse));
        gfaPwiResponse.setDsitem(gfaPwiDsItemResponse);
        return gfaPwiResponse;
    }

    private GFAPwdResponse getGfaPwdResponse() {
        GFAPwdResponse gfaPwdResponse = new GFAPwdResponse();
        GFAPwdTtItemResponse gfaPwdTtItemResponse = new GFAPwdTtItemResponse();
        gfaPwdTtItemResponse.setCustomerCode("PB");
        gfaPwdTtItemResponse.setItemBarcode("231365");
        gfaPwdTtItemResponse.setDestination("test");
        gfaPwdTtItemResponse.setDeliveryMethod("test");
        gfaPwdTtItemResponse.setRequestor("test");
        gfaPwdTtItemResponse.setRequestorFirstName("test");
        gfaPwdTtItemResponse.setRequestorLastName("test");
        gfaPwdTtItemResponse.setRequestorMiddleName("test");
        gfaPwdTtItemResponse.setRequestorEmail("test@email.com");
        gfaPwdTtItemResponse.setRequestorOther("test");
        gfaPwdTtItemResponse.setPriority("first");
        gfaPwdTtItemResponse.setNotes("test");
        gfaPwdTtItemResponse.setRequestDate(new Date());
        gfaPwdTtItemResponse.setRequestTime(new Time(new Long(10)));
        gfaPwdTtItemResponse.setErrorCode("test");
        gfaPwdTtItemResponse.setErrorNote("test");
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(gfaPwdTtItemResponse));
        gfaPwdDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwdDsItemResponse.setProdsHasChanges(true);
        gfaPwdResponse.setDsitem(gfaPwdDsItemResponse);
        return gfaPwdResponse;
    }

    private GFAPwdRequest getGfaPwdRequest() {
        GFAPwdRequest gfaPwdRequest = new GFAPwdRequest();
        GFAPwdDsItemRequest gfaPwdDsItemRequest = new GFAPwdDsItemRequest();
        GFAPwdTtItemRequest gfaPwdTtItemRequest = new GFAPwdTtItemRequest();
        gfaPwdTtItemRequest.setCustomerCode("PA");
        gfaPwdTtItemRequest.setItemBarcode("134556");
        gfaPwdTtItemRequest.setDestination("PA");
        gfaPwdTtItemRequest.setRequestor("test");
        gfaPwdDsItemRequest.setTtitem(Arrays.asList(gfaPwdTtItemRequest));
        gfaPwdRequest.setDsitem(gfaPwdDsItemRequest);
        return gfaPwdRequest;
    }

    private GFAEddItemResponse getGFAEddItemResponse() {
        GFAEddItemResponse gfaEddItemResponse = new GFAEddItemResponse();
        gfaEddItemResponse.setScreenMessage("Success");
        gfaEddItemResponse.setSuccess(true);
        RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
        TtitemEDDResponse ttitemEDDResponse = new TtitemEDDResponse();
        ttitemEDDResponse.setRequestId(1);
        ttitemEDDResponse.setCustomerCode("123456");
        ttitemEDDResponse.setRequestId(1);
        retrieveItemEDDRequest.setTtitem(Arrays.asList(ttitemEDDResponse));
        gfaEddItemResponse.setDsitem(retrieveItemEDDRequest);
        return gfaEddItemResponse;
    }

    private GFARetrieveEDDItemRequest getGFARetrieveEDDItemRequest() {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = new GFARetrieveEDDItemRequest();
        TtitemEDDResponse ttitemEDDResponse = new TtitemEDDResponse();
        ttitemEDDResponse.setItemBarcode("332445645758458");
        ttitemEDDResponse.setCustomerCode("AD");
        ttitemEDDResponse.setRequestId(1);
        ttitemEDDResponse.setRequestor("Test");
        ttitemEDDResponse.setRequestorFirstName("test");
        ttitemEDDResponse.setRequestorLastName("test");
        ttitemEDDResponse.setRequestorMiddleName("test");
        ttitemEDDResponse.setRequestorEmail("test@email.com");
        ttitemEDDResponse.setRequestorOther("test");
        ttitemEDDResponse.setBiblioTitle("test");
        ttitemEDDResponse.setBiblioLocation("Discovery");
        ttitemEDDResponse.setBiblioAuthor("John");
        ttitemEDDResponse.setBiblioVolume("V1");
        ttitemEDDResponse.setBiblioCode("A1");
        ttitemEDDResponse.setArticleTitle("Title");
        ttitemEDDResponse.setArticleDate(new Date().toString());
        ttitemEDDResponse.setArticleAuthor("john");
        ttitemEDDResponse.setArticleIssue("Test");
        ttitemEDDResponse.setArticleVolume("V1");
        ttitemEDDResponse.setStartPage("1");
        ttitemEDDResponse.setEndPage("10");
        ttitemEDDResponse.setPages("9");
        ttitemEDDResponse.setOther("test");
        ttitemEDDResponse.setPriority("test");
        ttitemEDDResponse.setNotes("notes");
        ttitemEDDResponse.setRequestDate(new Date().toString());
        ttitemEDDResponse.setRequestTime("06:05:00");
        ttitemEDDResponse.setErrorCode("test");
        ttitemEDDResponse.setErrorNote("test");

        RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
        retrieveItemEDDRequest.setTtitem(Arrays.asList(ttitemEDDResponse));
        gfaRetrieveEDDItemRequest.setDsitem(retrieveItemEDDRequest);
        return gfaRetrieveEDDItemRequest;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private GFARetrieveItemResponse getGfaRetrieveItemResponse() {
        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        RetrieveItem retrieveItem = new RetrieveItem();
        Ttitem ttitem = new Ttitem();
        ttitem.setRequestId(1);
        ttitem.setItemStatus("Success");
        ttitem.setItemBarcode("123456");
        retrieveItem.setTtitem(Arrays.asList(ttitem));
        gfaRetrieveItemResponse.setSuccess(true);
        gfaRetrieveItemResponse.setDsitem(retrieveItem);
        gfaRetrieveItemResponse.setScreenMessage("Success");
        return gfaRetrieveItemResponse;
    }

    private GFARetrieveItemRequest getGfaRetrieveItemRequest() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        RetrieveItemRequest retrieveItemRequest = new RetrieveItemRequest();
        TtitemRequest ttitemRequest = new TtitemRequest();
        ttitemRequest.setCustomerCode("PB");
        ttitemRequest.setDestination("PUL");
        ttitemRequest.setItemBarcode("123");
        ttitemRequest.setItemStatus("Available");
        retrieveItemRequest.setTtitem(Arrays.asList(ttitemRequest));
        gfaRetrieveItemRequest.setDsitem(retrieveItemRequest);
        return gfaRetrieveItemRequest;
    }

    private GFALasStatusCheckResponse getGfaLasStatusCheckResponse() {
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = new GFALasStatusCheckResponse();
        GFALasStatusDsItem gfaLasStatusDsItem = new GFALasStatusDsItem();
        GFALasStatusTtItem gfaLasStatusTtItem = new GFALasStatusTtItem();
        gfaLasStatusTtItem.setSuccess("true");
        gfaLasStatusTtItem.setScreenMessage("Success");
        gfaLasStatusTtItem.setImsLocationCode("PA");
        gfaLasStatusDsItem.setTtitem(Arrays.asList(gfaLasStatusTtItem));
        gfaLasStatusCheckResponse.setDsitem(gfaLasStatusDsItem);
        return gfaLasStatusCheckResponse;
    }

    private GFAItemStatusCheckResponse getGfaItemStatusCheckResponse() {
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = new GFAItemStatusCheckResponse();
        Dsitem dsitem = new Dsitem();
        Ttitem ttitem = new Ttitem();
        ttitem.setItemBarcode("7020");
        ttitem.setItemStatus("NOT ON FILE");
        dsitem.setTtitem(Arrays.asList(ttitem));
        gfaItemStatusCheckResponse.setDsitem(dsitem);
        return gfaItemStatusCheckResponse;
    }

    private GFAItemStatusCheckRequest getGfaItemStatusCheckRequest() {
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("21345");
        gfaItemStatusCheckRequest.setItemStatus(Arrays.asList(gfaItemStatus));
        return gfaItemStatusCheckRequest;
    }

    private GFALasStatusCheckRequest getGfaLasStatusCheckRequest() {
        GFALasStatusCheckRequest gfaLasStatusCheckRequest = new GFALasStatusCheckRequest();
        GFALasStatus gfaLasStatus = new GFALasStatus();
        gfaLasStatus.setImsLocationCode("PA");
        gfaLasStatusCheckRequest.setLasStatus(Arrays.asList(gfaLasStatus));
        return gfaLasStatusCheckRequest;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("1");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("7020");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);
        return itemEntity;
    }

    public ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setUsername("Discovery");
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setRequestId(1);
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
        itemRequestInformation.setChapterTitle("test");
        itemRequestInformation.setVolume("5");
        itemRequestInformation.setIssue("test");
        itemRequestInformation.setEmailAddress("test@email.com");
        itemRequestInformation.setStartPage("10");
        itemRequestInformation.setEndPage("100");
        itemRequestInformation.setImsLocationCode("PA");
        return itemRequestInformation;
    }

    public ItemInformationResponse getItemInformationResponse() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setCirculationStatus("test");
        itemInformationResponse.setSecurityMarker("test");
        itemInformationResponse.setFeeType("test");
        itemInformationResponse.setTransactionDate(new Date().toString());
        itemInformationResponse.setHoldQueueLength("10");
        itemInformationResponse.setTitleIdentifier("test");
        itemInformationResponse.setBibID("1223");
        itemInformationResponse.setDueDate(new Date().toString());
        itemInformationResponse.setExpirationDate("30-03-2017 00:00:00");
        itemInformationResponse.setRecallDate(new Date().toString());
        itemInformationResponse.setCurrentLocation("test");
        itemInformationResponse.setHoldPickupDate(new Date().toString());
        itemInformationResponse.setItemBarcode("32101077423406");
        itemInformationResponse.setRequestType("RECALL");
        itemInformationResponse.setRequestingInstitution("CUL");
        itemInformationResponse.setRequestId(392);
        return itemInformationResponse;
    }

    private RequestItemEntity getRequestItemEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        ItemEntity itemEntity = getItemEntity();
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("EDD_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("EDD ORDER PLACED");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(itemEntity);
        requestItemEntity.setNotes("test");
        return requestItemEntity;
    }

    private GFAEddItemResponse getGfaEddItemResponse() {
        GFAEddItemResponse gfaEddItemResponse = new GFAEddItemResponse();
        gfaEddItemResponse.setScreenMessage("Success");
        gfaEddItemResponse.setSuccess(true);
        RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
        TtitemEDDResponse ttitemEDDResponse = new TtitemEDDResponse();
        ttitemEDDResponse.setCustomerCode("123456");
        ttitemEDDResponse.setRequestId(1);
        ttitemEDDResponse.setErrorCode("errorCode");
        retrieveItemEDDRequest.setTtitem(Arrays.asList(ttitemEDDResponse));
        gfaEddItemResponse.setDsitem(retrieveItemEDDRequest);
        return gfaEddItemResponse;
    }

}
