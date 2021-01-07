package org.recap.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.controller.RequestItemController;
import org.recap.ils.model.response.ItemCheckoutResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BulkRequestItemDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.CommonUtil;
import org.recap.util.ItemRequestServiceUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;


public class BulkItemRequestProcessServiceUT extends BaseTestCaseUT {
    @InjectMocks
    BulkItemRequestProcessService bulkItemRequestProcessService;

    @Mock
    private BulkRequestItemDetailsRepository bulkRequestItemDetailsRepository;

    @Mock
    private ItemRequestDBService itemRequestDBService;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private RequestItemController requestItemController;

    @Mock
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    private GFAService gfaService;

    @Mock
    private CommonUtil commonUtil;

    @Before
    public void setup() {
    }

    @Test
    public void testBulkItemRequestProcessService() {
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setId(1);
        bulkRequestItemEntity.setBulkRequestName("TestFirstBulkRequest");
        bulkRequestItemEntity.setBulkRequestFileName("bulkItemUpload");
        bulkRequestItemEntity.setBulkRequestFileData("BARCODE\tCUSTOMER_CODE\n32101075852275\tPK".getBytes());
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setBulkRequestStatus(RecapConstants.PROCESSED);
        bulkRequestItemEntity.setCreatedBy("TestUser");
        bulkRequestItemEntity.setCreatedDate(new Date());
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setPatronId("45678915");
        try {
            bulkItemRequestProcessService.processBulkRequestItem("33433001888415", bulkRequestItemEntity.getId());
        } catch (Exception e) {
        }
    }

    @Test
    public void processBulkRequestItem() {
        String itemBarcode = "Complete";
        int bulkRequestId = 1;
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Mockito.when(bulkRequestItemDetailsRepository.findById(bulkRequestId)).thenReturn(Optional.of(bulkRequestItemEntity));
        Mockito.when(bulkRequestItemDetailsRepository.save(bulkRequestItemEntity)).thenReturn(bulkRequestItemEntity);
        bulkItemRequestProcessService.processBulkRequestItem(itemBarcode, bulkRequestId);
    }

    @Test
    public void processBulkRequestItemForException() {
        String itemBarcode = "Complete";
        int bulkRequestId = 1;
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        bulkItemRequestProcessService.processBulkRequestItem(itemBarcode, bulkRequestId);
    }

    @Test
    public void processBulkRequestItemForBarcode() {
        String itemBarcode = "123456";
        int bulkRequestId = 1;
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        ItemEntity itemEntity = getItemEntity();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setRequestTypeForScheduledOnWO(true);
        itemInformationResponse.setSuccess(true);
        Mockito.when(requestItemController.checkoutItem(any(), any())).thenReturn(itemCheckoutResponse);
        Mockito.when(bulkRequestItemDetailsRepository.findById(bulkRequestId)).thenReturn(Optional.of(bulkRequestItemEntity));
        Mockito.when(itemDetailsRepository.findByBarcode(itemBarcode)).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(gfaService.isUseQueueLasCall()).thenReturn(true);
        Mockito.doNothing().when(itemRequestDBService).updateItemAvailabilityStatus(Arrays.asList(itemEntity), bulkRequestItemEntity.getCreatedBy());
        Mockito.when(gfaService.executeRetrieveOrder(any(), any())).thenReturn(itemInformationResponse);
        bulkItemRequestProcessService.processBulkRequestItem(itemBarcode, bulkRequestId);
        itemInformationResponse.setRequestTypeForScheduledOnWO(false);
        Mockito.when(gfaService.isUseQueueLasCall()).thenReturn(true);
        Mockito.when(gfaService.executeRetrieveOrder(any(), any())).thenReturn(itemInformationResponse);
        bulkItemRequestProcessService.processBulkRequestItem(itemBarcode, bulkRequestId);
        itemInformationResponse.setSuccess(false);
        bulkItemRequestProcessService.processBulkRequestItem(itemBarcode, bulkRequestId);

    }

    @Test
    public void processBulkRequestItemForBarcodeFailure() {
        String itemBarcode = "123456";
        int bulkRequestId = 1;
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        ItemEntity itemEntity = getItemEntity();
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        itemCheckoutResponse.setSuccess(false);
        Mockito.when(bulkRequestItemDetailsRepository.findById(bulkRequestId)).thenReturn(Optional.of(bulkRequestItemEntity));
        Mockito.when(itemDetailsRepository.findByBarcode(itemBarcode)).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(gfaService.isUseQueueLasCall()).thenReturn(true);
        Mockito.doNothing().when(itemRequestDBService).updateItemAvailabilityStatus(Arrays.asList(itemEntity), bulkRequestItemEntity.getCreatedBy());
        Mockito.when(requestItemController.checkoutItem(any(), any())).thenReturn(itemCheckoutResponse);
        bulkItemRequestProcessService.processBulkRequestItem(itemBarcode, bulkRequestId);

    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123456"));
        itemRequestInformation.setTitleIdentifier(null);
        itemRequestInformation.setPatronBarcode("123456");
        itemRequestInformation.setItemOwningInstitution("");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("test@gmail.com");
        itemRequestInformation.setRequestType("RETRIEVAL");
        itemRequestInformation.setDeliveryLocation("PA");
        itemRequestInformation.setCustomerCode("PA");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setTrackingId(null);
        itemRequestInformation.setChapterTitle("");
        itemRequestInformation.setBibId("");
        itemRequestInformation.setUsername("test(Bulk)");
        itemRequestInformation.setRequestId(0);
        return itemRequestInformation;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        return itemEntity;
    }

    private BulkRequestItemEntity getBulkRequestItemEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        ItemEntity itemEntity = getItemEntity();
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL ORDER PLACED");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(itemEntity);
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setPatronId("123456");
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setNotes("test");
        bulkRequestItemEntity.setEmailId("test@gmail.com");
        bulkRequestItemEntity.setCreatedBy("test");
        bulkRequestItemEntity.setInstitutionEntity(institutionEntity);
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setRequestItemEntities(Arrays.asList(requestItemEntity));
        return bulkRequestItemEntity;
    }

}
