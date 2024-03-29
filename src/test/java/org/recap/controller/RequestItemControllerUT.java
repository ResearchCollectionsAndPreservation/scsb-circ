package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ils.connector.AbstractProtocolConnector;
import org.recap.ils.connector.factory.ILSProtocolConnectorFactory;
import org.recap.model.AbstractResponseItem;
import org.recap.model.BulkRequestInformation;
import org.recap.model.ItemRefileRequest;
import org.recap.model.response.*;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.request.ReplaceRequest;
import org.recap.request.service.ItemRequestService;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by hemalathas on 11/11/16.
 */
public class RequestItemControllerUT extends BaseTestCaseUT {



    @InjectMocks
    RequestItemController mockedRequestItemController;

    @Mock
    AbstractProtocolConnector abstractProtocolConnector;

    @Mock
    PropertyUtil propertyUtil;
    @Mock
    ItemRequestService itemRequestService;
    @Mock
    private ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Test
    public void checkGetters() {
        mockedRequestItemController.getIlsProtocolConnectorFactory();
        mockedRequestItemController.getItemRequestService();
    }

    @Test
    public void testCheckoutItemRequest() {
        String callInstitition = "PUL";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.EMPTY_LIST);
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemCheckoutResponse itemResponseInformation1 = new ItemCheckoutResponse();
        itemResponseInformation1.setScreenMessage("Checkout successfull");
        itemResponseInformation1.setSuccess(true);
        ItemCheckoutResponse itemResponseInformation = (ItemCheckoutResponse) mockedRequestItemController.checkoutItem(itemRequestInformation, callInstitition);
        assertNotNull(itemResponseInformation);
    }

    @Test
    public void testCheckoutItemRequestException() {
        String callInstitition = "PUL";
        String itemBarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itemBarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemCheckoutResponse itemResponseInformation1 = new ItemCheckoutResponse();
        itemResponseInformation1.setScreenMessage("Checkout successfull");
        itemResponseInformation1.setSuccess(true);
        try {
            ItemCheckoutResponse itemResponseInformation = (ItemCheckoutResponse) mockedRequestItemController.checkoutItem(itemRequestInformation, callInstitition);
            assertNotNull(itemResponseInformation);
        } catch (Exception e) {
        }
    }

    @Test
    public void testCheckinItemRequest() {
        String callInstitition = "PUL";
        String itemBarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("24657"));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemCheckinResponse itemResponseInformation1 = new ItemCheckinResponse();
        itemResponseInformation1.setScreenMessage("CheckIn successfull");
        itemResponseInformation1.setSuccess(true);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.checkinItem(itemRequestInformation, "PUL");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testCheckinItemRequestEmptyBarcode() {
        String callInstitition = "PUL";
        String itemBarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.EMPTY_LIST);
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemCheckinResponse itemResponseInformation1 = new ItemCheckinResponse();
        itemResponseInformation1.setScreenMessage("CheckIn successfull");
        itemResponseInformation1.setSuccess(true);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.checkinItem(itemRequestInformation, "PUL");
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testCheckinItemRequestException() {
        String callInstitition = "PUL";
        String itemBarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itemBarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemCheckinResponse itemResponseInformation1 = new ItemCheckinResponse();
        itemResponseInformation1.setScreenMessage("CheckIn successfull");
        itemResponseInformation1.setSuccess(true);
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.checkinItem(itemRequestInformation, "PUL");
            assertNotNull(abstractResponseItem);
        } catch (Exception e) {
        }
    }

    @Test
    public void testRefileItem() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123"));
        itemRefileRequest.setRequestIds(Arrays.asList(1));
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        Mockito.when(itemRequestService.reFileItem(any(), any())).thenReturn(itemRefileResponse);
        ItemRefileResponse refileResponse = mockedRequestItemController.refileItem(itemRefileRequest);
        assertNotNull(refileResponse);
    }

    public ItemHoldResponse getItemHoldResponse() {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        return itemHoldResponse;
    }

    private String getPickupLocation(String institution) {
        return "lb";
    }

    @Test
    public void testCancelHoldItemRequest() {
        String callInstitition = "PUL";
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itembarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemHoldResponse itemHoldResponse = getItemHoldResponse();
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(callInstitition, PropertyKeyConstants.ILS.ILS_DEFAULT_PICKUP_LOCATION)).thenReturn("PA");
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any()).cancelHold(itembarcode, itemRequestInformation.getRequestId(), itemRequestInformation.getPatronBarcode(),
                itemRequestInformation.getRequestingInstitution(),
                itemRequestInformation.getExpirationDate(),
                itemRequestInformation.getBibId(),
                "PA", itemRequestInformation.getTrackingId())).thenReturn(itemHoldResponse);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.cancelHoldItem(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }

    private String getPickupLocationDB(ItemRequestInformation itemRequestInformation, String callInstitution) {
        return (StringUtils.isBlank(itemRequestInformation.getPickupLocation())) ? getPickupLocation(callInstitution) : itemRequestInformation.getPickupLocation();
    }

    @Test
    public void testHoldItemRequest() {
        String callInstitution = "PUL";
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(callInstitution, itembarcode);
        ItemHoldResponse itemHoldResponse = getItemHoldResponse();
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(callInstitution, PropertyKeyConstants.ILS.ILS_USE_DELIVERY_LOCATION_AS_PICKUP_LOCATION)).thenReturn(Boolean.TRUE.toString());
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(abstractProtocolConnector.placeHold(itembarcode, itemRequestInformation.getRequestId(), itemRequestInformation.getPatronBarcode(),
                itemRequestInformation.getRequestingInstitution(),
                itemRequestInformation.getItemOwningInstitution(),
                itemRequestInformation.getExpirationDate(),
                itemRequestInformation.getBibId(),
                itemRequestInformation.getDeliveryLocation(),
                itemRequestInformation.getTrackingId(),
                itemRequestInformation.getTitleIdentifier(),
                itemRequestInformation.getAuthor(),
                itemRequestInformation.getCallNumber())).thenReturn(itemHoldResponse);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.holdItem(itemRequestInformation, callInstitution);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testHoldItemRequestException() {
        String callInstitition = "PUL";
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(callInstitition, itembarcode);
        ItemHoldResponse itemHoldResponse = getItemHoldResponse();
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.holdItem(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    private ItemRequestInformation getItemRequestInformation(String callInstitition, String itembarcode) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itembarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setItemOwningInstitution(callInstitition);
        itemRequestInformation.setCallNumber("X");
        itemRequestInformation.setAuthor("John");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        return itemRequestInformation;
    }

    @Test
    public void testItemInformation() {
        String callInstitition = "PUL";
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itembarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.itemInformation(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
            assertTrue(abstractResponseItem.isSuccess());
        } catch (Exception e) {
        }
    }

    @Test
    public void testBibCreation() {
        String callInstitition = null;
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itembarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setScreenMessage("Item Barcode already Exist");
        itemInformationResponse.setSuccess(true);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any()).lookupItem(itembarcode)).thenReturn(itemInformationResponse);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.createBibliogrphicItem(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testBibCreationItemNotFound() {
        String callInstitition = null;
        String itemBarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itemBarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setScreenMessage("ITEM BARCODE NOT FOUND.");
        itemInformationResponse.setSuccess(true);
        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setSuccess(true);
        itemCreateBibResponse.setScreenMessage("Success");
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any()).lookupItem(itemBarcode)).thenReturn(itemInformationResponse);
        Mockito.when(abstractProtocolConnector.createBib(itemBarcode, itemRequestInformation.getPatronBarcode(), itemRequestInformation.getRequestingInstitution(), itemRequestInformation.getTitleIdentifier())).thenReturn(itemCreateBibResponse);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.createBibliogrphicItem(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testBibCreationWithoutItemBarcode() {
        String callInstitition = "PUL";
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.EMPTY_LIST);
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.createBibliogrphicItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
        } catch (Exception e) {
        }
    }

    @Test
    public void testRecallItemRequest() {
        String callInstitition = "NYPL";
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itembarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setSuccess(true);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any()).recallItem(any(),any(),any(),any(),any(),any())).thenReturn(itemRecallResponse);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.recallItem(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testPatronInformation() {
        String callInstitition = "PUL";
        String itembarcode = "PULTST54325";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList(itembarcode));
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setExpirationDate(new Date().toString());
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setPatronIdentifier("198572368");
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any()).lookupPatron(itemRequestInformation.getPatronBarcode())).thenReturn(patronInformationResponse);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.patronInformation(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);

    }

    @Test
    public void refileItem() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123456"));
        itemRefileRequest.setRequestIds(Arrays.asList(1));
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setRequestId(1);
        Mockito.when(itemRequestService.reFileItem(any(), any())).thenReturn(itemRefileResponse);
        ItemRefileResponse itemRefileResponse1 = mockedRequestItemController.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse1);
        itemRefileResponse.setSuccess(true);
        ItemRefileResponse itemRefileResponse2 = mockedRequestItemController.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse2);
    }

    @Test
    public void patronValidationBulkRequest() {
        BulkRequestInformation bulkRequestInformation = new BulkRequestInformation();
        bulkRequestInformation.setPatronBarcode("123456");
        bulkRequestInformation.setRequestingInstitution("PUL");
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(bulkRequestInformation.getRequestingInstitution()).patronValidation(bulkRequestInformation.getRequestingInstitution(), bulkRequestInformation.getPatronBarcode())).thenReturn(true);
        boolean result = mockedRequestItemController.patronValidationBulkRequest(bulkRequestInformation);
        assertNotNull(result);
    }

    @Test
    public void refileItemInILSWithItemBarcode() {
        String callInstitition = "PUL";
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setJobId("1");
        itemRefileResponse.setRequestId(1);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101074849843"));
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.refileItemInILS(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void refileItemInILS() {
        String callInstitition = "PUL";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.EMPTY_LIST);
        AbstractResponseItem abstractResponseItem = mockedRequestItemController.refileItemInILS(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void replaceRequest() {
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setReplaceRequestByType("EDD");
        Map<String, String> result = mockedRequestItemController.replaceRequest(replaceRequest);
        assertNotNull(result);
    }

    @Test
    public void getPickupLocationCUL() {
        String institution = "CUL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_DEFAULT_PICKUP_LOCATION)).thenReturn("CIRCrecap");
        String pickUpLocation = mockedRequestItemController.getPickupLocation(institution);
        assertNotNull(pickUpLocation);
        assertEquals("CIRCrecap", pickUpLocation);
    }

    @Test
    public void getPickupLocationREST() {
        String institution = "NYPL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn("lb");
        String pickUpLocation = mockedRequestItemController.getPickupLocation(institution);
        assertNotNull(pickUpLocation);
        assertEquals("lb", pickUpLocation);
    }

    @Test
    public void getPickupLocationPUL() {
        String institution = "PUL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_DEFAULT_PICKUP_LOCATION)).thenReturn("rcpcirc");
        String pickUpLocation = mockedRequestItemController.getPickupLocation(institution);
        assertNotNull(pickUpLocation);
        assertEquals("rcpcirc", pickUpLocation);
    }

    @Test
    public void logMessages() {
        Object test = "test";
        Logger logger = LoggerFactory.getLogger(RequestItemController.class);
        mockedRequestItemController.logMessages(logger, test);
    }
}