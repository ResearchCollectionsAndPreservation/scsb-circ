package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.las.model.TtitemEDDResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BulkRequestItemDetailsRepository;
import org.recap.repository.jpa.GenericPatronDetailsRepository;
import org.recap.request.EmailService;
import org.recap.service.RestHeaderService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ItemRequestServiceUtilUT {
    @InjectMocks
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    private GenericPatronDetailsRepository genericPatronDetailsRepository;

    @Mock
    private RestHeaderService restHeaderService;

    @Mock
    private EmailService emailService;

    @Mock
    private BulkRequestItemDetailsRepository bulkRequestItemDetailsRepository;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(itemRequestServiceUtil, "scsbSolrClientUrl", "http://localhost:9090/");
    }
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

    }
    @Test
    public void testsetEddInfoToGfaRequest() {
        TtitemEDDResponse TtitemEDDResponse = new TtitemEDDResponse();
        String line = "Start Page:testdat:testdata:testdata:testdata:testdata";
        String line1 = "End Page:testdat:testdata:testdata:testdata:testdata";
        String line2 = "Volume Number:testdat:testdata:testdata:testdata:testdata";
        String line3 = "Issue:testdat:testdata:testdata:testdata:testdata";
        String line4 = "Article Author:testdat:testdata:testdata:testdata:testdata";
        String line5 = "Article/Chapter Title:testdat:testdata:testdata:testdata:testdata";
        itemRequestServiceUtil.setEddInfoToGfaRequest(line, TtitemEDDResponse);
        itemRequestServiceUtil.setEddInfoToGfaRequest(line1, TtitemEDDResponse);
        itemRequestServiceUtil.setEddInfoToGfaRequest(line2, TtitemEDDResponse);
        itemRequestServiceUtil.setEddInfoToGfaRequest(line3, TtitemEDDResponse);
        itemRequestServiceUtil.setEddInfoToGfaRequest(line4, TtitemEDDResponse);
        itemRequestServiceUtil.setEddInfoToGfaRequest(line5, TtitemEDDResponse);

    } @Test
    public void testSetEddInfoToScsbRequest() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        String line = "Start Page:testdat:testdata:testdata:testdata:testdata";
        String line1 = "End Page:testdat:testdata:testdata:testdata:testdata";
        String line2 = "Volume Number:testdat:testdata:testdata:testdata:testdata";
        String line3 = "Issue:testdat:testdata:testdata:testdata:testdata";
        String line4 = "Article Author:testdat:testdata:testdata:testdata:testdata";
        String line5 = "Article/Chapter Title:testdat:testdata:testdata:testdata:testdata";
        String line6 = "User:testdat:testdata:testdata:testdata:testdata";
        itemRequestServiceUtil.setEddInfoToScsbRequest(line, itemRequestInformation);
        itemRequestServiceUtil.setEddInfoToScsbRequest(line1, itemRequestInformation);
        itemRequestServiceUtil.setEddInfoToScsbRequest(line2, itemRequestInformation);
        itemRequestServiceUtil.setEddInfoToScsbRequest(line3, itemRequestInformation);
        itemRequestServiceUtil.setEddInfoToScsbRequest(line4, itemRequestInformation);
        itemRequestServiceUtil.setEddInfoToScsbRequest(line5, itemRequestInformation);
        itemRequestServiceUtil.setEddInfoToScsbRequest(line6, itemRequestInformation);

    }
    @Test
    public void getPatronIdBorrowingInstitutionPUL(){
        String requestingInstitution ="CUL";
        String owningInstitution = "PUL";
        String requestType = "EDD";
        GenericPatronEntity genericPatronEntity = getgenericPatronDetails();
        Mockito.when(genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(any(), any())).thenReturn(genericPatronEntity);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,requestType);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution("NYPL",owningInstitution,requestType);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,"RECALL");
        itemRequestServiceUtil.getPatronIdBorrowingInstitution("NYPL",owningInstitution,"RECALL");
    }

    @Test
    public void getPatronIdBorrowingInstitutionCUL(){
        String requestingInstitution ="PUL";
        String owningInstitution = "CUL";
        String requestType = "EDD";
        GenericPatronEntity genericPatronEntity = getgenericPatronDetails();
        Mockito.when(genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(any(), any())).thenReturn(genericPatronEntity);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,requestType);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution("NYPL",owningInstitution,requestType);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,"RECALL");
        itemRequestServiceUtil.getPatronIdBorrowingInstitution("NYPL",owningInstitution,"RECALL");
    }

    private GenericPatronEntity getgenericPatronDetails() {
        GenericPatronEntity genericPatronEntity = new GenericPatronEntity();
        genericPatronEntity.setGenericPatronId(1);
        genericPatronEntity.setEddGenericPatron("EDDPatron");
        genericPatronEntity.setRetrievalGenericPatron("RetrievalPatron");
        return genericPatronEntity;
    }

    @Test
    public void getPatronIdBorrowingInstitutionNYPL(){
        String requestingInstitution ="PUL";
        String owningInstitution = "NYPL";
        String requestType = "EDD";
        GenericPatronEntity genericPatronEntity = getgenericPatronDetails();
        Mockito.when(genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(any(), any())).thenReturn(genericPatronEntity);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,requestType);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution("CUL",owningInstitution,requestType);
        itemRequestServiceUtil.getPatronIdBorrowingInstitution(requestingInstitution,owningInstitution,"RECALL");
        itemRequestServiceUtil.getPatronIdBorrowingInstitution("CUL",owningInstitution,"RECALL");
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
