package org.recap.ils;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;


public class NCIPProtocolConnectorUT extends BaseTestCaseUT {

    @InjectMocks
    NCIPProtocolConnector ncipProtocolConnector;

    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Mock
    RestApiResponseUtil restApiResponseUtil;

    @Mock
    RestTemplate restTemplate;

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

    @Test
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

    @Test
    public void checkOutItem() {
        String itemIdentifier = "23456";
        Integer requestId = 2;
        String patronIdentifier = "43567";
        Object result = ncipProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
        assertNotNull(result);
    }

    @Test
    public void checkInItem() {
        Object result = ncipProtocolConnector.checkInItem("24456", "255677");
        assertNotNull(result);
    }

    @Test
    public void placeHold() {
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
        Object result = ncipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
        assertNotNull(result);
    }

    @Test
    public void cancelHold() {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        Integer requestId = 2;
        String pickupLocation = "PA";
        String trackingId = "67878890";
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
    public void lookupPatron() {
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupPatron("234566");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void recallItem() throws IOException {
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
        bibLookupData.setTile("test");
        itemLookupResponse.setItemLookupData(itemLookupData);
        itemLookupResponse.setBibLookupData(bibLookupData);
        return itemLookupResponse;
    }

}
