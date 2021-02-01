package org.recap.las;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.las.model.*;
import org.recap.model.IMSConfigProperties;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

public class GFALasImsLocationConnectorUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    GFALasImsLocationConnector gfaLasImsLocationConnector;

    @Mock
    IMSConfigProperties imsConfigProperties;

    @Mock
    private GFALasServiceUtil gfaLasServiceUtil;

    @Mock
    RestTemplate restTemplate = new RestTemplate();

    @Mock
    SimpleClientHttpRequestFactory simpleClientHttpRequestFactory;


    private String gfaLasStatus = "http://test:9092/lasapi/rest/lasapiSvc/lasStatus";

    private String gfaItemRetrival = "http://test:9092/lasapi/rest/lasapiSvc/retrieveItem";

    private String gfaItemEDDRetrival = "http://test:9092/lasapi/rest/lasapiSvc/retrieveEDD";

    private String gfaItemPermanentWithdrawlDirect = "http://test:9092/lasapi/rest/lasapiSvc/permanentlyRetrieveItem";

    private String gfaItemPermanentWithdrawlInDirect = "http://test:9092/lasapi/rest/lasapiSvc/permanentlyRetrieveItemIndirect";

    @Test
    public void supports() {
        Boolean result = gfaLasImsLocationConnector.supports("HD");
    }

    @Test
    public void checkGetters() {
        gfaLasImsLocationConnector.setImsLocationCode("HD");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        gfaLasImsLocationConnector.getRestTemplate();
    }

    @Test
    public void heartBeatCheck() throws JsonProcessingException {
        GFALasStatusCheckRequest gfaLasStatusCheckRequest = getGfaLasStatusCheckRequest();
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = getGfaLasStatusCheckResponse();
        ResponseEntity<GFALasStatusCheckResponse> responseEntity = new ResponseEntity<>(gfaLasStatusCheckResponse, HttpStatus.OK);
        ObjectMapper objectMapper = new ObjectMapper();
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasServerStatusEndpoint("http://test:9092/lasapi/rest/lasapiSvc/lasStatus");
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        String filterParamValue = objectMapper.writeValueAsString(gfaLasStatusCheckRequest);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://test:9092/lasapi/rest/lasapiSvc/lasStatus").queryParam(RecapConstants.GFA_SERVICE_PARAM, filterParamValue);
        HttpEntity requestEntity = new HttpEntity<>(new HttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.getRequestFactory()).thenReturn(simpleClientHttpRequestFactory);
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setConnectTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setReadTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFALasStatusCheckResponse.class)).thenReturn(responseEntity);
        GFALasStatusCheckResponse response = gfaLasImsLocationConnector.heartBeatCheck(gfaLasStatusCheckRequest);
        assertNotNull(response);
    }

    @Test
    public void heartBeatCheckException() {
        GFALasStatusCheckRequest gfaLasStatusCheckRequest = getGfaLasStatusCheckRequest();
        GFALasStatusCheckResponse response = gfaLasImsLocationConnector.heartBeatCheck(gfaLasStatusCheckRequest);
        assertNull(response);

    }

    @Test
    public void itemStatusCheck() throws JsonProcessingException {
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = getGfaItemStatusCheckRequest();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemStatusEndpoint("http://test:9092/lasapi/rest/lasapiSvc/lasItemStatus");
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAItemStatusCheckResponse> responseEntity = new ResponseEntity<>(gfaItemStatusCheckResponse, HttpStatus.OK);
        ObjectMapper objectMapper = new ObjectMapper();
        String filterParamValue = objectMapper.writeValueAsString(gfaItemStatusCheckRequest);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://test:9092/lasapi/rest/lasapiSvc/lasItemStatus").queryParam(RecapConstants.GFA_SERVICE_PARAM, filterParamValue);
        HttpEntity requestEntity = new HttpEntity<>(new HttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.getRequestFactory()).thenReturn(simpleClientHttpRequestFactory);
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setConnectTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setReadTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.when(restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFAItemStatusCheckResponse.class)).thenReturn(responseEntity);
        GFAItemStatusCheckResponse response = gfaLasImsLocationConnector.itemStatusCheck(gfaItemStatusCheckRequest);
        assertNotNull(response);
    }

    @Test
    public void itemStatusCheckException() {
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = getGfaItemStatusCheckRequest();
        GFAItemStatusCheckResponse response = gfaLasImsLocationConnector.itemStatusCheck(gfaItemStatusCheckRequest);
        assertNull(response);
    }

    @Test
    public void itemRetrieval() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = getGfaRetrieveItemRequest();
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemRetrival);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveItemRequest, getHttpHeaders());
        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.OK);
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemRetrival, HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaLasServiceUtil.getLASRetrieveResponse(any())).thenReturn(gfaRetrieveItemResponse);
        GFARetrieveItemResponse response = gfaLasImsLocationConnector.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemRetrievalWithErrorCode() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = getGfaRetrieveItemRequest();
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        gfaRetrieveItemResponse.getDsitem().getTtitem().get(0).setErrorCode("Error code");
        String gfaItemRetrival = "http://test:9092/lasapi/rest/lasapiSvc/retrieveItem";
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemRetrival);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveItemRequest, getHttpHeaders());
        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.OK);
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemRetrival, HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaLasServiceUtil.getLASRetrieveResponse(any())).thenReturn(gfaRetrieveItemResponse);
        GFARetrieveItemResponse response = gfaLasImsLocationConnector.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemRetrievalWithoutResponse() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = getGfaRetrieveItemRequest();
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveItemRequest, getHttpHeaders());
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemRetrival);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(HttpStatus.OK);
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemRetrival, HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        Mockito.when(gfaLasServiceUtil.getLASRetrieveResponse(any())).thenReturn(gfaRetrieveItemResponse);
        GFARetrieveItemResponse response = gfaLasImsLocationConnector.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemRetrievalFailureResponse() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = getGfaRetrieveItemRequest();
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveItemRequest, getHttpHeaders());
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemRetrival);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFARetrieveItemResponse> responseEntity = new ResponseEntity(gfaRetrieveItemResponse, HttpStatus.FORBIDDEN);
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemRetrival, HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenReturn(responseEntity);
        GFARetrieveItemResponse response = gfaLasImsLocationConnector.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemRetrievalHttpClientErrorException() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = getGfaRetrieveItemRequest();
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemRetrival);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveItemRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemRetrival, HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        GFARetrieveItemResponse response = gfaLasImsLocationConnector.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemRetrievalException() {
        GFARetrieveItemRequest gfaRetrieveItemRequest = getGfaRetrieveItemRequest();
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        GFARetrieveItemResponse response = gfaLasImsLocationConnector.itemRetrieval(gfaRetrieveItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemEDDRetrieval() throws JsonProcessingException {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        String gfaItemStatus = "http://test:9092/lasapi/rest/lasapiSvc/itemStatus";
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemStatus);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        imsConfigProperties.setLasItemEddOrderEndpoint(gfaItemEDDRetrival);
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAEddItemResponse> responseEntity1 = new ResponseEntity<>(gfaEddItemResponse, HttpStatus.OK);
        HttpEntity requestEntity1 = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemEDDRetrival, HttpMethod.POST, requestEntity1, GFAEddItemResponse.class)).thenReturn(responseEntity1);
        Mockito.when(gfaLasServiceUtil.getLASEddResponse(any())).thenReturn(gfaEddItemResponse);
        GFAEddItemResponse response = gfaLasImsLocationConnector.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemEDDRetrievalWithoutGfaItemStatusCheckResponse() {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        GFAEddItemResponse response = gfaLasImsLocationConnector.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemEDDRetrievalWithErrorCode() throws JsonProcessingException {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        gfaEddItemResponse.getDsitem().getTtitem().get(0).setErrorCode("001TE");
        String gfaItemStatus = "http://test:9092/lasapi/rest/lasapiSvc/itemStatus";
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemStatus);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        imsConfigProperties.setLasItemEddOrderEndpoint(gfaItemEDDRetrival);
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAEddItemResponse> responseEntity1 = new ResponseEntity<>(gfaEddItemResponse, HttpStatus.OK);
        HttpEntity requestEntity1 = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemEDDRetrival, HttpMethod.POST, requestEntity1, GFAEddItemResponse.class)).thenReturn(responseEntity1);
        Mockito.when(gfaLasServiceUtil.getLASEddResponse(any())).thenReturn(gfaEddItemResponse);
        GFAEddItemResponse response = gfaLasImsLocationConnector.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemEDDRetrievalWithNewResponse() throws JsonProcessingException {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        gfaEddItemResponse.getDsitem().getTtitem().get(0).setErrorCode("001TE");
        String gfaItemStatus = "http://test:9092/lasapi/rest/lasapiSvc/itemStatus";
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemStatus);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        imsConfigProperties.setLasItemEddOrderEndpoint(gfaItemEDDRetrival);
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAEddItemResponse> responseEntity1 = new ResponseEntity<>(HttpStatus.OK);
        HttpEntity requestEntity1 = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemEDDRetrival, HttpMethod.POST, requestEntity1, GFAEddItemResponse.class)).thenReturn(responseEntity1);
        Mockito.when(gfaLasServiceUtil.getLASEddResponse(any())).thenReturn(gfaEddItemResponse);
        GFAEddItemResponse response = gfaLasImsLocationConnector.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemEDDRetrievalWithBadResponse() throws JsonProcessingException {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        gfaEddItemResponse.getDsitem().getTtitem().get(0).setErrorCode("001TE");
        String gfaItemStatus = "http://test:9092/lasapi/rest/lasapiSvc/itemStatus";
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemStatus);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        imsConfigProperties.setLasItemEddOrderEndpoint(gfaItemEDDRetrival);
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAEddItemResponse> responseEntity1 = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        HttpEntity requestEntity1 = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemEDDRetrival, HttpMethod.POST, requestEntity1, GFAEddItemResponse.class)).thenReturn(responseEntity1);
        GFAEddItemResponse response = gfaLasImsLocationConnector.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemEDDRetrievalWithFailureResponse() throws JsonProcessingException {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        String gfaItemStatus = "http://test:9092/lasapi/rest/lasapiSvc/itemStatus";
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemStatus);
        imsConfigProperties.setLasItemEddOrderEndpoint(gfaItemEDDRetrival);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAEddItemResponse> responseEntity1 = new ResponseEntity<>(gfaEddItemResponse, HttpStatus.OK);
        HttpEntity requestEntity1 = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemEDDRetrival, HttpMethod.POST, requestEntity1, GFAEddItemResponse.class)).thenReturn(responseEntity1);
        Mockito.when(gfaLasServiceUtil.getLASEddResponse(any())).thenReturn(gfaEddItemResponse);
        GFAEddItemResponse response = gfaLasImsLocationConnector.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        assertNotNull(response);
    }

    @Test
    public void itemEDDRetrievalHttpClientErrorException() throws JsonProcessingException {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
        String gfaItemStatus = "http://test:9092/lasapi/rest/lasapiSvc/itemStatus";
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasItemRetrievalOrderEndpoint(gfaItemStatus);
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        imsConfigProperties.setLasItemEddOrderEndpoint(gfaItemEDDRetrival);
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        HttpEntity requestEntity1 = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange(gfaItemEDDRetrival, HttpMethod.POST, requestEntity1, GFAEddItemResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        GFAEddItemResponse response = gfaLasImsLocationConnector.itemEDDRetrieval(gfaRetrieveEDDItemRequest);
        assertNotNull(response);
    }

    @Test
    public void gfaPermanentWithdrawlDirect() {
        GFAPwdRequest gfaPwdRequest = getGfaPwdRequest();
        GFAPwdResponse gfaPwdResponse = getGfaPwdResponse();
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasServerStatusEndpoint("http://test:9092/lasapi/rest/lasapiSvc/lasStatus");
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        imsConfigProperties.setLasPermanentWithdrawalDirectEndpoint(gfaItemPermanentWithdrawlDirect);
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAPwdResponse> responseEntity = new ResponseEntity<>(gfaPwdResponse, HttpStatus.OK);
        HttpEntity<GFAPwdRequest> requestEntity = new HttpEntity<>(gfaPwdRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(gfaLasServiceUtil.convertJsonToString(requestEntity.getBody())).thenReturn("");
        Mockito.when(restTemplate.getRequestFactory()).thenReturn(simpleClientHttpRequestFactory);
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setConnectTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setReadTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.when(restTemplate.exchange(gfaItemPermanentWithdrawlDirect, HttpMethod.POST, requestEntity, GFAPwdResponse.class)).thenReturn(responseEntity);
        GFAPwdResponse response = gfaLasImsLocationConnector.gfaPermanentWithdrawalDirect(gfaPwdRequest);
        assertNotNull(response);
    }
    @Test
    public void gfaPermanentWithdrawlDirectException() {
        GFAPwdRequest gfaPwdRequest = getGfaPwdRequest();
        gfaLasImsLocationConnector.gfaPermanentWithdrawalDirect(gfaPwdRequest);
    }

    @Test
    public void gfaPermanentWithdrawlInDirect() {
        GFAPwiRequest gfaPwiRequest = getGFAPwiRequest();
        GFAPwiResponse gfaPwiResponse = getGfaPwiResponse();
        IMSConfigProperties imsConfigProperties = new IMSConfigProperties();
        imsConfigProperties.setLasServerStatusEndpoint("http://test:9092/lasapi/rest/lasapiSvc/lasStatus");
        imsConfigProperties.setLasServerResponseTimeoutMillis("1000");
        imsConfigProperties.setLasPermanentWithdrawalIndirectEndpoint(gfaItemPermanentWithdrawlInDirect);
        gfaLasImsLocationConnector.setImsConfigProperties(imsConfigProperties);
        ResponseEntity<GFAPwiResponse> responseEntity = new ResponseEntity<>(gfaPwiResponse, HttpStatus.OK);
        HttpEntity<GFAPwiRequest> requestEntity = new HttpEntity<>(gfaPwiRequest, getHttpHeaders());
        Mockito.when(gfaLasImsLocationConnector.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.getRequestFactory()).thenReturn(simpleClientHttpRequestFactory);
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setConnectTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.doNothing().when(simpleClientHttpRequestFactory).setReadTimeout(Integer.parseInt(imsConfigProperties.getLasServerResponseTimeoutMillis()));
        Mockito.when(restTemplate.exchange(gfaItemPermanentWithdrawlInDirect, HttpMethod.POST, requestEntity, GFAPwiResponse.class)).thenReturn(responseEntity);
        GFAPwiResponse response = gfaLasImsLocationConnector.gfaPermanentWithdrawalInDirect(gfaPwiRequest);
        assertNotNull(response);
    }
    @Test
    public void gfaPermanentWithdrawlInDirectException() {
        GFAPwiRequest gfaPwiRequest = getGFAPwiRequest();
        gfaLasImsLocationConnector.gfaPermanentWithdrawalInDirect(gfaPwiRequest);
    }
    private GFALasStatusCheckRequest getGfaLasStatusCheckRequest() {
        GFALasStatusCheckRequest gfaLasStatusCheckRequest = new GFALasStatusCheckRequest();
        GFALasStatus gfaLasStatus = new GFALasStatus();
        gfaLasStatus.setImsLocationCode("PA");
        gfaLasStatusCheckRequest.setLasStatus(Arrays.asList(gfaLasStatus));
        return gfaLasStatusCheckRequest;
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

    private GFAItemStatusCheckRequest getGfaItemStatusCheckRequest() {
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("21345");
        gfaItemStatusCheckRequest.setItemStatus(Arrays.asList(gfaItemStatus));
        return gfaItemStatusCheckRequest;
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

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
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
        gfaRetrieveEDDItemRequest.setDsitem(retrieveItemEDDRequest);
        return gfaRetrieveEDDItemRequest;
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
        gfaPwdTtItemResponse.setRequestorEmail("hemalatha.s@htcindia.com");
        gfaPwdTtItemResponse.setRequestorOther("test");
        gfaPwdTtItemResponse.setPriority("first");
        gfaPwdTtItemResponse.setNotes("test");
        gfaPwdTtItemResponse.setRequestDate(new Date());
        gfaPwdTtItemResponse.setRequestTime("");
        gfaPwdTtItemResponse.setErrorCode("test");
        gfaPwdTtItemResponse.setErrorNote("test");
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(gfaPwdTtItemResponse));
        gfaPwdDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwdDsItemResponse.setProdsHasChanges(true);
        gfaPwdResponse.setDsitem(gfaPwdDsItemResponse);
        return gfaPwdResponse;
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

}
