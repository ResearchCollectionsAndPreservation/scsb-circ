package org.recap.request;

import io.swagger.models.auth.In;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.controller.RequestItemController;
import org.recap.ils.IJSIPConnector;
import org.recap.ils.JSIPConnectorFactory;
import org.recap.ils.model.response.ItemInformationResponse;

import org.recap.model.ItemRefileRequest;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.RestHeaderService;
import org.recap.util.CommonUtil;
import org.recap.util.ItemRequestServiceUtil;
import org.recap.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.Normalizer;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

/**
 * Created by hemalathas on 20/3/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(UriComponentsBuilder.class)
public class ItemRequestServiceUT {

    private static final Logger logger = LoggerFactory.getLogger(ItemRequestServiceUT.class);
    @InjectMocks
    ItemRequestService mockedItemRequestService;
    @Mock
    IJSIPConnector ijsipConnector;
    @Mock
    ItemRequestService itemRequestService;
    @Mock
    ItemRequestService mockItemRequestService;
    @Mock
    Exchange exchange;
    @Mock
    JSIPConnectorFactory jsipConnectorFactory;
    @Mock
    private ItemDetailsRepository mockedItemDetailsRepository;

    @Mock
    private RequestItemController mockedRequestItemController;

    @Mock
    private RequestItemDetailsRepository mockedRequestItemDetailsRepository;

    @Mock
    private EmailService mockedEmailService;

    @Mock
    private RequestItemStatusDetailsRepository mockedRequestItemStatusDetailsRepository;

    @Mock
    private GFAService mockedGfaService;

    @Mock
    private ItemRequestDBService mockedItemRequestDBService;

    @Mock
    private CustomerCodeDetailsRepository mockedCustomerCodeDetailsRepository;

    @Mock
    private ItemStatusDetailsRepository mockedItemStatusDetailsRepository;

    @Mock
    private RestHeaderService mockedRestHeaderService;

    @Mock
    private ItemRequestServiceUtil mockedIitemRequestServiceUtil;

    @Mock
    private ProducerTemplate mockedProducerTemplate;

    @Mock
    private RequestParamaterValidatorService mockedRequestParamaterValidatorService;

    @Mock
    private ItemValidatorService mockedItemValidatorService;

    @Mock
    private SecurityUtil mockedSecurityUtil;

    @Mock
    private CommonUtil mockedCommonUtil;

    @Mock
    private ItemEDDRequestService mockedItemEDDRequestService;

    @Mock
    ItemEntity mockedItemEntity;

    @Mock
    UriComponentsBuilder builder;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRequestItem() throws Exception{
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        builder = UriComponentsBuilder.fromHttpUrl("http://localhost:9090/" + RecapConstants.SEARCH_RECORDS_SOLR)
                .queryParam(RecapConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, RecapConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(RecapConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setOwningInstitutionId(1);
        ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
        SearchResultRow searchResultRow = new SearchResultRow();
        ItemRequestService mockedObject = PowerMockito.mock(ItemRequestService.class);
        searchResultRow.setAuthor("test");
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedCustomerCodeDetailsRepository.findByCustomerCode(itemRequestInfo.getDeliveryLocation())).thenReturn(customerCodeEntity);
       // PowerMockito.mockStatic(UriComponentsBuilder.class);
       // PowerMockito.when(UriComponentsBuilder.fromHttpUrl("http://localhost:9090/")).thenReturn(builder);
        //restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>()
        Mockito.when(mockedItemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.when(mockedItemDetailsRepository.findByItemId(itemEntity.getItemId())).thenReturn(itemEntity);
        Mockito.doNothing().when(mockedItemRequestDBService).updateItemAvailabilutyStatus(Arrays.asList(itemEntity), itemRequestInfo.getUsername());
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PROCESSING, null)).thenReturn(1);
        Mockito.doNothing().when(mockedCommonUtil).rollbackUpdateItemAvailabilutyStatus(itemEntity, itemRequestInfo.getUsername());
        Mockito.doNothing().when(mockedCommonUtil).saveItemChangeLogEntity(itemRequestInfo.getRequestId(), itemRequestInfo.getUsername(), RecapConstants.REQUEST_RETRIEVAL, itemRequestInfo.getRequestNotes());
       // mockedItemRequestService.requestItem(itemRequestInfo,exchange);
    }
    @Test
    public void reFileItem(){
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        RequestItemEntity requestItemEntity = createRequestItem();
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        itemEntity.setItemAvailabilityStatusId(2);
        String itemBarcode = itemEntity.getBarcode();
        List<String> requestItemStatusList = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(mockedGfaService.callGfaItemStatus(itemEntity.getBarcode())).thenReturn("REFILED SUCCESSFULLY");
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, RecapCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        Mockito.doNothing().when(mockedIitemRequestServiceUtil).updateSolrIndex(itemEntity);
        mockedItemRequestService.reFileItem(itemRefileRequest,itemRefileResponse);
    }
    @Test
    public void reFileItemNotRecalled(){
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        RequestItemEntity requestItemEntity = createRequestItem();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("NYPL");
        institutionEntity.setInstitutionName("NYPL");
        requestItemEntity.setInstitutionEntity(institutionEntity);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        itemEntity.setItemAvailabilityStatusId(2);
        String itemBarcode = itemEntity.getBarcode();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setItemBarcodes(Collections.singletonList(itemBarcode));
        itemRequestInfo.setItemOwningInstitution(requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
        itemRequestInfo.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
        itemRequestInfo.setRequestType(requestItemEntity.getRequestTypeEntity().getRequestTypeCode());

        List<String> requestItemStatusList = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(mockedRequestItemController.getJsipConectorFactory()).thenReturn(jsipConnectorFactory);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemController.getJsipConectorFactory().getJSIPConnector(itemRequestInfo.getRequestingInstitution())).thenReturn(ijsipConnector);
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(mockedGfaService.callGfaItemStatus(itemEntity.getBarcode())).thenReturn("REFILED SUCCESSFULLY");
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, RecapCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(null);
        Mockito.when(mockedRequestItemController.getJsipConectorFactory().getJSIPConnector(itemRequestInfo.getRequestingInstitution()).refileItem(itemBarcode)).thenReturn("");
        Mockito.doNothing().when(mockedIitemRequestServiceUtil).updateSolrIndex(itemEntity);
        mockedItemRequestService.reFileItem(itemRefileRequest,itemRefileResponse);
    }

    @Test
    public void refileItemWithoutRequestEntities(){
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        String itemBarcode = itemEntity.getBarcode();
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("LAS_REFILE_REQUEST_PLACED");
        requestStatusEntity.setRequestStatusDescription("LAS_REFILE_REQUEST_PLACED");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RECALL");
        requestTypeEntity.setRequestTypeDesc("RECALL");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setItemBarcodes(Collections.singletonList(itemBarcode));
        itemRequestInfo.setItemOwningInstitution(requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
        itemRequestInfo.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
        itemRequestInfo.setPatronBarcode(requestItemEntity.getPatronId());
        itemRequestInfo.setRequestNotes(requestItemEntity.getNotes());
        itemRequestInfo.setRequestId(requestItemEntity.getId());
        itemRequestInfo.setUsername(requestItemEntity.getCreatedBy());
        itemRequestInfo.setDeliveryLocation(requestItemEntity.getStopCode());
        itemRequestInfo.setCustomerCode(itemEntity.getCustomerCode());
        Mockito.when(mockedGfaService.callGfaItemStatus(itemEntity.getBarcode())).thenReturn("OUT");
        List<String> requestItemStatusList = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(null);
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedGfaService.isUseQueueLasCall()).thenReturn(true);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PENDING, null)).thenReturn(1);
        Mockito.when(mockedGfaService.executeRetrieveOrder(itemRequestInfo, itemResponseInformation)).thenReturn(itemResponseInformation);
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        mockedItemRequestService.reFileItem(itemRefileRequest,itemRefileResponse);
    }

    private ItemRequestInformation getItemRequestInformation2() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123456"));
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setPatronBarcode("123");
        itemRequestInformation.setEmailAddress("");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setRequestType("RETRIEVAL");
        itemRequestInformation.setCustomerCode("PA");
        itemRequestInformation.setChapterTitle("");
        itemRequestInformation.setBibId("");
        itemRequestInformation.setUsername("test");
        return itemRequestInformation;
    }

    @Test
    public void updateChangesToDb(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestId(1);
        itemInformationResponse.setUsername("test");
        Mockito.when(mockedCommonUtil.getUser(itemInformationResponse.getUsername())).thenReturn("1");
        Mockito.doNothing().when(mockedCommonUtil).saveItemChangeLogEntity(itemInformationResponse.getRequestId(), "1", "RECALL", itemInformationResponse.getRequestNotes());
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemInformationResponse)).thenReturn(itemInformationResponse);
        mockedItemRequestService.updateChangesToDb(itemInformationResponse,"RECALL");
    }
   /* @Test
    public  void sendMessageToTopic(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();

        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.PRINCETON,RecapCommonConstants.REQUEST_TYPE_RETRIEVAL,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.COLUMBIA,RecapCommonConstants.REQUEST_TYPE_RETRIEVAL,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.NYPL,RecapCommonConstants.REQUEST_TYPE_RETRIEVAL,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.PRINCETON,RecapCommonConstants.REQUEST_TYPE_EDD,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.COLUMBIA,RecapCommonConstants.REQUEST_TYPE_EDD,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.NYPL,RecapCommonConstants.REQUEST_TYPE_EDD,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.PRINCETON,RecapCommonConstants.REQUEST_TYPE_RECALL,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.COLUMBIA,RecapCommonConstants.REQUEST_TYPE_RECALL,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.NYPL,RecapCommonConstants.REQUEST_TYPE_RECALL,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.PRINCETON,RecapCommonConstants.REQUEST_TYPE_BORROW_DIRECT,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.COLUMBIA,RecapCommonConstants.REQUEST_TYPE_BORROW_DIRECT,itemInformationResponse,exchange);
        mockedItemRequestService.sendMessageToTopic(RecapCommonConstants.NYPL,RecapCommonConstants.REQUEST_TYPE_BORROW_DIRECT,itemInformationResponse,exchange);
    }*/
    private ItemEntity getItemEntity() {

        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusDescription("COMPLETE");
        itemStatusEntity.setStatusCode("AVAILABLE");
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCollectionGroupCode("Complete");
        collectionGroupEntity.setCollectionGroupDescription("Complete");
        collectionGroupEntity.setLastUpdatedDate(new Date());
        collectionGroupEntity.setCreatedDate(new Date());
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        return itemEntity;
    }
    @Test // Test Cases RequestIds
    public void testUpdateRecapRequestItem() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
       /* Integer response = itemRequestService.updateRecapRequestItem(getItemRequestInformation(), bibliographicEntity.getItemEntities().get(0), "REFILED");
        assertTrue(response != 0);
        itemRequestService.getEmailService();
        itemRequestService.getGfaService();
        //updateItemAvailabilutyStatus*/
        Random random = new Random();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        //InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);

        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setCatalogingStatus("Complete");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity = new ItemEntity();
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("Not Available");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
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
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        List<ItemEntity> list = new ArrayList<ItemEntity>();
        list.add(itemEntity);
        try {
            /*boolean status = itemRequestService.updateItemAvailabilutyStatus(list, "recap");
            assertTrue(status);*/
        } catch (Exception e) {
        }
        try {
            /*ItemInformationResponse itemInformationResponse = itemRequestService.recallItem(getItemRequestInformation(), exchange);
            assertNotNull(itemInformationResponse);*/
        } catch (Exception e) {
        }

        /*ItemInformationResponse itemInformationResponse = itemRequestService.updateGFA(getItemRequestInformation(), getItemInformationResponse());
        assertNotNull(itemInformationResponse);
        String body = getItemInformationResponse().toString();
        try {
            itemRequestService.processLASRetrieveResponse(body);
            itemRequestService.processLASEddRetrieveResponse(body);
            itemRequestService.removeDiacritical("tests");
        } catch (Exception e) {
*/
      /*  }
        try {
            boolean bstatus = itemRequestService.executeLasitemCheck(getItemRequestInformation(), getItemInformationResponse());
            assertTrue(bstatus);
        } catch (Exception e) {}*/
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setReplaceRequestByType("RequestStatus");
        replaceRequest.setEndRequestId("320");
        replaceRequest.setFromDate(new Date().toString());
        replaceRequest.setToDate(new Date().toString());
        replaceRequest.setRequestIds("2");
        replaceRequest.setStartRequestId("1");
        replaceRequest.setRequestStatus("test");
        Map<String, String> listMap = new HashMap<>();
        // listMap = itemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(listMap);

      /*  try { itemRequestService.sendMessageToTopic("PUL","RETRIEVAL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("PUL","EDD",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("PUL","RECALL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("PUL","BORROW DIRECT",getItemInformationResponse(),exchange);} catch (Exception e) {}

        try { itemRequestService.sendMessageToTopic("CUL","RETRIEVAL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("CUL","EDD",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("CUL","RECALL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("CUL","BORROW DIRECT",getItemInformationResponse(),exchange);} catch (Exception e) {}

        try { itemRequestService.sendMessageToTopic("NYPLL","RETRIEVAL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("NYPLL","EDD",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("NYPLL","RECALL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("NYPLL","BORROW DIRECT",getItemInformationResponse(),exchange);} catch (Exception e) {}

    }*/
    }
    @Test
    public void testUpdateRecapRqstItem() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestId(requestItemEntity.getId());
        /*ItemInformationResponse response = itemRequestService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(response);*/
    }

    @Test
    public void testUpdateRecapRequestStatus() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestId(requestItemEntity.getId());
       /* ItemInformationResponse response = itemRequestService.updateRecapRequestStatus(itemInformationResponse);
        assertNotNull(response);*/
    }

    @Test
    public void recallItemException(){
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        /*ItemInformationResponse itemInformationResponse = itemRequestService.recallItem(itemRequestInformation,exchange);
        assertNotNull(itemInformationResponse);*/
    }
    @Test
    public void recallItem(){
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCustomerCode("PA");
        itemEntity.setBarcode("123456");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        //Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(itemRequestInformation.getItemBarcodes())).thenReturn(Arrays.asList(itemEntity));
       // Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemRequestInformation.getItemBarcodes().get(0), RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)).thenReturn(requestItemEntity);
       // Mockito.doReturn(searchResultRow).when(itemRequestService).searchRecords(itemEntity);
       // Mockito.when(ReflectionTestUtils.invokeMethod(itemRequestService, "searchRecords", itemEntity)).thenReturn(searchResultRow);
       // Mockito.when(mockedItemRequestService.recallItem(itemRequestInformation,exchange)).thenCallRealMethod();
        ItemInformationResponse itemInformationResponse = mockedItemRequestService.recallItem(itemRequestInformation,exchange);
        assertNotNull(itemInformationResponse);
    }

    @Test
    public void processLASRetrieveResponse(){
        String body = "text";
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        Mockito.when(mockedGfaService.processLASRetrieveResponse(body)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestStatus(itemInformationResponse)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.rollbackAfterGFA(itemInformationResponse)).thenReturn(itemRequestInformation);
        Mockito.when(mockedRequestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        mockedItemRequestService.processLASRetrieveResponse(body);
    }

    @Test
    public void processLASEddRetrieveResponse(){
        String body = "text";
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        Mockito.when(mockedGfaService.processLASEDDRetrieveResponse(body)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestStatus(itemInformationResponse)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.rollbackAfterGFA(itemInformationResponse)).thenReturn(itemRequestInformation);
        mockedItemRequestService.processLASEddRetrieveResponse(body);
    }
    @Test
    public void processLASEddRetrieveResponseFailure(){
        String body = "text";
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        Mockito.when(mockedGfaService.processLASEDDRetrieveResponse(body)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestStatus(itemInformationResponse)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.rollbackAfterGFA(itemInformationResponse)).thenReturn(itemRequestInformation);
        mockedItemRequestService.processLASEddRetrieveResponse(body);
    }

    @Test
    public void executeLasitemCheck(){
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setSuccess(true);
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        Mockito.when(mockedRequestItemDetailsRepository.findById(itemRequestInfo.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(mockedGfaService.executeRetrieveOrder(itemRequestInfo, itemResponseInformation)).thenReturn(itemResponseInformation);
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_PENDING)).thenReturn(requestStatusEntity);
        mockedItemRequestService.executeLasitemCheck(itemRequestInfo,itemResponseInformation);
    }
   /* @Test
    public void testRequestItem() throws Exception {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        itemRequestInformation.setItemBarcodes(Arrays.asList(bibliographicEntity.getItemEntities().get(0).getBarcode()));
        ItemInformationResponse response = itemRequestService.requestItem(itemRequestInformation, exchange);
        assertNotNull(response);
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123"));
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        requestIds.add(2);
        itemRefileRequest.setRequestIds(requestIds);

        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setSuccess(false);
        itemRefileResponse.setScreenMessage(RecapConstants.REQUEST_ITEM_BARCODE_NOT_FOUND);
        ItemRefileResponse refileResponse = itemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
        assertNotNull(refileResponse);
    }
*/
    public ItemRequestInformation getItemRequestInformation() {
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
        itemInformationResponse.setRequestId(2);
        return itemInformationResponse;
    }

    public BibliographicEntity getBibliographicEntity(){

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        /*InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);
        assertNotNull(entity);*/

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setCatalogingStatus("Complete");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
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
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        /*BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);*/
        return bibliographicEntity;
    }

    public RequestItemEntity createRequestItem() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("University of Chicago");

        BibliographicEntity bibliographicEntity = getBibliographicEntity();

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("REFILE");
        requestStatusEntity.setRequestStatusDescription("REFILE");

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getItemId());
        requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setPatronId("123");
        requestItemEntity.setStopCode("test");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(4);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setNotes("test las:\nrefile request: Placed Successfully");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(getItemEntity());
        requestItemEntity.setInstitutionEntity(institutionEntity);
        return requestItemEntity;
    }

    @Test
    public void removeDia() {
        String input = "[No Restrictions] Afghānistān / |c nivīsandah, Aḥmad Shāh Farzān [RECAP] أَبَنَ فُلانًا: عَابَه ورَمَاه بخَلَّة سَوء.";
        logger.info(input);

        logger.info(input.replaceAll("[^\\p{ASCII}]", ""));

        logger.info(input.replaceAll("[^\\u0000-\\uFFFF]", ""));
        logger.info(input.replaceAll("[^\\x20-\\x7e]", ""));

        String normailzed = Normalizer.normalize(input, Normalizer.Form.NFD);

        logger.info("Normailzed : " + normailzed);
        logger.info(normailzed.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));

        normailzed = Normalizer.normalize(input, Normalizer.Form.NFKD);
        logger.info(normailzed.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));

        logger.info(normailzed.replaceAll("[^\\x20-\\x7e]", ""));

       // logger.info("removeDiacritical: " + itemRequestService.removeDiacritical(input));

        logger.info(Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));


    }


}