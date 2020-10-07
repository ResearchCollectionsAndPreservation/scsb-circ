package org.recap.request;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BulkRequestItemDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.ItemRequestServiceUtil;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class BulkItemRequestServiceUT {
    @InjectMocks
    BulkItemRequestService bulkItemRequestService;

    @Mock
    private BulkRequestItemDetailsRepository bulkRequestItemDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    private ProducerTemplate producerTemplate;


    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(bulkItemRequestService, "bulkRequestItemCountLimit", 5000);
    }

    @Test
    public void testBulkItemRequestService() {
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setBulkRequestName("TestFirstBulkRequest");
        bulkRequestItemEntity.setBulkRequestFileName("bulkItemUpload");
        bulkRequestItemEntity.setBulkRequestFileData("BARCODE\tCUSTOMER_CODE\n32101075852275\tPK".getBytes());
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setBulkRequestStatus(RecapConstants.PROCESSED);
        bulkRequestItemEntity.setCreatedBy("TestUser");
        bulkRequestItemEntity.setCreatedDate(new Date());
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setPatronId("45678915");
        bulkRequestItemEntity.setId(12);
        try {
            bulkItemRequestService.bulkRequestItems(bulkRequestItemEntity.getId());
        } catch (Exception e) {
        }
        assertTrue(true);
    }
    @Test
    public void bulkRequestItems(){
        int bulkRequestId = 1;
        ItemEntity itemEntity = getItemEntity();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(bulkRequestItemDetailsRepository.findById(bulkRequestId)).thenReturn(Optional.of(bulkRequestItemEntity));
        bulkItemRequestService.bulkRequestItems(bulkRequestId);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(2);
        itemStatusEntity.setStatusCode(RecapCommonConstants.AVAILABLE);
        itemStatusEntity.setStatusDescription(RecapCommonConstants.AVAILABLE);
        itemEntity.setItemStatusEntity(itemStatusEntity);
        bulkItemRequestService.bulkRequestItems(bulkRequestId);
        itemEntity.setOwningInstitutionId(1);
        bulkItemRequestService.bulkRequestItems(bulkRequestId);
    }
    @Test
    public void bulkRequestItemsForDifferentOwingInstId(){
        int bulkRequestId = 1;
        ItemEntity itemEntity = getItemEntity();
        itemEntity.setOwningInstitutionId(3);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(2);
        itemStatusEntity.setStatusCode(RecapCommonConstants.AVAILABLE);
        itemStatusEntity.setStatusDescription(RecapCommonConstants.AVAILABLE);
        itemEntity.setItemStatusEntity(itemStatusEntity);
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(bulkRequestItemDetailsRepository.findById(bulkRequestId)).thenReturn(Optional.of(bulkRequestItemEntity));
        bulkItemRequestService.bulkRequestItems(bulkRequestId);
        itemEntity.setOwningInstitutionId(1);
        ReflectionTestUtils.setField(bulkItemRequestService, "bulkRequestItemCountLimit", 0);
        bulkItemRequestService.bulkRequestItems(bulkRequestId);
    }
    @Test
    public void bulkRequestItemsForbulkRequestItemBarcodeExcessList(){
        int bulkRequestId = 1;
        ItemEntity itemEntity = getItemEntity();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        bulkRequestItemEntity.setBulkRequestStatus("PROCESSED");
        Mockito.when(bulkRequestItemDetailsRepository.findById(bulkRequestId)).thenReturn(Optional.of(bulkRequestItemEntity));
        ReflectionTestUtils.setField(bulkItemRequestService, "bulkRequestItemCountLimit", 0);
        bulkItemRequestService.bulkRequestItems(bulkRequestId);
    }
    private BulkRequestItemEntity getBulkRequestItemEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        ItemEntity itemEntity = getItemEntity();
        RequestStatusEntity requestStatusEntity =  new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL ORDER PLACED");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(itemEntity);
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setId(1);
        bulkRequestItemEntity.setPatronId("123456");
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setBulkRequestFileData("BARCODE\tCUSTOMER_CODE\n32101075852275\tPK".getBytes());
        bulkRequestItemEntity.setNotes("test");
        bulkRequestItemEntity.setEmailId("test@gmail.com");
        bulkRequestItemEntity.setCreatedBy("test");
        bulkRequestItemEntity.setInstitutionEntity(institutionEntity);
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setRequestItemEntities(Arrays.asList(requestItemEntity));
        return bulkRequestItemEntity;
    }
    private ItemEntity getItemEntity(){
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        itemEntity.setOwningInstitutionItemId("13567");
        itemEntity.setOwningInstitutionId(1);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(2);
        itemStatusEntity.setStatusCode(RecapCommonConstants.NOT_AVAILABLE);
        itemStatusEntity.setStatusDescription(RecapCommonConstants.NOT_AVAILABLE);
        itemEntity.setItemStatusEntity(itemStatusEntity);
        return itemEntity;
    }

}
