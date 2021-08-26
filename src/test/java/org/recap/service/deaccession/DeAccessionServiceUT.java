package org.recap.service.deaccession;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.RequestItemController;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.response.ItemHoldResponse;
import org.recap.model.response.ItemInformationResponse;
import org.recap.ims.connector.AbstractLASImsLocationConnector;
import org.recap.ims.service.GFALasService;
import org.recap.ims.connector.factory.LASImsLocationConnectorFactory;
import org.recap.ims.model.*;
import org.recap.model.deaccession.DeAccessionDBResponseEntity;
import org.recap.model.deaccession.DeAccessionItem;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.RestHeaderService;
import org.recap.util.CommonUtil;
import org.recap.request.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by angelind on 10/11/16.
 */
public class DeAccessionServiceUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    DeAccessionService deAccessionService;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    CommonUtil commonUtil;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    DeliveryCodeDetailsRepository deliveryCodeDetailsRepository;

    @Mock
    DeliveryCodeTranslationDetailsRepository deliveryCodeTranslationDetailsRepository;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    AbstractLASImsLocationConnector abstractLASImsLocationConnector;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    RequestItemController requestItemController;

    @Mock
    ItemHoldResponse itemHoldResponse;

    @Mock
    GFALasService gfaLasService;

    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    DeaccesionItemChangeLogDetailsRepository deaccesionItemChangeLogDetailsRepository;

    @Mock
    ItemEntity itemEntity;

    @Mock
    UserDetailRepository userDetailRepository;

    @Before
    public void setup() {
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
    }

    @Test
    public void deaccession() {
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionRequest.setDeAccessionItems(Arrays.asList(deAccessionItem));
        deAccessionRequest.setUsername("Test");
        deAccessionRequest.setNotes("test");
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCatalogingStatus("Complete");
        RequestItemEntity requestItemEntity = getRequestItem();
        String itemBarcode = "123456";
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PA");
        Set<String> itemBarcodeList = barcodeAndStopCodeMap.keySet();
        Map<Integer, String> itemIdAndMessageMap = new HashMap<>();
        itemIdAndMessageMap.put(1, "LAS server is not reachable. Please contact ReCAP staff (<a href=\"mailto:null\">null</a>) for further assistance.");
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        List<DeaccessionItemChangeLog> itemChangeLogEntities = new ArrayList<>();
        DeaccessionItemChangeLog deaccessionItemChangeLog = getDeaccessionItemChangeLog(itemIdAndMessageMap);
        itemChangeLogEntities.add(deaccessionItemChangeLog);
        GFAPwdResponse gfaPwdResponse = getGFAPwdResponse();
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        itemHoldResponse.setScreenMessage("Success");
        when(gfaLasService.callGfaItemStatus(itemBarcode)).thenReturn("INC ON WO:");
        when(itemDetailsRepository.findByBarcode(itemBarcode)).thenReturn(Arrays.asList(itemEntity));
        when(itemDetailsRepository.findByBarcodeIn(new ArrayList<>(itemBarcodeList))).thenReturn(Arrays.asList(itemEntity));
        when(requestItemDetailsRepository.findByItemBarcode(itemBarcode)).thenReturn(Arrays.asList(requestItemEntity));
        when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        when(requestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(commonUtil.findAllInstitutionsExceptSupportInstitution()).thenReturn(Arrays.asList(itemEntity.getInstitutionEntity()));
        Mockito.when(requestItemController.cancelHoldItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(getRequestItem().getRequestStatusEntity());
        Mockito.when(requestItemDetailsRepository.save(any())).thenReturn(requestItemEntity);
        Mockito.when(deaccesionItemChangeLogDetailsRepository.saveAll(itemChangeLogEntities)).thenReturn(Arrays.asList(deaccessionItemChangeLog));
        Mockito.when(userDetailRepository.findInstitutionCodeByUserName(any())).thenReturn("PUL");
        Mockito.when(userDetailRepository.getUserRoles(any())).thenReturn(Arrays.asList("test"));
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.gfaPermanentWithdrawalDirect(any())).thenReturn(gfaPwdResponse);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getItemEntity().getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(any())).thenReturn(getImsLocationEntity());
        Mockito.when(deliveryCodeTranslationDetailsRepository.findByRequestingInstitutionandImsLocation(any(), any(), any())).thenReturn(getDeliveryCodeTranslationEntity());
        Map<String, String> result = deAccessionService.deAccession(deAccessionRequest);
        assertNotNull(result);
        getGFAPwdTtItemResponse(gfaPwdResponse);
        Map<String, String> result2 = deAccessionService.deAccession(deAccessionRequest);
        assertNotNull(result2);
    }

    private void getGFAPwdTtItemResponse(GFAPwdResponse gfaPwdResponse) {
        GFAPwdTtItemResponse gfaPwdTtItemResponse = new GFAPwdTtItemResponse();
        gfaPwdTtItemResponse.setErrorCode("error");
        gfaPwdTtItemResponse.setErrorNote("Error");
        gfaPwdTtItemResponse.setCustomerCode("PA");
        gfaPwdTtItemResponse.setDestination("GA");
        gfaPwdResponse.getDsitem().setTtitem(Arrays.asList(gfaPwdTtItemResponse));
    }

    @Test
    public void deaccessionWithoutDeaccessionItems() {
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionRequest.setDeAccessionItems(Collections.EMPTY_LIST);
        deAccessionRequest.setUsername("Test");
        deAccessionRequest.setNotes("test");
        Map<String, String> result2 = deAccessionService.deAccession(deAccessionRequest);
        assertNotNull(result2);
    }

    private DeaccessionItemChangeLog getDeaccessionItemChangeLog(Map<Integer, String> itemIdAndMessageMap) {
        DeaccessionItemChangeLog deaccessionItemChangeLog = new DeaccessionItemChangeLog();
        deaccessionItemChangeLog.setUpdatedBy("Test");
        deaccessionItemChangeLog.setCreatedDate(new Date());
        deaccessionItemChangeLog.setOperationType("Deaccession Rollback");
        deaccessionItemChangeLog.setRecordId(1);
        deaccessionItemChangeLog.setNotes(itemIdAndMessageMap.get(1) + " Hence, the transaction of deaccessioning item is rolled back.");
        return deaccessionItemChangeLog;
    }

    @Test
    public void checkAndCancelHolds() throws Exception {
        RequestItemEntity requestItemEntity = getMockRequestItemEntities().get(0);
        Map<String, String> resultMap = new HashMap<>();
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode("123456");
        deAccessionItem.setDeliveryLocation("PB");
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setScreenMessage("Success");
        itemInformationResponse.setHoldQueueLength("1");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        RequestStatusEntity requestStatusEntity = getRequestItem().getRequestStatusEntity();
        Mockito.when(requestItemDetailsRepository.save(any())).thenReturn(getRequestItem());
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(requestStatusEntity);
        Mockito.doNothing().when(itemRequestServiceUtil).updateSolrIndex(any());
        Mockito.when(requestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(requestItemController.cancelHoldItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(requestItemDetailsRepository.findByItemBarcode("123")).thenReturn(Arrays.asList(getRequestItem()));
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
        assertEquals(deAccessionItem.getItemBarcode(), barcodeAndStopCodeMap.keySet().toArray()[1]);
        itemHoldResponse.setSuccess(false);
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
        itemInformationResponse.setHoldQueueLength("0");
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);

    }

    @Test
    public void checkAndCancelHoldsException() throws Exception {
        RequestItemEntity requestItemEntity = getRequestItem();
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        Mockito.when(requestItemDetailsRepository.findByItemBarcode("123")).thenThrow(new NullPointerException());
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
    }

    @Test
    public void checkAndCancelHoldsWithoutActiveRecallRequestAndActiveRetrievalRequest() throws Exception {
        RequestItemEntity requestItemEntity = getRequestItem();
        requestItemEntity.getRequestTypeEntity().setRequestTypeCode("EDD");
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Mockito.when(requestItemDetailsRepository.findByItemBarcode("123")).thenReturn(Arrays.asList(requestItemEntity));
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
    }

    @Test
    public void checkAndCancelHoldsWithActiveRecallRequestAndActiveRetrievalRequest() throws Exception {
        RequestItemEntity requestItemEntity = getRequestItem();
        requestItemEntity.getRequestTypeEntity().setRequestTypeCode(ScsbCommonConstants.RETRIEVAL);
        requestItemEntity.getRequestStatusEntity().setRequestStatusCode(ScsbConstants.LAS_REFILE_REQUEST_PLACED);
        RequestItemEntity requestItemEntity1 = getRequestItem();
        requestItemEntity1.getRequestTypeEntity().setRequestTypeCode(ScsbCommonConstants.REQUEST_TYPE_RECALL);
        requestItemEntity1.getRequestStatusEntity().setRequestStatusCode(ScsbConstants.LAS_REFILE_REQUEST_PLACED);
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        itemHoldResponse.setScreenMessage("Bad Request");
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Mockito.when(requestItemController.cancelHoldItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(requestItemDetailsRepository.findByItemBarcode("123")).thenReturn(Arrays.asList(requestItemEntity, requestItemEntity1));
        Mockito.when(requestItemDetailsRepository.save(any())).thenReturn(getRequestItem());
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(getRequestItem().getRequestStatusEntity());
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
        itemHoldResponse.setSuccess(false);
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
    }

    @Test
    public void checkAndCancelHoldsWithActiveRecallRequestAndActiveRetrievalRequestDifferentRequestingId() throws Exception {
        RequestItemEntity requestItemEntity = getRequestItem();
        requestItemEntity.getInstitutionEntity().setInstitutionCode("CUL");
        requestItemEntity.getRequestTypeEntity().setRequestTypeCode(ScsbCommonConstants.RETRIEVAL);
        requestItemEntity.getRequestStatusEntity().setRequestStatusCode(ScsbConstants.LAS_REFILE_REQUEST_PLACED);
        RequestItemEntity requestItemEntity1 = getRequestItem();
        requestItemEntity1.getRequestTypeEntity().setRequestTypeCode(ScsbCommonConstants.REQUEST_TYPE_RECALL);
        requestItemEntity1.getRequestStatusEntity().setRequestStatusCode(ScsbConstants.LAS_REFILE_REQUEST_PLACED);
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        itemHoldResponse.setScreenMessage("Bad Request");
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Mockito.when(requestItemController.cancelHoldItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(requestItemDetailsRepository.findByItemBarcode("123")).thenReturn(Arrays.asList(requestItemEntity, requestItemEntity1));
        Mockito.when(requestItemDetailsRepository.save(any())).thenReturn(getRequestItem());
        Mockito.when(requestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(getRequestItem().getRequestStatusEntity());
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
    }

    @Test
    public void checkAndCancelHoldsForInitialLoadRequest() throws Exception {
        RequestItemEntity requestItemEntity = getRequestItem();
        requestItemEntity.getRequestStatusEntity().setRequestStatusCode("INITIAL_LOAD");
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        RequestStatusEntity requestStatusEntity = getRequestItem().getRequestStatusEntity();
        Mockito.when(requestItemDetailsRepository.save(any())).thenReturn(getRequestItem());
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(requestStatusEntity);
        Mockito.doNothing().when(itemRequestServiceUtil).updateSolrIndex(any());
        Mockito.when(requestItemDetailsRepository.findByItemBarcode("123")).thenReturn(Arrays.asList(requestItemEntity));
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
    }

    @Test
    public void checkAndCancelHoldsWithActiveRecallRequestAndWithoutActiveRetrievalRequest() throws Exception {
        RequestItemEntity requestItemEntity = getRequestItem();
        requestItemEntity.getRequestStatusEntity().setRequestStatusCode("LAS_REFILE_REQUEST_PLACED");
        requestItemEntity.getRequestTypeEntity().setRequestTypeCode("RECALL");
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Mockito.when(requestItemDetailsRepository.findByItemBarcode("123")).thenReturn(Arrays.asList(requestItemEntity));
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
    }

    @Test
    public void callGfaDeaccessionServiceWithResponse() {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setItemStatus(ScsbCommonConstants.NOT_AVAILABLE);
        deAccessionDBResponseEntity.setStatus(ScsbCommonConstants.SUCCESS);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        GFAPwiResponse gfaPwiResponse = getGfaPwiResponse();
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.gfaPermanentWithdrawalInDirect(any())).thenReturn(gfaPwiResponse);
        ReflectionTestUtils.invokeMethod(deAccessionService, "callGfaDeaccessionService", deAccessionDBResponseEntities, username);
    }

    @Test
    public void callGfaDeaccessionServiceWithoutResponse() {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setItemStatus(ScsbCommonConstants.NOT_AVAILABLE);
        deAccessionDBResponseEntity.setStatus(ScsbCommonConstants.SUCCESS);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(any(), any())).thenReturn(Boolean.TRUE.toString());
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.gfaPermanentWithdrawalInDirect(any())).thenReturn(null);
        ReflectionTestUtils.invokeMethod(deAccessionService, "callGfaDeaccessionService", deAccessionDBResponseEntities, username);
    }

    @Test
    public void rollbackLASRejectedItems() {
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setStatus(ScsbCommonConstants.FAILURE);
        deAccessionDBResponseEntity.setReasonForFailure(ScsbCommonConstants.LAS_SERVER_NOT_REACHABLE);
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        ReflectionTestUtils.invokeMethod(deAccessionService, "rollbackLASRejectedItems", deAccessionDBResponseEntities, username);
    }

    @Test
    public void checkGfaItemStatusItemDeleted() {
        List<DeAccessionItem> deAccessionItems = new ArrayList<>();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionItems.add(deAccessionItem);
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setDeleted(true);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", deAccessionItems, deAccessionDBResponseEntities, barcodeAndStopCodeMap);
    }

    @Test
    public void checkGfaItemStatusItemNotCmpleted() {
        List<DeAccessionItem> deAccessionItems = new ArrayList<>();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionItems.add(deAccessionItem);
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCatalogingStatus(ScsbCommonConstants.INCOMPLETE_STATUS);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", deAccessionItems, deAccessionDBResponseEntities, barcodeAndStopCodeMap);
    }

    @Test
    public void checkGfaItemStatusSchOnStatus() {
        List<DeAccessionItem> deAccessionItems = new ArrayList<>();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionItems.add(deAccessionItem);
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        Mockito.when(gfaLasService.callGfaItemStatus(any())).thenReturn("SCH ON REFILE WO:");
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", deAccessionItems, deAccessionDBResponseEntities, barcodeAndStopCodeMap);
    }

    @Test
    public void checkGfaItemStatusItemNotAvailable() {
        List<DeAccessionItem> deAccessionItems = new ArrayList<>();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionItems.add(deAccessionItem);
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        itemEntity.getItemStatusEntity().setStatusCode(ScsbCommonConstants.NOT_AVAILABLE);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        Mockito.when(gfaLasService.callGfaItemStatus(any())).thenReturn(ScsbConstants.ILS_CONNECTION_FAILED);
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", deAccessionItems, deAccessionDBResponseEntities, barcodeAndStopCodeMap);
    }

    @Test
    public void checkGfaItemStatusBlankStatus() {
        List<DeAccessionItem> deAccessionItems = new ArrayList<>();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionItems.add(deAccessionItem);
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        Mockito.when(gfaLasService.callGfaItemStatus(any())).thenReturn("");
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", deAccessionItems, deAccessionDBResponseEntities, barcodeAndStopCodeMap);
    }

    @Test
    public void checkGfaItemStatusWithoutItemEntites() {
        List<DeAccessionItem> deAccessionItems = new ArrayList<>();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionItems.add(deAccessionItem);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Collections.EMPTY_LIST);
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", deAccessionItems, deAccessionDBResponseEntities, barcodeAndStopCodeMap);
    }

    @Test
    public void checkGfaItemStatusBlankBarcode() {
        List<DeAccessionItem> deAccessionItems = new ArrayList<>();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionItem.setItemBarcode("");
        deAccessionItems.add(deAccessionItem);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", deAccessionItems, deAccessionDBResponseEntities, barcodeAndStopCodeMap);
    }

    @Test
    public void checkGfaItemStatusException() {
        ReflectionTestUtils.invokeMethod(deAccessionService, "checkGfaItemStatus", null, null, null);
    }

    @Test
    public void callGfaDeaccessionService() {
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = getDeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setItemStatus(ScsbCommonConstants.AVAILABLE);
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        String username = "test";
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(deAccessionDBResponseEntity.getInstitutionCode())).thenReturn(getItemEntity().getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(deAccessionDBResponseEntity.getImsLocationCode())).thenReturn(getImsLocationEntity());
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.gfaPermanentWithdrawalDirect(any())).thenReturn(null);
        Mockito.when(deliveryCodeTranslationDetailsRepository.findByRequestingInstitutionandImsLocation(any(), any(), any())).thenReturn(getDeliveryCodeTranslationEntity());
        ReflectionTestUtils.invokeMethod(deAccessionService, "callGfaDeaccessionService", deAccessionDBResponseEntities, username);
    }

    private DeAccessionDBResponseEntity getDeAccessionDBResponseEntity() {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("12345");
        deAccessionDBResponseEntity.setStatus(ScsbCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setReasonForFailure(ScsbCommonConstants.ITEM_BARCDE_DOESNOT_EXIST);
        deAccessionDBResponseEntity.setHoldingIds(Arrays.asList(1));
        deAccessionDBResponseEntity.setBibliographicIds(Arrays.asList(1));
        deAccessionDBResponseEntity.setItemId(1);
        deAccessionDBResponseEntity.setImsLocationCode("PUL");
        return deAccessionDBResponseEntity;
    }

    @Test
    public void cancelRequest() {
        RequestItemEntity requestItemEntity = getMockRequestItemEntities().get(0);
        String username = "username";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation2();
        itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setScreenMessage("Canceled Successfully");
        itemHoldResponse.setItemBarcode(itemRequestInformation.getItemBarcodes().get(0));
        itemHoldResponse.setBibId(itemRequestInformation.getBibId());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldResponse.setInstitutionID("1");
        itemHoldResponse.setIsbn("34567");
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setSuccess(true);
        Mockito.when(requestItemController.cancelHoldItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.doNothing().when(itemRequestServiceUtil).updateSolrIndex(requestItemEntity.getItemEntity());
        Mockito.when(requestItemDetailsRepository.save(requestItemEntity)).thenReturn(requestItemEntity);
        ItemHoldResponse itemHoldResponse = deAccessionService.cancelRequest(requestItemEntity, username);
        assertNotNull(itemHoldResponse);
    }

    @Test
    public void deAccessionItemsInDB() {
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("1", "345890");
        Set<String> itemBarcodeList = barcodeAndStopCodeMap.keySet();
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        String username = "username";
        Mockito.when(itemDetailsRepository.findByBarcodeIn(new ArrayList<>(itemBarcodeList))).thenReturn(Arrays.asList(getItemEntity()));
        Mockito.when(holdingsDetailsRepository.getNonDeletedItemsCount(1, "1")).thenReturn(1l);
        Mockito.when(bibliographicDetailsRepository.getNonDeletedItemsCount(1, "1234")).thenReturn(1l);
        deAccessionService.deAccessionItemsInDB(barcodeAndStopCodeMap, deAccessionDBResponseEntities, username);
    }

    @Test
    public void deAccessionItemsInDBInnerException() {
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("1", "345890");
        Set<String> itemBarcodeList = barcodeAndStopCodeMap.keySet();
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        String username = "username";
        Mockito.when(itemDetailsRepository.findByBarcodeIn(new ArrayList<>(itemBarcodeList))).thenReturn(Arrays.asList(getItemEntity()));
        Mockito.when(holdingsDetailsRepository.getNonDeletedItemsCount(1, "1")).thenReturn(null);
        deAccessionService.deAccessionItemsInDB(barcodeAndStopCodeMap, deAccessionDBResponseEntities, username);
    }

    @Test
    public void deAccessionItemsInDBException() {
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        Set<String> itemBarcodeList = barcodeAndStopCodeMap.keySet();
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        String username = "username";
        Mockito.when(itemDetailsRepository.findByBarcodeIn(new ArrayList<>(itemBarcodeList))).thenReturn(null);
        deAccessionService.deAccessionItemsInDB(barcodeAndStopCodeMap, deAccessionDBResponseEntities, username);
    }

    @Test
    public void removeDeaccessionItems(){
        List<DeAccessionItem> removeDeaccessionItems = new ArrayList<>();
        removeDeaccessionItems.add(getDeAccessionItem());
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        List<DeAccessionItem> removeDeaccessionItemsList = new ArrayList<>();
        removeDeaccessionItemsList.add(getDeAccessionItem());
        Map<String, String> resultMap = new HashMap<>();
        ReflectionTestUtils.invokeMethod(deAccessionService,"removeDeaccessionItems",removeDeaccessionItems,deAccessionRequest,removeDeaccessionItemsList,resultMap);
    }

    @Test
    public void getDeliveryLocation(){
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        deAccessionRequest.setDeAccessionItems(Arrays.asList(getDeAccessionItem()));
        List<DeAccessionItem> removeDeaccessionItems = new ArrayList<>();
        removeDeaccessionItems.add(getDeAccessionItem());
        String barCode = "123456";
        ReflectionTestUtils.invokeMethod(deAccessionService,"getDeliveryLcation",barCode,deAccessionRequest,removeDeaccessionItems);
    }

    @Test
    public void validateUserRoles(){
        List<String> userRoles = new ArrayList<>();
        userRoles.add(ScsbConstants.ROLE_RECAP);
        ReflectionTestUtils.invokeMethod(deAccessionService,"validateUserRoles",userRoles);
    }

    @Test
    public void validateBarcodesWithUserName(){
        String userName = "test";
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntities.add(getDeAccessionDBResponseEntity());
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        deAccessionRequest.setDeAccessionItems(Arrays.asList(getDeAccessionItem()));
        List<DeAccessionItem> removeDeaccessionItems = new ArrayList<>();
        removeDeaccessionItems.add(getDeAccessionItem());
        Map<String, String> resultMap = new HashMap<>();
        List<String> userRoles = new ArrayList<>();
        userRoles.add("test");
        Mockito.when(userDetailRepository.findInstitutionCodeByUserName(any())).thenReturn("2");
        Mockito.when(userDetailRepository.getUserRoles(any())).thenReturn(userRoles);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(any())).thenReturn(Arrays.asList(getItemEntity()));
        Mockito.when(commonUtil.findAllInstitutionsExceptSupportInstitution()).thenReturn(Arrays.asList(getItemEntity().getInstitutionEntity()));
        try {
            ReflectionTestUtils.invokeMethod(deAccessionService, "validateBarcodesWithUserName", deAccessionRequest, userName, deAccessionDBResponseEntities, removeDeaccessionItems, resultMap);
        }catch (Exception e){}
    }

    @Test
    public void deAcessionItemsInSolrSuccess() {
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("123456");
        deAccessionDBResponseEntity.setStatus("Success");
        deAccessionDBResponseEntity.setBibliographicIds(Arrays.asList(1));
        deAccessionDBResponseEntity.setHoldingIds(Arrays.asList(1));
        deAccessionDBResponseEntity.setItemId(1);
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> resultMap = new HashMap<>();
        deAccessionService.deAccessionItemsInSolr(deAccessionDBResponseEntities, resultMap);
    }

    @Test
    public void deAcessionItemsInSolrFailure() {
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("123456");
        deAccessionDBResponseEntity.setStatus("Failure");
        deAccessionDBResponseEntity.setReasonForFailure("Barcode UnAvailable");
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        Map<String, String> resultMap = new HashMap<>();
        deAccessionService.deAccessionItemsInSolr(deAccessionDBResponseEntities, resultMap);
    }

    public ItemInformationResponse getItemInformationResponse() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setCirculationStatus("test");
        itemInformationResponse.setSecurityMarker("test");
        itemInformationResponse.setFeeType("test");
        itemInformationResponse.setTransactionDate(new Date().toString());
        itemInformationResponse.setHoldQueueLength("10");
        itemInformationResponse.setTitleIdentifier("test");
        itemInformationResponse.setBibID("1223");
        itemInformationResponse.setDueDate(new Date().toString());
        itemInformationResponse.setExpirationDate("30-03-2017 00:00:00");
        itemInformationResponse.setRecallDate(new Date().toString());
        itemInformationResponse.setCurrentLocation("test");
        itemInformationResponse.setHoldPickupDate(new Date().toString());
        itemInformationResponse.setItemBarcode("32101077423406");
        itemInformationResponse.setRequestType("RECALL");
        itemInformationResponse.setRequestingInstitution("CUL");
        itemInformationResponse.setRequestId(2);
        return itemInformationResponse;
    }

    private ItemRequestInformation getItemRequestInformation2() {
        ItemEntity itemEntity = getMockRequestItemEntities().get(0).getItemEntity();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.singletonList(itemEntity.getBarcode()));
        itemRequestInformation.setItemOwningInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
        itemRequestInformation.setBibId(itemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId());
        itemRequestInformation.setRequestingInstitution(getMockRequestItemEntities().get(0).getInstitutionEntity().getInstitutionCode());
        itemRequestInformation.setPatronBarcode(getMockRequestItemEntities().get(0).getPatronId());
        itemRequestInformation.setDeliveryLocation(getMockRequestItemEntities().get(0).getStopCode());
        return itemRequestInformation;
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setUsername("Discovery");
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setCallNumber("X");
        itemRequestInformation.setAuthor("John");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setExpirationDate("30-03-2017 00:00:00");
        itemRequestInformation.setCustomerCode("PB");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setRequestType("RETRIEVAL");
        return itemRequestInformation;
    }

    public RequestItemEntity getRequestItem() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(16);
        requestItemEntity.setItemId(1);
        requestItemEntity.setRequestTypeId(3);
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RETRIEVAL");
        requestTypeEntity.setRequestTypeDesc("RETRIEVAL");
        ItemEntity itemEntity = getItemEntity();
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("LAS_REFILE_REQUEST_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL ORDER PLACED");
        requestItemEntity.setId(1);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setRequestingInstitutionId(2);
        requestItemEntity.setStopCode("test");
        requestItemEntity.setNotes("test");
        requestItemEntity.setItemEntity(getItemEntity());
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setPatronId("1");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(3);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setEmailId("test@gmail.com");
        requestItemEntity.setLastUpdatedDate(new Date());
        return requestItemEntity;
    }

    private ItemEntity getItemEntity() {

        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusDescription("COMPLETE");
        itemStatusEntity.setStatusCode("Available");
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCollectionGroupCode("Complete");
        collectionGroupEntity.setCollectionGroupDescription("Complete");
        collectionGroupEntity.setLastUpdatedDate(new Date());
        collectionGroupEntity.setCreatedDate(new Date());
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setId(123456);
        holdingsEntity.setOwningInstitutionHoldingsId("1");
        holdingsEntity.setOwningInstitutionId(1);
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCgdProtection(true);
        itemEntity.setImsLocationEntity(getImsLocationEntity());
        assertTrue(itemEntity.isCgdProtection());
        return itemEntity;
    }

    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("1");
        imsLocationEntity.setImsLocationName("test");
        imsLocationEntity.setCreatedBy("test");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setActive(true);
        imsLocationEntity.setDescription("test");
        imsLocationEntity.setUpdatedBy("test");
        imsLocationEntity.setUpdatedDate(new Date());
        return imsLocationEntity;
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(1);
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1234");
        return bibliographicEntity;
    }

    private DeAccessionItem getDeAccessionItem() {
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode("123456");
        deAccessionItem.setDeliveryLocation("PB");
        return deAccessionItem;
    }

    @Test
    public void deAccessionItemsInDB1() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put(itemBarcode, "PB");
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);
        Thread.sleep(3000);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionService.deAccessionItemsInDB(barcodeAndStopCodeMap, deAccessionDBResponseEntities, ScsbConstants.GUEST_USER);
        assertNotNull(deAccessionDBResponseEntities);

    }

    @Test
    public void populateDeAccessionDBResponseEntity(){
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBibliographicEntities(Arrays.asList(getBibliographicEntity()));
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        ReflectionTestUtils.invokeMethod(deAccessionService,"populateDeAccessionDBResponseEntity",itemEntity,deAccessionDBResponseEntity);
    }

    @Test
    public void processAndSave() throws Exception {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("12345");
        deAccessionDBResponseEntity.setStatus(ScsbCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setReasonForFailure(ScsbCommonConstants.ITEM_BARCDE_DOESNOT_EXIST);

        List<ReportEntity> reportEntities = deAccessionService.processAndSaveReportEntities(Arrays.asList(deAccessionDBResponseEntity));
        assertNotNull(reportEntities);
    }

    private BibliographicEntity getBibEntityWithHoldingsAndItem(String itemBarcode) throws Exception {
        Random random = new Random();
        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(itemBarcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }



   /* @Test
    public void deaccessionFlowTest() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put(itemBarcode, "PB");
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        //entityManager.refresh(savedBibliographicEntity);
//        assertNotNull(savedBibliographicEntity);
//        assertNotNull(savedBibliographicEntity.getBibliographicId());
        Thread.sleep(3000);
//        when(requestItemDetailsRepository.findByItemBarcode(itemBarcode)).thenReturn(getMockRequestItemEntities());

        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode(itemBarcode);
        deAccessionItem.setDeliveryLocation("PB");
        deAccessionRequest.setDeAccessionItems(Arrays.asList(deAccessionItem));
        deAccessionRequest.setUsername("Test");
        Map<String, String> resultMap = deAccessionService.deAccession(deAccessionRequest);
        assertNotNull(resultMap);
    }*/

    private List<RequestItemEntity> getMockRequestItemEntities() {
        InstitutionEntity institutionEntity1 = new InstitutionEntity();
        institutionEntity1.setId(1);
        institutionEntity1.setInstitutionCode("PUL");
        institutionEntity1.setInstitutionName("Princeton");

        InstitutionEntity institutionEntity2 = new InstitutionEntity();
        institutionEntity2.setId(2);
        institutionEntity2.setInstitutionCode("CUL");
        institutionEntity2.setInstitutionName("Columbia");

        RequestTypeEntity requestTypeEntity1 = new RequestTypeEntity();
        requestTypeEntity1.setId(1);
        requestTypeEntity1.setRequestTypeCode("RETRIEVAL");
        requestTypeEntity1.setRequestTypeDesc("RETRIEVAL");

        RequestTypeEntity requestTypeEntity2 = new RequestTypeEntity();
        requestTypeEntity2.setId(2);
        requestTypeEntity2.setRequestTypeCode("RECALL");
        requestTypeEntity2.setRequestTypeDesc("RECALL");

        RequestStatusEntity requestStatusEntity1 = new RequestStatusEntity();
        requestStatusEntity1.setId(1);
        requestStatusEntity1.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity1.setRequestStatusDescription("RETRIEVAL_ORDER_PLACED");

        RequestStatusEntity requestStatusEntity2 = new RequestStatusEntity();
        requestStatusEntity2.setId(2);
        requestStatusEntity2.setRequestStatusCode("RECALL_ORDER_PLACED");
        requestStatusEntity2.setRequestStatusDescription("RECALL_ORDER_PLACED");

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(1);
        itemEntity1.setBarcode("123");
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setInstitutionEntity(institutionEntity1);

        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setOwningInstitutionBibId("345");
        bibliographicEntity1.setInstitutionEntity(institutionEntity1);
        assertNotNull(bibliographicEntity1.getInstitutionEntity());
        itemEntity1.setBibliographicEntities(Arrays.asList(bibliographicEntity1));

        List<RequestItemEntity> requestItemEntities = new ArrayList<>();

        RequestItemEntity requestItemEntity1 = new RequestItemEntity();
        requestItemEntity1.setId(1);
        requestItemEntity1.setItemId(2);
        requestItemEntity1.setRequestTypeId(1);
        requestItemEntity1.setRequestingInstitutionId(1);
        requestItemEntity1.setPatronId("123");
        requestItemEntity1.setEmailId("test1@gmail.com");
        requestItemEntity1.setRequestExpirationDate(new Date());
        requestItemEntity1.setCreatedBy("Test");
        requestItemEntity1.setCreatedDate(new Date());
        requestItemEntity1.setStopCode("PA");
        requestItemEntity1.setRequestStatusId(1);
        requestItemEntity1.setNotes("Test Notes");
        requestItemEntity1.setInstitutionEntity(institutionEntity1);
        requestItemEntity1.setRequestTypeEntity(requestTypeEntity1);
        requestItemEntity1.setRequestStatusEntity(requestStatusEntity1);
        requestItemEntity1.setItemEntity(itemEntity1);
        requestItemEntities.add(requestItemEntity1);

        RequestItemEntity requestItemEntity2 = new RequestItemEntity();
        requestItemEntity2.setId(2);
        requestItemEntity2.setItemId(2);
        requestItemEntity2.setRequestTypeId(2);
        requestItemEntity2.setRequestingInstitutionId(1);
        requestItemEntity2.setPatronId("321");
        requestItemEntity2.setEmailId("test2@gmail.com");
        requestItemEntity2.setRequestExpirationDate(new Date());
        requestItemEntity2.setCreatedBy("Test");
        requestItemEntity2.setCreatedDate(new Date());
        requestItemEntity2.setStopCode("PA");
        requestItemEntity2.setRequestStatusId(2);
        requestItemEntity2.setNotes("Test Notes");
        requestItemEntity2.setInstitutionEntity(institutionEntity1);
        requestItemEntity2.setRequestTypeEntity(requestTypeEntity2);
        requestItemEntity2.setRequestStatusEntity(requestStatusEntity2);
        requestItemEntity2.setItemEntity(itemEntity1);
        requestItemEntities.add(requestItemEntity2);

        return requestItemEntities;
    }

    private GFAPwdResponse getGFAPwdResponse() {
        GFAPwdResponse gfaPwdResponse = new GFAPwdResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        GFAPwdTtItemResponse gfaPwdTtItemResponse = new GFAPwdTtItemResponse();
        gfaPwdTtItemResponse.setCustomerCode("PA");
        gfaPwdTtItemResponse.setDestination("GA");
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(gfaPwdTtItemResponse));
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
        GFAPwiResponse gfaPwiResponse = new GFAPwiResponse();
        GFAPwiDsItemResponse gfaPwiDsItemResponse = new GFAPwiDsItemResponse();
        GFAPwiTtItemResponse gfaPwiTtItemResponse = new GFAPwiTtItemResponse();
        gfaPwiTtItemResponse.setCustomerCode("CA");
        gfaPwiTtItemResponse.setErrorNote("ErrorNote");
        gfaPwiDsItemResponse.setTtitem(Arrays.asList(gfaPwiTtItemResponse));
        gfaPwiResponse.setDsitem(gfaPwiDsItemResponse);
        return gfaPwiResponse;
    }
    private DeliveryCodeEntity getDeliveryCodeEntity() {
        DeliveryCodeEntity deliveryCodeEntity = new DeliveryCodeEntity();
        deliveryCodeEntity.setId(1);
        deliveryCodeEntity.setDescription("Test");
        deliveryCodeEntity.setDeliveryCode("PA");
        deliveryCodeEntity.setAddress("Test");
        deliveryCodeEntity.setDeliveryCodeTypeId(1);
        deliveryCodeEntity.setActive('Y');
        return deliveryCodeEntity;
    }
    private DeliveryCodeTranslationEntity getDeliveryCodeTranslationEntity() {
        DeliveryCodeTranslationEntity deliveryCodeTranslationEntity = new DeliveryCodeTranslationEntity();
        deliveryCodeTranslationEntity.setId(1);
        deliveryCodeTranslationEntity.setImsLocationDeliveryCode("HD");
        deliveryCodeTranslationEntity.setRequestingInstitutionId(1);
        deliveryCodeTranslationEntity.setRequestingInstitutionDeliveryCodeId(1);
        deliveryCodeTranslationEntity.setInstitutionEntity(getItemEntity().getInstitutionEntity());
        return deliveryCodeTranslationEntity;
    }
}
