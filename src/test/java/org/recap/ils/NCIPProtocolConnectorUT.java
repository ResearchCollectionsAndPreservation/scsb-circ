package org.recap.ils;

import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.extensiblecatalog.ncip.v2.common.Translator;
import org.extensiblecatalog.ncip.v2.service.ServiceContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.BibLookupData;
import org.recap.ils.model.nypl.ItemLookupData;
import org.recap.ils.model.nypl.response.ItemLookupResponse;
import org.recap.ils.model.nypl.response.RefileResponse;
import org.recap.ils.service.RestApiResponseUtil;
import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;


public class NCIPProtocolConnectorUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    NCIPProtocolConnector ncipProtocolConnector;

    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Mock
    RestApiResponseUtil restApiResponseUtil;

    @Mock
    CloseableHttpClient client;

    @Mock
    Header header;

    @Mock
    ByteArrayInputStream requestMessageStream;

    @Mock
    CloseableHttpResponse httpResponse;

    @Mock
    HttpEntity httpEntity;

    @Mock
    InputStream inputStream;

    @Mock
    NameValuePair nameValuePair;

    @Mock
    StatusLine statusLine;

    @Mock
    RestTemplate restTemplate;

    @Test
    public void checkGetters(){
        ncipProtocolConnector.getNcipScheme();
        ncipProtocolConnector.getEndPointUrl();
        ncipProtocolConnector.getRestTemplate();
        ncipProtocolConnector.getRestApiResponseUtil();
    }
    @Test
    public void supports() {
        boolean result = ncipProtocolConnector.supports("NCIP");
        assertNotNull(result);
    }

    @Test
    public void setInstitution() {
        ncipProtocolConnector.setInstitution("NYPL");
    }

    @Test
    public void setIlsConfigProperties() {
        ncipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
    }

   /* @Test
    public void lookupItem() throws Exception {
        ItemLookupResponse itemLookupResponse = getItemLookupResponse();
        ResponseEntity<ItemLookupResponse> responseEntity = new ResponseEntity<>(itemLookupResponse, HttpStatus.OK);
        Mockito.when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(any())).thenReturn("PUL");
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupItem("13245676");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void lookupItemHttpClientErrorException() throws Exception {
        Mockito.when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(any())).thenReturn("PUL");
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupItem("13245676");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void lookupItemException() throws Exception {
        Mockito.when(restApiResponseUtil.getItemOwningInstitutionByItemBarcode(any())).thenReturn("PUL");
        doThrow(new NullPointerException()).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<RefileResponse>>any());
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupItem("13245676");
        assertNotNull(abstractResponseItem);
    }

*/

    @Test
    public void checkOutItem() throws IOException {
        String itemIdentifier = "23456";
        Integer requestId = 2;
        String patronIdentifier = "43567";
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Object result = ncipProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(result);
    }


    @Test
    public void checkOutItemException() throws IOException {
        String itemIdentifier = "23456";
        Integer requestId = 2;
        String patronIdentifier = "43567";
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        Object result = ncipProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkOutItemHttpClientErrorException() throws IOException {
        String itemIdentifier = "23456";
        Integer requestId = 2;
        String patronIdentifier = "43567";
        getHttpClientErrorException();
        Object result = ncipProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkInItem() throws IOException {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Object result = ncipProtocolConnector.checkInItem(itemIdentifier, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkInItemException() throws IOException {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        Object result = ncipProtocolConnector.checkInItem(itemIdentifier, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkInItemHttpClientErrorException() throws IOException {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        getHttpClientErrorException();
        Object result = ncipProtocolConnector.checkInItem(itemIdentifier, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void placeHold() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "24";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";
        String trackingId = "67878890";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        Integer requestId = 2;
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Object result = ncipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(result);
    }

    @Test
    public void placeHoldException() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "24";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";
        String trackingId = "67878890";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        Integer requestId = 2;
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        Object result = ncipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(result);
    }

    @Test
    public void placeHoldHttpClientErrorException() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "24";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";
        String trackingId = "67878890";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        Integer requestId = 2;
        getHttpClientErrorException();
        Object result = ncipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(result);
    }

    @Test
    public void cancelHold() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        Integer requestId = 2;
        String pickupLocation = "PA";
        String trackingId = "67878890";
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Object result = ncipProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(result);
    }

    @Test
    public void cancelHoldException() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        Integer requestId = 2;
        String pickupLocation = "PA";
        String trackingId = "67878890";
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        Object result = ncipProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(result);
    }

    @Test
    public void cancelHoldHttpClientErrorException() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        Integer requestId = 2;
        String pickupLocation = "PA";
        String trackingId = "67878890";
        getHttpClientErrorException();
        Object result = ncipProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
        assertNotNull(result);
    }

    @Test
    public void createBib() {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String titleIdentifier = "245";
        Object result = ncipProtocolConnector.createBib(itemIdentifier, patronIdentifier, institutionId, titleIdentifier);
        assertNull(result);
    }

    @Test
    public void patronValidation() {
        boolean result = ncipProtocolConnector.patronValidation("23434", "234563");
        assertFalse(result);
    }

    @Test
    public void lookupPatron() throws IOException {
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupPatron("234566");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void lookupPatronException() throws IOException {
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupPatron("234566");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void lookupPatronHttpClientErrorException() throws IOException {
        getHttpClientErrorException();
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupPatron("234566");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void recallItem() throws IOException {
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Object result = ncipProtocolConnector.recallItem("225563", "345677", "1", new Date().toString(), "345622", "PA");
        assertNotNull(result);
    }

    @Test
    public void recallItemException() throws IOException {
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        Object result = ncipProtocolConnector.recallItem("225563", "345677", "1", new Date().toString(), "345622", "PA");
        assertNotNull(result);
    }

    @Test
    public void recallItemHttpClientErrorException() throws IOException {
        getMockedResponse();
        Object result = ncipProtocolConnector.recallItem("225563", "345677", "1", new Date().toString(), "345622", "PA");
        assertNotNull(result);
    }

    @Test
    public void refileItem() {
        Object result = ncipProtocolConnector.refileItem("23556");
        assertNull(result);
    }

    private ItemLookupResponse getItemLookupResponse() {
        ItemLookupResponse itemLookupResponse = new ItemLookupResponse();
        ItemLookupData itemLookupData = new ItemLookupData();
        Map processTypeList = new HashMap<>();
        processTypeList.put("value", "3456");
        itemLookupData.setBarcode("2344556");
        itemLookupData.setProcesstype(processTypeList);
        BibLookupData bibLookupData = new BibLookupData();
        bibLookupData.setBibId("24561");
        bibLookupData.setTitle("test");
        itemLookupResponse.setItemLookupData(itemLookupData);
        itemLookupResponse.setBibLookupData(bibLookupData);
        return itemLookupResponse;
    }

    private void getMockedResponse() throws IOException {
        inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
        HeaderElement elements = Mockito.mock(HeaderElement.class);
        HeaderElement[] headerElements = {elements};
        NameValuePair[] param = {nameValuePair};
        Mockito.when(header.getElements()).thenReturn(headerElements);
        Mockito.when(ncipProtocolConnector.buildCloseableHttpClient()).thenReturn(client);
        Mockito.when(client.execute(any())).thenReturn(httpResponse);
        Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        Mockito.when(httpEntity.getContentType()).thenReturn(header);
        Mockito.when(httpEntity.getContent()).thenReturn(inputStream);
        Mockito.when(httpEntity.getContentLength()).thenReturn(-1L);
        Mockito.when(elements.getName()).thenReturn("testHeader");
        Mockito.when(elements.getParameters()).thenReturn(param);
        Mockito.when(nameValuePair.getName()).thenReturn("charset");
        Mockito.when(nameValuePair.getValue()).thenReturn(StandardCharsets.UTF_8.toString());
        Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    }

    private void getHttpClientErrorException() throws IOException {
        Mockito.when(ncipProtocolConnector.buildCloseableHttpClient()).thenReturn(client);
        Mockito.when(client.execute(any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
    }


}
