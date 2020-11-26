package org.recap.ils;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.ils.model.nypl.CheckinData;
import org.recap.ils.model.nypl.NYPLHoldData;
import org.recap.ils.model.nypl.NyplHoldRequest;
import org.recap.ils.model.nypl.NyplPatronData;
import org.recap.ils.model.nypl.request.*;
import org.recap.ils.model.nypl.response.*;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.service.NyplApiResponseUtil;
import org.recap.ils.service.NyplOauthTokenApiService;
import org.recap.model.ILSConfigProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

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
        Mockito.when(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemId)).thenReturn("1");
        Mockito.when(nyplApiResponseUtil.getNyplSource(institutionId)).thenReturn("rest");
        Mockito.when(nyplApiResponseUtil.getNormalizedItemIdForNypl(itemId)).thenReturn("holdItem");
        Mockito.when(restProtocolConnector.getApiUrl(source,itemIdentifier)).thenReturn("localhost:9090/holdItem");
        Mockito.when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");

        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiResponseUtil.buildItemInformationResponse(any())).thenReturn(itemInformationResponse);
        ItemInformationResponse informationResponse = restProtocolConnector.lookupItem(itemId);
        assertNotNull(informationResponse);
    }

    @Test
    public void checkOutItem() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String apiURL = "null/checkout-requests";
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setPatronBarcode(patronIdentifier);
        checkoutRequest.setItemBarcode(itemIdentifier);
        checkoutRequest.setDesiredDateDue(new Date().toString());
        CheckoutResponse checkoutResponse = new CheckoutResponse();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(checkoutResponse,HttpStatus.OK);
        HttpEntity<CheckoutRequest> requestEntity = new HttpEntity(checkoutRequest, getHttpHeaders());
        Mockito.when(nyplApiResponseUtil.getExpirationDateForNypl()).thenReturn(new Date().toString());
        Mockito.when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
//        Mockito.when(restTemplate.exchange(apiURL,HttpMethod.POST, requestEntity, CheckoutResponse.class)).thenReturn(responseEntity);
        restProtocolConnector.checkOutItem(itemIdentifier,patronIdentifier);
    }
    @Test
    public void checkInItem() throws Exception {
        String itemIdentifier = "236784";
        String patronIdentifier = "234673";
        String apiURL = "null/checkin-requests";
        CheckinResponse checkinResponse = new CheckinResponse();
        checkinResponse.setCount(1);
        CheckinData checkinData = new CheckinData();
        checkinData.setCreatedDate(new Date().toString());
        checkinData.setJobId("1");
        checkinResponse.setData(checkinData);
        checkinResponse.setStatusCode(244);
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode(itemIdentifier);

        HttpEntity<CheckinRequest> requestEntity = new HttpEntity(checkinRequest, getHttpHeaders());
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<>(checkinResponse,HttpStatus.OK);
        HttpHeaders headers = getHttpHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String authorization = "Bearer " + "Recap";
        headers.set("Authorization", authorization);
        Mockito.when(nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword())).thenReturn("Recap");
//        Mockito.when(restTemplate.exchange(apiURL,HttpMethod.POST, requestEntity, CheckinResponse.class)).thenReturn(responseEntity);
        restProtocolConnector.checkInItem(itemIdentifier,patronIdentifier);
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
        CreateHoldRequest createHoldRequest = new CreateHoldRequest();
        createHoldRequest.setTrackingId(trackingId);
        createHoldRequest.setOwningInstitutionId(itemInstitutionId);
        createHoldRequest.setItemBarcode(itemIdentifier);
        createHoldRequest.setPatronBarcode(patronIdentifier);
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        HttpEntity<CreateHoldRequest> requestEntity = new HttpEntity(createHoldRequest, getHttpHeaders());
        String recapHoldApiUrl =Mockito.when(restProtocolConnector.getRestDataApiUrl()).thenReturn("localhost:9090")+ RecapConstants.NYPL_RECAP_HOLD_REQUEST_URL;
//        Mockito.when(restTemplate.exchange(recapHoldApiUrl, HttpMethod.POST, requestEntity, CreateHoldResponse.class)).thenReturn(responseEntity);
        restProtocolConnector.placeHold(itemIdentifier,patronIdentifier,callInstitutionId,itemInstitutionId,expirationDate,bibId,deliveryLocation,trackingId,title,author,callNumber);
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
        Mockito.when(restProtocolConnector.getRestDataApiUrl()).thenReturn("localhost");
        Mockito.when(nyplApiResponseUtil.getNyplSource(itemInstitutionId)).thenReturn("initiateNyplHoldRequest");
        Mockito.when(nyplApiResponseUtil.getNormalizedItemIdForNypl(itemIdentifier)).thenReturn("records");
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
        String apiUrl = "localhost:9090" + RecapConstants.NYPL_RECAP_CANCEL_HOLD_REQUEST_URL;
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        CancelHoldRequest cancelHoldRequest = new CancelHoldRequest();
        cancelHoldRequest.setTrackingId(trackingId);
        cancelHoldRequest.setOwningInstitutionId("2");
        cancelHoldRequest.setItemBarcode(itemIdentifier);
        cancelHoldRequest.setPatronBarcode(patronIdentifier);
        HttpEntity<CancelHoldRequest> requestEntity = new HttpEntity(cancelHoldRequest, getHttpHeaders());
        Mockito.when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("localhost:9090");
        Mockito.when(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn("2");
//        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, CancelHoldResponse.class)).thenReturn(responseEntity);
        restProtocolConnector.cancelHold(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation,trackingId);
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
        String apiUrl = "localhost:9090" + RecapConstants.NYPL_RECAP_REFILE_REQUEST_URL;
        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        RefileRequest refileRequest = new RefileRequest();
        refileRequest.setItemBarcode(itemIdentifier);

        HttpEntity<RefileRequest> requestEntity = new HttpEntity(refileRequest,getHttpHeaders());
        Mockito.when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("localhost:9090");
       // Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, RefileResponse.class)).thenReturn(responseEntity);
        restProtocolConnector.refileItem(itemIdentifier);
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
}
