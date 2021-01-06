package org.recap.ils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.processor.NyplJobResponsePollingProcessor;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;


/**
 * Created by rajeshbabuk on 19/12/16.
 */
public class NyplApiServiceConnectorUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(NyplApiServiceConnector.class);

    public String nyplDataApiUrl = "http://localhost:8090/rest";

    private String operatorUserId = "htc_scsb";


    private String operatorPassword = "m0Fg7xbm3ZPq5djD3gBHTu3mQYrBpf6U";


    @Mock
    NyplOauthTokenApiService nyplOauthTokenApiService;

    @Mock
    NyplApiServiceConnector nyplApiServiceConnector;

    @Mock
    NyplApiResponseUtil nyplApiResponseUtil;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    RestTemplate restTemplate;

    @Mock
    HttpHeaders httpHeaders;

    @Mock
    NyplJobResponsePollingProcessor nyplJobResponsePollingProcessor;

    public HttpEntity getHttpEntity(HttpHeaders headers){
        return new HttpEntity(headers);
    }

    public String getOperatorUserId() {
        return operatorUserId;
    }

    public String getOperatorPassword() {
        return operatorPassword;
    }

    @Before
    public  void setup(){
        ReflectionTestUtils.setField(nyplApiServiceConnector, "nyplDataApiUrl", nyplDataApiUrl);
    }
    @Test
    public void checkGetters(){
        Mockito.when(nyplApiServiceConnector.getNyplHoldRequest()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getApiUrl("","")).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getCancelHoldRequest()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getLogger()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getHttpEntity(new HttpHeaders())).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getHttpHeader()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getCheckInRequest()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getCheckOutRequest()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getCreateHoldRequest()).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getRefileRequest()).thenCallRealMethod();
        nyplApiServiceConnector.getNyplHoldRequest();
        nyplApiServiceConnector.getNyplOauthTokenApiService();
        nyplApiServiceConnector.getRestTemplate();
        nyplApiServiceConnector.getApiUrl("","");
        nyplApiServiceConnector.getCancelHoldRequest();
        nyplApiServiceConnector.getNyplJobResponsePollingProcessor();
        nyplApiServiceConnector.getLogger();
        nyplApiServiceConnector.getNyplApiResponseUtil();
        nyplApiServiceConnector.getNyplDataApiUrl();
        nyplApiServiceConnector.getHttpEntity(new HttpHeaders());
        nyplApiServiceConnector.getHttpHeader();
        nyplApiServiceConnector.getCheckOutRequest();
        nyplApiServiceConnector.getCheckInRequest();
        nyplApiServiceConnector.getCreateHoldRequest();
        nyplApiServiceConnector.getRefileRequest();
    }

    @Test
    public void lookupItem() throws Exception {
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        String institutionId = "NYPL";
        String source = "recap-NYPL";
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn(institutionId);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getNyplSource(institutionId)).thenReturn(source);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getNormalizedItemIdForNypl(itemIdentifier)).thenReturn(itemIdentifier);
        ResponseEntity<ItemResponse> responseEntity = new ResponseEntity<ItemResponse>(getItemResponse(), HttpStatus.OK);
        ItemResponse itemResponse = responseEntity.getBody();
        HttpHeaders headers = new HttpHeaders();
        Mockito.when(nyplApiServiceConnector.getHttpHeader()).thenReturn(headers);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer");
        HttpEntity requestEntity = new HttpEntity(headers);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getHttpEntity(headers)).thenReturn(requestEntity);
        String apiUrl = nyplDataApiUrl + "/items/" + source + "/" + itemIdentifier;
        Mockito.when(nyplApiServiceConnector.getApiUrl(source, itemIdentifier)).thenReturn(apiUrl);
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemInformationResponse(itemResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.lookupItem(itemIdentifier)).thenCallRealMethod();
        ItemInformationResponse itemInformationResponse = nyplApiServiceConnector.lookupItem(itemIdentifier);
        assertNotNull(itemInformationResponse);
    }

    @Test
    public void lookupItemHttpClientErrorException() throws Exception {
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        String itemBarcode = getBibEntity().getItemEntities().get(0).getBarcode();
        String institutionId = "NYPL";
        String source = "recap-NYPL";
        HttpHeaders headers = new HttpHeaders();
        Mockito.when(nyplApiServiceConnector.getHttpHeader()).thenReturn(headers);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer");
        HttpEntity requestEntity = new HttpEntity(headers);
        String apiUrl = nyplDataApiUrl + "/items/" + source + "/" + itemIdentifier;
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getHttpEntity(headers)).thenReturn(requestEntity);
        Mockito.when(nyplApiServiceConnector.getApiUrl(source, itemIdentifier)).thenReturn(apiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenReturn(institutionId);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getNyplSource(institutionId)).thenReturn(source);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getNormalizedItemIdForNypl(itemIdentifier)).thenReturn(itemIdentifier);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Mockito.when(nyplApiServiceConnector.lookupItem(itemIdentifier)).thenCallRealMethod();
        ItemInformationResponse itemInformationResponse = nyplApiServiceConnector.lookupItem(itemIdentifier);
        assertNotNull(itemInformationResponse);
    }
    @Test
    public void lookupItemException() throws Exception {
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.lookupItem(itemIdentifier)).thenCallRealMethod();
        ItemInformationResponse itemInformationResponse = nyplApiServiceConnector.lookupItem(itemIdentifier);
        assertNotNull(itemInformationResponse);
    }

    @Test
    public void checkoutItem() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(getCheckOutItemResponse(),HttpStatus.OK);
        CheckoutResponse checkoutResponse = responseEntity.getBody();
        String apiUrl = nyplDataApiUrl + "/checkout-requests";
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getCheckOutRequest()).thenReturn(checkoutRequest);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getExpirationDateForNypl()).thenCallRealMethod();
        HttpEntity<CheckoutRequest> requestEntity = new HttpEntity(checkoutRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getRestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, CheckoutResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemCheckoutResponse(checkoutResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        Mockito.when((ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = (ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckoutResponse);
    }
    @Test
    public void checkoutItemWithoutJobData() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(getCheckOutItemResponse(),HttpStatus.OK);
        CheckoutResponse checkoutResponse = responseEntity.getBody();
        String apiUrl = nyplDataApiUrl + "/checkout-requests";
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getCheckOutRequest()).thenReturn(checkoutRequest);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getExpirationDateForNypl()).thenCallRealMethod();
        HttpEntity<CheckoutRequest> requestEntity = new HttpEntity(checkoutRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getRestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, CheckoutResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemCheckoutResponse(checkoutResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        Mockito.when((ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = (ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckoutResponse);
    }
    @Test
    public void checkoutItemHttpClientErrorException() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        ResponseEntity<CheckoutResponse> responseEntity = new ResponseEntity<CheckoutResponse>(getCheckOutItemResponse(),HttpStatus.OK);
        CheckoutResponse checkoutResponse = responseEntity.getBody();
        String apiUrl = nyplDataApiUrl + "/checkout-requests";
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getCheckOutRequest()).thenReturn(checkoutRequest);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getExpirationDateForNypl()).thenCallRealMethod();
        HttpEntity<CheckoutRequest> requestEntity = new HttpEntity(checkoutRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getRestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, CheckoutResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Mockito.when((ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = (ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckoutResponse);
    }
    @Test
    public void checkoutItemException() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when((ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = (ItemCheckoutResponse) nyplApiServiceConnector.checkOutItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckoutResponse);
    }

    @Test
    public void checkinItem() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        CheckinRequest checkinRequest = new CheckinRequest();
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<CheckinResponse>(getCheckinResponse(),HttpStatus.OK);
        CheckinResponse checkinResponse = responseEntity.getBody();
        String apiUrl = nyplDataApiUrl + "/checkin-requests";
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getCheckInRequest()).thenReturn(checkinRequest);
        HttpEntity<CheckoutRequest> requestEntity = new HttpEntity(checkinRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getRestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, CheckinResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemCheckinResponse(checkinResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        Mockito.when((ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckinResponse itemCheckinResponse = (ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckinResponse);
    }
    @Test
    public void checkinItemWithoutJobData() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        CheckinRequest checkinRequest = new CheckinRequest();
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<CheckinResponse>(getCheckinResponse(),HttpStatus.OK);
        CheckinResponse checkinResponse = responseEntity.getBody();
        String apiUrl = nyplDataApiUrl + "/checkin-requests";
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getCheckInRequest()).thenReturn(checkinRequest);
        HttpEntity<CheckoutRequest> requestEntity = new HttpEntity(checkinRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getRestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, CheckinResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemCheckinResponse(checkinResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        Mockito.when((ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckinResponse itemCheckinResponse = (ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckinResponse);
    }
    @Test
    public void checkinItemHttpClientErrorException() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        CheckinRequest checkinRequest = new CheckinRequest();
        ResponseEntity<CheckinResponse> responseEntity = new ResponseEntity<CheckinResponse>(getCheckinResponse(),HttpStatus.OK);
        CheckinResponse checkinResponse = responseEntity.getBody();
        String apiUrl = nyplDataApiUrl + "/checkin-requests";
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getCheckInRequest()).thenReturn(checkinRequest);
        HttpEntity<CheckoutRequest> requestEntity = new HttpEntity(checkinRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getRestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, CheckinResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Mockito.when((ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckinResponse itemCheckinResponse = (ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckinResponse);
    }
    @Test
    public void checkinItemException() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when((ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode)).thenCallRealMethod();
        ItemCheckinResponse itemCheckinResponse = (ItemCheckinResponse) nyplApiServiceConnector.checkInItem(itemBarcode, patronBarcode);
        assertNotNull(itemCheckinResponse);
    }

    @Test
    public void placeHold() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        String callInstitutionId = "NYPL";
        String itemInstitutionId = "NYPL";
        String expirationDate = new Date().toString();
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";
        String title = "";
        String author = "";
        String callNumber = "";
        String recapHoldApiUrl = nyplDataApiUrl + "/recap/hold-requests";
        String apiUrl = nyplDataApiUrl + "/hold-requests/" + trackingId;
        String dataApiUrl = nyplDataApiUrl + RecapConstants.NYPL_PATRON_BY_BARCODE_URL + patronBarcode;
        CreateHoldRequest createHoldRequest = new CreateHoldRequest();
        HttpHeaders headers = getHttpHeaders();
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getCreateHoldRequest()).thenReturn(createHoldRequest);
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<CreateHoldResponse>(getCreateHoldResponse(),HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> responseEntity1 = new ResponseEntity<NYPLHoldResponse>(getNyplHoldResponse(),HttpStatus.OK);
        CreateHoldResponse createHoldResponse = responseEntity.getBody();
        HttpEntity<CreateHoldRequest> requestEntity = new HttpEntity(createHoldRequest, getHttpHeaders());
        Mockito.when(restTemplate.exchange(recapHoldApiUrl, HttpMethod.POST, requestEntity, CreateHoldResponse.class)).thenReturn(responseEntity);
        requestEntity = new HttpEntity(headers);
        Mockito.when(nyplApiServiceConnector.getHttpEntity(headers)).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, NYPLHoldResponse.class)).thenReturn(responseEntity1);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemHoldResponse(createHoldResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        ItemHoldResponse itemHoldResponse1 = new ItemHoldResponse();
        itemHoldResponse1.setSuccess(true);
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(itemHoldResponse);
    }
    @Test
    public void placeHoldWithoutJobData() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        String callInstitutionId = "NYPL";
        String itemInstitutionId = "NYPL";
        String expirationDate = new Date().toString();
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";
        String title = "";
        String author = "";
        String callNumber = "";
        String recapHoldApiUrl = nyplDataApiUrl + "/recap/hold-requests";
        String apiUrl = nyplDataApiUrl + "/hold-requests/" + trackingId;
        CreateHoldRequest createHoldRequest = new CreateHoldRequest();
        HttpHeaders headers = getHttpHeaders();
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getCreateHoldRequest()).thenReturn(createHoldRequest);
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<CreateHoldResponse>(getCreateHoldResponse(),HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> responseEntity1 = new ResponseEntity<NYPLHoldResponse>(getNyplHoldResponse(),HttpStatus.OK);
        CreateHoldResponse createHoldResponse = responseEntity.getBody();
        HttpEntity<CreateHoldRequest> requestEntity = new HttpEntity(createHoldRequest, getHttpHeaders());
        Mockito.when(restTemplate.exchange(recapHoldApiUrl, HttpMethod.POST, requestEntity, CreateHoldResponse.class)).thenReturn(responseEntity);
        requestEntity = new HttpEntity(headers);
        Mockito.when(nyplApiServiceConnector.getHttpEntity(headers)).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, NYPLHoldResponse.class)).thenReturn(responseEntity1);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemHoldResponse(createHoldResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        ItemHoldResponse itemHoldResponse1 = new ItemHoldResponse();
        itemHoldResponse1.setSuccess(true);
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(itemHoldResponse);
    }
    @Test
    public void placeHoldInitiateNyplHoldRequest() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        String callInstitutionId = "NYPL";
        String itemInstitutionId = "NYPL";
        String expirationDate = new Date().toString();
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "";
        String title = "";
        String author = "";
        String callNumber = "";
        String recapHoldApiUrl = nyplDataApiUrl + "/recap/hold-requests";
        String apiUrl = nyplDataApiUrl + "/hold-requests/" + trackingId;
        String nyplHoldApiUrl = nyplDataApiUrl + RecapConstants.NYPL_HOLD_REQUEST_URL;
        String dataApiUrl = nyplDataApiUrl + RecapConstants.NYPL_PATRON_BY_BARCODE_URL + patronBarcode;
        CreateHoldRequest createHoldRequest = new CreateHoldRequest();
        HttpHeaders headers = getHttpHeaders();
        HttpEntity requestEntity1 = new HttpEntity<>(getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
//        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
//        Mockito.when(nyplApiServiceConnector.getCreateHoldRequest()).thenReturn(createHoldRequest);
        Mockito.when(nyplApiResponseUtil.getNyplSource(any())).thenReturn("recap");
        Mockito.when(nyplApiResponseUtil.getNormalizedItemIdForNypl(any())).thenReturn("recap");
        Mockito.when(nyplApiResponseUtil.getExpirationDateForNypl()).thenReturn(new Date().toString());
        NyplHoldRequest nyplHoldRequest = getNyplHoldRequest();
        Mockito.when(nyplApiServiceConnector.getNyplHoldRequest()).thenReturn(nyplHoldRequest);
        ResponseEntity<CreateHoldResponse> responseEntity = new ResponseEntity<CreateHoldResponse>(getCreateHoldResponse(),HttpStatus.OK);
        ResponseEntity<NYPLHoldResponse> responseEntity1 = new ResponseEntity<NYPLHoldResponse>(getNyplHoldResponse(),HttpStatus.OK);
        ResponseEntity<NyplPatronResponse> responseEntity2 = new ResponseEntity<NyplPatronResponse>(getNyplPatronResponse(),HttpStatus.OK);
        CreateHoldResponse createHoldResponse = responseEntity.getBody();
        HttpEntity<CreateHoldRequest> requestEntity = new HttpEntity(createHoldRequest, getHttpHeaders());
        HttpEntity<NyplHoldRequest> requestEntity2 = new HttpEntity<>(getNyplHoldRequest(), getHttpHeaders());
//        Mockito.when(restTemplate.exchange(recapHoldApiUrl, HttpMethod.POST, requestEntity, CreateHoldResponse.class)).thenReturn(responseEntity);
        requestEntity = new HttpEntity(headers);
        Mockito.when(restTemplate.exchange(dataApiUrl, HttpMethod.GET, requestEntity1, NyplPatronResponse.class)).thenReturn(responseEntity2);
//        Mockito.when(restTemplate.exchange(nyplHoldApiUrl, HttpMethod.POST, requestEntity2, NYPLHoldResponse.class)).thenReturn(responseEntity1);
       // Mockito.when(nyplApiServiceConnector.getHttpEntity(headers)).thenReturn(requestEntity);
//        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, NYPLHoldResponse.class)).thenReturn(responseEntity1);
//        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemHoldResponse(createHoldResponse)).thenCallRealMethod();
//        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        ItemHoldResponse itemHoldResponse1 = new ItemHoldResponse();
        itemHoldResponse1.setSuccess(true);
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(itemHoldResponse);
    }

    @Test
    public void placeHoldHttpClientErrorException() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        String callInstitutionId = "NYPL";
        String itemInstitutionId = "NYPL";
        String expirationDate = new Date().toString();
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";
        String title = "";
        String author = "";
        String callNumber = "";
        String recapHoldApiUrl = nyplDataApiUrl + "/recap/hold-requests";
        CreateHoldRequest createHoldRequest = new CreateHoldRequest();
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getCreateHoldRequest()).thenReturn(createHoldRequest);
        HttpEntity<CreateHoldRequest> requestEntity = new HttpEntity(createHoldRequest, getHttpHeaders());
        Mockito.when(restTemplate.exchange(recapHoldApiUrl, HttpMethod.POST, requestEntity, CreateHoldResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(itemHoldResponse);
    }
    @Test
    public void placeHoldException() throws Exception {
        String itemBarcode = "33433001888415";
        String patronBarcode = "23333095887111";
        String callInstitutionId = "NYPL";
        String itemInstitutionId = "NYPL";
        String expirationDate = new Date().toString();
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";
        String title = "";
        String author = "";
        String callNumber = "";
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.placeHold(itemBarcode, patronBarcode, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(itemHoldResponse);
    }

    private NyplPatronResponse getNyplPatronResponse() {
        NyplPatronResponse nyplPatronResponse = new NyplPatronResponse();
        nyplPatronResponse.setCount(1);
        NyplPatronData nyplPatronData = new NyplPatronData();
        nyplPatronData.setDeleted(true);
        nyplPatronData.setBarCodes(Arrays.asList("123456"));
        nyplPatronData.setCreatedDate(new Date().toString());
        nyplPatronData.setDeletedDate(new Date());
        nyplPatronData.setId("1");
        nyplPatronResponse.setData(Arrays.asList(nyplPatronData));
        nyplPatronResponse.setStatusCode(1);
        return nyplPatronResponse;
    }

    @Test
    public void cancelHold() throws Exception {
        String itemBarcode = getBibEntity().getItemEntities().get(0).getBarcode();
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        String patronBarcode = "23333095887111";
        String institutionId = "NYPL";
        String expirationDate = "";
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";

        String apiUrl = nyplDataApiUrl + "/recap/cancel-hold-requests";
        CancelHoldRequest cancelHoldRequest = new CancelHoldRequest();
        HttpEntity<CancelHoldRequest> requestEntity = new HttpEntity(cancelHoldRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getCancelHoldRequest()).thenReturn(cancelHoldRequest);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemDetailsRepository().findByBarcode(itemBarcode)).thenReturn(getBibEntity().getItemEntities());
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<CancelHoldResponse>(getCancelHoldResponse(),HttpStatus.OK);
        CancelHoldResponse cancelHoldResponse = responseEntity.getBody();
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, CancelHoldResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemCancelHoldResponse(cancelHoldResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(getJobResponse());
        ItemHoldResponse itemHoldResponse1 = new ItemHoldResponse();
        itemHoldResponse1.setScreenMessage("Request cancelled.");
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(itemHoldResponse);

    }
    @Test
    public void cancelHoldWithoutJobData() throws Exception {
        String itemBarcode = getBibEntity().getItemEntities().get(0).getBarcode();
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        String patronBarcode = "23333095887111";
        String institutionId = "NYPL";
        String expirationDate = "";
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";

        String apiUrl = nyplDataApiUrl + "/recap/cancel-hold-requests";
        CancelHoldRequest cancelHoldRequest = new CancelHoldRequest();
        HttpEntity<CancelHoldRequest> requestEntity = new HttpEntity(cancelHoldRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getCancelHoldRequest()).thenReturn(cancelHoldRequest);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemDetailsRepository().findByBarcode(itemBarcode)).thenReturn(getBibEntity().getItemEntities());
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<CancelHoldResponse>(getCancelHoldResponse(),HttpStatus.OK);
        CancelHoldResponse cancelHoldResponse = responseEntity.getBody();
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, CancelHoldResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().buildItemCancelHoldResponse(cancelHoldResponse)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor().pollNyplRequestItemJobResponse(any())).thenReturn(new JobResponse());
        ItemHoldResponse itemHoldResponse1 = new ItemHoldResponse();
        itemHoldResponse1.setScreenMessage("Request cancelled.");
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(itemHoldResponse);

    }
    @Test
    public void cancelHoldHttpClientErrorException() throws Exception {
        String itemBarcode = getBibEntity().getItemEntities().get(0).getBarcode();
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        String patronBarcode = "23333095887111";
        String institutionId = "NYPL";
        String expirationDate = "";
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";

        String apiUrl = nyplDataApiUrl + "/recap/cancel-hold-requests";
        CancelHoldRequest cancelHoldRequest = new CancelHoldRequest();
        HttpEntity<CancelHoldRequest> requestEntity = new HttpEntity(cancelHoldRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getCancelHoldRequest()).thenReturn(cancelHoldRequest);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier)).thenCallRealMethod();
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil().getItemDetailsRepository().findByBarcode(itemBarcode)).thenReturn(getBibEntity().getItemEntities());
        ResponseEntity<CancelHoldResponse> responseEntity = new ResponseEntity<CancelHoldResponse>(getCancelHoldResponse(),HttpStatus.OK);
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, CancelHoldResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(itemHoldResponse);

    }
    @Test
    public void cancelHoldException() throws Exception {
        String itemBarcode = getBibEntity().getItemEntities().get(0).getBarcode();
        String patronBarcode = "23333095887111";
        String institutionId = "NYPL";
        String expirationDate = "";
        String bibId = "";
        String pickupLocation = "";
        String trackingId = "1231";
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when((ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId)).thenCallRealMethod();
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse)nyplApiServiceConnector.cancelHold(itemBarcode, patronBarcode, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(itemHoldResponse);

    }

    @Test
    public void queryForJob() throws Exception {
        String jobId = "1245";
        String apiUrl = nyplDataApiUrl + "/jobs/" + jobId;
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        JobResponse jobResponse = getJobResponse();
        ResponseEntity<JobResponse> responseEntity = new ResponseEntity<JobResponse>(jobResponse,HttpStatus.OK);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, JobResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.queryForJob(jobId)).thenCallRealMethod();
        JobResponse response =nyplApiServiceConnector.queryForJob(jobId);
        assertNotNull(response);
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

    @Test
    public void createBib(){
        String itemIdentifier = "2344656";
        String patronIdentifier = "357625";
        String institutionId = "2";
        String titleIdentifier = "3456521";
        Mockito.when(nyplApiServiceConnector.createBib(itemIdentifier,patronIdentifier,institutionId,titleIdentifier)).thenCallRealMethod();
        Object obj = nyplApiServiceConnector.createBib(itemIdentifier,patronIdentifier,institutionId,titleIdentifier);
        assertNull(obj);
    }
    @Test
    public void patronValidation(){
        String institutionId = "2";
        String patronIdentifier = "3254657";
        Mockito.when(nyplApiServiceConnector.patronValidation(institutionId,patronIdentifier)).thenCallRealMethod();
        Boolean res = nyplApiServiceConnector.patronValidation(institutionId,patronIdentifier);
        assertTrue(res);
    }
    @Test
    public void lookupPatron(){
        String patronIdentifier = "3254657";
        Mockito.when(nyplApiServiceConnector.lookupPatron(patronIdentifier)).thenCallRealMethod();
        AbstractResponseItem responseItem = nyplApiServiceConnector.lookupPatron(patronIdentifier);
        assertNull(responseItem);
    }
    @Test
    public void recallItem() throws Exception {
        String itemIdentifier = "123456";
        String patronIdentifier = "123456";
        String institutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "1";
        String pickupLocation = "PA";
        RecallRequest recallRequest = new RecallRequest();
        recallRequest.setOwningInstitutionId("PUL");
        recallRequest.setItemBarcode(itemIdentifier);
        String apiUrl = nyplDataApiUrl +"/recap/recall-requests";
        HttpEntity<RecallRequest> requestEntity = new HttpEntity(recallRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        ResponseEntity<RecallResponse>responseEntity = new ResponseEntity<RecallResponse>(getRecallResponse(),HttpStatus.OK);
        RecallResponse recallResponse = responseEntity.getBody();
        Mockito.when(nyplApiServiceConnector.recallItem(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation)).thenCallRealMethod();
        nyplApiServiceConnector.recallItem(itemIdentifier,patronIdentifier,institutionId,expirationDate,bibId,pickupLocation);
    }
    @Test
    public void refileItem() throws Exception {
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();

        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<RefileResponse>(getRefileResponse(),HttpStatus.OK);
        RefileResponse refileResponse = responseEntity.getBody();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer");
        RefileRequest refileRequest = new RefileRequest();
        refileRequest.setItemBarcode(itemIdentifier);
        String apiUrl = nyplDataApiUrl + "/recap/refile-requests";
        ItemRefileResponse itemRefileResponse = getItemRefileResponse();
        HttpEntity<RefileRequest> requestEntity = new HttpEntity<>(refileRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRefileRequest()).thenReturn(refileRequest);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiResponseUtil.buildItemRefileResponse(refileResponse)).thenReturn(itemRefileResponse);
        Mockito.when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(itemRefileResponse.getJobId())).thenReturn(getJobResponse());
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, RefileResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.refileItem(itemIdentifier)).thenCallRealMethod();
        ItemRefileResponse response = nyplApiServiceConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }
    @Test
    public void refileItemWithoutJobData() throws Exception {
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<RefileResponse>(getRefileResponse(),HttpStatus.OK);
        RefileResponse refileResponse = responseEntity.getBody();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer");
        RefileRequest refileRequest = new RefileRequest();
        refileRequest.setItemBarcode(itemIdentifier);
        String apiUrl = nyplDataApiUrl + "/recap/refile-requests";
        ItemRefileResponse itemRefileResponse = getItemRefileResponse();
        HttpEntity<RefileRequest> requestEntity = new HttpEntity<>(refileRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRefileRequest()).thenReturn(refileRequest);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(nyplApiServiceConnector.getNyplApiResponseUtil()).thenReturn(nyplApiResponseUtil);
        Mockito.when(nyplApiServiceConnector.getNyplJobResponsePollingProcessor()).thenReturn(nyplJobResponsePollingProcessor);
        Mockito.when(nyplApiResponseUtil.buildItemRefileResponse(refileResponse)).thenReturn(itemRefileResponse);
        Mockito.when(nyplJobResponsePollingProcessor.pollNyplRequestItemJobResponse(itemRefileResponse.getJobId())).thenReturn(new JobResponse());
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, RefileResponse.class)).thenReturn(responseEntity);
        Mockito.when(nyplApiServiceConnector.refileItem(itemIdentifier)).thenCallRealMethod();
        ItemRefileResponse response = nyplApiServiceConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }
    @Test
    public void refileItemHttpClientErrorException() throws Exception {
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        ResponseEntity<RefileResponse> responseEntity = new ResponseEntity<RefileResponse>(getRefileResponse(),HttpStatus.OK);
        RefileResponse refileResponse = responseEntity.getBody();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer");
        RefileRequest refileRequest = new RefileRequest();
        refileRequest.setItemBarcode(itemIdentifier);
        String apiUrl = nyplDataApiUrl + "/recap/refile-requests";
        HttpEntity<RefileRequest> requestEntity = new HttpEntity<>(refileRequest, getHttpHeaders());
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.getRefileRequest()).thenReturn(refileRequest);
        Mockito.when(nyplApiServiceConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplApiServiceConnector.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplApiServiceConnector.getNyplDataApiUrl()).thenReturn(nyplDataApiUrl);
        Mockito.when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, RefileResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Mockito.when(nyplApiServiceConnector.refileItem(itemIdentifier)).thenCallRealMethod();
        ItemRefileResponse response = nyplApiServiceConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }
    @Test
    public void refileItemException() throws Exception {
        String itemIdentifier = getBibEntity().getItemEntities().get(0).getBarcode();
        Mockito.when(nyplApiServiceConnector.getLogger()).thenReturn(logger);
        Mockito.when(nyplApiServiceConnector.refileItem(itemIdentifier)).thenCallRealMethod();
        ItemRefileResponse response = nyplApiServiceConnector.refileItem(itemIdentifier);
        assertNotNull(response);
    }

    private ItemRefileResponse getItemRefileResponse() {
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setJobId("1");
        itemRefileResponse.setItemBarcode("234435");
        itemRefileResponse.setSuccess(true);
        itemRefileResponse.setRequestId(1);
        return itemRefileResponse;
    }

    private RefileResponse getRefileResponse(){
        RefileResponse refileResponse = new RefileResponse();
        refileResponse.setCount(1);
        RefileData refileData = new RefileData();
        refileData.setCreatedDate(new Date().toString());
        refileData.setId(1);
        refileData.setItemBarcode("123456");
        refileData.setJobId("1");
        refileData.setUpdatedDate(new Date());
        refileResponse.setData(refileData);
        return refileResponse;
    }
    private RecallResponse getRecallResponse(){
        RecallResponse recallResponse = new RecallResponse();
        recallResponse.setCount(1);
        recallResponse.setStatusCode(1);
        RecallData recallData = new RecallData();
        recallData.setCreatedDate(new Date().toString());
        recallData.setId(1);
        recallData.setJobId("1");
        recallData.setOwningInstitutionId("1");
        recallData.setItemBarcode("123456");
        recallData.setUpdatedDate(new Date());
        recallResponse.setData(recallData);
        return recallResponse;
    }
    public CheckoutResponse getCheckOutItemResponse(){
        CheckoutResponse checkoutResponse = new CheckoutResponse();
        CheckoutData checkoutData = new CheckoutData();
        checkoutData.setId(123);
        checkoutData.setJobId("14214");
        checkoutData.setProcessed(true);
        checkoutData.setSuccess(true);
        checkoutData.setUpdatedDate("2017-03-30");
        checkoutData.setCreatedDate("2017-03-30");
        checkoutData.setPatronBarcode("23333095887111");
        checkoutData.setItemBarcode("33433001888415");
        checkoutData.setDesiredDateDue("2017-03-30");
        checkoutResponse.setData(checkoutData);
        checkoutResponse.setCount(1);
        checkoutResponse.setStatusCode(1);
        checkoutResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        return checkoutResponse;
    }

    public CheckinResponse getCheckinResponse(){
        CheckinResponse checkinResponse = new CheckinResponse();
        CheckinData checkinData = new CheckinData();
        checkinData.setId(1);
        checkinData.setJobId("123");
        checkinData.setProcessed(true);
        checkinData.setSuccess(true);
        checkinData.setUpdatedDate("2017-03-30");
        checkinData.setCreatedDate("2017-03-30");
        checkinData.setItemBarcode("33433001888415");
        checkinResponse.setData(checkinData);
        checkinResponse.setCount(1);
        checkinResponse.setStatusCode(1);
        checkinResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        assertNotNull(checkinData.getId());
        return checkinResponse;
    }

    public ItemResponse getItemResponse(){
        ItemResponse itemResponse = new ItemResponse();
        ItemData itemData = new ItemData();
        VarField varField = new VarField();
        SubField subField = new SubField();
        subField.setContent("test");
        subField.setTag("test");
        varField.setContent("test");
        varField.setFieldTag("test");
        varField.setInd1("test");
        varField.setInd2("test");
        varField.setFieldTag("test");
        varField.setSubFields(Arrays.asList(subField));

        itemData.setNyplSource("test");
        itemData.setBibIds(Arrays.asList("123"));
        itemData.setId("468");
        itemData.setNyplType("test");
        itemData.setUpdatedDate(new Date().toString());
        itemData.setDeletedDate(new Date().toString());
        itemData.setDeleted(true);
        itemData.setBarcode("33236547125452");
        itemData.setCallNumber("12");
        itemData.setItemType("test");
        itemData.setFixedFields("test");
        itemData.setVarFields(Arrays.asList(varField));

        itemResponse.setItemData(itemData);
        itemResponse.setCount(1);
        itemResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        itemResponse.setStatusCode(1);
        return itemResponse;
    }

    public ItemInformationResponse getItemInformationResponse(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setCirculationStatus("test");
        itemInformationResponse.setSecurityMarker("test");
        itemInformationResponse.setFeeType("test");
        itemInformationResponse.setTransactionDate(new Date().toString());
        itemInformationResponse.setHoldQueueLength("10");
        itemInformationResponse.setTitleIdentifier("test");
        itemInformationResponse.setBibID("1223");
        itemInformationResponse.setDueDate(new Date().toString());
        itemInformationResponse.setExpirationDate(new Date().toString());
        itemInformationResponse.setRecallDate(new Date().toString());
        itemInformationResponse.setCurrentLocation("test");
        itemInformationResponse.setHoldPickupDate(new Date().toString());
        itemInformationResponse.setItemBarcode("123");
        itemInformationResponse.setSuccess(true);
        return itemInformationResponse;
    }


    public BibliographicEntity getBibEntity() throws Exception {

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(2);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setCatalogingStatus("Complete");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(2);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(2);
        itemEntity.setBarcode("0002");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    private HttpHeaders getHttpHeaders() throws Exception {
        String authorization = "Bearer " + nyplOauthTokenApiService.generateAccessTokenForNyplApi(getOperatorUserId(), getOperatorPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);
        return headers;
    }

    public CreateHoldResponse getCreateHoldResponse(){
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

    public NYPLHoldResponse getNyplHoldResponse(){
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

    public CancelHoldResponse getCancelHoldResponse(){
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

}