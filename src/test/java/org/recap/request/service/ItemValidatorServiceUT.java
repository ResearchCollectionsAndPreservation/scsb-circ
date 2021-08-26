package org.recap.request.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.ItemController;
import org.recap.model.jpa.*;
import org.recap.model.request.ItemRequestInformation;
import org.recap.repository.jpa.*;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;


/**
 * Created by hemalathas on 11/11/16.
 */
public class ItemValidatorServiceUT extends BaseTestCaseUT {

    @InjectMocks
    ItemValidatorService itemValidatorService;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private ItemController itemController;

    @Mock
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Mock
    private DeliveryCodeDetailsRepository deliveryCodeDetailsRepository;

    @Mock
    DeliveryCodeTranslationDetailsRepository deliveryCodeTranslationDetailsRepository;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    private PropertyUtil propertyUtil;

    @Mock
    ItemValidatorService mockedItemValidatorService;

    @Before
    public void setup(){
    }

    @Test
    public void testValidItem() throws Exception{
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"TRUE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals("Item Validation ", ScsbConstants.INITIAL_LOAD_ITEM_EXISTS,responseEntity.getBody());
    }

    @Test
    public void testValidItemFreezeUnavailable() throws Exception{
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        requestItemEntity.setId(0);
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"TRUE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"{1}");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals("Item Validation ", ScsbConstants.CIRCULATION_FREEZE_UNAVAILABLE_ITEM,responseEntity.getBody());
    }
    @Test
    public void testValidItemWithoutId() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"FALSE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        requestItemEntity.setId(0);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        ResponseEntity responseEntity1 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity1);
    }
    @Test
    public void testValidRecallAvailable() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        itemStatusEntity.setStatusCode(ScsbConstants.ITEM_STATUS_AVAILABLE);
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        requestItemEntity.setId(0);
        ImsLocationEntity imsLocationEntity = getImsLocationEntity();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"FALSE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_RECALL);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        ResponseEntity responseEntity2 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity2);
    }
    @Test
    public void testValidRecallNotAvailable() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        requestItemEntity.setId(0);
        ImsLocationEntity imsLocationEntity = getImsLocationEntity();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"FALSE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_RECALL);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        Mockito.when(imsLocationDetailsRepository.findById(any())).thenReturn(Optional.of(imsLocationEntity));
        ResponseEntity responseEntity2 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity2);
    }
    @Test
    public void testValidItemStatus() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        requestItemEntity.setId(0);
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"FALSE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(), anyString())).thenReturn(requestItemEntity);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        ResponseEntity responseEntity3 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity3);
    }
    @Test
    public void testValidItemStatusAvailable() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        requestItemEntity.setId(0);
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"FALSE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusDescription(ScsbCommonConstants.AVAILABLE);
        itemStatusEntity.setStatusCode(ScsbCommonConstants.AVAILABLE);
        Mockito.when(itemStatusDetailsRepository.findById(any())).thenReturn(Optional.of(itemStatusEntity));
        ResponseEntity responseEntity4 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity4);
    }
    @Test
    public void testValidItemStatusRecall() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestItemEntity requestItemEntity1 = getRequestItemEntity();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"TRUE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_STATUS_RECALLED);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(),anyString())).thenReturn(requestItemEntity1);
        ResponseEntity responseEntity5 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity5);
    }
    @Test
    public void testValidEDD() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestItemEntity requestItemEntity1 = getRequestItemEntity();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"TRUE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(any(),anyString())).thenReturn(requestItemEntity);
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_EDD);
        ResponseEntity responseEntity6 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity6);
    }
    @Test
    public void testValidMultipleBarcode() throws Exception {
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestItemEntity requestItemEntity1 = getRequestItemEntity();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"TRUE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity7 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity7);
    }

    @Test
    public void testItemValidationForRECALL(){
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        OwnerCodeEntity ownerCodeEntity = getOwnerCodeEntity();
        ImsLocationEntity imsLocationEntity = getImsLocationEntity();
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_RECALL);
        itemValidatorService.itemValidation(itemRequestInformation);
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"FALSE");
        Map<String, String> frozenInstitutionMessagesPropertyMap = new HashMap<>();
        frozenInstitutionMessagesPropertyMap.put(itemEntity.getInstitutionEntity().getInstitutionCode(),"Test");
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(imsLocationDetailsRepository.findById(any())).thenReturn(Optional.of(getImsLocationEntity()));
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_RECALL_FUNCTIONALITY_AVAILABLE)).thenReturn(frozenInstitutionMessagesPropertyMap);
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
//        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndOwningInstitutionCode(any(),any())).thenReturn(ownerCodeEntity);
//        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRequestingInstitution(any(),any(),anyString())).thenReturn(Collections.EMPTY_LIST);
        itemValidatorService.itemValidation(itemRequestInformation);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
