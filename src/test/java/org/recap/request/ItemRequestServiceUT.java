package org.recap.request;

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
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.RequestItemController;
import org.recap.ils.AbstractProtocolConnector;
import org.recap.ils.ILSProtocolConnectorFactory;
import org.recap.ils.model.response.ItemCheckinResponse;
import org.recap.ils.model.response.ItemCreateBibResponse;
import org.recap.ils.model.response.ItemHoldResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.model.response.ItemRecallResponse;
import org.recap.las.GFALasService;
import org.recap.model.ItemRefileRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.GenericPatronEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.OwnerCodeEntity;
import org.recap.model.jpa.ReplaceRequest;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.model.jpa.SearchResultRow;
import org.recap.repository.jpa.GenericPatronDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.service.RestHeaderService;
import org.recap.util.CommonUtil;
import org.recap.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;
import org.recap.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class ItemRequestServiceUT extends BaseTestCaseUT {


    /**
     * Created by hemalathas on 20/3/17.
     */
    private String scsbSolrClientUrl = "http://localhost:8161/jmxrmi";

    private static final Logger logger = LoggerFactory.getLogger(ItemRequestServiceUT.class);

    @InjectMocks
    ItemRequestService mockedItemRequestService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    AbstractProtocolConnector abstractProtocolConnector;

    @Mock
    GenericPatronDetailsRepository genericPatronDetailsRepository;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    private ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Mock
    Exchange exchange;

    @Mock
    PropertyUtil propertyUtil;

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
    private GFALasService mockedGfaLasService;

    @Mock
    private ItemRequestDBService mockedItemRequestDBService;

    @Mock
    private OwnerCodeDetailsRepository mockedOwnerCodeDetailsRepository;

    @Mock
    private ItemStatusDetailsRepository mockedItemStatusDetailsRepository;

    @Mock
    private RestHeaderService mockedRestHeaderService;

    @Mock
    private ItemRequestServiceUtil mockedItemRequestServiceUtil;

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


    @Before
    public void setUp() {
        ReflectionTestUtils.setField(mockedItemRequestService, "scsbSolrClientUrl", scsbSolrClientUrl);
    }

    @Test
    public void checkGetters() {
        mockedItemRequestService.getEmailService();
        mockedItemRequestService.getGfaLasService();
    }

    @Test
    public void testRequestItem() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestType("RECALL");
        ItemEntity itemEntity = getItemEntity();
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        itemResponseInformation.setScreenMessage("LAS Exception :");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(itemRequestInfo.getDeliveryLocation())).thenReturn(ownerCodeEntity);
        Mockito.when(mockedItemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.when(mockedItemDetailsRepository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        Mockito.doNothing().when(mockedItemRequestDBService).updateItemAvailabilityStatus(Arrays.asList(itemEntity), itemRequestInfo.getUsername());
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInfo, itemEntity, ScsbConstants.REQUEST_STATUS_PROCESSING, null)).thenReturn(1);
        Mockito.when(mockedGfaLasService.isUseQueueLasCall(any())).thenReturn(true);
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInformationResponse = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse);
        itemRequestInfo.setItemOwningInstitution("test");
        itemHoldResponse.setSuccess(false);
        ItemInformationResponse itemInformationResponse2 = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse2);
    }

    @Test
    public void testRequestItemDifferentId() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("CUL");
        ItemEntity itemEntity = getItemEntity();
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(itemRequestInfo.getDeliveryLocation())).thenReturn(ownerCodeEntity);
        Mockito.when(mockedItemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.when(mockedItemDetailsRepository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        Mockito.doNothing().when(mockedItemRequestDBService).updateItemAvailabilityStatus(Arrays.asList(itemEntity), itemRequestInfo.getUsername());
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInfo, itemEntity, ScsbConstants.REQUEST_STATUS_PROCESSING, null)).thenReturn(1);
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenReturn(itemResponseInformation);
        Mockito.when(mockedGfaLasService.isUseQueueLasCall(any())).thenReturn(true);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInfo.getRequestingInstitution(), "use.generic.patron.retrieval.cross")).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInformationResponse = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse);
        itemRequestInfo.setItemOwningInstitution("test");
        itemHoldResponse.setSuccess(false);
    }

    @Test
    public void testRequestItemDifferentIdInnerException() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("CUL");
        ItemEntity itemEntity = getItemEntity();
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(itemRequestInfo.getDeliveryLocation())).thenReturn(ownerCodeEntity);
        Mockito.when(mockedItemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.when(mockedItemDetailsRepository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        Mockito.doNothing().when(mockedItemRequestDBService).updateItemAvailabilityStatus(Arrays.asList(itemEntity), itemRequestInfo.getUsername());
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInfo, itemEntity, ScsbConstants.REQUEST_STATUS_PROCESSING, null)).thenReturn(1);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInformationResponse = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse);
        itemRequestInfo.setItemOwningInstitution("test");
        itemHoldResponse.setSuccess(false);
    }

    @Test
    public void testRequestItemRestClientException() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(itemRequestInfo.getDeliveryLocation())).thenThrow(new RestClientException("Bad Request"));
        ItemInformationResponse itemInformationResponse = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse);
    }

    @Test
    public void testRequestItemWithoutId() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setItemAvailabilityStatusId(1);
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        ItemInformationResponse itemInformationResponse = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(itemRequestInfo.getDeliveryLocation())).thenReturn(ownerCodeEntity);
        Mockito.when(mockedItemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.when(mockedItemDetailsRepository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        Mockito.doNothing().when(mockedCommonUtil).rollbackUpdateItemAvailabilityStatus(itemEntity, itemRequestInfo.getUsername());
        ItemInformationResponse itemInformationResponse1 = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse1);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInfo, itemEntity, ScsbConstants.REQUEST_STATUS_PROCESSING, null)).thenReturn(1);
        ItemInformationResponse itemInformationResponse2 = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse2);
    }

    @Test
    public void testRequestItemWithId() throws Exception {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("CUL");
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setItemAvailabilityStatusId(2);
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(false);
        itemResponseInformation.setScreenMessage("\"LAS Exception : Item not available in LAS");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        ItemCreateBibResponse itemCreateBibResponse = getItemCreateBibResponse();
        itemCreateBibResponse.setSuccess(false);
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInfo, itemEntity, ScsbConstants.REQUEST_STATUS_PROCESSING, null)).thenReturn(1);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(itemRequestInfo.getDeliveryLocation())).thenReturn(ownerCodeEntity);
        Mockito.when(mockedItemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.when(mockedItemDetailsRepository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenReturn(itemResponseInformation);
        ItemInformationResponse itemInformationResponse1 = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse1);
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        ItemInformationResponse itemInformationResponse4 = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse4);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInformationResponse2 = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse2);
        Mockito.when(mockedRequestItemController.createBibliogrphicItem(any(), any())).thenReturn(itemCreateBibResponse);
        ItemInformationResponse itemInformationResponse3 = mockedItemRequestService.requestItem(itemRequestInfo, exchange);
        assertNotNull(itemInformationResponse3);
    }

    @Test
    public void reFileItem() {
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
        List<String> requestItemStatusList = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), anyString())).thenReturn("FALSE");
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(mockedGfaLasService.callGfaItemStatus(itemEntity.getBarcode())).thenReturn("REFILED SUCCESSFULLY");
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        Mockito.doNothing().when(mockedItemRequestServiceUtil).updateSolrIndex(itemEntity);
        ItemRefileResponse response = mockedItemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
        assertNotNull(response);
    }

    @Test
    public void reFileItemForRefile() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        RequestItemEntity requestItemEntity = createRequestItem();
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("REFILE");
        requestTypeEntity.setRequestTypeDesc("REFILE");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        itemEntity.setItemAvailabilityStatusId(2);
        String itemBarcode = itemEntity.getBarcode();
        List<String> requestItemStatusList = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), anyString())).thenReturn("FALSE");
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        Mockito.doNothing().when(mockedItemRequestServiceUtil).updateSolrIndex(itemEntity);
        ItemRefileResponse response = mockedItemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
        assertNotNull(response);
    }

    @Test
    public void reFileItemWithDifferentRequestingId() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        RequestItemEntity requestItemEntity = createRequestItem();
        RequestItemEntity requestItemEntityRecalled = createRequestItem();
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RECALL");
        requestTypeEntity.setRequestTypeDesc("RECALL");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        itemEntity.setItemAvailabilityStatusId(2);
        String itemBarcode = itemEntity.getBarcode();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        List<String> requestItemStatusList = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn("FALSE");
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(mockedGfaLasService.callGfaItemStatus(itemEntity.getBarcode())).thenReturn("REFILED SUCCESSFULLY");
        requestItemEntityRecalled.setRequestingInstitutionId(3);
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntityRecalled);
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenReturn(itemResponseInformation);
        ItemRefileResponse response = mockedItemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
        assertNotNull(response);
    }

    @Test
    public void reFileItemWithDifferentRequestingIdForEDD() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        RequestItemEntity requestItemEntity = createRequestItem();
        RequestItemEntity requestItemEntityRecalled = createRequestItem();
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        itemEntity.setItemAvailabilityStatusId(2);
        String itemBarcode = itemEntity.getBarcode();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        List<String> requestItemStatusList = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(anyString(), anyString())).thenReturn("FALSE");
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        requestItemEntityRecalled.setRequestingInstitutionId(3);
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntityRecalled);
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenReturn(itemResponseInformation);
        ItemRefileResponse response = mockedItemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
        assertNotNull(response);
    }

    @Test
    public void reFileItemNotRecalled() {
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
        GenericPatronEntity genericPatronEntity = getGenericPatronEntity();
        String itemBarcode = itemEntity.getBarcode();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setItemBarcodes(Collections.singletonList(itemBarcode));
        itemRequestInfo.setItemOwningInstitution(requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
        itemRequestInfo.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
        itemRequestInfo.setRequestType(requestItemEntity.getRequestTypeEntity().getRequestTypeCode());

        List<String> requestItemStatusList = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        Mockito.when(mockedRequestItemController.getIlsProtocolConnectorFactory()).thenReturn(ilsProtocolConnectorFactory);
        Mockito.when(mockedRequestItemController.getIlsProtocolConnectorFactory().getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_REFILED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.when(mockedGfaLasService.callGfaItemStatus(itemEntity.getBarcode())).thenReturn("REFILED SUCCESSFULLY");
        Mockito.when(genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(any(), any())).thenReturn(genericPatronEntity);
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(null);
        Mockito.doNothing().when(mockedItemRequestServiceUtil).updateSolrIndex(itemEntity);
        mockedItemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
    }

    private GenericPatronEntity getGenericPatronEntity() {
        GenericPatronEntity genericPatronEntity = new GenericPatronEntity();
        genericPatronEntity.setGenericPatronId(1);
        genericPatronEntity.setEddGenericPatron("edd");
        genericPatronEntity.setCreatedBy("test");
        genericPatronEntity.setCreatedDate(new Date());
        genericPatronEntity.setRetrievalGenericPatron("retrieve");
        genericPatronEntity.setItemOwningInstitutionId(1);
        genericPatronEntity.setOwningInstitutionEntity(new InstitutionEntity());
        genericPatronEntity.setRequestingInstitutionEntity(new InstitutionEntity());
        genericPatronEntity.setRequestingInstitutionId(1);
        genericPatronEntity.setUpdatedBy("test");
        genericPatronEntity.setUpdatedDate(new Date());
        return genericPatronEntity;
    }

    @Test
    public void refileItemWithoutRequestEntities() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setRequestTypeForScheduledOnWO(true);
        itemResponseInformation.setSuccess(true);
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        String itemBarcode = itemEntity.getBarcode();
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("LAS_REFILE_REQUEST_PLACED");
        requestStatusEntity.setRequestStatusDescription("LAS_REFILE_REQUEST_PLACED");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation(requestItemEntity, itemEntity, itemBarcode);
        Mockito.when(mockedGfaLasService.callGfaItemStatus(itemEntity.getBarcode())).thenReturn("IN");
        List<String> requestItemStatusList = Arrays.asList(ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, ScsbCommonConstants.REQUEST_STATUS_EDD, ScsbCommonConstants.REQUEST_STATUS_CANCELED, ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(mockedCommonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), true)).thenReturn(Boolean.TRUE);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList)).thenReturn(null);
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes())).thenReturn(Arrays.asList(requestItemEntity));
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        searchResultRow.setTitle("TEST");
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenReturn(itemResponseInformation);
        ItemRefileResponse refileResponse = mockedItemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
        assertNotNull(refileResponse);
    }

    private ItemRequestInformation getItemRequestInformation(RequestItemEntity requestItemEntity, ItemEntity itemEntity, String itemBarcode) {
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
        return itemRequestInfo;
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
    public void updateChangesToDb() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestId(1);
        itemInformationResponse.setUsername("test");
        Mockito.when(mockedCommonUtil.getUser(itemInformationResponse.getUsername())).thenReturn("1");
        Mockito.doNothing().when(mockedCommonUtil).saveItemChangeLogEntity(itemInformationResponse.getRequestId(), "1", "RECALL", itemInformationResponse.getRequestNotes());
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemInformationResponse)).thenReturn(itemInformationResponse);
        mockedItemRequestService.updateChangesToDb(itemInformationResponse, "RECALL");
    }

    @Test
    public void sendMessageToTopic() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.PRINCETON, ScsbCommonConstants.REQUEST_TYPE_RETRIEVAL, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.COLUMBIA, ScsbCommonConstants.REQUEST_TYPE_RETRIEVAL, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.NYPL, ScsbCommonConstants.REQUEST_TYPE_RETRIEVAL, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.PRINCETON, ScsbCommonConstants.REQUEST_TYPE_EDD, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.COLUMBIA, ScsbCommonConstants.REQUEST_TYPE_EDD, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.NYPL, ScsbCommonConstants.REQUEST_TYPE_EDD, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.PRINCETON, ScsbCommonConstants.REQUEST_TYPE_RECALL, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.COLUMBIA, ScsbCommonConstants.REQUEST_TYPE_RECALL, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.NYPL, ScsbCommonConstants.REQUEST_TYPE_RECALL, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.PRINCETON, ScsbCommonConstants.REQUEST_TYPE_BORROW_DIRECT, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.COLUMBIA, ScsbCommonConstants.REQUEST_TYPE_BORROW_DIRECT, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
        try {
            mockedItemRequestService.sendMessageToTopic(ScsbCommonConstants.NYPL, ScsbCommonConstants.REQUEST_TYPE_BORROW_DIRECT, itemInformationResponse, exchange);
        } catch (Exception e) {
        }
    }

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
        itemEntity.setId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setItemAvailabilityStatusId(2);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setImsLocationEntity(getImsLocationEntity());
        return itemEntity;
    }

    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("1");
        imsLocationEntity.setImsLocationName("test");
        imsLocationEntity.setCreatedBy("test");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setActive(true);
        imsLocationEntity.setDescription("test");
        imsLocationEntity.setUpdatedBy("test");
        imsLocationEntity.setUpdatedDate(new Date());
        return imsLocationEntity;
    }

    @Test // Test Cases RequestIds
    public void testUpdateRecapRequestItem() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        Random random = new Random();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
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
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        List<ItemEntity> list = new ArrayList<ItemEntity>();
        list.add(itemEntity);
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setReplaceRequestByType("RequestStatus");
        replaceRequest.setEndRequestId("320");
        replaceRequest.setFromDate(new Date().toString());
        replaceRequest.setToDate(new Date().toString());
        replaceRequest.setRequestIds("2");
        replaceRequest.setStartRequestId("1");
        replaceRequest.setRequestStatus("test");
        Map<String, String> listMap = new HashMap<>();
    }

    @Test
    public void setRequestItemEntity() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        RequestItemEntity requestItemEntity = createRequestItem();
        Mockito.when(mockedGfaLasService.callGfaItemStatus(requestItemEntity.getItemEntity().getBarcode())).thenReturn("Available");
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbConstants.LAS_REFILE_REQUEST_PLACED)).thenThrow(new NullPointerException());
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "setRequestItemEntity", itemRequestInformation, requestItemEntity);
    }

    @Test
    public void setRequestItemEntityWithImsAvailable() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        RequestItemEntity requestItemEntity = createRequestItem();
        Mockito.when(mockedGfaLasService.callGfaItemStatus(requestItemEntity.getItemEntity().getBarcode())).thenReturn("test");
        Mockito.when(mockedCommonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), anyString(), true)).thenReturn(Boolean.TRUE);
        Mockito.doNothing().when(mockedProducerTemplate).sendBodyAndHeader(anyString(), any(), any(), any());
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "setRequestItemEntity", itemRequestInformation, requestItemEntity);
    }

    @Test
    public void buildRetrieveRequestInfoAndReplaceToSCSB() {
        RequestItemEntity requestItemEntity = createRequestItem();
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Failure", HttpStatus.OK);
        Mockito.when(mockedSecurityUtil.getDecryptedValue(any())).thenReturn("test@gmail.com");
        Mockito.when(mockedRequestParamaterValidatorService.validateItemRequestParameters(any())).thenReturn(null);
        Mockito.when(mockedItemValidatorService.itemValidation(any())).thenReturn(responseEntity);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "buildRetrieveRequestInfoAndReplaceToSCSB", requestItemEntity);
    }

    @Test
    public void rollbackAfterGFA() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        itemInformationResponse.setBulk(true);
        Mockito.when(mockedItemRequestDBService.rollbackAfterGFA(any())).thenReturn(itemRequestInformation);
        Mockito.when(mockedRequestItemDetailsRepository.findById(any())).thenReturn(Optional.of(createRequestItem()));
        Mockito.when(mockedRequestItemController.checkinItem(any(), any())).thenReturn(itemCheckinResponse);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "rollbackAfterGFA", itemInformationResponse);
    }

    @Test
    public void checkInstAfterPlacingRequestException() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestingInstitution("CUL");
        itemRequestInformation.setRequestType("EDD");
        ItemEntity itemEntity = getItemEntity();
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenThrow(new NullPointerException());
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "checkInstAfterPlacingRequest", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void checkInstAfterPlacingRequestInnerException() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestingInstitution("CUL");
        itemRequestInformation.setRequestType("EDD");
        ItemEntity itemEntity = getItemEntity();
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        Mockito.when(mockedItemRequestServiceUtil.getPatronIdBorrowingInstitution(any(), any(), anyString())).thenThrow(new NullPointerException());
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "checkInstAfterPlacingRequest", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void checkOwningInstitutionRecallWithoutCirculationStatus() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = createRequestItem();
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "checkOwningInstitutionRecall", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void checkOwningInstitutionRecall() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestingInstitution("CUL");
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemRecallResponse itemRecallResponse = getItemRecallResponse();
        ItemHoldResponse itemHoldResponse = getItemHoldResponse();
        OwnerCodeEntity ownerCodeEntity = getOwnerCodeEntity();
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(any())).thenReturn(ownerCodeEntity);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(requestItemEntity.getInstitutionEntity().getInstitutionCode(), "ils.checkedout.circulation.status")).thenReturn(itemInformationResponse.getCirculationStatus());
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInformation.getRequestingInstitution(), "use.generic.patron.retrieval.cross")).thenReturn(Boolean.TRUE.toString());
        Mockito.when(mockedItemRequestServiceUtil.getPatronIdBorrowingInstitution(any(), any(), anyString())).thenReturn("PUL");
        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(mockedRequestItemController.recallItem(any(), any())).thenReturn(itemRecallResponse);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "checkOwningInstitutionRecall", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void checkOwningInstitutionRecallFailure() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemRecallResponse itemRecallResponse = getItemRecallResponse();
        itemRecallResponse.setSuccess(false);
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(requestItemEntity.getInstitutionEntity().getInstitutionCode(), "ils.checkedout.circulation.status")).thenReturn(itemInformationResponse.getCirculationStatus());
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(mockedRequestItemController.recallItem(any(), any())).thenReturn(itemRecallResponse);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "checkOwningInstitutionRecall", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void checkOwningInstitutionRecallException() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestingInstitution("CUL");
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemHoldResponse itemHoldResponse = getItemHoldResponse();
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(requestItemEntity.getInstitutionEntity().getInstitutionCode(), "ils.checkedout.circulation.status")).thenReturn(itemInformationResponse.getCirculationStatus());
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInformation.getRequestingInstitution(), "use.generic.patron.retrieval.cross")).thenReturn(Boolean.TRUE.toString());
        Mockito.when(mockedItemRequestServiceUtil.getPatronIdBorrowingInstitution(any(), any(), anyString())).thenReturn("PUL");
        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "checkOwningInstitutionRecall", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void checkOwningInstitutionFailureItemResponse() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestingInstitution("CUL");
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemHoldResponse itemHoldResponse = getItemHoldResponse();
        itemHoldResponse.setSuccess(false);
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(requestItemEntity.getInstitutionEntity().getInstitutionCode(), "ils.checkedout.circulation.status")).thenReturn(itemInformationResponse.getCirculationStatus());
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "checkOwningInstitutionRecall", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void createBibAndHold() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemCreateBibResponse createBibResponse = getItemCreateBibResponse();
        createBibResponse.setSuccess(false);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        Mockito.when(mockedRequestItemController.createBibliogrphicItem(any(), any())).thenReturn(createBibResponse);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "createBibAndHold", itemRequestInformation, itemInformationResponse, itemEntity);
    }

    @Test
    public void setEddInformation() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        HashMap<String, String> eddNotesMap = new HashMap<>();
        eddNotesMap.put("Start Page", "1");
        eddNotesMap.put("End Page", "1789");
        eddNotesMap.put("Chapter", "12");
        eddNotesMap.put("Article Author", "John");
        eddNotesMap.put("Volume Number", "6");
        eddNotesMap.put("Article/Chapter Title", "Last Break");
        eddNotesMap.put("Issue", "test");
        eddNotesMap.put("User", "test");
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "setEddInformation", itemRequestInformation, eddNotesMap);
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
    public void recallItemWithoutRequestEntites() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setItemOwningInstitution("CUL");
        RequestItemEntity requestItemEntity = createRequestItem();
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(null);
        ItemInformationResponse itemInformationResponse = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(itemInformationResponse);
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        ItemInformationResponse itemInformationResponse1 = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(itemInformationResponse1);

    }

    @Test
    public void recallItem() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        RequestItemEntity requestItemEntity = createRequestItem();
        requestItemEntity.setId(1);
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCustomerCode("PA");
        itemEntity.setBarcode("123456");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "ItemRequest");
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestId(1);
        itemInformationResponse.setUsername("test");
        itemInformationResponse.setCirculationStatus("CHARGED");
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemRecallResponse itemRecallResponse = getItemRecallResponse();
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(itemInformationResponse.getCirculationStatus());
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(itemRequestInformation.getDeliveryLocation())).thenReturn(ownerCodeEntity);
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(mockedRequestItemController.recallItem(any(), any())).thenReturn(itemRecallResponse);
        ItemInformationResponse response = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, ScsbConstants.REQUEST_STATUS_PROCESSING, null)).thenReturn(1);
        ItemInformationResponse response1 = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response1);
    }

    @Test
    public void recallItemRestClientException() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        RequestItemEntity requestItemEntity = createRequestItem();
        requestItemEntity.setId(1);
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCustomerCode("PA");
        itemEntity.setBarcode("123456");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "ItemRequest");
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestId(1);
        itemInformationResponse.setUsername("test");
        itemInformationResponse.setCirculationStatus("CHARGED");
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemRecallResponse itemRecallResponse = getItemRecallResponse();
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenThrow(new RestClientException("Bad Request"));
        ItemInformationResponse response = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response);
    }

    @Test
    public void recallItemInnerException() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        RequestItemEntity requestItemEntity = createRequestItem();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UCL");
        institutionEntity.setInstitutionName("University of Chicago");
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setId(1);

        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCustomerCode("PA");
        itemEntity.setBarcode("123456");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "ItemRequest");
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestId(0);
        itemInformationResponse.setUsername("test");
        itemInformationResponse.setCirculationStatus("IN TRANSIT");
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemRecallResponse itemRecallResponse = getItemRecallResponse();
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        itemHoldResponse.setScreenMessage("Success");
        ItemCreateBibResponse itemCreateBibResponse = getItemCreateBibResponse();
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
//        Mockito.when(mockedRequestItemController.createBibliogrphicItem(any(), any())).thenReturn(itemCreateBibResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(any(), any(), any(), any())).thenReturn(1);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(any())).thenReturn(ownerCodeEntity);
//        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
//        Mockito.when(mockedRequestItemController.recallItem(any(), any())).thenThrow(new RestClientException("Bad Request"));
        ItemInformationResponse response = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response);
    }

    @Test
    public void recallItemCheckInstitution() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        RequestItemEntity requestItemEntity = createRequestItem();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UCL");
        institutionEntity.setInstitutionName("University of Chicago");
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setId(1);

        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCustomerCode("PA");
        itemEntity.setBarcode("123456");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "ItemRequest");
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestId(0);
        itemInformationResponse.setUsername("test");
        itemInformationResponse.setCirculationStatus("");
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ItemRecallResponse itemRecallResponse = getItemRecallResponse();
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        itemHoldResponse.setScreenMessage("Success");
        ItemCreateBibResponse itemCreateBibResponse = getItemCreateBibResponse();
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
//        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInformation.getRequestingInstitution(), "use.generic.patron.retrieval.cross")).thenReturn(Boolean.TRUE.toString());
        Mockito.when(mockedItemRequestDBService.updateRecapRequestItem(any(), any(), any(), any())).thenReturn(1);
        Mockito.when(mockedItemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedRequestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(mockedOwnerCodeDetailsRepository.findByOwnerCode(any())).thenReturn(ownerCodeEntity);
//        Mockito.when(mockedRequestItemController.holdItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
//        Mockito.when(mockedRequestItemController.recallItem(any(), any())).thenReturn(itemRecallResponse);
        ItemInformationResponse response = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response);
        itemInformationResponse.setCirculationStatus("IN TRANSIT");
        ItemInformationResponse response6 = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response6);
        itemCreateBibResponse.setSuccess(false);
        ItemInformationResponse response4 = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response4);
        itemRecallResponse.setSuccess(false);
        ItemInformationResponse response1 = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response1);
        itemInformationResponse.setSuccess(false);
        Mockito.when(mockedRequestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        ItemInformationResponse response2 = mockedItemRequestService.recallItem(itemRequestInformation, exchange);
        assertNotNull(response2);
    }

    private ItemCreateBibResponse getItemCreateBibResponse() {
        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setItemId("1");
        itemCreateBibResponse.setBibId("1");
        itemCreateBibResponse.setScreenMessage("Success");
        itemCreateBibResponse.setSuccess(true);
        return itemCreateBibResponse;
    }

    @Test
    public void replaceRequestsToLASQueueForRequestStatus() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        Map<String, String> result = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result);
        Mockito.when(mockedRequestItemDetailsRepository.findByRequestStatusCode(Collections.singletonList(ScsbConstants.REQUEST_STATUS_PENDING))).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedGfaLasService.buildRequestInfoAndReplaceToLAS(any())).thenReturn(ScsbCommonConstants.SUCCESS);
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        Map<String, String> result2 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result2);
        replaceRequest.setRequestStatus("EXCEPTION");
        Map<String, String> result3 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result3);
        replaceRequest.setRequestStatus("PLACED");
        Map<String, String> result4 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result4);
        replaceRequest.setRequestStatus("");
        Map<String, String> result5 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result5);
    }


    @Test
    public void replaceRequestsToLASQueueForRequestIdsForRecallException() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus("EXCEPTION");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(requestIds, Collections.singletonList(ScsbConstants.REQUEST_STATUS_EXCEPTION))).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(requestIds, Collections.singletonList(ScsbConstants.REQUEST_STATUS_EXCEPTION))).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result2 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result2);
    }

    @Test
    public void replaceRequestsToLASQueueForRequestIdsForRetrivelException() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus("EXCEPTION");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RETRIEVAL");
        requestTypeEntity.setRequestTypeDesc("RETRIEVAL");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        ResponseEntity responseEntity = new ResponseEntity<>("All request parameters are valid.Patron is eligible to raise a request", HttpStatus.OK);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(requestIds, Collections.singletonList(ScsbConstants.REQUEST_STATUS_EXCEPTION))).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedGfaLasService.callGfaItemStatus(any())).thenReturn("IN");
        Mockito.when(mockedItemValidatorService.itemValidation(any())).thenReturn(responseEntity);
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRequestIdsForEDDException() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus("EXCEPTION");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        ItemEntity itemEntity = getItemEntity();
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = new ResponseEntity<>("All request parameters are valid.Patron is eligible to raise a request", HttpStatus.OK);
        Mockito.when(mockedItemValidatorService.itemValidation(any())).thenReturn(responseEntity1);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(requestIds, Collections.singletonList(ScsbConstants.REQUEST_STATUS_EXCEPTION))).thenReturn(Arrays.asList(requestItemEntity));
        Mockito.when(mockedGfaLasService.callGfaItemStatus(any())).thenReturn("OUT");
        Mockito.when(mockedRequestItemDetailsRepository.save(any())).thenReturn(requestItemEntity);
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbConstants.LAS_REFILE_REQUEST_PLACED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRequestIdsForPending() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus("PENDING");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        Mockito.when(mockedRequestItemDetailsRepository.findByIdsAndStatusCodes(requestIds, Collections.singletonList(ScsbConstants.REQUEST_STATUS_PENDING))).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRequestIdsForOthers() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus("PLACED");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRequestIdsForBlank() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus("");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRangeOfRequestIdsForPending() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbConstants.REQUEST_IDS_RANGE);
        replaceRequest.setRequestStatus("PENDING");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        Integer startRequestId = Integer.valueOf(replaceRequest.getStartRequestId());
        Integer endRequestId = Integer.valueOf(replaceRequest.getEndRequestId());
        Mockito.when(mockedRequestItemDetailsRepository.getRequestsBasedOnRequestIdRangeAndRequestStatusCode(startRequestId, endRequestId, ScsbConstants.REQUEST_STATUS_PENDING)).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRangeOfRequestIdsForException() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbConstants.REQUEST_IDS_RANGE);
        replaceRequest.setRequestStatus("EXCEPTION");
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        Integer startRequestId = Integer.valueOf(replaceRequest.getStartRequestId());
        Integer endRequestId = Integer.valueOf(replaceRequest.getEndRequestId());
        Mockito.when(mockedRequestItemDetailsRepository.getRequestsBasedOnRequestIdRangeAndRequestStatusCode(startRequestId, endRequestId, ScsbConstants.REQUEST_STATUS_EXCEPTION)).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRangeOfRequestIdsForOthers() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbConstants.REQUEST_IDS_RANGE);
        replaceRequest.setRequestStatus("PLACED");
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRangeOfRequestIdsForBlank() {
        ReplaceRequest replaceRequest = getReplaceRequest();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbConstants.REQUEST_IDS_RANGE);
        replaceRequest.setRequestStatus("");
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRangeOfRequestDatesForPending() throws ParseException {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbConstants.REQUEST_DATES_RANGE);
        replaceRequest.setRequestStatus("PENDING");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(ScsbConstants.DEFAULT_DATE_FORMAT);
        Date fromDate = dateFormatter.parse(replaceRequest.getFromDate());
        Date toDate = dateFormatter.parse(replaceRequest.getToDate());
        Mockito.when(mockedRequestItemDetailsRepository.getRequestsBasedOnDateRangeAndRequestStatusCode(fromDate, toDate, ScsbConstants.REQUEST_STATUS_PENDING)).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void replaceRequestsToLASQueueForRangeOfRequestDatesForException() throws ParseException {
        ReplaceRequest replaceRequest = getReplaceRequest();
        RequestItemEntity requestItemEntity = createRequestItem();
        replaceRequest.setRequestIds("1");
        replaceRequest.setReplaceRequestByType(ScsbConstants.REQUEST_DATES_RANGE);
        replaceRequest.setRequestStatus("EXCEPTION");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(ScsbConstants.DEFAULT_DATE_FORMAT);
        Date fromDate = dateFormatter.parse(replaceRequest.getFromDate());
        Date toDate = dateFormatter.parse(replaceRequest.getToDate());
        ItemEntity itemEntity = getItemEntity();
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = new ResponseEntity<>("All edd request parameters are valid.Patron is eligible to raise a request", HttpStatus.OK);
        Mockito.when(mockedItemValidatorService.itemValidation(any())).thenReturn(responseEntity1);
        Mockito.when(mockedRequestItemDetailsRepository.getRequestsBasedOnDateRangeAndRequestStatusCode(fromDate, toDate, ScsbConstants.REQUEST_STATUS_EXCEPTION)).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result1 = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result1);
    }

    @Test
    public void getTitle(){
        String title = "";
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setUseRestrictions("test");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        searchResultRow.setTitle("Mathematische Poetik / von Solomon Marcus ; aus dem Rumaenichen uebertragen von Edith Mandroiu.Cumulative bulletin - Bureau of Alcohol, Tobacco & Firearms.");
        mockedItemRequestService.getTitle(title,itemEntity,searchResultRow);
    }

    @Test
    public void getTitleWithoutSearchResultRow(){
        String title = "";
        mockedItemRequestService.getTitle(title,null,null);
    }

    @Test
    public void setItemRequestInfoForRequest() {
        ItemEntity itemEntity = getItemEntity();
        itemEntity.getBibliographicEntities().get(0).setOwningInstitutionBibId("");
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("CUL");
        RequestItemEntity requestItemEntity = createRequestItem();
        requestItemEntity.getRequestTypeEntity().setRequestTypeCode("EDD");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setAuthor("test");
        searchResultRow.setTitle("test");
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        ResponseEntity<List<SearchResultRow>> responseEntity = new ResponseEntity<List<SearchResultRow>>(Arrays.asList(searchResultRow), HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + ScsbConstants.SEARCH_RECORDS_SOLR)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                .queryParam(ScsbConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
        })).thenReturn(responseEntity);
        ReflectionTestUtils.invokeMethod(mockedItemRequestService, "setItemRequestInfoForRequest", itemEntity, itemRequestInfo, requestItemEntity);
    }

    private ReplaceRequest getReplaceRequest() {
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setEndRequestId("1");
        replaceRequest.setFromDate("19-09-2019 04:45");
        replaceRequest.setReplaceRequestByType("RequestStatus");
        replaceRequest.setRequestStatus("PENDING");
        replaceRequest.setStartRequestId("1");
        replaceRequest.setToDate("19-09-2020 04:45");
        return replaceRequest;
    }

    @Test
    public void replaceRequestsToLASQueueWithBlankReplaceRequestType() {
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setEndRequestId("1");
        replaceRequest.setFromDate(new Date().toString());
        replaceRequest.setReplaceRequestByType("");
        replaceRequest.setRequestIds("1");
        replaceRequest.setRequestStatus("PLACED");
        replaceRequest.setStartRequestId("1");
        replaceRequest.setToDate(new Date().toString());
        Map<String, String> result = mockedItemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(result);
    }

    @Test
    public void processLASRetrieveResponse() {
        String body = "text";
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        Mockito.when(mockedGfaLasService.processLASRetrieveResponse(body)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestStatus(itemInformationResponse)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.rollbackAfterGFA(itemInformationResponse)).thenReturn(itemRequestInformation);
        Mockito.when(mockedRequestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        mockedItemRequestService.processLASRetrieveResponse(body);
    }

    @Test
    public void processLASEddRetrieveResponse() {
        String body = "text";
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        Mockito.when(mockedGfaLasService.processLASEDDRetrieveResponse(body)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestStatus(itemInformationResponse)).thenReturn(itemInformationResponse);
        mockedItemRequestService.processLASEddRetrieveResponse(body);
    }

    @Test
    public void processLASEddRetrieveResponseFailure() {
        String body = "text";
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        Mockito.when(mockedGfaLasService.processLASEDDRetrieveResponse(body)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.updateRecapRequestStatus(itemInformationResponse)).thenReturn(itemInformationResponse);
        Mockito.when(mockedItemRequestDBService.rollbackAfterGFA(itemInformationResponse)).thenReturn(itemRequestInformation);
        mockedItemRequestService.processLASEddRetrieveResponse(body);
    }

    @Test
    public void executeLasitemCheck() {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setSuccess(true);
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        Mockito.when(mockedRequestItemDetailsRepository.findById(itemRequestInfo.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(itemRequestInfo, itemResponseInformation)).thenReturn(itemResponseInformation);
        Mockito.when(mockedRequestItemStatusDetailsRepository.findByRequestStatusCode(ScsbConstants.REQUEST_STATUS_PENDING)).thenReturn(requestStatusEntity);
        mockedItemRequestService.executeLasitemCheck(itemRequestInfo, itemResponseInformation);
    }

    @Test
    public void executeLasitemCheckFailure() {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setSuccess(false);
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        Mockito.when(mockedRequestItemDetailsRepository.findById(itemRequestInfo.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(mockedGfaLasService.executeRetrieveOrder(itemRequestInfo, itemResponseInformation)).thenReturn(itemResponseInformation);
        mockedItemRequestService.executeLasitemCheck(itemRequestInfo, itemResponseInformation);
    }

    @Test
    public void updateItemAvailabilutyStatus() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        ItemEntity itemEntity = getItemEntity();
        itemEntities.add(itemEntity);
        String username = "test";
        Mockito.when(mockedItemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(itemEntities.get(0).getItemStatusEntity());
        Mockito.when(mockedItemDetailsRepository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        boolean result = mockedItemRequestService.updateItemAvailabilityStatus(itemEntities, username);
//        assertTrue(result);
    }

    public ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setUsername("Discovery");
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

    public BibliographicEntity getBibliographicEntity() {

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
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
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
        institutionEntity.setInstitutionName("PUL");

        BibliographicEntity bibliographicEntity = getBibliographicEntity();

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("REFILE");
        requestStatusEntity.setRequestStatusDescription("REFILE");

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getId());
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

    private OwnerCodeEntity getOwnerCodeEntity() {
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setId(1);
        return ownerCodeEntity;
    }

    private ItemRecallResponse getItemRecallResponse() {
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setSuccess(true);
        return itemRecallResponse;
    }

    private ItemHoldResponse getItemHoldResponse() {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        itemHoldResponse.setScreenMessage("success");
        return itemHoldResponse;
    }


}
