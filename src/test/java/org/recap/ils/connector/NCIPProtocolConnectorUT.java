package org.recap.ils.connector;

import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.extensiblecatalog.ncip.v2.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.ncip.CheckinItem;
import org.recap.ils.protocol.rest.model.BibLookupData;
import org.recap.ils.protocol.rest.model.ItemLookupData;
import org.recap.ils.protocol.rest.model.response.ItemLookupResponse;
import org.recap.ils.protocol.rest.util.RestApiResponseUtil;
import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.response.ItemLookUpInformationResponse;
import org.recap.model.response.PatronInformationResponse;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.PropertyUtil;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


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
    PropertyUtil propertyUtil;

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
    AbstractResponseItem abstractResponseItem;

    @Mock
    RestTemplate restTemplate;

    @Mock
    org.springframework.http.HttpHeaders headers;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    CheckinItem checkinItem;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkGetters() {
        ncipProtocolConnector.getNcipScheme();
        ncipProtocolConnector.getEndPointUrl();
        ncipProtocolConnector.getRestTemplate();
        ncipProtocolConnector.getRestApiResponseUtil();
        ncipProtocolConnector.getHttpHeader();
        ncipProtocolConnector.getItemDetailsRepository();
        ncipProtocolConnector.getBibDataApiUrl();
//        Mockito.when(ilsConfigProperties.getIlsRestDataApi()).thenReturn("");
    }

    @Test
    public void supports() {
        boolean result = ncipProtocolConnector.supports("NCIP");
        assertNotNull(result);
    }

    @Test
    public void setInstitution() {
        ncipProtocolConnector.setInstitution("PUL");
    }

    @Test
    public void setIlsConfigProperties() {
        ncipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
    }

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
    public void lookupItem() throws Exception {
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupItem("13245676");
        assertNotNull(abstractResponseItem);
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
        ReflectionTestUtils.setField(ncipProtocolConnector, "institutionCode", "PUL");
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(itemDetailsRepository.findByBarcode(itemIdentifier)).thenReturn(Arrays.asList(getItemEntity()));
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), anyString())).thenReturn("TRUE");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndLocationAndKey(any(), any(), anyString())).thenReturn("test");
        Mockito.doReturn(getCheckInItemResponseData()).when(ncipProtocolConnector).getCheckinResponse(any(), any());
        Object result = ncipProtocolConnector.checkInItem(getItemRequestInformation(), patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkInItemNotInRemote() throws IOException {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        CheckInItemResponseData checkInItemResponseData = Mockito.mock(CheckInItemResponseData.class);
        Mockito.when(checkInItemResponseData.toString()).thenReturn("CheckInItemResponseData");
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), anyString())).thenReturn("FALSE");
        Mockito.when(itemDetailsRepository.findByBarcode(itemIdentifier)).thenReturn(Arrays.asList(getItemEntity()));
        Mockito.doReturn(checkInItemResponseData)
                .when(ncipProtocolConnector)
                .getCheckinResponse(any(), any());

        Object result = ncipProtocolConnector.checkInItem(getItemRequestInformation(), patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkInItemSameInstutionWithProblems() throws IOException {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestType("EDD");
        getMockedResponse();
        ReflectionTestUtils.setField(ncipProtocolConnector, "institutionCode", "CUL");
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), anyString())).thenReturn("TRUE");
        Mockito.when(itemDetailsRepository.findByBarcode(itemIdentifier)).thenReturn(Arrays.asList(getItemEntity()));
        Mockito.doReturn(getCheckInItemResponseData()).when(ncipProtocolConnector).getCheckinResponse(any(), any());
        Object result = ncipProtocolConnector.checkInItem(itemRequestInformation, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkInItemSameInstutionWithoutProblems() throws IOException {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestType("EDD");

        CheckInItemResponseData checkInItemResponseData = getCheckInItemResponseData();
        checkInItemResponseData.setProblems(Collections.EMPTY_LIST);

        getMockedResponse();
        ReflectionTestUtils.setField(ncipProtocolConnector, "institutionCode", "CUL");
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), anyString())).thenReturn("TRUE");
        Mockito.when(itemDetailsRepository.findByBarcode(itemIdentifier)).thenReturn(Arrays.asList(getItemEntity()));

        Mockito.doReturn(checkInItemResponseData)
                .when(ncipProtocolConnector)
                .getCheckinResponse(any(), any());

        Object result = ncipProtocolConnector.checkInItem(itemRequestInformation, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void getCheckinResponse() throws Exception {
        CheckinItem checkInItem = new CheckinItem();
        CheckInItemInitiationData checkInItemInitiationData = new CheckInItemInitiationData();
        CheckInItemResponseData mockResponse = Mockito.mock(CheckInItemResponseData.class);
        assertNotNull(checkInItem);
        assertNotNull(checkInItemInitiationData);
    }

    @Test
    public void acceptItem() throws IOException {
        String itemIdentifier = "223467";
        Integer requestId = 1;
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "24";
        String pickupLocation = "PA";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        getMockedResponse();
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(getItemEntity()));
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn("1");
        ReflectionTestUtils.invokeMethod(ncipProtocolConnector, "acceptItem", itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, pickupLocation, title, author, callNumber);
    }

    @Test
    public void acceptItemException() throws IOException {
        String itemIdentifier = "223467";
        Integer requestId = 1;
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "24";
        String pickupLocation = "PA";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setUseRestrictions("1");
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn("1");
        ReflectionTestUtils.invokeMethod(ncipProtocolConnector, "acceptItem", itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, pickupLocation, title, author, callNumber);
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
        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setSuccess(Boolean.TRUE);
        getMockedResponse();
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.doReturn(patronInformationResponse).when(ncipProtocolConnector).lookupPatron(patronIdentifier);
        Object result = ncipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(result);
    }

    @Test
    public void placeHoldSameInstitution() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "1";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";
        String trackingId = "67878890";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        Integer requestId = 2;
        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setSuccess(Boolean.TRUE);
        getMockedResponse();
        Mockito.doReturn(patronInformationResponse).when(ncipProtocolConnector).lookupPatron(patronIdentifier);
        Object result = ncipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(result);
    }

    @Test
    public void placeHoldDifferentInstitution() throws IOException {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "4";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";
        String trackingId = "67878890";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        Integer requestId = 2;
        getMockedResponse();
        Object result = ncipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(result);
    }

    @Test
    public void getParamsMap() {
        Map<String, String> map = ncipProtocolConnector.getParamsMap("1", "1", "1");
        assertNotNull(map);
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
        assertNotNull(result);
        assertTrue(result);
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

    @Test
    public void buildResponse() {
        ItemLookupResponse itemLookupResponse = getItemLookupResponse();
        ItemLookUpInformationResponse response = ncipProtocolConnector.buildResponse(itemLookupResponse);
        assertNotNull(response);
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

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.singletonList("123456"));
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setRequestType("RETRIEVAL");
        return itemRequestInformation;
    }

    private CheckInItemResponseData getCheckInItemResponseData() {
        CheckInItemResponseData checkInItemResponseData = new CheckInItemResponseData();
        ItemId itemId = new ItemId();
        itemId.setItemIdentifierValue("24365");
        Problem problem = getProblem();
        ItemOptionalFields itemOptionalFields = new ItemOptionalFields();
        itemOptionalFields.setDateDue(new GregorianCalendar());
        checkInItemResponseData.setItemId(itemId);
        checkInItemResponseData.setProblems(Arrays.asList(problem));
        checkInItemResponseData.setItemOptionalFields(itemOptionalFields);
        return checkInItemResponseData;
    }

    private Problem getProblem() {
        Problem problem = new Problem();
        ProblemType problemType = new ProblemType("43656");
        problem.setProblemType(problemType);
        problem.setProblemDetail("Bad Request");
        problem.setProblemValue("43656");
        problem.setProblemElement("Error");
        return problem;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        return itemEntity;
    }
}
