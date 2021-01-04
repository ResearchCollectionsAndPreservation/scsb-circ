package org.recap.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.controller.ItemController;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


/**
 * Created by hemalathas on 11/11/16.
 */

public class ItemValidatorServiceUT extends BaseTestCaseUT {

    @Value("${scsb.solr.doc.url}")
    String scsbSolrClientUrl;

    @InjectMocks
    ItemValidatorService itemValidatorService;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private ItemController itemController;

    @Mock
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

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
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestItemEntity requestItemEntity1 = getRequestItemEntity();
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        ImsLocationEntity imsLocationEntity = getImsLocationEntity();
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals("Item Validation ", RecapConstants.INITIAL_LOAD_ITEM_EXISTS,responseEntity.getBody());
        requestItemEntity.setId(0);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        ResponseEntity responseEntity1 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity1);
        itemRequestInformation.setRequestType(RecapCommonConstants.REQUEST_TYPE_RECALL);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_EDD)).thenReturn(requestItemEntity1);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        Mockito.when(imsLocationDetailsRepository.findById(any())).thenReturn(Optional.of(imsLocationEntity));
        ResponseEntity responseEntity2 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity2);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_EDD)).thenReturn(requestItemEntity);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(requestItemEntity1);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        ResponseEntity responseEntity3 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity3);
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusDescription(RecapCommonConstants.AVAILABLE);
        itemStatusEntity.setStatusCode(RecapCommonConstants.AVAILABLE);
        Mockito.when(itemStatusDetailsRepository.findById(itemEntity.getItemAvailabilityStatusId())).thenReturn(Optional.of(itemStatusEntity));
        ResponseEntity responseEntity4 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity4);
        itemRequestInformation.setRequestType(RecapCommonConstants.REQUEST_STATUS_RECALLED);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity1);
        ResponseEntity responseEntity5 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity5);
        itemRequestInformation.setRequestType(RecapCommonConstants.REQUEST_TYPE_EDD);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(itemEntity.getCustomerCode())).thenReturn(customerCodeEntity);
        ResponseEntity responseEntity6 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity6);
        itemBarcodes.add("1355321");
        ResponseEntity responseEntity7 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity7);

    }

    @Test
    public void testItemValidationForRECALL(){
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("10123");
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(itemBarcodes);
        ItemEntity itemEntity = getItemEntity();
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        customerCodeEntity.setDeliveryRestrictions("PA");
        ImsLocationEntity imsLocationEntity = getImsLocationEntity();
        itemRequestInformation.setRequestType(RecapCommonConstants.REQUEST_TYPE_RECALL);
        itemValidatorService.itemValidation(itemRequestInformation);
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(itemRequestInformation.getDeliveryLocation(), itemRequestInformation.getRequestingInstitution())).thenReturn(getCustomerCodeEntity());
        itemValidatorService.itemValidation(itemRequestInformation);
        Mockito.when(imsLocationDetailsRepository.findById(any())).thenReturn(Optional.of(imsLocationEntity));
        itemValidatorService.itemValidation(itemRequestInformation);
        ItemRequestInformation itemRequestInformation1 = getItemRequestInformation(itemBarcodes);
        itemRequestInformation1.setDeliveryLocation("PA");
        itemRequestInformation1.setRequestType(RecapCommonConstants.REQUEST_TYPE_RECALL);
        itemValidatorService.itemValidation(itemRequestInformation1);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(any(),any())).thenReturn(getCustomerCodeEntity());
        itemValidatorService.itemValidation(itemRequestInformation1);
    }
    @Test
    public void getCheckDeliveryLocation(){
        String customerCode = "PA";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("2456744"));
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(itemRequestInformation.getDeliveryLocation(), itemRequestInformation.getRequestingInstitution())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        itemValidatorService.checkDeliveryLocation(customerCode,itemRequestInformation);
        customerCodeEntity.setDeliveryRestrictions("CA");
        itemValidatorService.checkDeliveryLocation(customerCode,itemRequestInformation);
        customerCodeEntity.setDeliveryRestrictions("PA");
        itemValidatorService.checkDeliveryLocation(customerCode,itemRequestInformation);
    }
    @Test
    public void getCheckDeliveryLocationForDifferentInstitution(){
        String customerCode = "PA";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("2456744"));
        itemRequestInformation.setRequestingInstitution("3");
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(itemRequestInformation.getDeliveryLocation(), itemRequestInformation.getRequestingInstitution())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        itemValidatorService.checkDeliveryLocation(customerCode,itemRequestInformation);
    }
    @Test
    public void getCheckDeliveryLocationForDifferentInstitutionWithSameDeliveryRestrictions(){
        String customerCode = "PA";
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(Arrays.asList("2456744"));
        itemRequestInformation.setRequestingInstitution("PUL");
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(any(), any())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        itemValidatorService.checkDeliveryLocation(customerCode,itemRequestInformation);
        itemRequestInformation.setDeliveryLocation("PA");
        itemValidatorService.checkDeliveryLocation(customerCode,itemRequestInformation);
    }

    private ItemRequestInformation getItemRequestInformation(List<String> itemBarcodes) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(itemBarcodes);
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setRequestType(RecapCommonConstants.RETRIEVAL);
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
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestItemEntity requestItemEntity1 = getRequestItemEntity();
        requestItemEntity1.setId(0);
        itemBarcodes.add("1355321");
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        customerCodeEntity.setDeliveryRestrictions("PB");
        Mockito.when(itemController.findByBarcodeIn(itemBarcodes.toString())).thenReturn(Arrays.asList(itemEntity));
        itemRequestInformation.setRequestType(RecapCommonConstants.RECALL);
        ResponseEntity responseEntity = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity);

        itemRequestInformation.setRequestType(RecapCommonConstants.RETRIEVAL);
        itemEntity.setItemAvailabilityStatusId(2);
        ResponseEntity responseEntity1 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity1);

        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity2 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity2);

        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity1);
        itemRequestInformation.setRequestType(RecapCommonConstants.REQUEST_STATUS_RECALLED);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        ResponseEntity responseEntity3 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity3);

        itemRequestInformation.setRequestType(RecapCommonConstants.REQUEST_STATUS_RECALLED);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD)).thenReturn(requestItemEntity1);
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemEntity.getBarcode(), RecapCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity1);
        ResponseEntity responseEntity4 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity4);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(any(),any())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        ResponseEntity responseEntity5 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity5);

        CustomerCodeEntity customerCodeEntity1 = getCustomerCodeEntity();
        customerCodeEntity1.setDeliveryRestrictions("PA");
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(any(),any())).thenReturn(customerCodeEntity1);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity1);
        ResponseEntity responseEntity6 = itemValidatorService.itemValidation(itemRequestInformation);
        assertNotNull(responseEntity6);

    }
    private CustomerCodeEntity getCustomerCodeEntity() {
        ItemEntity itemEntity =getItemEntity();
        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setId(1);
        customerCodeEntity.setCustomerCode(itemEntity.getCustomerCode());
        customerCodeEntity.setPickupLocation("PA");
        customerCodeEntity.setPwdDeliveryRestrictions("PU");
        DeliveryRestrictionEntity deliveryRestrictionEntity = new DeliveryRestrictionEntity();
        deliveryRestrictionEntity.setDeliveryRestriction("PA");
        deliveryRestrictionEntity.setInstitutionEntity(getInstitutionEntity());
        customerCodeEntity.setDeliveryRestrictionEntityList(Arrays.asList(deliveryRestrictionEntity));
        return customerCodeEntity;
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
        itemStatusEntity.setStatusCode(RecapCommonConstants.NOT_AVAILABLE);
        itemStatusEntity.setStatusDescription(RecapCommonConstants.NOT_AVAILABLE);
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
        itemRequestInformation.setRequestType(RecapCommonConstants.RETRIEVAL);
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
        itemEntity1.setItemId(1);
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
        itemEntity2.setItemId(1);
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
        assertNotNull(bibliographicEntity.getBibliographicId());
        assertNotNull(bibliographicEntity.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(bibliographicEntity.getItemEntities().get(0).getItemId());
        assertNotNull(bibliographicEntity.getItemEntities().get(1).getItemId());


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
        itemEntity.setBarcode("0325");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setCatalogingStatus("Complete");
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
        itemEntity.setItemId(1);
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("0325");
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
        assertNotNull(savedBibliographicEntity.getBibliographicId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0).getItemId());
    }

    private HoldingsEntity getHoldingsEntity(Random random, Integer institutionId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setHoldingsId(1);
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
        bibliographicEntity1.setBibliographicId(1);
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
}