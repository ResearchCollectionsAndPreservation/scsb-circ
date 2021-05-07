package org.recap.ils;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.ils.model.nypl.*;
import org.recap.ils.model.nypl.request.CancelHoldRequest;
import org.recap.ils.model.nypl.request.CheckinRequest;
import org.recap.ils.model.nypl.request.CheckoutRequest;
import org.recap.ils.model.nypl.request.CreateHoldRequest;
import org.recap.ils.model.nypl.response.*;
import org.recap.ils.model.response.ItemCheckinResponse;
import org.recap.ils.model.response.ItemCheckoutResponse;
import org.recap.ils.model.response.ItemHoldResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.service.RestApiResponseUtil;
import org.recap.ils.service.RestOauthTokenApiService;
import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.processor.RestProtocolJobResponsePollingProcessor;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RestProtocolConnectorUT extends BaseTestCaseUT {

    @InjectMocks
    RestProtocolConnector restProtocolConnector;

    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Mock
    RestApiResponseUtil restApiResponseUtil;

    @Mock
    RestTemplate restTemplate;

    @Mock
    RestOauthTokenApiService restOauthTokenApiService;

    @Mock
    RestProtocolJobResponsePollingProcessor restProtocolJobResponsePollingProcessor;

    @Test
    public void checkGetters() {
        restProtocolConnector.getRestOauthTokenApiService();
        restProtocolConnector.getRestTemplate();
        restProtocolConnector.getCheckOutRequest();
    }

    @Test
    public void supports() {
        String protocol = ScsbConstants.REST_PROTOCOL;
        boolean result = restProtocolConnector.supports(protocol);
        assertTrue(result);
    }

    @Test
    public void setInstitution() {
        String institutionCode = "NYPL";
        restProtocolConnector.setInstitution(institutionCode);
    }

    @Test
    public void getCheckOutRequest() {
        CheckoutRequest checkoutRequest = restProtocolConnector.getCheckOutRequest();
        assertNotNull(checkoutRequest);
    }

    @Test
    public void getCheckInRequest() {
        CheckinRequest checkinRequest = restProtocolConnector.getCheckInRequest();
        assertNotNull(checkinRequest);
    }

    @Test
    public void getCreateHoldRequest() {
        CreateHoldRequest createHoldRequest = restProtocolConnector.getCreateHoldRequest();
        assertNotNull(createHoldRequest);
    }

    @Test
    public void getCancelHoldRequest() {
        CancelHoldRequest cancelHoldRequest = restProtocolConnector.getCancelHoldRequest();
        assertNotNull(cancelHoldRequest);
    }

    public String getHost() {
        return ilsConfigProperties.getHost();
    }

    public int getPort() {
        return ilsConfigProperties.getPort();
    }

    public String getOperatorUserId() {
        return ilsConfigProperties.getOperatorUserId();
    }

    public String getOperatorPassword() {
        return ilsConfigProperties.getOperatorPassword();
    }

    public String getOperatorLocation() {
        return ilsConfigProperties.getOperatorLocation();
    }

    @Test
    public void lookupItem() throws Exception {
        String itemId = "25678";
        ResponseEntity<ItemResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setScreenMessage("Success");
        String institutionId = "1";
        String source = "rest";
        String itemIdentifier = "holdItem";
        String apiUrl = "localhost:9090/holdItem/items/rest/holdItem";
        String authorization = "Bearer " + null;
        HttpHeaders headers = getHttpHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);

        HttpEntity requestEntity = getHttpEntity(headers);
        when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemId)).thenReturn("1");
        when(restApiResponseUtil.getRestApiSourceForInstitution(any(), any())).thenReturn("rest");
        when(restApiResponseUtil.getNormalizedItemIdForRestProtocolApi(itemId)).thenReturn("holdItem");
        when(restProtocolConnector.getApiUrl(source, itemIdentifier)).thenReturn("localhost:9090/holdItem");
        when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class)).thenReturn(responseEntity);
        when(restApiResponseUtil.buildItemInformationResponse(any())).thenReturn(itemInformationResponse);
        ItemInformationResponse informationResponse = restProtocolConnector.lookupItem(itemId);
        assertNotNull(informationResponse);
    }

    @Test
    public void lookupItemHttpClientErrorException() throws Exception {
        String itemId = "25678";
        ResponseEntity<ItemResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setScreenMessage("Success");
        String institutionId = "1";
        String source = "rest";
        String itemIdentifier = "holdItem";
        String apiUrl = "localhost:9090/holdItem/items/rest/holdItem";
        String authorization = "Bearer " + null;
        HttpHeaders headers = getHttpHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);

        HttpEntity requestEntity = getHttpEntity(headers);
        when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemId)).thenReturn("1");
        when(restApiResponseUtil.getRestApiSourceForInstitution(any(), any())).thenReturn("rest");
        when(restApiResponseUtil.getNormalizedItemIdForRestProtocolApi(itemId)).thenReturn("holdItem");
        when(restProtocolConnector.getApiUrl(source, itemIdentifier)).thenReturn("localhost:9090/holdItem");
        when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        ItemInformationResponse informationResponse = restProtocolConnector.lookupItem(itemId);
        assertNotNull(informationResponse);
    }

    @Test
    public void lookupItemException() throws Exception {
        String itemId = "25678";
        ItemInformationResponse informationResponse = restProtocolConnector.lookupItem(itemId);
        assertNotNull(informationResponse);
    }

    @Test
    public void queryForJob() throws Exception {
        String jobId = "1";
        ResponseEntity<JobResponse> responseEntity = new ResponseEntity<JobResponse>(getJobResponse(), HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckoutResponse>>any());
        restProtocolConnector.queryForJob(jobId);
    }

    @Test
    public void checkOutItem() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        Integer requestId = 2;
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPatronBarcode(patronIdentifier);
        checkoutRequest.setItemBarcode(itemIdentifier);
        checkoutRequest.setDesiredDateDue(new Date().toString());
        CheckoutResponse checkoutResponse = getCheckoutResponse();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(checkoutResponse, HttpStatus.OK);
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.getExpirationDateForRest()).thenReturn(new Date().toString());
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckoutResponse>>any());
        when(restApiResponseUtil.buildItemCheckoutResponse(checkoutResponse)).thenReturn(new ItemCheckoutResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(getJobResponse());
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void checkOutItemWithoutJobData() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        Integer requestId = 2;
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPatronBarcode(patronIdentifier);
        checkoutRequest.setItemBarcode(itemIdentifier);
        checkoutRequest.setDesiredDateDue(new Date().toString());
        CheckoutResponse checkoutResponse = getCheckoutResponse();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(checkoutResponse, HttpStatus.OK);
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.getExpirationDateForRest()).thenReturn(new Date().toString());
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckoutResponse>>any());
        when(restApiResponseUtil.buildItemCheckoutResponse(checkoutResponse)).thenReturn(new ItemCheckoutResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(new JobResponse());
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void checkOutItemHttpClientErrorException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        Integer requestId = 2;
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPatronBarcode(patronIdentifier);
        checkoutRequest.setItemBarcode(itemIdentifier);
        checkoutRequest.setDesiredDateDue(new Date().toString());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.getExpirationDateForRest()).thenReturn(new Date().toString());
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckoutResponse>>any());
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void checkOutItemException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        Integer requestId = 2;
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void checkInItem() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckinResponse checkinResponse = getCheckinResponse();
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode(itemIdentifier);
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<>(checkinResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckinResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemCheckinResponse(checkinResponse)).thenReturn(new ItemCheckinResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(getJobResponse());
        ItemCheckinResponse response = restProtocolConnector.checkInItem(getItemRequestInformation(), patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void checkInItemWithoutJobId() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckinResponse checkinResponse = getCheckinResponse();
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode(itemIdentifier);
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<>(checkinResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckinResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemCheckinResponse(checkinResponse)).thenReturn(new ItemCheckinResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(new JobResponse());
        ItemCheckinResponse response = restProtocolConnector.checkInItem(getItemRequestInformation(), patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void checkInItemHttpClientErrorException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode(itemIdentifier);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckinResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        ItemCheckinResponse response = restProtocolConnector.checkInItem(getItemRequestInformation(), patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void checkInItemException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        ItemCheckinResponse response = restProtocolConnector.checkInItem(getItemRequestInformation(), patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void placeHold() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        Integer requestId = 2;
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String trackingId = "543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        CreateHoldResponse createHoldResponse = getCreateHoldResponse();
        NYPLHoldResponse nyplHoldResponse = getNyplHoldResponse();
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(createHoldResponse, HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> responseEntity1 = new ResponseEntity<>(nyplHoldResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                Mockito.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<CreateHoldRequest>>any(),
                ArgumentMatchers.<Class<CreateHoldResponse>>any());
        doReturn(responseEntity1).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                Mockito.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<NyplHoldRequest>>any(),
                ArgumentMatchers.<Class<NYPLHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemHoldResponse(any())).thenReturn(new ItemHoldResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(getJobResponse());
        AbstractResponseItem responseItem = restProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, deliveryLocation, trackingId, title, author, callNumber);
        assertNotNull(responseItem);
    }

    @Test
    public void placeHoldWithoutJobId() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String trackingId = "543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        Integer requestId = 2;
        CreateHoldResponse createHoldResponse = getCreateHoldResponse();
        NYPLHoldResponse nyplHoldResponse = getNyplHoldResponse();
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(createHoldResponse, HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> responseEntity1 = new ResponseEntity<>(nyplHoldResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                Mockito.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<CreateHoldRequest>>any(),
                ArgumentMatchers.<Class<CreateHoldResponse>>any());
        doReturn(responseEntity1).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                Mockito.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<NyplHoldRequest>>any(),
                ArgumentMatchers.<Class<NYPLHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemHoldResponse(any())).thenReturn(new ItemHoldResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(new JobResponse());
        AbstractResponseItem responseItem = restProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, deliveryLocation, trackingId, title, author, callNumber);
        assertNotNull(responseItem);
    }

    @Test
    public void placeHoldHttpClientErrorException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String trackingId = "543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        Integer requestId = 2;
        CreateHoldResponse createHoldResponse = getCreateHoldResponse();
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(createHoldResponse, HttpStatus.OK);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckinResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        AbstractResponseItem responseItem = restProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, deliveryLocation, trackingId, title, author, callNumber);
        assertNotNull(responseItem);
    }

    @Test
    public void placeHoldException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String trackingId = "543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        Integer requestId = 2;
        AbstractResponseItem responseItem = restProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, deliveryLocation, trackingId, title, author, callNumber);
        assertNotNull(responseItem);
    }

    @Test
    public void placeHoldWithoutTrackingId() throws Exception {
        String itemIdentifier = "236784";
        Integer requestId = 2;
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        NYPLHoldResponse nyplHoldResponse = new NYPLHoldResponse();
        NYPLHoldData nyplHoldData = new NYPLHoldData();
        nyplHoldData.setId(1);
        nyplHoldResponse.setCount(1);
        nyplHoldResponse.setData(nyplHoldData);
        NyplPatronResponse nyplPatronResponse = new NyplPatronResponse();
        NyplPatronData nyplPatronData = new NyplPatronData();
        nyplPatronData.setId("1");
        nyplPatronResponse.setCount(1);
        nyplPatronResponse.setData(Arrays.asList(nyplPatronData));
        ResponseEntity<NyplPatronResponse> jobResponseEntity = new ResponseEntity<>(nyplPatronResponse, HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> nyplHoldResponseEntity = new ResponseEntity<>(nyplHoldResponse, HttpStatus.OK);
        when(restProtocolConnector.getRestDataApiUrl()).thenReturn("localhost");
        when(restApiResponseUtil.getRestApiSourceForInstitution(any(), any())).thenReturn("initiateNyplHoldRequest");
        when(restApiResponseUtil.getNormalizedItemIdForRestProtocolApi(itemIdentifier)).thenReturn("records");
        doReturn(jobResponseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                Mockito.eq(HttpMethod.GET),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<NyplPatronResponse>>any());
        doReturn(nyplHoldResponseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                Mockito.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<NyplHoldRequest>>any(),
                ArgumentMatchers.<Class<NYPLHoldResponse>>any());
        restProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, deliveryLocation, "", title, author, callNumber);
    }

    @Test
    public void cancelHold() throws Exception {
        String itemIdentifier = "236784";
        Integer requestId = 2;
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId = "543587";
        CancelHoldResponse cancelHoldResponse = getCancelHoldResponse();
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<>(cancelHoldResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CancelHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemCancelHoldResponse(any())).thenReturn(new ItemHoldResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(getJobResponse());
        when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn("2");
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(responseItem);
    }

    @Test
    public void cancelHoldWithoutJobId() throws Exception {
        String itemIdentifier = "236784";
        Integer requestId = 2;
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId = "543587";
        CancelHoldResponse cancelHoldResponse = getCancelHoldResponse();
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<>(cancelHoldResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CancelHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemCancelHoldResponse(any())).thenReturn(new ItemHoldResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(new JobResponse());
        when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn("2");
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(responseItem);
    }

    @Test
    public void cancelHoldHttpClientErrorException() throws Exception {
        String itemIdentifier = "236784";
        Integer requestId = 2;
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId = "543587";
        CancelHoldResponse cancelHoldResponse = getCancelHoldResponse();
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<>(cancelHoldResponse, HttpStatus.OK);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CancelHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn("2");
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(responseItem);
    }

    @Test
    public void cancelHoldException() throws Exception {
        String itemIdentifier = "236784";
        Integer requestId = 2;
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId = "543587";
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(responseItem);
    }

    @Test
    public void recallItem() {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        restProtocolConnector.recallItem(itemIdentifier, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation);
    }

    @Test
    public void refileItem() throws Exception {
        String itemIdentifier = "2345343";
        RefileResponse refileResponse = new RefileResponse();
        refileResponse.setCount(1);
        refileResponse.setData(new RefileData());
        refileResponse.setStatusCode(1);
        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<>(refileResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemRefileResponse(any(), any())).thenReturn(new ItemRefileResponse());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(getJobResponse());
        ItemRefileResponse response = restProtocolConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }

    @Test
    public void refileItemWithoutJobId() throws Exception {
        String itemIdentifier = "2345343";
        ilsConfigProperties.setOperatorPassword("scsb");
        ilsConfigProperties.setOperatorUserId("scsb");
        ilsConfigProperties.setOperatorLocation("location");
        RefileResponse refileResponse = new RefileResponse();
        refileResponse.setCount(1);
        refileResponse.setData(new RefileData());
        refileResponse.setStatusCode(1);
        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<>(refileResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        when(restProtocolJobResponsePollingProcessor.pollRestApiRequestItemJobResponse(any(), any())).thenReturn(new JobResponse());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        when(restApiResponseUtil.buildItemRefileResponse(any(), any())).thenReturn(new ItemRefileResponse());
        ItemRefileResponse response = restProtocolConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }

    @Test
    public void refileItemHttpClientErrorException() throws Exception {
        String itemIdentifier = "2345343";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//scsb/rest");
        ItemRefileResponse response = restProtocolConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }

    @Test
    public void refileItemException() throws Exception {
        String itemIdentifier = "2345343";
        ItemRefileResponse response = restProtocolConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }

    @Test
    public void createBib() {
        String itemIdentifier = "25673";
        String patronIdentifier = "5767623";
        String institutionId = "1";
        String titleIdentifier = "PA";
        Object obj = restProtocolConnector.createBib(itemIdentifier, patronIdentifier, institutionId, titleIdentifier);
        assertNull(obj);
    }

    @Test
    public void patronValidation() {
        String patronIdentifier = "5767623";
        String institutionId = "1";
        Boolean result = restProtocolConnector.patronValidation(institutionId, patronIdentifier);
        assertTrue(result);
    }

    @Test
    public void lookupPatron() {
        String patronIdentifier = "5767623";
        AbstractResponseItem responseItem = restProtocolConnector.lookupPatron(patronIdentifier);
        assertNull(responseItem);
    }

    private NyplHoldRequest getNyplHoldRequest(String deliveryLocation) {
        NyplHoldRequest nyplHoldRequest = new NyplHoldRequest();
        nyplHoldRequest.setRecord("records");
        nyplHoldRequest.setPatron("1");
        nyplHoldRequest.setNyplSource("initiateNyplHoldRequest");
        nyplHoldRequest.setRecordType(ScsbConstants.REST_RECORD_TYPE);
        nyplHoldRequest.setPickupLocation("");
        nyplHoldRequest.setDeliveryLocation(deliveryLocation);
        nyplHoldRequest.setNumberOfCopies(1);
        nyplHoldRequest.setNeededBy(new Date().toString());
        return nyplHoldRequest;
    }

    public HttpHeaders getHttpHeader() {
        return new HttpHeaders();
    }

    public HttpEntity getHttpEntity(HttpHeaders headers) {
        return new HttpEntity(headers);
    }

    private HttpHeaders getHttpHeaders() throws Exception {
        String authorization = "Bearer " + null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);
        return headers;
    }

    private CheckoutResponse getCheckoutResponse() {
        CheckoutResponse checkoutResponse = new CheckoutResponse();
        checkoutResponse.setCount(1);
        checkoutResponse.setStatusCode(1);
        CheckoutData checkoutData = new CheckoutData();
        checkoutData.setId(1);
        checkoutData.setJobId("1");
        checkoutResponse.setData(checkoutData);
        return checkoutResponse;
    }

    private CheckinResponse getCheckinResponse() {
        CheckinResponse checkinResponse = new CheckinResponse();
        checkinResponse.setCount(1);
        CheckinData checkinData = new CheckinData();
        checkinData.setCreatedDate(new Date().toString());
        checkinData.setJobId("1");
        checkinResponse.setData(checkinData);
        checkinResponse.setStatusCode(244);
        return checkinResponse;
    }

    private CreateHoldRequest getCreateHoldRequest(String itemIdentifier, String patronIdentifier, String itemInstitutionId, String trackingId) {
        CreateHoldRequest createHoldRequest = new CreateHoldRequest();
        createHoldRequest.setTrackingId(trackingId);
        createHoldRequest.setOwningInstitutionId(itemInstitutionId);
        createHoldRequest.setItemBarcode(itemIdentifier);
        createHoldRequest.setPatronBarcode(patronIdentifier);
        return createHoldRequest;
    }

    private CreateHoldResponse getCreateHoldResponse() {
        CreateHoldResponse createHoldResponse = new CreateHoldResponse();
        CreateHoldData createHoldData = new CreateHoldData();
        Description description = new Description();
        description.setAuthor("John");
        description.setCallNumber("PB");
        description.setTitle("test");
        createHoldData.setCreatedDate("2017-03-30");
        createHoldData.setDescription(description);
        createHoldData.setId(1);
        createHoldData.setItemBarcode("33433001888415");
        createHoldData.setPatronBarcode("23333095887111");
        createHoldData.setOwningInstitutionId("NYPL");
        createHoldData.setTrackingId("1231");
        createHoldData.setUpdatedDate("2017-03-30");
        createHoldResponse.setCount(1);
        createHoldResponse.setData(createHoldData);
        createHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        createHoldResponse.setStatusCode(1);
        return createHoldResponse;
    }

    private NYPLHoldResponse getNyplHoldResponse() {
        NYPLHoldResponse nyplHoldResponse = new NYPLHoldResponse();
        NYPLHoldData nyplHoldData = new NYPLHoldData();
        nyplHoldData.setId(1);
        nyplHoldData.setPatron("45632892514");
        nyplHoldData.setJobId("1231");
        nyplHoldData.setProcessed(true);
        nyplHoldData.setSuccess(true);
        nyplHoldData.setUpdatedDate("2017-03-30");
        nyplHoldData.setCreatedDate("2017-03-30");
        nyplHoldData.setRecordType("test");
        nyplHoldData.setRecord("test");
        nyplHoldData.setNyplSource("test");
        nyplHoldData.setPickupLocation("test");
        nyplHoldData.setNeededBy("test");
        nyplHoldData.setNumberOfCopies(1);
        nyplHoldResponse.setStatusCode(1);
        nyplHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        nyplHoldResponse.setCount(1);
        nyplHoldResponse.setData(nyplHoldData);
        return nyplHoldResponse;
    }

    private CancelHoldResponse getCancelHoldResponse() {
        CancelHoldResponse cancelHoldResponse = new CancelHoldResponse();
        CancelHoldData cancelHoldData = new CancelHoldData();
        cancelHoldData.setCreatedDate("2017-03-30");
        cancelHoldData.setId(1);
        cancelHoldData.setItemBarcode("33433001888415");
        cancelHoldData.setPatronBarcode("23333095887111");
        cancelHoldData.setOwningInstitutionId("NYPL");
        cancelHoldData.setTrackingId("1231");
        cancelHoldData.setUpdatedDate("2017-03-30");
        cancelHoldData.setJobId("1");
        cancelHoldResponse.setData(cancelHoldData);
        cancelHoldResponse.setStatusCode(1);
        cancelHoldResponse.setCount(1);
        cancelHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        assertNotNull(cancelHoldData.getId());
        return cancelHoldResponse;
    }

    private NyplHoldRequest getNyplHoldRequest() {
        NyplHoldRequest nyplHoldRequest = new NyplHoldRequest();
        nyplHoldRequest.setRecord("scsb");
        nyplHoldRequest.setPatron("1");
        nyplHoldRequest.setNyplSource("scsb");
        nyplHoldRequest.setRecordType(ScsbConstants.REST_RECORD_TYPE);
        nyplHoldRequest.setPickupLocation("");
        nyplHoldRequest.setDeliveryLocation("");
        nyplHoldRequest.setNumberOfCopies(1);
        nyplHoldRequest.setNeededBy(new Date().toString());
        return nyplHoldRequest;
    }

    private JobResponse getJobResponse() {
        JobResponse jobResponse = new JobResponse();
        jobResponse.setCount(1);
        jobResponse.setStatusCode(1);
        JobData jobData = new JobData();
        jobData.setId("1");
        jobData.setStarted(true);
        jobData.setSuccess(true);
        jobResponse.setData(jobData);
        return jobResponse;
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.singletonList("123456"));
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setRequestType("RETRIEVAL");
        return itemRequestInformation;
    }

}
