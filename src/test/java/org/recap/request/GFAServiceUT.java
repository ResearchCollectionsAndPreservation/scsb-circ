package org.recap.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.util.json.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ScsbCircApplication;
import org.recap.gfa.model.*;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.deaccession.DeAccessionDBResponseEntity;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.util.ItemRequestServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Time;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Created by hemalathas on 21/2/17.
 */
@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScsbCircApplication.class)
public class GFAServiceUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(GFAServiceUT.class);

    @Mock
    GFAService gfaService;

    @Autowired
    GFAService getGfaService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    private ProducerTemplate producer;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Value("${gfa.item.status}")
    private String gfaItemStatus;

    @Value("${gfa.item.retrieval.order}")
    private String gfaItemRetrival;

    @Value("${gfa.item.permanent.withdrawl.direct}")
    private String gfaItemPermanentWithdrawlDirect;

    @Value("${gfa.item.permanent.withdrawl.indirect}")
    private String gfaItemPermanentWithdrawlInDirect;

    @Value("${gfa.item.edd.retrieval.order}")
    private String gfaItemEDDRetrival;

    @Value("${las.use.queue}")
    private boolean useQueueLasCall;

    @Value("${status.reconciliation.batch.size}")
    private Integer batchSize;

    @Value("${status.reconciliation.day.limit}")
    private Integer statusReconciliationDayLimit;

    @Value("${status.reconciliation.las.barcode.limit}")
    private Integer statusReconciliationLasBarcodeLimit;

    @Value("${gfa.server.response.timeout.milliseconds}")
    private Integer gfaServerResponseTimeOutMilliseconds;

    @Mock
    private EmailService emailService;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestServiceUtil itemRequestServicUtil;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGFAService() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        RetrieveItemRequest retrieveItemRequest = new RetrieveItemRequest();
        TtitemRequest ttitemRequest = new TtitemRequest();
        ttitemRequest.setCustomerCode("PB");
        ttitemRequest.setDestination("PUL");
        ttitemRequest.setItemBarcode("123");
        ttitemRequest.setItemStatus("Available");
        retrieveItemRequest.setTtitem(Arrays.asList(ttitemRequest));
        gfaRetrieveItemRequest.setDsitem(retrieveItemRequest);

        RetrieveItem retrieveItem = new RetrieveItem();
        Ttitem ttitem = new Ttitem();
        ttitem.setItemStatus("Success");
        ttitem.setItemBarcode("123456");
        retrieveItem.setTtitem(Arrays.asList(ttitem));
        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(true);
        gfaRetrieveItemResponse.setDsitem(retrieveItem);
        gfaRetrieveItemResponse.setScreenMessage("Success");

        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaRetrieveItemRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemRetrival()).thenReturn(gfaItemRetrival);
        Mockito.when(gfaService.getGfaItemStatus()).thenReturn(gfaItemStatus);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemRetrival(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.itemRetrieval(gfaRetrieveItemRequest)).thenCallRealMethod();
        GFARetrieveItemResponse response = gfaService.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
        assertNotNull(response.getDsitem());
    }
    @Test
    public void testGFAServiceWithTTitemErrorMessage() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        RetrieveItemRequest retrieveItemRequest = new RetrieveItemRequest();
        TtitemRequest ttitemRequest = new TtitemRequest();
        ttitemRequest.setCustomerCode("PB");
        ttitemRequest.setDestination("PUL");
        ttitemRequest.setItemBarcode("123");
        ttitemRequest.setItemStatus("Available");
        retrieveItemRequest.setTtitem(Arrays.asList(ttitemRequest));
        gfaRetrieveItemRequest.setDsitem(retrieveItemRequest);

        RetrieveItem retrieveItem = new RetrieveItem();
        Ttitem ttitem = new Ttitem();
        ttitem.setItemStatus("Failure");
        ttitem.setItemBarcode("123456");
        ttitem.setErrorCode("Bad Request");
        retrieveItem.setTtitem(Arrays.asList(ttitem));
        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        gfaRetrieveItemResponse.setSuccess(true);
        gfaRetrieveItemResponse.setDsitem(retrieveItem);
        gfaRetrieveItemResponse.setScreenMessage("Success");

        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaRetrieveItemRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemRetrival()).thenReturn(gfaItemRetrival);
        Mockito.when(gfaService.getGfaItemStatus()).thenReturn(gfaItemStatus);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemRetrival(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.itemRetrieval(gfaRetrieveItemRequest)).thenCallRealMethod();
        GFARetrieveItemResponse response = gfaService.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
        assertNotNull(response.getDsitem());
    }
    @Test
    public void testGFAServiceWithoutResponse() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        RetrieveItemRequest retrieveItemRequest = new RetrieveItemRequest();
        TtitemRequest ttitemRequest = new TtitemRequest();
        ttitemRequest.setCustomerCode("PB");
        ttitemRequest.setDestination("PUL");
        ttitemRequest.setItemBarcode("123");
        ttitemRequest.setItemStatus("Available");
        retrieveItemRequest.setTtitem(Arrays.asList(ttitemRequest));
        gfaRetrieveItemRequest.setDsitem(retrieveItemRequest);
        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaRetrieveItemRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemRetrival()).thenReturn(gfaItemRetrival);
        Mockito.when(gfaService.getGfaItemStatus()).thenReturn(gfaItemStatus);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemRetrival(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.itemRetrieval(gfaRetrieveItemRequest)).thenCallRealMethod();
        GFARetrieveItemResponse response = gfaService.itemRetrieval(gfaRetrieveItemRequest);
    }
    @Test
    public void testGFAServiceException() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        RetrieveItemRequest retrieveItemRequest = new RetrieveItemRequest();
        TtitemRequest ttitemRequest = new TtitemRequest();
        retrieveItemRequest.setTtitem(Arrays.asList(ttitemRequest));
        gfaRetrieveItemRequest.setDsitem(retrieveItemRequest);
        HttpEntity requestEntity = new HttpEntity(gfaRetrieveItemRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemRetrival()).thenReturn(gfaItemRetrival);
        Mockito.when(gfaService.getGfaItemStatus()).thenReturn(gfaItemStatus);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemRetrival(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(null);
        Mockito.when(gfaService.itemRetrieval(gfaRetrieveItemRequest)).thenCallRealMethod();
        GFARetrieveItemResponse response = gfaService.itemRetrieval(gfaRetrieveItemRequest);
    }
    @Test
    public void testGFAServiceWithBadRequest() {
        GFARetrieveItemRequest gfaRetrieveItemRequest =null;
        RetrieveItemRequest retrieveItemRequest = new RetrieveItemRequest();
        TtitemRequest ttitemRequest = new TtitemRequest();
        retrieveItemRequest.setTtitem(Arrays.asList(ttitemRequest));
        HttpEntity requestEntity = new HttpEntity(null, getHttpHeaders());
        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.BAD_REQUEST);
        Mockito.when(gfaService.getGfaItemRetrival()).thenReturn(gfaItemRetrival);
        Mockito.when(gfaService.getGfaItemStatus()).thenReturn(gfaItemStatus);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemRetrival(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.itemRetrieval(gfaRetrieveItemRequest)).thenCallRealMethod();
        GFARetrieveItemResponse response = gfaService.itemRetrieval(gfaRetrieveItemRequest);
    }
    @Test
    public void buildRequestInfoAndReplaceToLASForRETRIEVAL(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestTypeEntity requestTypeEntity = getRequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RETRIEVAL");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.LAS_REFILE_REQUEST_PLACED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(gfaService.buildRequestInfoAndReplaceToLAS(requestItemEntity)).thenCallRealMethod();
        gfaService.buildRequestInfoAndReplaceToLAS(requestItemEntity);
    }
    @Test
    public void buildRequestInfoAndReplaceToLASForEDD(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestTypeEntity requestTypeEntity = getRequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        searchResultRow.setTitle("TEST");
        Mockito.doCallRealMethod().when(itemRequestServiceUtil).setEddInfoToGfaRequest(any(), any());
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        Mockito.when(itemRequestService.getTitle(anyString(), any(), any())).thenReturn("Title Of the Book");
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.LAS_REFILE_REQUEST_PLACED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(gfaService.buildRequestInfoAndReplaceToLAS(requestItemEntity)).thenCallRealMethod();
        gfaService.buildRequestInfoAndReplaceToLAS(requestItemEntity);
    }

    @Test
    public void processLASEDDRetrieveResponse() throws JsonProcessingException {
        RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
        JsonObject object = new JsonObject();
        String body = object.toString() ;
        GFAEddItemResponse gfaEddItemResponse = new GFAEddItemResponse();
        gfaEddItemResponse.setScreenMessage("Success");
        gfaEddItemResponse.setSuccess(true);
        TtitemEDDResponse ttitemEDDResponse = new TtitemEDDResponse();
        ttitemEDDResponse.setCustomerCode("123456");
        ttitemEDDResponse.setRequestId(1);
        retrieveItemEDDRequest.setTtitem(Arrays.asList(ttitemEDDResponse));
        gfaEddItemResponse.setDsitem(retrieveItemEDDRequest);
        Mockito.when(objectMapper.readValue(body, GFAEddItemResponse.class)).thenReturn(new GFAEddItemResponse());
        Mockito.when(gfaService.processLASEDDRetrieveResponse(body)).thenCallRealMethod();
        gfaService.processLASEDDRetrieveResponse(body);
    }
    private RequestItemEntity getRequestItemEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        ItemEntity itemEntity = getItemEntity();
        RequestStatusEntity requestStatusEntity =  new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL ORDER PLACED");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(itemEntity);
        return requestItemEntity;
    }
    private ItemEntity getItemEntity(){
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
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);
        return itemEntity;
    }
    @Test
    public void checkGetterServices() {
        Mockito.when(gfaService.getGFARetrieveEDDItemRequest()).thenCallRealMethod();
        Mockito.when(gfaService.getItemChangeLogDetailsRepository()).thenCallRealMethod();
        Mockito.when(gfaService.getItemDetailsRepository()).thenCallRealMethod();
        Mockito.when(gfaService.getItemStatusDetailsRepository()).thenCallRealMethod();
        Mockito.when(gfaService.getRestTemplate()).thenCallRealMethod();
        Mockito.when(gfaService.getGfaItemEDDRetrival()).thenCallRealMethod();
        Mockito.when(gfaService.getGfaItemPermanentWithdrawlDirect()).thenCallRealMethod();
        Mockito.when(gfaService.getGfaItemPermanentWithdrawlInDirect()).thenCallRealMethod();
        Mockito.when(gfaService.getProducer()).thenCallRealMethod();
        Mockito.when(gfaService.getObjectMapper()).thenCallRealMethod();
        Mockito.when(gfaService.isUseQueueLasCall()).thenCallRealMethod();
        Mockito.when(gfaService.getGfaServerResponseTimeOutMilliseconds()).thenCallRealMethod();
        Mockito.when(gfaService.getGfaItemStatus()).thenCallRealMethod();
        Mockito.when(gfaService.getGfaItemRetrival()).thenCallRealMethod();

        assertNotEquals(gfaService.getGFARetrieveEDDItemRequest(), gfaRetrieveEDDItemRequest);
        assertNotEquals(gfaService.getItemChangeLogDetailsRepository(), itemChangeLogDetailsRepository);
        assertNotEquals(gfaService.getItemDetailsRepository(), itemDetailsRepository);
        assertNotEquals(gfaService.getItemStatusDetailsRepository(), itemStatusDetailsRepository);
        assertNotEquals(gfaService.getRestTemplate(), restTemplate);
        assertNotEquals(gfaService.getGfaItemEDDRetrival(), gfaItemEDDRetrival);
        assertNotEquals(gfaService.getGfaItemPermanentWithdrawlDirect(), gfaItemPermanentWithdrawlDirect);
        assertNotEquals(gfaService.getGfaItemPermanentWithdrawlInDirect(), gfaItemPermanentWithdrawlInDirect);
        assertNotEquals(gfaService.getProducer(), producer);
        assertNotEquals(gfaService.getObjectMapper(), objectMapper);

    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    public void testGfaPWD() {
        GFAPwdRequest gfaPwdRequest = new GFAPwdRequest();
        GFAPwdDsItemRequest gfaPwdDsItemRequest = new GFAPwdDsItemRequest();
        GFAPwdTtItemRequest gfaPwdTtItemRequest = new GFAPwdTtItemRequest();
        gfaPwdTtItemRequest.setCustomerCode("AR");
        gfaPwdTtItemRequest.setItemBarcode("AR00000612");
        gfaPwdTtItemRequest.setDestination("AR");
        gfaPwdTtItemRequest.setRequestor("Dev Tesr");
        gfaPwdDsItemRequest.setTtitem(Arrays.asList(gfaPwdTtItemRequest));
        gfaPwdRequest.setDsitem(gfaPwdDsItemRequest);

        GFAPwdResponse gfaPwdResponse = new GFAPwdResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse = getGFAPwdDsItemResponse();
        assertNotNull(gfaPwdDsItemResponse.getProdsBefore());
        assertNotNull(gfaPwdDsItemResponse.getTtitem());
        assertNotNull(gfaPwdDsItemResponse.getProdsHasChanges());
        gfaPwdResponse.setDsitem(gfaPwdDsItemResponse);

        ResponseEntity<GFAPwdResponse> responseEntity = new ResponseEntity(gfaPwdResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaPwdRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemPermanentWithdrawlDirect()).thenReturn(gfaItemPermanentWithdrawlDirect);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemPermanentWithdrawlDirect(), HttpMethod.POST, requestEntity, GFAPwdResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.gfaPermanentWithdrawlDirect(gfaPwdRequest)).thenCallRealMethod();
        GFAPwdResponse response = gfaService.gfaPermanentWithdrawlDirect(gfaPwdRequest);
        assertNull(response);
        //assertNotNull(response.getDsitem());
    }

    @Test
    public void testbuildRequestInfoAndReplaceToLAS() {
        BibliographicEntity bibliographicEntity = null;
        try {
            bibliographicEntity = getBibliographicEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setId(1);
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");
//        RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        //       assertNotNull(savedRequestTypeEntity);

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
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        RequestTypeEntity requestTypeEntityNew = getRequestTypeEntity();
        requestItemEntity.setRequestTypeEntity(requestTypeEntityNew);
        String res = getGfaService.buildRequestInfoAndReplaceToLAS(requestItemEntity);
        assertNotNull(res);
        ItemInformationResponse itemInformationResponse = getGfaService.processLASEDDRetrieveResponse("test");
        ItemInformationResponse itemInformationResponseNew = getGfaService.processLASRetrieveResponse("test");
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        GFAPwdRequest gfaPwdRequest = new GFAPwdRequest();
        GFAPwdDsItemRequest gfaPwdDsItemRequest = new GFAPwdDsItemRequest();
        GFAPwdTtItemRequest gfaPwdTtItemRequest = new GFAPwdTtItemRequest();
        gfaPwdTtItemRequest.setCustomerCode(deAccessionDBResponseEntity.getCustomerCode());
        gfaPwdTtItemRequest.setItemBarcode(deAccessionDBResponseEntity.getBarcode());
        gfaPwdTtItemRequest.setDestination(deAccessionDBResponseEntity.getDeliveryLocation());
        gfaPwdTtItemRequest.setRequestor("test");
        gfaPwdDsItemRequest.setTtitem(Arrays.asList(gfaPwdTtItemRequest));
        gfaPwdRequest.setDsitem(gfaPwdDsItemRequest);
        GFAPwdResponse gfaPwdResponse = getGfaService.gfaPermanentWithdrawlDirect(gfaPwdRequest);
        ItemInformationResponse responseitemInformationResponse = getGfaService.executeRetrieveOrder(getItemRequestInformation(), getItemInformationResponse());
        TtitemEDDResponse ttitemEDDResponse = new TtitemEDDResponse();
        ttitemEDDResponse.setItemBarcode("332445645758458");
        ttitemEDDResponse.setCustomerCode("AD");
        ttitemEDDResponse.setRequestId(1);
        ttitemEDDResponse.setRequestor("Test");
        ttitemEDDResponse.setRequestorFirstName("test");
        ttitemEDDResponse.setRequestorLastName("test");
        ttitemEDDResponse.setRequestorMiddleName("test");
        ttitemEDDResponse.setRequestorEmail("hemalatha.s@htcindia.com");
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

        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = new GFARetrieveEDDItemRequest();
        gfaRetrieveEDDItemRequest.setDsitem(retrieveItemEDDRequest);
        GFAEddItemResponse gfaEddItemResponse = getGfaService.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(2);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        //InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);
        assertNotNull(institutionEntity);

        Random random = new Random();
        //BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(institutionEntity.getId());
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
        List<ItemEntity> list = new ArrayList<ItemEntity>();
        list.add(itemEntity);
        List<List<ItemEntity>> listList = new ArrayList<List<ItemEntity>>();
        listList.add(list);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaService.getGFAItemStatusCheckResponse(list);
        String testres = getGfaService.callGfaItemStatus("33245645454584");
        //getGfaService.startPolling("33245645454584");
        assertNull(testres);
    }
    private RequestTypeEntity getRequestTypeEntity() {
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setId(1);
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        return requestTypeEntity;
    }
    public BibliographicEntity getBibliographicEntity() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
//        InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);
        //       assertNotNull(entity);

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(institutionEntity.getId());
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

//        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        //     entityManager.refresh(savedBibliographicEntity);
        return bibliographicEntity;
    }

    @Test
    public void testcallItemEDDRetrivate() throws JsonProcessingException {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        Mockito.when(gfaService.isUseQueueLasCall()).thenReturn(true);
        Mockito.when(gfaService.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(gfaService.getObjectMapper().writeValueAsString(gfaRetrieveEDDItemRequest)).thenReturn("test");
        Mockito.when(gfaService.getGFARetrieveEDDItemRequest()).thenReturn(gfaRetrieveEDDItemRequest);
        Mockito.when(gfaService.getProducer()).thenReturn(producer);
        Mockito.when(gfaService.callItemEDDRetrievable(itemRequestInformation, itemInformationResponse)).thenCallRealMethod();
        ItemInformationResponse response = gfaService.callItemEDDRetrievable(itemRequestInformation, itemInformationResponse);
        assertNotNull(response);
        assertEquals(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL, response.getScreenMessage());

    }

    @Test
    public void testGfaPWI() {
        GFAPwiRequest gfaPwiRequest = new GFAPwiRequest();
        GFAPwiDsItemRequest gfaPwiDsItemRequest = new GFAPwiDsItemRequest();
        GFAPwiTtItemRequest gfaPwiTtItemRequest = new GFAPwiTtItemRequest();
        gfaPwiTtItemRequest.setCustomerCode("AR");
        gfaPwiTtItemRequest.setItemBarcode("AR00051608");
        gfaPwiDsItemRequest.setTtitem(Arrays.asList(gfaPwiTtItemRequest));
        gfaPwiRequest.setDsitem(gfaPwiDsItemRequest);

        GFAPwiResponse gfaPwiResponse = new GFAPwiResponse();
        GFAPwiDsItemResponse gfaPwiDsItemResponse = getGFAPwiDsItemResponse();

        assertNotNull(gfaPwiDsItemResponse.getProdsBefore());
        assertNotNull(gfaPwiDsItemResponse.getProdsHasChanges());
        assertNotNull(gfaPwiDsItemResponse.getTtitem());
        gfaPwiResponse.setDsitem(gfaPwiDsItemResponse);
        assertNotNull(gfaPwiResponse.getDsitem());

        ResponseEntity<GFAPwiResponse> responseEntity = new ResponseEntity(gfaPwiResponse, HttpStatus.OK);
        HttpEntity requestEntity = new HttpEntity(gfaPwiRequest, getHttpHeaders());
        Mockito.when(gfaService.getGfaItemPermanentWithdrawlInDirect()).thenReturn(gfaItemPermanentWithdrawlInDirect);
        Mockito.when(gfaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaService.getRestTemplate().exchange(gfaService.getGfaItemPermanentWithdrawlInDirect(), HttpMethod.POST, requestEntity, GFAPwiResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaService.gfaPermanentWithdrawlInDirect(gfaPwiRequest)).thenCallRealMethod();
        GFAPwiResponse response = gfaService.gfaPermanentWithdrawlInDirect(gfaPwiRequest);
//        assertNotNull(response);
        //      assertNotNull(response.getDsitem());
        assertTrue(true);
    }

    @Test
    public void testGFAPwdTtItemResponse() {
        GFAPwdTtItemResponse gfaPwdTtItemResponse = getGFAPwdTtItemResponse();
        assertNotNull(gfaPwdTtItemResponse.getCustomerCode());
        assertNotNull(gfaPwdTtItemResponse.getItemBarcode());
        assertNotNull(gfaPwdTtItemResponse.getDestination());
        assertNotNull(gfaPwdTtItemResponse.getDeliveryMethod());
        assertNotNull(gfaPwdTtItemResponse.getRequestor());
        assertNotNull(gfaPwdTtItemResponse.getRequestorFirstName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorLastName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorMiddleName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorEmail());
        assertNotNull(gfaPwdTtItemResponse.getRequestorOther());
        assertNotNull(gfaPwdTtItemResponse.getPriority());
        assertNotNull(gfaPwdTtItemResponse.getNotes());
        assertNotNull(gfaPwdTtItemResponse.getRequestDate());
        assertNotNull(gfaPwdTtItemResponse.getRequestTime());
        assertNotNull(gfaPwdTtItemResponse.getErrorCode());
        assertNotNull(gfaPwdTtItemResponse.getErrorNote());
    }

    @Test
    public void testGfaEddItemResponse() {

    }

    public GFAPwdTtItemResponse getGFAPwdTtItemResponse() {
        GFAPwdTtItemResponse gfaPwdTtItemResponse = new GFAPwdTtItemResponse();
        gfaPwdTtItemResponse.setCustomerCode("PB");
        gfaPwdTtItemResponse.setItemBarcode("231365");
        gfaPwdTtItemResponse.setDestination("test");
        gfaPwdTtItemResponse.setDeliveryMethod("test");
        gfaPwdTtItemResponse.setRequestor("test");
        gfaPwdTtItemResponse.setRequestorFirstName("test");
        gfaPwdTtItemResponse.setRequestorLastName("test");
        gfaPwdTtItemResponse.setRequestorMiddleName("test");
        gfaPwdTtItemResponse.setRequestorEmail("hemalatha.s@htcindia.com");
        gfaPwdTtItemResponse.setRequestorOther("test");
        gfaPwdTtItemResponse.setPriority("first");
        gfaPwdTtItemResponse.setNotes("test");
        gfaPwdTtItemResponse.setRequestDate(new Date());
        gfaPwdTtItemResponse.setRequestTime(new Time(new Long(10)));
        gfaPwdTtItemResponse.setErrorCode("test");
        gfaPwdTtItemResponse.setErrorNote("test");
        return gfaPwdTtItemResponse;
    }

    public GFAPwdDsItemResponse getGFAPwdDsItemResponse() {
        GFAPwdTtItemResponse gfaPwdTtItemResponse = getGFAPwdTtItemResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(gfaPwdTtItemResponse));
        gfaPwdDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwdDsItemResponse.setProdsHasChanges(true);
        return gfaPwdDsItemResponse;
    }

    @Test
    public void testGFAPwiTtItemResponse() {
        GFAPwiTtItemResponse gfaPwiTtItemResponse = getGFAPwiTtItemResponse();
        assertNotNull(gfaPwiTtItemResponse.getCustomerCode());
        assertNotNull(gfaPwiTtItemResponse.getErrorCode());
        assertNotNull(gfaPwiTtItemResponse.getErrorNote());
        assertNotNull(gfaPwiTtItemResponse.getItemBarcode());
    }

    public GFAPwiTtItemResponse getGFAPwiTtItemResponse() {
        GFAPwiTtItemResponse gfaPwiTtItemResponse = new GFAPwiTtItemResponse();
        gfaPwiTtItemResponse.setCustomerCode("PB");
        gfaPwiTtItemResponse.setErrorCode("test");
        gfaPwiTtItemResponse.setErrorNote("test");
        gfaPwiTtItemResponse.setItemBarcode("336985245642355");
        return gfaPwiTtItemResponse;
    }

    public GFAPwiDsItemResponse getGFAPwiDsItemResponse() {
        GFAPwiDsItemResponse gfaPwiDsItemResponse = new GFAPwiDsItemResponse();
        gfaPwiDsItemResponse.setTtitem(Arrays.asList(getGFAPwiTtItemResponse()));
        gfaPwiDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwiDsItemResponse.setProdsHasChanges(true);
        return gfaPwiDsItemResponse;
    }

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
        itemRequestInformation.setChapterTitle("test");
        itemRequestInformation.setVolume("5");
        itemRequestInformation.setIssue("test");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setStartPage("10");
        itemRequestInformation.setEndPage("100");

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

    @Test
    public void testBuildingEddInfoForGFAFromRequestNotes() throws Exception {
        String notes = "User: 1\n" +
                "\n" +
                "Start Page: 1 \n" +
                "End Page: 2 \n" +
                "Volume Number: 3 \n" +
                "Issue: 4 \n" +
                "Article Author: author \n" +
                "Article/Chapter Title: title";

        TtitemEDDResponse ttitem001 = new TtitemEDDResponse();
        new BufferedReader(new StringReader(notes)).lines().forEach(line -> itemRequestServicUtil.setEddInfoToGfaRequest(line, ttitem001));
        assertNotNull(ttitem001);
       /* assertEquals(null, ttitem001.getStartPage());
        assertEquals("2", ttitem001.getEndPage());
        assertEquals("3", ttitem001.getArticleVolume());
        assertEquals("4", ttitem001.getArticleIssue());
        assertEquals("author", ttitem001.getArticleAuthor());
        assertEquals("title", ttitem001.getArticleTitle());*/
    }

    @Test
    public void startPolling(){
        String barcode = "25464";
        Mockito.doCallRealMethod().when(gfaService).startPolling(barcode);
        gfaService.startPolling(barcode);
    }

    @Test
    public void callGfaItemStatus(){
        String itemBarcode ="1346673";
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        Mockito.when(gfaService.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        Mockito.when(gfaService.callGfaItemStatus(itemBarcode)).thenCallRealMethod();
        String gfaItemStatusValue = gfaService.callGfaItemStatus(itemBarcode);
        assertNotNull(gfaItemStatusValue);
    }

    @Test
    public void heartBeatCheck(){
        GFALasStatusCheckRequest gfaLasStatusCheckRequest = new GFALasStatusCheckRequest();
        gfaLasStatusCheckRequest.setLasStatus(new ArrayList<>());
        getGfaService.heartBeatCheck(gfaLasStatusCheckRequest);
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

    @Test
    public void callGfaDeaccessionServiceWithAvailableStatus(){
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setItemStatus(RecapCommonConstants.AVAILABLE);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        GFAPwdResponse gfaPwdResponse = getGfaPwdResponse();
        Mockito.when(gfaService.gfaPermanentWithdrawlDirect(any())).thenReturn(gfaPwdResponse);
        Mockito.doCallRealMethod().when(gfaService).callGfaDeaccessionService(deAccessionDBResponseEntities,username);
        gfaService.callGfaDeaccessionService(deAccessionDBResponseEntities,username);
    }

    private GFAPwdResponse getGfaPwdResponse() {
        GFAPwdResponse gfaPwdResponse = new GFAPwdResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        GFAPwdTtItemResponse gfaPwdTtItemResponse = new GFAPwdTtItemResponse();
        gfaPwdTtItemResponse.setErrorNote("ErrorNote");
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(gfaPwdTtItemResponse));
        gfaPwdResponse.setDsitem(gfaPwdDsItemResponse);
        return gfaPwdResponse;
    }

    @Test
    public void callGfaDeaccessionServiceWithAvailableStatusWithoutResponse(){
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setItemStatus(RecapCommonConstants.AVAILABLE);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        Mockito.doCallRealMethod().when(gfaService).callGfaDeaccessionService(deAccessionDBResponseEntities,username);
        gfaService.callGfaDeaccessionService(deAccessionDBResponseEntities,username);
    }
    @Test
    public void callGfaDeaccessionServiceWithNotAvailableStatusWithoutResponse(){
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setItemStatus(RecapCommonConstants.NOT_AVAILABLE);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        Mockito.doCallRealMethod().when(gfaService).callGfaDeaccessionService(deAccessionDBResponseEntities,username);
        gfaService.callGfaDeaccessionService(deAccessionDBResponseEntities,username);
    }
    @Test
    public void callGfaDeaccessionServiceWithNotAvailableStatus(){
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setItemStatus(RecapCommonConstants.NOT_AVAILABLE);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        GFAPwiResponse gfaPwiResponse = getGfaPwiResponse();
        Mockito.when(gfaService.gfaPermanentWithdrawlInDirect(any())).thenReturn(gfaPwiResponse);
        Mockito.doCallRealMethod().when(gfaService).callGfaDeaccessionService(deAccessionDBResponseEntities,username);
        gfaService.callGfaDeaccessionService(deAccessionDBResponseEntities,username);
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
}