//        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(imsLocationDetailsRepository.findById(any())).thenReturn(Optional.of(imsLocationEntity));
        itemValidatorService.itemValidation(itemRequestInformation);
        ItemRequestInformation itemRequestInformation1 = getItemRequestInformation(itemBarcodes);
        itemRequestInformation1.setDeliveryLocation("PA");
        itemRequestInformation1.setRequestType(ScsbCommonConstants.REQUEST_TYPE_RECALL);
        itemValidatorService.itemValidation(itemRequestInformation1);
    }
    @Test
    public void getCheckDeliveryLocation(){
        String ownerCode = "PA";
        Integer institution = Integer.valueOf(1);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("2456744"));
        OwnerCodeEntity ownerCodeEntity = getOwnerCodeEntity();
//        Mockito.when(ownerCodeDetailsRepository.findByOwnerCode(any())).thenReturn(ownerCodeEntity);
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        itemValidatorService.checkDeliveryLocation(ownerCode,institution, itemRequestInformation);
        itemValidatorService.checkDeliveryLocation(ownerCode,institution, itemRequestInformation);
        itemValidatorService.checkDeliveryLocation(ownerCode,institution, itemRequestInformation);
    }
    @Test
    public void getCheckDeliveryLocationForDifferentInstitution(){
        String ownerCode = "PA";
        Integer institution = Integer.valueOf(1);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("2456744"));
        itemRequestInformation.setRequestingInstitution("3");
        OwnerCodeEntity ownerCodeEntity = getOwnerCodeEntity();
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
//        Mockito.when(ownerCodeDetailsRepository.findByOwnerCode(any())).thenReturn(ownerCodeEntity);
        itemValidatorService.checkDeliveryLocation(ownerCode, institution, itemRequestInformation);
    }
    @Test
    public void getCheckDeliveryLocationForDifferentInstitutionWithSameDeliveryRestrictions(){
        String ownerCode = "PA";
        Integer institution = Integer.valueOf(1);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("2456744"));
        itemRequestInformation.setRequestingInstitution("PUL");
        OwnerCodeEntity ownerCodeEntity = getOwnerCodeEntity();
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getItemOwningInstitution())).thenReturn(getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndOwningInstitutionCode(anyString(),anyString())).thenReturn(ownerCodeEntity);
        itemValidatorService.checkDeliveryLocation(ownerCode, institution, itemRequestInformation);
        itemRequestInformation.setDeliveryLocation("PA");
        itemValidatorService.checkDeliveryLocation(ownerCode, institution, itemRequestInformation);
    }

    @Test
    public void checkDeliveryLocationTranslationCode(){
        ItemEntity itemEntity = getItemEntity();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("135787"));
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(deliveryCodeTranslationDetailsRepository.findByRequestingInstitutionandImsLocation(any(), any(), any())).thenReturn(getDeliveryCodeTranslationEntity());
        int bSuccess =  itemValidatorService.checkDeliveryLocationTranslationCode(itemEntity,itemRequestInformation);
        assertNotNull(bSuccess);
        assertEquals(1,bSuccess);
    }
    @Test
    public void checkDeliveryLocationTranslationCodeNull(){
        ItemEntity itemEntity = getItemEntity();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("135787"));
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(deliveryCodeTranslationDetailsRepository.findByRequestingInstitutionandImsLocation(any(), any(), any())).thenReturn(null);
        int bSuccess =  itemValidatorService.checkDeliveryLocationTranslationCode(itemEntity,itemRequestInformation);
        assertNotNull(bSuccess);
        assertEquals(-1,bSuccess);
    }

    private ItemRequestInformation getItemRequestInformation(List<String> itemBarcodes) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(itemBarcodes);
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setRequestType(ScsbCommonConstants.RETRIEVAL);
        return itemRequestInformation;
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

    @Test
    public void testValidateItemwithMultipleBarcodes(){
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();

        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestItemEntity requestItemEntity1 = getRequestItemEntity();
        requestItemEntity1.setId(0);
        itemBarcodes.add("1355321");
        OwnerCodeEntity ownerCodeEntity = getOwnerCodeEntity();
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        itemRequestInformation.setRequestType(ScsbCommonConstants.RECALL);
        ResponseEntity responseEntity = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity);

        itemRequestInformation.setRequestType(ScsbCommonConstants.RETRIEVAL);
        itemEntity.setItemAvailabilityStatusId(2);
        ResponseEntity responseEntity1 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity1);

        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity2 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity2);

        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity1);
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_STATUS_RECALLED);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity3 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity3);

        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_STATUS_RECALLED);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity1);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity1);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(getInstitutionEntity());
        ResponseEntity responseEntity4 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity4);
