package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ils.AbstractProtocolConnector;
import org.recap.ils.ILSProtocolConnectorFactory;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ImsLocationDetailsRepository;
import org.recap.request.ItemValidatorService;
import org.recap.request.RequestParamaterValidatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by hemalathas on 11/11/16.
 */
public class RequestItemValidatorControllerUT extends BaseTestCaseUT {

    @InjectMocks
    RequestItemValidatorController requestItemValidatorController;

    @Mock
    RequestParamaterValidatorService requestParamaterValidatorService;

    @Mock
    ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Mock
    ItemController itemController;

    @Mock
    AbstractProtocolConnector abstractProtocolConnector;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ItemValidatorService itemValidatorService;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Test
    public void testValidRequest() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsMultipleItem();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_RETRIEVAL);
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setItemBarcodes(Arrays.asList(bibliographicEntity.getItemEntities().get(0).getBarcode()));
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        Mockito.when(requestParamaterValidatorService.validateItemRequestParameters(any())).thenReturn(null);
        Mockito.when(itemValidatorService.itemValidation(any())).thenReturn(responseEntity);
        Mockito.when(ilsProtocolConnectorFactory.getIlsProtocolConnector(any())).thenReturn(abstractProtocolConnector);
        Mockito.when(abstractProtocolConnector.patronValidation(any(), any())).thenReturn(false);
        ResponseEntity response = requestItemValidatorController.validateItemRequestInformations(itemRequestInformation);
        assertNotNull(response);
    }

    @Test
    public void testvalidateItemRequest() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsMultipleItem();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(bibliographicEntity);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        Mockito.when(requestParamaterValidatorService.validateItemRequestParameters(any())).thenReturn(responseEntity);
        ResponseEntity response = requestItemValidatorController.validateItemRequest(itemRequestInformation);
        assertNotNull(response);
    }

    @Test
    public void testvalidateItemRequestNullResponse() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsMultipleItem();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(bibliographicEntity);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        Mockito.when(requestParamaterValidatorService.validateItemRequestParameters(any())).thenReturn(null);
        Mockito.when(itemValidatorService.itemValidation(any())).thenReturn(responseEntity);
        ResponseEntity response = requestItemValidatorController.validateItemRequest(itemRequestInformation);
        assertNotNull(response);
    }

    private ItemRequestInformation getItemRequestInformation(BibliographicEntity bibliographicEntity) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("4567gfdr8915");
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_RETRIEVAL);
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setItemBarcodes(Arrays.asList(bibliographicEntity.getItemEntities().get(0).getBarcode()));
        return itemRequestInformation;
    }

    private BibliographicEntity saveBibSingleHoldingsMultipleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = getBibliographicEntity(1, String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = getHoldingsEntity(random, 1);

        ImsLocationEntity imsLocationEntity = getImsLocationEntity();

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("etl");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("etl");
        itemEntity1.setCustomerCode("PB");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setBarcode("009");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity1.setCatalogingStatus("Complete");
        itemEntity1.setImsLocationEntity(imsLocationEntity);

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setCreatedBy("etl");
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setLastUpdatedBy("etl");
        itemEntity2.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setCustomerCode("PB");
        itemEntity2.setBarcode("010");
        itemEntity2.setItemAvailabilityStatusId(1);
        itemEntity2.setCallNumber("x.12321");
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCallNumberType("1");
        itemEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity2.setCatalogingStatus("Complete");
        itemEntity2.setImsLocationEntity(imsLocationEntity);

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));


        return bibliographicEntity;
    }

    private HoldingsEntity getHoldingsEntity(Random random, Integer institutionId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
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
        bibliographicEntity1.setContent("mock Content".getBytes());
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setCreatedBy("etl");
        bibliographicEntity1.setLastUpdatedBy("etl");
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(institutionId);
        bibliographicEntity1.setOwningInstitutionBibId(owningInstitutionBibId1);
        return bibliographicEntity1;
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

}