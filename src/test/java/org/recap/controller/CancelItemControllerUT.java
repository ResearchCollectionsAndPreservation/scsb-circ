package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.response.ItemHoldResponse;
import org.recap.model.response.ItemInformationResponse;
import org.recap.model.CancelRequestResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.request.service.EmailService;
import org.recap.request.service.ItemRequestService;
import org.recap.util.CommonUtil;
import org.recap.request.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by hemalathas on 17/2/17.
 */
public class CancelItemControllerUT extends BaseTestCaseUT {

    @InjectMocks
    CancelItemController cancelItemController;

    @Mock
    EmailService emailService;

    @Mock
    PropertyUtil propertyUtil;

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
    ItemInformationResponse itemInformationResponse;

    @Test
    public void testCancelRequest() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setSuccess(true);
        CancelRequestResponse cancelRequestResponse = null;
        Mockito.when(requestItemDetailsRepository.findById(16)).thenReturn(Optional.of(requestItemEntity));
        Mockito.doNothing().when(itemRequestService).saveItemChangeLogEntity(requestItemEntity.getId(), ScsbConstants.GUEST_USER, ScsbConstants.REQUEST_ITEM_CANCEL_ITEM_AVAILABILITY_STATUS, ScsbCommonConstants.REQUEST_STATUS_CANCELED + requestItemEntity.getItemId());
        Mockito.when(itemRequestService.getEmailService()).thenReturn(emailService);
        Mockito.when(requestItemController.itemInformation(any(), any())).thenReturn(itemInformationResponse);
        Mockito.when(requestItemDetailsRepository.save(any())).thenReturn(requestItemEntity);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(any(), any())).thenReturn(itemInformationResponse.getCirculationStatus());
        Mockito.when(requestItemController.cancelHoldItem(any(), any())).thenReturn(itemHoldResponse);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(requestItemEntity.getRequestStatusEntity());
        Mockito.doNothing().when(emailService).sendEmail(any(),any(),any(),any(),any(), any(),any());
        Mockito.doNothing().when(commonUtil).rollbackUpdateItemAvailabilityStatus(requestItemEntity.getItemEntity(), ScsbConstants.GUEST_USER);
        Mockito.doNothing().when(itemRequestServiceUtil).updateSolrIndex(requestItemEntity.getItemEntity());
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        RequestStatusEntity requestStatusEntity =  new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL_ORDER_PLACED");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        itemHoldResponse.setSuccess(false);
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        itemInformationResponse.setHoldQueueLength("");
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        itemHoldResponse.setSuccess(true);
        itemInformationResponse.setHoldQueueLength("2");
        requestStatusEntity.setRequestStatusCode("RECALL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RECALL_ORDER_PLACED");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        itemHoldResponse.setSuccess(false);
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        itemInformationResponse.setHoldQueueLength("");
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        requestStatusEntity.setRequestStatusCode("CANCELED");
        requestStatusEntity.setRequestStatusDescription("CANCELED");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);

    }
    @Test
    public void testCancelRequestException() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        CancelRequestResponse cancelRequestResponse = null;
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
        Mockito.when(requestItemDetailsRepository.findById(16)).thenReturn(Optional.of(requestItemEntity));
        cancelRequestResponse = cancelItemController.cancelRequest(requestItemEntity.getId());
        assertNotNull(cancelRequestResponse);
    }

    @Test
    public void processCancelRequest() throws Exception {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setHoldQueueLength("0");
        RequestItemEntity requestItemEntity = createRequestItem();
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInformation.getRequestingInstitution(), PropertyKeyConstants.ILS.ILS_CHECKEDOUT_CIRCULATION_STATUS)).thenReturn(null);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(createRequestItem().getRequestStatusEntity());
        Mockito.when(requestItemDetailsRepository.save(requestItemEntity)).thenReturn(createRequestItem());
        Mockito.when(itemRequestService.getEmailService()).thenReturn(emailService);
        Mockito.doNothing().when(itemRequestService).saveItemChangeLogEntity(any(), any(), any(), any());
        Mockito.doNothing().when(emailService).sendEmail(any(),any(),any(),any(),any(), any(),any());
        ReflectionTestUtils.invokeMethod(cancelItemController,"processCancelRequest",itemRequestInformation,itemInformationResponse,requestItemEntity);
    }

    @Test
    public void processRecall() throws Exception {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setHoldQueueLength("0");
        RequestItemEntity requestItemEntity = createRequestItem();
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInformation.getRequestingInstitution(), PropertyKeyConstants.ILS.ILS_CHECKEDOUT_CIRCULATION_STATUS)).thenReturn(null);
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(ScsbCommonConstants.REQUEST_STATUS_CANCELED)).thenReturn(createRequestItem().getRequestStatusEntity());
        Mockito.when(requestItemDetailsRepository.save(requestItemEntity)).thenReturn(createRequestItem());
        Mockito.doNothing().when(itemRequestService).saveItemChangeLogEntity(any(), any(), any(), any());
        ReflectionTestUtils.invokeMethod(cancelItemController,"processRecall",itemRequestInformation,itemInformationResponse,requestItemEntity);
    }
    private ItemInformationResponse getItemInformationResponse() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101074849843"));
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestingInstitution("PUL");
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setBibID("134556");
        itemInformationResponse.setItemOwningInstitution("PUL");
        itemInformationResponse.setItemBarcode("32101074849843");
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setScreenMessage("SUCCESS");
        itemInformationResponse.setHoldQueueLength("2");
        itemInformationResponse.setCirculationStatus("IN_TRANSIT");
        return itemInformationResponse;
    }

    public RequestItemEntity createRequestItem() throws Exception {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setId(1);
        requestTypeEntity.setRequestTypeDesc("EDD");
        requestTypeEntity.setRequestTypeCode("EDD");
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setId(1);
        requestStatusEntity.setRequestStatusDescription("LAS_REFILE_REQUEST_PLACED");
        requestStatusEntity.setRequestStatusCode("LAS_REFILE_REQUEST_PLACED");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(16);
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getId());
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
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setLastUpdatedDate(new Date());
        //RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        return requestItemEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(1);
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setId(1);
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
        itemEntity.setId(1);
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
        itemEntity.setImsLocationEntity(getImsLocationEntity());
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        /*BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);*/
        return bibliographicEntity;

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
