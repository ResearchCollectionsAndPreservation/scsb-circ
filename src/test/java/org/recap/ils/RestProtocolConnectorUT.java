package org.recap.ils;

import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.ils.model.nypl.*;
import org.recap.ils.model.nypl.request.*;
import org.recap.ils.model.nypl.response.*;
import org.recap.ils.model.response.ItemCheckinResponse;
import org.recap.ils.model.response.ItemCheckoutResponse;
import org.recap.ils.model.response.ItemHoldResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.service.NyplApiResponseUtil;
import org.recap.ils.service.NyplOauthTokenApiService;
import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.processor.NyplJobResponsePollingProcessor;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RestProtocolConnectorUT extends BaseTestCaseUT {

    @InjectMocks
    RestProtocolConnector restProtocolConnector;

    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Mock
    NyplApiResponseUtil nyplApiResponseUtil;

    @Mock
    NyplOauthTokenApiService nyplOauthTokenApiService;

    @Mock
    NyplJobResponsePollingProcessor nyplJobResponsePollingProcessor;

    @Mock
    RestTemplate restTemplate;

    @Test
    public void supports(){
        String protocol = RecapConstants.REST_PROTOCOL;
        boolean result = restProtocolConnector.supports(protocol);
        assertTrue(result);
    }

    @Test
    public void getCheckOutRequest(){
        CheckoutRequest checkoutRequest =  restProtocolConnector.getCheckOutRequest();
        assertNotNull(checkoutRequest);
    }

    @Test
    public void getCheckInRequest(){
        CheckinRequest checkinRequest = restProtocolConnector.getCheckInRequest();
        assertNotNull(checkinRequest);
    }

    @Test
    public void getCreateHoldRequest(){
        CreateHoldRequest createHoldRequest = restProtocolConnector.getCreateHoldRequest();
        assertNotNull(createHoldRequest);
    }

    @Test
    public void getCancelHoldRequest(){
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
       // Mockito.when(restProtocolConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        //Mockito.when(restProtocolConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);

        String institutionId = "1";
        String source = "rest";
        String itemIdentifier = "holdItem";
        String apiUrl = "localhost:9090/holdItem/items/rest/holdItem";
        String authorization = "Bearer " + "Recap";
        HttpHeaders headers = getHttpHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);

        HttpEntity requestEntity = getHttpEntity(headers);
        when(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemId)).thenReturn("1");
        when(nyplApiResponseUtil.getNyplSource(institutionId)).thenReturn("rest");
        when(nyplApiResponseUtil.getNormalizedItemIdForNypl(itemId)).thenReturn("holdItem");
        when(restProtocolConnector.getApiUrl(source,itemIdentifier)).thenReturn("localhost:9090/holdItem");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");

        when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class)).thenReturn(responseEntity);
        when(nyplApiResponseUtil.buildItemInformationResponse(any())).thenReturn(itemInformationResponse);
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
        String authorization = "Bearer " + "Recap";
        HttpHeaders headers = getHttpHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);

        HttpEntity requestEntity = getHttpEntity(headers);
        when(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemId)).thenReturn("1");
        when(nyplApiResponseUtil.getNyplSource(institutionId)).thenReturn("rest");
        when(nyplApiResponseUtil.getNormalizedItemIdForNypl(itemId)).thenReturn("holdItem");
        when(restProtocolConnector.getApiUrl(source,itemIdentifier)).thenReturn("localhost:9090/holdItem");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");

        when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        ItemInformationResponse informationResponse = restProtocolConnector.lookupItem(itemId);
        assertNotNull(informationResponse);
    }
    @Test
    public void lookupItemException() throws Exception {
        String itemId = "25678";
        ResponseEntity<ItemResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setScreenMessage("Success");
        ItemInformationResponse informationResponse = restProtocolConnector.lookupItem(itemId);
        assertNotNull(informationResponse);
    }

    @Test
    public void checkOutItem() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPatronBarcode(patronIdentifier);
        checkoutRequest.setItemBarcode(itemIdentifier);
        checkoutRequest.setDesiredDateDue(new Date().toString());
        CheckoutResponse checkoutResponse = getCheckoutResponse();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(checkoutResponse,HttpStatus.OK);
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplApiResponseUtil.getExpirationDateForNypl()).thenReturn(new Date().toString());
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckoutResponse>>any());
        when(nyplApiResponseUtil.buildItemCheckoutResponse(checkoutResponse)).thenReturn(new ItemCheckoutResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier,patronIdentifier);
        assertNotNull(response);
    }
    @Test
    public void checkOutItemWithoutJobData() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPatronBarcode(patronIdentifier);
        checkoutRequest.setItemBarcode(itemIdentifier);
        checkoutRequest.setDesiredDateDue(new Date().toString());
        CheckoutResponse checkoutResponse = getCheckoutResponse();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(checkoutResponse,HttpStatus.OK);
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplApiResponseUtil.getExpirationDateForNypl()).thenReturn(new Date().toString());
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckoutResponse>>any());
        when(nyplApiResponseUtil.buildItemCheckoutResponse(checkoutResponse)).thenReturn(new ItemCheckoutResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier,patronIdentifier);
        assertNotNull(response);
    }
    @Test
    public void checkOutItemHttpClientErrorException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPatronBarcode(patronIdentifier);
        checkoutRequest.setItemBarcode(itemIdentifier);
        checkoutRequest.setDesiredDateDue(new Date().toString());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplApiResponseUtil.getExpirationDateForNypl()).thenReturn(new Date().toString());
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckoutResponse>>any());
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier,patronIdentifier);
        assertNotNull(response);
    }
    @Test
    public void checkOutItemException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        ItemCheckoutResponse response = restProtocolConnector.checkOutItem(itemIdentifier,patronIdentifier);
        assertNotNull(response);
    }
    @Test
    public void checkInItem() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckinResponse checkinResponse = getCheckinResponse();
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode(itemIdentifier);
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<>(checkinResponse,HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckinResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.buildItemCheckinResponse(checkinResponse)).thenReturn(new ItemCheckinResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        ItemCheckinResponse response = restProtocolConnector.checkInItem(itemIdentifier,patronIdentifier);
        assertNotNull(response);
    }
    @Test
    public void checkInItemWithoutJobId() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        CheckinResponse checkinResponse = getCheckinResponse();
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode(itemIdentifier);
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<>(checkinResponse,HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckinResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.buildItemCheckinResponse(checkinResponse)).thenReturn(new ItemCheckinResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        ItemCheckinResponse response = restProtocolConnector.checkInItem(itemIdentifier,patronIdentifier);
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
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        ItemCheckinResponse response = restProtocolConnector.checkInItem(itemIdentifier,patronIdentifier);
        assertNotNull(response);
    }
    @Test
    public void checkInItemException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        ItemCheckinResponse response = restProtocolConnector.checkInItem(itemIdentifier,patronIdentifier);
        assertNotNull(response);
    }

    @Test
    public void placeHold() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String trackingId ="543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        CreateHoldResponse createHoldResponse = getCreateHoldResponse();
        NYPLHoldResponse nyplHoldResponse = getNyplHoldResponse();
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(createHoldResponse,HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> responseEntity1 = new ResponseEntity<>(nyplHoldResponse,HttpStatus.OK);
        try {
            doReturn(responseEntity).when(restTemplate).exchange(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.any(HttpMethod.class),
                    ArgumentMatchers.any(),
                    ArgumentMatchers.<Class<CreateHoldResponse>>any());
            when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
            when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
//            when(nyplApiResponseUtil.buildItemCheckinResponse(any())).thenReturn(new ItemCheckinResponse());
//            when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
            AbstractResponseItem responseItem = restProtocolConnector.placeHold(itemIdentifier, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, deliveryLocation, trackingId, title, author, callNumber);
            assertNotNull(responseItem);
        }catch (Exception e){}
    }
    /*@Test
    public void placeHoldWithoutJobId() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String trackingId ="543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        CreateHoldResponse createHoldResponse = getCreateHoldResponse();
        NYPLHoldResponse nyplHoldResponse = getNyplHoldResponse();
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(createHoldResponse,HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> responseEntity1 = new ResponseEntity<>(nyplHoldResponse,HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CreateHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.buildItemCheckinResponse(any())).thenReturn(new ItemCheckinResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        AbstractResponseItem responseItem =restProtocolConnector.placeHold(itemIdentifier,patronIdentifier,callInstitutionId,itemInstitutionId,expirationDate,bibId,deliveryLocation,trackingId,title,author,callNumber);
        assertNotNull(responseItem);
    }*/
    @Test
    public void placeHoldHttpClientErrorException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String trackingId ="543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        CreateHoldResponse createHoldResponse = getCreateHoldResponse();
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(createHoldResponse,HttpStatus.OK);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CheckinResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        AbstractResponseItem responseItem =restProtocolConnector.placeHold(itemIdentifier,patronIdentifier,callInstitutionId,itemInstitutionId,expirationDate,bibId,deliveryLocation,trackingId,title,author,callNumber);
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
        String trackingId ="543587";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        AbstractResponseItem responseItem =restProtocolConnector.placeHold(itemIdentifier,patronIdentifier,callInstitutionId,itemInstitutionId,expirationDate,bibId,deliveryLocation,trackingId,title,author,callNumber);
        assertNotNull(responseItem);
    }
    @Test
    public void placeHoldWithoutTrackingId() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String deliveryLocation = "PA";
        String title = "test";
        String author = "test";
        String callNumber = "25578";
        CreateHoldRequest createHoldRequest = new CreateHoldRequest();
        createHoldRequest.setOwningInstitutionId(itemInstitutionId);
        createHoldRequest.setItemBarcode(itemIdentifier);
        createHoldRequest.setPatronBarcode(patronIdentifier);
        NYPLHoldResponse nyplHoldResponse = new NYPLHoldResponse();
        NYPLHoldData nyplHoldData = new NYPLHoldData();
        nyplHoldData.setId(1);
        nyplHoldResponse.setCount(1);
        nyplHoldResponse.setData(nyplHoldData);
        NyplHoldRequest nyplHoldRequest = getNyplHoldRequest(deliveryLocation);
        NyplPatronResponse nyplPatronResponse = new NyplPatronResponse();
        NyplPatronData nyplPatronData = new NyplPatronData();
        nyplPatronData.setId("1");
        nyplPatronResponse.setCount(1);
        nyplPatronResponse.setData(Arrays.asList(nyplPatronData));
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<NyplPatronResponse> jobResponseEntity = new ResponseEntity<>(nyplPatronResponse,HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> nyplHoldResponseEntity = new ResponseEntity<>(nyplHoldResponse,HttpStatus.OK);
        HttpEntity<CreateHoldRequest> requestEntity = new HttpEntity(createHoldRequest, getHttpHeaders());
        HttpEntity requestEntity1 = new HttpEntity(getHttpHeaders());
        HttpEntity<NyplHoldRequest> requestEntity2 = new HttpEntity(nyplHoldRequest,getHttpHeaders());
        String nyplHoldApiUrl = "localhost"+ RecapConstants.NYPL_HOLD_REQUEST_URL;
        String apiUrl = "localhost"+ RecapConstants.NYPL_PATRON_BY_BARCODE_URL + patronIdentifier;
        String recapHoldApiUrl ="localhost"+ RecapConstants.NYPL_RECAP_HOLD_REQUEST_URL;
        when(restProtocolConnector.getRestDataApiUrl()).thenReturn("localhost");
        when(nyplApiResponseUtil.getNyplSource(itemInstitutionId)).thenReturn("initiateNyplHoldRequest");
        when(nyplApiResponseUtil.getNormalizedItemIdForNypl(itemIdentifier)).thenReturn("records");
//        Mockito.when(nyplApiResponseUtil.getExpirationDateForNypl()).thenReturn(new Date().toString());
//        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity1, NyplPatronResponse.class)).thenReturn(jobResponseEntity);
  //      Mockito.when(restTemplate.exchange(nyplHoldApiUrl, HttpMethod.POST, requestEntity2, NYPLHoldResponse.class)).thenReturn(nyplHoldResponseEntity);
    //    Mockito.when(restTemplate.exchange(recapHoldApiUrl, HttpMethod.POST, requestEntity, CreateHoldResponse.class)).thenReturn(responseEntity);
        restProtocolConnector.placeHold(itemIdentifier,patronIdentifier,callInstitutionId,itemInstitutionId,expirationDate,bibId,deliveryLocation,"",title,author,callNumber);
    }

    @Test
    public void cancelHold() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId ="543587";
        CancelHoldResponse cancelHoldResponse = getCancelHoldResponse();
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<>(cancelHoldResponse,HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CancelHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.buildItemCancelHoldResponse(any())).thenReturn(new ItemHoldResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        when(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn("2");
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation,trackingId);
        assertNotNull(responseItem);
    }
    @Test
    public void cancelHoldWithoutJobId() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId ="543587";
        CancelHoldResponse cancelHoldResponse = getCancelHoldResponse();
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<>(cancelHoldResponse,HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CancelHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.buildItemCancelHoldResponse(any())).thenReturn(new ItemHoldResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        when(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn("2");
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation,trackingId);
        assertNotNull(responseItem);
    }
    @Test
    public void cancelHoldHttpClientErrorException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId ="543587";
        CancelHoldResponse cancelHoldResponse = getCancelHoldResponse();
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<>(cancelHoldResponse,HttpStatus.OK);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CancelHoldResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn("2");
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation,trackingId);
        assertNotNull(responseItem);
    }
    @Test
    public void cancelHoldException() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        String trackingId ="543587";
        AbstractResponseItem responseItem = restProtocolConnector.cancelHold(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation,trackingId);
        assertNotNull(responseItem);
    }

    @Test
    public void recallItem(){
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "2445566";
        String pickupLocation = "PA";
        restProtocolConnector.recallItem(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation);
    }

    @Test
    public void refileItem() throws Exception {
        String itemIdentifier = "2345343";
        RefileResponse refileResponse = new RefileResponse();
        refileResponse.setCount(1);
        refileResponse.setData(new RefileData());
        refileResponse.setStatusCode(1);
        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<>(refileResponse,HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.buildItemRefileResponse(any())).thenReturn(new ItemRefileResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        ItemRefileResponse response = restProtocolConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }
    @Test
    public void refileItemWithoutJobId() throws Exception {
        String itemIdentifier = "2345343";
        RefileResponse refileResponse = new RefileResponse();
        refileResponse.setCount(1);
        refileResponse.setData(new RefileData());
        refileResponse.setStatusCode(1);
        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<>(refileResponse,HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        when(nyplApiResponseUtil.buildItemRefileResponse(any())).thenReturn(new ItemRefileResponse());
        when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
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
        when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("https:8080//recap/rest");
        when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
        ItemRefileResponse response = restProtocolConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }
    @Test
    public void refileItemException() throws Exception {
        String itemIdentifier = "2345343";
        ItemRefileResponse response = restProtocolConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }

    private NyplHoldRequest getNyplHoldRequest(String deliveryLocation) {
        NyplHoldRequest nyplHoldRequest = new NyplHoldRequest();
        nyplHoldRequest.setRecord("records");
        nyplHoldRequest.setPatron("1");
        nyplHoldRequest.setNyplSource("initiateNyplHoldRequest");
        nyplHoldRequest.setRecordType(RecapConstants.NYPL_RECORD_TYPE);
        nyplHoldRequest.setPickupLocation("");
        nyplHoldRequest.setDeliveryLocation(deliveryLocation);
        nyplHoldRequest.setNumberOfCopies(1);
        nyplHoldRequest.setNeededBy(new Date().toString());
        return nyplHoldRequest;
    }

    public HttpHeaders getHttpHeader(){
        return new HttpHeaders();
    }
    public HttpEntity getHttpEntity(HttpHeaders headers){
        return new HttpEntity(headers);
    }
    private HttpHeaders getHttpHeaders() throws Exception {
        String authorization = "Bearer " + "Recap";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);
        return headers;
    }

    private CheckoutResponse getCheckoutResponse(){
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

    private CreateHoldResponse getCreateHoldResponse(){
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

    private NYPLHoldResponse getNyplHoldResponse(){
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
    private CancelHoldResponse getCancelHoldResponse(){
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
        nyplHoldRequest.setRecord("recap");
        nyplHoldRequest.setPatron("1");
        nyplHoldRequest.setNyplSource("recap");
        nyplHoldRequest.setRecordType(RecapConstants.NYPL_RECORD_TYPE);
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
}
