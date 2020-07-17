package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.gfa.model.TtitemEDDResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BulkRequestItemDetailsRepository;
import org.recap.request.EmailService;
import org.recap.service.RestHeaderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ItemRequestServiceUtilUT {
    @InjectMocks
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    private RestHeaderService restHeaderService;

    @Mock
    private EmailService emailService;

    @Mock
    private BulkRequestItemDetailsRepository bulkRequestItemDetailsRepository;

   /* @Before
    public void before(){
        itemRequestServiceUtil= Mockito.mock(ItemRequestServiceUtil.class);
    }*/
    @Test
    public void testupdateSolrIndex(){
        ItemEntity itemEntity=new ItemEntity();
        itemRequestServiceUtil.updateSolrIndex(itemEntity);
    }
    @Test
    public void testupdateStatusToBarcodes(){
        BulkRequestItem bulkRequestItem = getBulkRequestItem();
        List<BulkRequestItem> test=new ArrayList<>();
        test.add(bulkRequestItem);
        BulkRequestItemEntity BulkRequestItemEntity = getBulkRequestItemEntity();
        itemRequestServiceUtil.updateStatusToBarcodes(test,BulkRequestItemEntity);
        assertTrue(true);
    }

    @Test
    public void testbuildCsvFormatData(){
        BulkRequestItem bulkRequestItem = getBulkRequestItem();
        List<BulkRequestItem> test=new ArrayList<>();
        test.add(bulkRequestItem);
        StringBuilder testdata =new StringBuilder("test data");
        itemRequestServiceUtil.buildCsvFormatData(test,testdata);
        assertTrue(true);
    }
    @Test
    public void testgenerateReportAndSendEmail(){
        Integer bulkRequestId=1234;
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Mockito.when(bulkRequestItemDetailsRepository.findById(bulkRequestId)).thenReturn(Optional.of(bulkRequestItemEntity));
        /*Mockito.doNothing().when(emailService.sendBulkRequestEmail(String.valueOf(bulkRequestItemEntity.get().getId()),
                bulkRequestItemEntity.get().getBulkRequestName(), bulkRequestItemEntity.get().getBulkRequestFileName(),
                bulkRequestItemEntity.get().getBulkRequestStatus(), new String(bulkRequestItemEntity.get().getBulkRequestFileData()),
                "Bulk Request Process Report"));*/
        itemRequestServiceUtil.generateReportAndSendEmail(bulkRequestId);
        assertTrue(true);
    }
    @Test
    public void testsetEddInfoToGfaRequest() {
        TtitemEDDResponse TtitemEDDResponse = new TtitemEDDResponse();
        String line = "testdata:testdat:testdata:testdata:testdata:testdata";
        itemRequestServiceUtil.setEddInfoToGfaRequest(line, TtitemEDDResponse);
        assertTrue(true);
    }
    @Test
    public void testsetEddInfoToScsbRequest() {
        ItemRequestInformation ItemRequestInformation =  new ItemRequestInformation();
        String line = "testdata:testdat:testdata";
        itemRequestServiceUtil.setEddInfoToScsbRequest(line, ItemRequestInformation);
        assertTrue(true);
    }

    @Test
    public void getPatronIdBorrowingInstitutionPUL(){
        String requestingInstitution ="PUL";
        String owningInstitution = "CUL";
        String requestType = "EDD";
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,requestType);
    }
    @Test
    public void getPatronIdBorrowingInstitutionNYPL(){
        String requestingInstitution ="PUL";
        String owningInstitution = "NYPL";
        String requestType = "EDD";
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,requestType);
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
        return itemEntity;
    }
    private BulkRequestItem getBulkRequestItem() {
        BulkRequestItem bulkRequestItem =new BulkRequestItem();
        bulkRequestItem.setStatus("SUCCESS");
        bulkRequestItem.setCustomerCode("PA");
        bulkRequestItem.setItemBarcode("12345");
        bulkRequestItem.setRequestId("1");
        bulkRequestItem.setRequestStatus("PENDING");
        return bulkRequestItem;
    }
}
