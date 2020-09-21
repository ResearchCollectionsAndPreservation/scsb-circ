package org.recap.service;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapConstants;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.PendingRequestDetailsRespository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class IdentifyPendingRequestServiceUT{
    @InjectMocks
    IdentifyPendingRequestService identifyPendingRequestService;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    PendingRequestDetailsRespository pendingRequestDetailsRespository;

    @Mock
    ProducerTemplate producerTemplate;

    @Test
    public void testidentifyNonPendingRequest() {
        RequestItemEntity requestItemEntity = getRequestItem();
        Mockito.when(requestItemDetailsRepository.findPendingAndLASReqNotNotified(Arrays.asList(RecapConstants.REQUEST_STATUS_PENDING, RecapConstants.REQUEST_STATUS_LAS_ITEM_STATUS_PENDING))).thenReturn(Arrays.asList(requestItemEntity));
        boolean status = identifyPendingRequestService.identifyPendingRequest();
        assertTrue(status);
    } @Test
    public void testidentifyPendingRequest() {
        RequestItemEntity requestItemEntity = getRequestItem();
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusDescription("PENDING");
        requestStatusEntity.setRequestStatusCode("PENDING");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime then = now.minusDays(7);
        Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());
        requestItemEntity.setCreatedDate(date);
        Mockito.when(requestItemDetailsRepository.findPendingAndLASReqNotNotified(Arrays.asList(RecapConstants.REQUEST_STATUS_PENDING, RecapConstants.REQUEST_STATUS_LAS_ITEM_STATUS_PENDING))).thenReturn(Arrays.asList(requestItemEntity));
        boolean status = identifyPendingRequestService.identifyPendingRequest();
        assertTrue(status);
    }
    public RequestItemEntity getRequestItem() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(16);
        requestItemEntity.setItemId(1);
        requestItemEntity.setRequestTypeId(3);
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

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        ItemEntity itemEntity = getItemEntity();
        RequestStatusEntity requestStatusEntity =  new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("REQUEST_PLACED");
        requestStatusEntity.setRequestStatusDescription("REQUEST PLACED");

        requestItemEntity.setId(1);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(itemEntity);
        return requestItemEntity;
    }

    private ItemEntity getItemEntity() {

        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusDescription("COMPLETE");
        itemStatusEntity.setStatusCode("AVAILABLE");
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCollectionGroupCode("Complete");
        collectionGroupEntity.setCollectionGroupDescription("Complete");
        collectionGroupEntity.setLastUpdatedDate(new Date());
        collectionGroupEntity.setCreatedDate(new Date());
        Random random = new Random();
        //BibliographicEntity bibliographicEntity = getBibliographicEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        //itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        return itemEntity;
    }
}