//        Mockito.when(ownerCodeDetailsRepository.findByOwnerCode(any())).thenReturn(ownerCodeEntity);
        ResponseEntity responseEntity5 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity5);

        OwnerCodeEntity ownerCodeEntity1 = getOwnerCodeEntity();
//        Mockito.when(ownerCodeDetailsRepository.findByOwnerCode(any())).thenReturn(ownerCodeEntity1);
        ResponseEntity responseEntity6 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity6);

    }
    private OwnerCodeEntity getOwnerCodeEntity() {
        ItemEntity itemEntity =getItemEntity();
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setId(1);
        ownerCodeEntity.setOwnerCode(itemEntity.getCustomerCode());
        return ownerCodeEntity;
    }
    private InstitutionEntity getInstitutionEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }

    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(2);
        itemStatusEntity.setStatusCode(ScsbCommonConstants.NOT_AVAILABLE);
        itemStatusEntity.setStatusDescription(ScsbCommonConstants.NOT_AVAILABLE);
        return itemStatusEntity;
    }

    @Test
    public void testInValidItem() throws Exception{
        saveBibSingleHoldingsMultipleItem();
        saveBibSingleHoldingsSingleItem();
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        itemBarcodes.add("11123");
        itemBarcodes.add("0325");
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setRequestType(ScsbCommonConstants.RETRIEVAL);
        itemRequestInformation.setItemBarcodes(itemBarcodes);
        itemRequestInformation.setDeliveryLocation("PB");
        ResponseEntity responseEntity = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity);
    }

    public void saveBibSingleHoldingsMultipleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(1);
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setCustomerCode("PB");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setBarcode("10123");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity1.setCatalogingStatus("Complete");

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setId(1);
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setCreatedBy("etl");
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setLastUpdatedBy("etl");
        itemEntity2.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setCustomerCode("PB");
        itemEntity2.setBarcode("11123");
        itemEntity2.setItemAvailabilityStatusId(1);
        itemEntity2.setCallNumber("x.12321");
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCallNumberType("1");
        itemEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity2.setCatalogingStatus("Complete");

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        assertNotNull(bibliographicEntity);
        assertNotNull(bibliographicEntity.getId());
        assertNotNull(bibliographicEntity.getHoldingsEntities().get(0).getId());
        assertNotNull(bibliographicEntity.getItemEntities().get(0).getId());
        assertNotNull(bibliographicEntity.getItemEntities().get(1).getId());


    }

    private ItemEntity getItemEntity(){
        Random random = new Random();
        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("{0}");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setImsLocationEntity(getImsLocationEntity());
        return itemEntity;
    }

    public void saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("{0}");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setCatalogingStatus("Complete");

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicEntity;

        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getId());
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0).getId());
    }

    private HoldingsEntity getHoldingsEntity(Random random, Integer institutionId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setId(1);
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(institutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        return holdingsEntity;
    }

    private BibliographicEntity getBibliographicEntity(Integer institutionId, String owningInstitutionBibId1) {
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setId(1);
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(institutionId);
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId1);
        return bibliographicEntity1;
    }
    private RequestItemEntity getRequestItemEntity(){
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setId(1);
        requestItemEntity.setItemEntity(getItemEntity());
        return requestItemEntity;
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
        deliveryCodeTranslationEntity.setInstitutionEntity(getInstitutionEntity());
        return deliveryCodeTranslationEntity;
    }
}
