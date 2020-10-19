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
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 11/11/16.
 */
public class RequestItemControllerUT extends BaseTestCase {


    private static final Logger logger = LoggerFactory.getLogger(RequestItemControllerUT.class);

    @InjectMocks
    RequestItemController mockedRequestItemController;
    @Mock
    RequestItemController requestItemController;
    @Mock
    PropertyUtil propertyUtil;
    @Autowired
    RequestItemController requestItemControllerAutowired;
    @Mock
    JSIPConnectorFactory jsipConectorFactory;
    @Mock
    AbstractProtocolConnector abstractProtocolConnector;

    @Mock
    private ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Mock
    ItemRequestService itemRequestService;
    @Mock
    private ColumbiaJSIPConnector columbiaJSIPConnector;
    @Mock
    private PrincetonJSIPConnector princetonJSIPConnector;

    @Mock
    private NyplApiConnector nyplAPIConnector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(requestItemController).build();
    }

    @Test
    public void testCheckoutItemRequest() {
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
            Mockito.when(requestItemController.getJsipConectorFactory()).thenReturn(jsipConectorFactory);
            Mockito.when(jsipConectorFactory.getPrincetonJSIPConnector()).thenReturn(princetonJSIPConnector);
            Mockito.when(jsipConectorFactory.getColumbiaJSIPConnector()).thenReturn(columbiaJSIPConnector);
            Mockito.when(jsipConectorFactory.getNyplAPIConnector()).thenReturn(nyplAPIConnector);
            Mockito.when(requestItemController.getJsipConectorFactory().getJSIPConnector(callInstitition)).thenCallRealMethod();
            Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(callInstitition).checkOutItem(itemBarcode, itemRequestInformation.getPatronBarcode())).thenReturn(itemResponseInformation1);
            Mockito.when((ItemCheckoutResponse) requestItemController.checkoutItem(itemRequestInformation, "PUL")).thenCallRealMethod();
            ItemCheckoutResponse itemResponseInformation = (ItemCheckoutResponse) requestItemController.checkoutItem(itemRequestInformation, "PUL");
            assertNotNull(itemResponseInformation);
            logger.info(itemResponseInformation.getTitleIdentifier());
            logger.info(itemResponseInformation.getScreenMessage());
            assertTrue(itemResponseInformation.isSuccess());
        } catch (Exception e) {
        }
    }

    @Test
    public void testCheckinItemRequest() {
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
            Mockito.when(requestItemController.getJsipConectorFactory()).thenReturn(jsipConectorFactory);
            Mockito.when(jsipConectorFactory.getPrincetonJSIPConnector()).thenReturn(princetonJSIPConnector);
            Mockito.when(jsipConectorFactory.getColumbiaJSIPConnector()).thenReturn(columbiaJSIPConnector);
            Mockito.when(jsipConectorFactory.getNyplAPIConnector()).thenReturn(nyplAPIConnector);
            Mockito.when(requestItemController.getJsipConectorFactory().getJSIPConnector(callInstitition)).thenCallRealMethod();
            Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(callInstitition).checkInItem(itemBarcode, itemRequestInformation.getPatronBarcode())).thenReturn(itemResponseInformation1);
            Mockito.when((ItemCheckoutResponse) requestItemController.checkinItem(itemRequestInformation, "PUL")).thenCallRealMethod();
            AbstractResponseItem abstractResponseItem = requestItemController.checkinItem(itemRequestInformation, "PUL");
            assertNotNull(abstractResponseItem);
            assertTrue(abstractResponseItem.isSuccess());
        }catch (Exception e){}
    }

    @Test
    public void testRefileItem() {
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123"));
        itemRefileRequest.setRequestIds(Arrays.asList(1));
        ItemRefileResponse refileResponse = requestItemController.refileItem(itemRefileRequest);
        assertNull(refileResponse);
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
           /* Mockito.when(requestItemController.getIlsProtocolConnectorFactory().getIlsProtocolConnector(callInstitition)).thenReturn(abstractProtocolConnector);
            Mockito.when(requestItemController.getIlsProtocolConnectorFactory()).thenReturn(ilsProtocolConnectorFactory);
            Mockito.when((ItemHoldResponse) requestItemController.getIlsProtocolConnectorFactory().getIlsProtocolConnector(callInstitition).cancelHold(itembarcode, itemRequestInformation.getPatronBarcode(),
                    itemRequestInformation.getRequestingInstitution(),
                    itemRequestInformation.getExpirationDate(),
                    itemRequestInformation.getBibId(),
                    getPickupLocationDB(itemRequestInformation, callInstitition), itemRequestInformation.getTrackingId())).thenReturn(getItemHoldResponse());*/
            //Mockito.when((ItemHoldResponse) requestItemController.getIlsProtocolConnectorFactory().getIlsProtocolConnector(any()).cancelHold(any(), anyString(),anyString(), anyString(),any(),anyString(),anyString())).thenReturn(getItemHoldResponse());
            Mockito.when((ItemInformationResponse) requestItemController.cancelHoldItem(itemRequestInformation, callInstitition)).thenCallRealMethod();
            AbstractResponseItem abstractResponseItem = requestItemController.cancelHoldItem(itemRequestInformation, callInstitition);
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
            Mockito.when(requestItemController.getJsipConectorFactory()).thenReturn(jsipConectorFactory);
            Mockito.when(jsipConectorFactory.getPrincetonJSIPConnector()).thenReturn(princetonJSIPConnector);
            Mockito.when(jsipConectorFactory.getColumbiaJSIPConnector()).thenReturn(columbiaJSIPConnector);
            Mockito.when(jsipConectorFactory.getNyplAPIConnector()).thenReturn(nyplAPIConnector);
            Mockito.when(requestItemController.getJsipConectorFactory().getJSIPConnector(callInstitition)).thenCallRealMethod();
            Mockito.when(requestItemController.getPickupLocation(callInstitition)).thenCallRealMethod();
            Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(callInstitition).placeHold(itembarcode, itemRequestInformation.getPatronBarcode(),
                    itemRequestInformation.getRequestingInstitution(),
                    itemRequestInformation.getItemOwningInstitution(),
                    itemRequestInformation.getExpirationDate(),
                    itemRequestInformation.getBibId(),
                    getPickupLocation(callInstitition),
                    itemRequestInformation.getTrackingId(),
                    itemRequestInformation.getTitleIdentifier(),
                    itemRequestInformation.getAuthor(),
                    itemRequestInformation.getCallNumber())).thenReturn(getItemHoldResponse());
            Mockito.when((ItemInformationResponse) requestItemController.holdItem(itemRequestInformation, callInstitition)).thenCallRealMethod();
            AbstractResponseItem abstractResponseItem = requestItemController.holdItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
            assertTrue(abstractResponseItem.isSuccess());
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
            Mockito.when(requestItemController.getJsipConectorFactory()).thenReturn(jsipConectorFactory);
            Mockito.when(jsipConectorFactory.getPrincetonJSIPConnector()).thenReturn(princetonJSIPConnector);
            Mockito.when(jsipConectorFactory.getColumbiaJSIPConnector()).thenReturn(columbiaJSIPConnector);
            Mockito.when(jsipConectorFactory.getNyplAPIConnector()).thenReturn(nyplAPIConnector);
            Mockito.when(requestItemController.getJsipConectorFactory().getJSIPConnector(callInstitition)).thenCallRealMethod();
            Mockito.when((ItemInformationResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInstitition).lookupItem(itembarcode)).thenReturn(itemInformationResponse);
            Mockito.when((ItemInformationResponse) requestItemController.itemInformation(itemRequestInformation, callInstitition)).thenCallRealMethod();

            AbstractResponseItem abstractResponseItem = requestItemController.itemInformation(itemRequestInformation, callInstitition);
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
            Mockito.when(requestItemController.getJsipConectorFactory()).thenReturn(jsipConectorFactory);
            Mockito.when(jsipConectorFactory.getPrincetonJSIPConnector()).thenReturn(princetonJSIPConnector);
            Mockito.when(jsipConectorFactory.getColumbiaJSIPConnector()).thenReturn(columbiaJSIPConnector);
            Mockito.when(jsipConectorFactory.getNyplAPIConnector()).thenReturn(nyplAPIConnector);
            Mockito.when(requestItemController.getJsipConectorFactory().getJSIPConnector(callInstitition)).thenCallRealMethod();
            Mockito.when((ItemInformationResponse) requestItemController.itemInformation(itemRequestInformation, itemRequestInformation.getRequestingInstitution())).thenCallRealMethod();
            Mockito.when((ItemInformationResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInstitition).lookupItem(itembarcode)).thenReturn(itemInformationResponse);
            Mockito.when(requestItemController.createBibliogrphicItem(itemRequestInformation, callInstitition)).thenCallRealMethod();
            AbstractResponseItem abstractResponseItem = requestItemController.createBibliogrphicItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
            abstractResponseItem.setEsipDataIn("test");
            abstractResponseItem.setItemOwningInstitution("PUL");
            assertTrue(abstractResponseItem.isSuccess());
            assertEquals(abstractResponseItem.getScreenMessage(), "Item Barcode already Exist");
            assertNotNull(abstractResponseItem.getEsipDataIn());
            assertNotNull(abstractResponseItem.getItemOwningInstitution());
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
            Mockito.when(requestItemController.getJsipConectorFactory()).thenReturn(jsipConectorFactory);
            Mockito.when(jsipConectorFactory.getPrincetonJSIPConnector()).thenReturn(princetonJSIPConnector);
            Mockito.when(jsipConectorFactory.getColumbiaJSIPConnector()).thenReturn(columbiaJSIPConnector);
            Mockito.when(jsipConectorFactory.getNyplAPIConnector()).thenReturn(nyplAPIConnector);
            Mockito.when(requestItemController.getJsipConectorFactory().getJSIPConnector(callInstitition)).thenCallRealMethod();
            Mockito.when(requestItemController.getPickupLocation(callInstitition)).thenCallRealMethod();
            Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(callInstitition).recallItem(itembarcode, itemRequestInformation.getPatronBarcode(),
                    itemRequestInformation.getRequestingInstitution(),
                    itemRequestInformation.getExpirationDate(),
                    itemRequestInformation.getBibId(),
                    getPickupLocation(callInstitition))).thenReturn(itemRecallResponse);
            Mockito.when(requestItemController.recallItem(itemRequestInformation, callInstitition)).thenCallRealMethod();
            AbstractResponseItem abstractResponseItem = requestItemController.recallItem(itemRequestInformation, callInstitition);
            assertNotNull(abstractResponseItem);
            assertTrue(abstractResponseItem.isSuccess());
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
            Mockito.when(requestItemController.getJsipConectorFactory()).thenReturn(jsipConectorFactory);
            Mockito.when(jsipConectorFactory.getPrincetonJSIPConnector()).thenReturn(princetonJSIPConnector);
            Mockito.when(jsipConectorFactory.getColumbiaJSIPConnector()).thenReturn(columbiaJSIPConnector);
            Mockito.when(jsipConectorFactory.getNyplAPIConnector()).thenReturn(nyplAPIConnector);
            Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(callInstitition)).thenCallRealMethod();
            Mockito.when((PatronInformationResponse) requestItemController.getJsipConectorFactory().getJSIPConnector(callInstitition).lookupPatron(itemRequestInformation.getPatronBarcode())).thenReturn(patronInformationResponse);
            Mockito.when(requestItemController.patronInformation(itemRequestInformation, callInstitition)).thenCallRealMethod();
            AbstractResponseItem abstractResponseItem = requestItemController.patronInformation(itemRequestInformation, callInstitition);
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
        ItemRefileResponse itemRefileResponse = requestItemControllerAutowired.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse);
    }

    @Test
    public void patronValidationBulkRequest() {
        BulkRequestInformation bulkRequestInformation = new BulkRequestInformation();
        bulkRequestInformation.setPatronBarcode("123456");
        bulkRequestInformation.setRequestingInstitution("PUL");
        boolean result = requestItemControllerAutowired.patronValidationBulkRequest(bulkRequestInformation);
        assertNotNull(result);
    }

    @Test
    public void refileItemInILSWithItemBarcode() {
        String callInstitition = "PUL";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101074849843"));
        Mockito.doCallRealMethod().when(requestItemController).refileItemInILS(itemRequestInformation, callInstitition);
        AbstractResponseItem abstractResponseItem = requestItemController.refileItemInILS(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void refileItemInILS() {
        String callInstitition = "PUL";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.EMPTY_LIST);
        Mockito.doCallRealMethod().when(requestItemController).refileItemInILS(itemRequestInformation, callInstitition);
        AbstractResponseItem abstractResponseItem = requestItemController.refileItemInILS(itemRequestInformation, callInstitition);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void replaceRequest() {
        ReplaceRequest replaceRequest = new ReplaceRequest();
        Map<String, String> result = requestItemControllerAutowired.replaceRequest(replaceRequest);
        assertNotNull(result);
    }

    /*@Test
    public void replaceRequestException(){
        ReplaceRequest replaceRequest = new ReplaceRequest();
        Mockito.when(itemRequestService.replaceRequestsToLASQueue(replaceRequest)).thenThrow(new Exception());
        Mockito.doCallRealMethod().when(requestItemController).replaceRequest(replaceRequest);
        Map<String, String> result = requestItemController.replaceRequest(replaceRequest);
        assertNotNull(result);
    }*/
    @Test
    public void replaceRequestEDD() {
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setReplaceRequestByType("EDD");
        Map<String, String> result = requestItemControllerAutowired.replaceRequest(replaceRequest);
        assertNotNull(result);
    }

    @Test
    public void getPickupLocationCUL() {
        String institution = "CUL";
        String pickUpLocation = requestItemControllerAutowired.getPickupLocation(institution);
        assertNotNull(pickUpLocation);
        assertEquals("CIRCrecap", pickUpLocation);
    }

    @Test
    public void getPickupLocationNYPL() {
        String institution = "NYPL";
        String pickUpLocation = requestItemControllerAutowired.getPickupLocation(institution);
        assertNotNull(pickUpLocation);
        assertEquals("lb", pickUpLocation);
    }

    @Test
    public void logMessages() {
        Object test = "test";
        Logger logger = LoggerFactory.getLogger(RequestItemController.class);
        requestItemControllerAutowired.logMessages(logger, test);
    }
}