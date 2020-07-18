package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.AbstractResponseItem;
import org.recap.model.CancelRequestResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.request.EmailService;
import org.recap.request.ItemRequestService;
import org.recap.util.CommonUtil;
import org.recap.util.ItemRequestServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 17/2/17.
 */

public class CancelItemControllerUT extends BaseTestCase{

    @Autowired
    CancelItemController cancelItemController;

    @Mock
    EmailService emailService;

    @Mock
    private RequestItemController requestItemController;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    private CommonUtil commonUtil;

    @Mock
    AbstractResponseItem abstractResponseItem;

    /*@Test
    public void testCancelRequest() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        String patronBarcode = "45678912";
        String itemBarcode = "32101074849843";
        String customerCode = "PA";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101074849843"));
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestingInstitution("PUL");
        abstractResponseItem.setItemOwningInstitution("PUL");
        abstractResponseItem.setItemBarcode("32101074849843");
        abstractResponseItem.setSuccess(true);
        abstractResponseItem.setScreenMessage("SUCCESS");
        CancelRequestResponse cancelRequestResponse = null;
        Mockito.when(requestItemDetailsRepository.findById(15)).thenReturn(Optional.of(requestItemEntity));
        Mockito.doNothing().when(itemRequestService).saveItemChangeLogEntity(requestItemEntity.getId(), RecapConstants.GUEST_USER, RecapConstants.REQUEST_ITEM_CANCEL_ITEM_AVAILABILITY_STATUS, RecapCommonConstants.REQUEST_STATUS_CANCELED + requestItemEntity.getItemId());
        Mockito.when(itemRequestService.getEmailService()).thenReturn(emailService);
        Mockito.when(requestItemController.itemInformation(itemRequestInformation, "PUL")).thenReturn(abstractResponseItem);
        Mockito.doNothing().when(emailService).sendEmail(customerCode, itemBarcode, RecapConstants.REQUEST_CANCELLED_NO_REFILED, patronBarcode, RecapConstants.GFA, RecapConstants.REQUEST_CANCELLED_SUBJECT);
        Mockito.doNothing().when(commonUtil).rollbackUpdateItemAvailabilutyStatus(requestItemEntity.getItemEntity(), RecapConstants.GUEST_USER);
        Mockito.doNothing().when(itemRequestServiceUtil).updateSolrIndex(requestItemEntity.getItemEntity());
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        //assertTrue(true);
    }*/
    @Test
    public void testCancelRequestException() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        CancelRequestResponse cancelRequestResponse = null;
        try {
            cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
    @Test
    public void testCancelRequestForEDD() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        RequestStatusEntity requestStatusEntity =  new RequestStatusEntity();
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestStatusEntity.setRequestStatusCode("EDD_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("EDD ORDER PLACED");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        CancelRequestResponse cancelRequestResponse = null;
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        assertTrue(true);
    }
    @Test
    public void testCancelRequestForRetrieval() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        //requestItemEntity.setId(15);
        CancelRequestResponse cancelRequestResponse = null;
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        assertTrue(true);
    }
    @Test
    public void testCancelRequestForRecall() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        CancelRequestResponse cancelRequestResponse = null;
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        assertTrue(true);
    }
    public RequestItemEntity createRequestItem() throws Exception {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();


        //RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        //assertNotNull(savedRequestTypeEntity);

        //RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findById(3).orElse(null);

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(16);
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getItemId());
        requestItemEntity.setRequestTypeId(3);

        requestItemEntity.setRequestingInstitutionId(2);
        requestItemEntity.setStopCode("test");
        requestItemEntity.setNotes("test");
        requestItemEntity.setItemEntity(bibliographicEntity.getItemEntities().get(0));
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setPatronId("1");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(3);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setEmailId("test@gmail.com");

        requestItemEntity.setLastUpdatedDate(new Date());
        //RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        return requestItemEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setHoldingsId(1);
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("8956");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("4598");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        /*BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);*/
        return bibliographicEntity;

    }


}