package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ils.*;
import org.recap.ils.model.response.*;
import org.recap.model.AbstractResponseItem;
import org.recap.model.BulkRequestInformation;
import org.recap.model.ItemRefileRequest;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.ItemResponseInformation;
import org.recap.model.jpa.ReplaceRequest;
import org.recap.request.ItemRequestService;
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


    private static final Logger logger = LoggerFactory.getLogger(RequestItemControllerUT.class);

    @InjectMocks
    RequestItemController mockedRequestItemController;

    @Mock
    ItemRequestService itemRequestService;

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
        try {
            ItemCheckoutResponse itemResponseInformation = (ItemCheckoutResponse) mockedRequestItemController.checkoutItem(itemRequestInformation, callInstitition);
            assertNotNull(itemResponseInformation);
        } catch (Exception e) {
        }
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
        itemRequestInformation.setItemBarcodes(Collections.EMPTY_LIST);
        itemRequestInformation.setPatronBarcode("198572368");
        itemRequestInformation.setRequestingInstitution(callInstitition);
        ItemCheckinResponse itemResponseInformation1 = new ItemCheckinResponse();
        itemResponseInformation1.setScreenMessage("CheckIn successfull");
        itemResponseInformation1.setSuccess(true);
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.checkinItem(itemRequestInformation, "PUL");
            assertNotNull(abstractResponseItem);
        }catch (Exception e){}
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
        }catch (Exception e){}
    }

    @Test
    public void testRefileItem() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123"));
        itemRefileRequest.setRequestIds(Arrays.asList(1));
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        Mockito.when(itemRequestService.reFileItem(any(),any())).thenReturn(itemRefileResponse);
        ItemRefileResponse refileResponse = mockedRequestItemController.refileItem(itemRefileRequest);
        assertNotNull(refileResponse);
    }

    public ItemHoldResponse getItemHoldResponse() {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        return itemHoldResponse;
    }

    private String getPickupLocation(String institution) {
        String pickUpLocation = "";
        if (institution.equalsIgnoreCase(RecapCommonConstants.PRINCETON)) {
            pickUpLocation = RecapConstants.DEFAULT_PICK_UP_LOCATION_PUL;
        } else if (institution.equalsIgnoreCase(RecapCommonConstants.COLUMBIA)) {
            pickUpLocation = RecapConstants.DEFAULT_PICK_UP_LOCATION_CUL;
        } else if (institution.equalsIgnoreCase(RecapCommonConstants.NYPL)) {
            pickUpLocation = RecapConstants.DEFAULT_PICK_UP_LOCATION_NYPL;
        }
        return pickUpLocation;
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
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.cancelHoldItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
            assertTrue(abstractResponseItem.isSuccess());
        }catch (Exception e){}
    }

    private String getPickupLocationDB(ItemRequestInformation itemRequestInformation, String callInstitution) {
        if (RecapCommonConstants.NYPL.equalsIgnoreCase(callInstitution)) {
            return itemRequestInformation.getDeliveryLocation();
        }
        return (StringUtils.isBlank(itemRequestInformation.getPickupLocation())) ? getPickupLocation(callInstitution) : itemRequestInformation.getPickupLocation();
    }

    @Test
    public void testHoldItemRequest() {
        String callInstitition = "PUL";
        String itembarcode = "PULTST54325";
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
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.holdItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
        } catch (Exception e) {
        }
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
        }catch (Exception e){}
    }

    @Test
    public void testBibCreation() {
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
        itemInformationResponse.setScreenMessage("Item Barcode already Exist");
        itemInformationResponse.setSuccess(true);
        try {
            Mockito.when((ItemInformationResponse) mockedRequestItemController.itemInformation(itemRequestInformation, itemRequestInformation.getRequestingInstitution())).thenReturn(itemInformationResponse);
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.createBibliogrphicItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
        }catch (Exception e){}
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
        }catch (Exception e){}
    }

    @Test
    public void testRecallItemRequest() {
        String callInstitition = "PUL";
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
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.recallItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
        } catch (Exception e) {
        }
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
        try {
            AbstractResponseItem abstractResponseItem = mockedRequestItemController.patronInformation(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
        } catch (Exception e) {
        }
    }


    @Test
    public void testJsonResponseParse() throws Exception {
        String strJson = "{\"patronBarcode\":null,\"itemBarcode\":\"32101095533293\",\"requestType\":null,\"deliveryLocation\":null,\"requestingInstitution\":null,\"bibliographicId\":null,\"expirationDate\":null,\"screenMessage\":\"Checkout Successful.\",\"success\":true,\"emailAddress\":null,\"startPage\":null,\"endPage\":null,\"titleIdentifier\":\"Accommodating Muslims under common law : a comparative analysis / Salim Farrar and Ghena Krayem.\",\"dueDate\":\"20170301    234500\"}";
        ObjectMapper om = new ObjectMapper();
        ItemResponseInformation itemResponseInformation = om.readValue(strJson, ItemResponseInformation.class);
        logger.info(itemResponseInformation.getScreenMessage());
    }

    @Test
    public void refileItem() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123456"));
        itemRefileRequest.setRequestIds(Arrays.asList(1));
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setRequestId(1);
        Mockito.when(itemRequestService.reFileItem(any(),any())).thenReturn(itemRefileResponse);
        ItemRefileResponse itemRefileResponse1 = mockedRequestItemController.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse1);
        itemRefileResponse.setSuccess(true);
        ItemRefileResponse itemRefileResponse2= mockedRequestItemController.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse2);
    }

    @Test
    public void patronValidationBulkRequest() {
        BulkRequestInformation bulkRequestInformation = new BulkRequestInformation();
        bulkRequestInformation.setPatronBarcode("123456");
        bulkRequestInformation.setRequestingInstitution("PUL");
        try {
            boolean result = mockedRequestItemController.patronValidationBulkRequest(bulkRequestInformation);
            assertNotNull(result);
        }catch (Exception e){}
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
        String pickUpLocation = mockedRequestItemController.getPickupLocation(institution);
        assertNotNull(pickUpLocation);
        assertEquals("CIRCrecap", pickUpLocation);
    }

    @Test
    public void getPickupLocationNYPL() {
        String institution = "NYPL";
        String pickUpLocation = mockedRequestItemController.getPickupLocation(institution);
        assertNotNull(pickUpLocation);
        assertEquals("lb", pickUpLocation);
    }

    @Test
    public void logMessages() {
        Object test = "test";
        Logger logger = LoggerFactory.getLogger(RequestItemController.class);
        mockedRequestItemController.logMessages(logger, test);
    }
}