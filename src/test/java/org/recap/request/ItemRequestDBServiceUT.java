package org.recap.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.OwnerCodeEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.recap.util.CommonUtil;
import org.recap.util.SecurityUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by hemalathas on 21/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRequestDBServiceUT{

    @InjectMocks
    @Spy
    ItemRequestDBService itemRequestDBService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Mock
    SecurityUtil securityUtil;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailRepository;

    @Mock
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;
    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;
    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;
    @Mock
    CommonUtil commonUtil;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void updateRecapRequestItem(){
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setRequestId(1);
        ItemEntity itemEntity = getItemEntity();
        String requestStatusCode = "REFILED" ;
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity() ;
        Mockito.when(commonUtil.getUser(itemRequestInformation.getUsername())).thenReturn("userName");
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(requestStatusCode)).thenReturn(requestStatusEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(itemEntity.getInstitutionEntity());
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemRequestInformation.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(requestItemDetailsRepository.findById(itemRequestInformation.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        int requestId = itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, requestStatusCode, bulkRequestItemEntity);
        assertNotNull(requestId);
    }

    @Test
    public void updateRecapRequestItemException() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        String requestStatusCode = "REFILED";
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, requestStatusCode, bulkRequestItemEntity);
    }

    @Test
    public void updateRecapRequestItemParseException() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setExpirationDate("test");
        ItemEntity itemEntity = getItemEntity();
        String requestStatusCode = "REFILED";
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(itemEntity.getInstitutionEntity());
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemRequestInformation.getRequestType())).thenReturn(requestTypeEntity);
        //Mockito.doThrow(new ParseException("Unparseable date:",1)).when(requestItemDetailsRepository).saveAndFlush(any());
        itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, requestStatusCode, bulkRequestItemEntity);
    }

    @Test
    public void updateRecapRequestItemWithoutRequestId() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        String requestStatusCode = "REFILED";
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Mockito.when(commonUtil.getUser(itemRequestInformation.getUsername())).thenReturn("userName");
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(requestStatusCode)).thenReturn(requestStatusEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(itemEntity.getInstitutionEntity());
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemRequestInformation.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, requestStatusCode, bulkRequestItemEntity);
    }

    @Test
    public void updateRecapRequestItemWithoutRequestIdWithEmailId() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setEmailAddress("test@gmail.com");
        ItemEntity itemEntity = getItemEntity();
        String requestStatusCode = "REFILED";
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Mockito.when(commonUtil.getUser(itemRequestInformation.getUsername())).thenReturn("userName");
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(requestStatusCode)).thenReturn(requestStatusEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(itemEntity.getInstitutionEntity());
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemRequestInformation.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(securityUtil.getEncryptedValue(any())).thenReturn("test@gmail.com");
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, requestStatusCode, null);
    }

    @Test
    public void updateRecapRequestItemWithFailureItemResponse() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setSuccess(false);
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_EXCEPTION)).thenReturn(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateRecapRequestItemWithSuccessItemResponseForRecall(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestType("RECALL");
        itemInformationResponse.setEmailAddress("test@gmail.com");
        itemInformationResponse.setRequestId(null);
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestStatusEntity);
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemInformationResponse.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemInformationResponse.getRequestingInstitution())).thenReturn(getBulkRequestItemEntity().getInstitutionEntity());
        Mockito.when(securityUtil.getEncryptedValue(itemInformationResponse.getEmailAddress())).thenReturn("test@gmail.com");
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateRecapRequestItemWithSuccessItemResponseForEDD(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestType("EDD");
        itemInformationResponse.setEmailAddress("test@gmail.com");
        itemInformationResponse.setRequestId(null);
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_EDD)).thenReturn(requestStatusEntity);
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemInformationResponse.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemInformationResponse.getRequestingInstitution())).thenReturn(getBulkRequestItemEntity().getInstitutionEntity());
        Mockito.when(securityUtil.getEncryptedValue(itemInformationResponse.getEmailAddress())).thenReturn("test@gmail.com");
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateRecapRequestItemWithSuccessItemResponseForRetrieval(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestType("RETRIEVAL");
        itemInformationResponse.setEmailAddress("test@gmail.com");
        itemInformationResponse.setRequestId(null);
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)).thenReturn(requestStatusEntity);
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemInformationResponse.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemInformationResponse.getRequestingInstitution())).thenReturn(getBulkRequestItemEntity().getInstitutionEntity());
        Mockito.when(securityUtil.getEncryptedValue(itemInformationResponse.getEmailAddress())).thenReturn("test@gmail.com");
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }

    @Test
    public void updateRecapRequestItemWithoutEmailId() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestType("RETRIEVAL");
        itemInformationResponse.setEmailAddress("");
        itemInformationResponse.setRequestId(null);
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)).thenReturn(requestStatusEntity);
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemInformationResponse.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemInformationResponse.getRequestingInstitution())).thenReturn(getBulkRequestItemEntity().getInstitutionEntity());
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }

    @Test
    public void updateRecapRequestException() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestType("RETRIEVAL");
        itemInformationResponse.setEmailAddress("");
        itemInformationResponse.setRequestId(null);
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }

    @Test
    public void updateRecapRequestStatusForRetrieval() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RETRIEVAL");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)).thenReturn(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestStatus(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateRecapRequestStatusForRecall(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RECALL");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)).thenReturn(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestStatus(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateRecapRequestStatusForEDD(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_EDD)).thenReturn(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestStatus(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateRecapRequestStatus(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setSuccess(false);
        itemInformationResponse.setBulk(true);
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        requestItemEntity.setBulkRequestItemEntity(null);
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_EXCEPTION)).thenReturn(requestStatusEntity);
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestStatus(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }

    @Test
    public void updateRecapRequestStatusIsBulk(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setSuccess(false);
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestStatus(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateItemAvailabilityStatus() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(getItemEntity());
        String userName = "userName";
        ItemStatusEntity itemStatusEntity = getItemEntity().getItemStatusEntity();
        Mockito.when(commonUtil.getUser(userName)).thenReturn("userName");
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.doNothing().when(commonUtil).saveItemChangeLogEntity(itemEntities.get(0).getId(), userName, RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_UPDATE, RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_UPDATE);
        itemRequestDBService.updateItemAvailabilityStatus(itemEntities, userName);
    }

    @Test
    public void getExpirationDate() {
        ReflectionTestUtils.invokeMethod(itemRequestDBService, "getExpirationDate", "test");
    }

    @Test
    public void rollbackAfterGFA() {
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setInstitutionId(1);
        ownerCodeEntity.setInstitutionEntity(getBulkRequestItemEntity().getInstitutionEntity());
        ownerCodeEntity.setOwnerCode("PA");
        ownerCodeEntity.setId(1);
        ownerCodeEntity.setDescription("test");
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCode(requestItemEntity.getItemEntity().getCustomerCode())).thenReturn(ownerCodeEntity);
        ItemRequestInformation itemRequestInformation = itemRequestDBService.rollbackAfterGFA(itemInformationResponse);
        assertNotNull(itemRequestInformation);
    }
    private ItemInformationResponse getItemInformationResponse() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setBibID("1");
        itemInformationResponse.setRequestId(1);
        itemInformationResponse.setBulk(false);
        itemInformationResponse.setScreenMessage("SUCCESS");
        itemInformationResponse.setSuccess(true);
        return itemInformationResponse;
    }

    private RequestItemEntity getRequestItemEntity(){
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(1);
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setRequestTypeId(1);
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setCreatedBy("userName");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setPatronId("12345");
        requestItemEntity.setStopCode("PA");
        requestItemEntity.setRequestStatusId(1);
        requestItemEntity.setBulkRequestItemEntity(getBulkRequestItemEntity());
        return requestItemEntity;
    }
    private BulkRequestItemEntity getBulkRequestItemEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setId(1);
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        ItemEntity itemEntity = getItemEntity();
        RequestStatusEntity requestStatusEntity =  new RequestStatusEntity();
        requestStatusEntity.setId(1);
        requestStatusEntity.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL ORDER PLACED");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setId(1);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setItemEntity(itemEntity);
        requestItemEntity.setInstitutionEntity(institutionEntity);
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
        requestItemEntity.setBulkRequestItemEntity(bulkRequestItemEntity);
        return bulkRequestItemEntity;
    }
    private  ItemRequestInformation getItemRequestInformation(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setExpirationDate("09-21-2012 11:20:12");
        itemRequestInformation.setRequestingInstitution("NYPL");
        itemRequestInformation.setUsername("john");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setPatronBarcode("426598712");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setRequestNotes("test");

        return itemRequestInformation;
    }
    private ItemEntity getItemEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("843617540");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setCatalogingStatus("Incomplete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setUseRestrictions("restrictions");
        itemEntity.setDeleted(false);
        itemEntity.setInstitutionEntity(institutionEntity);

        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("PA");
        itemStatusEntity.setStatusDescription("AVAILABLE");

        itemEntity.setItemStatusEntity(itemStatusEntity);

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setOwningInstitutionBibId("23456");

        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        return itemEntity;
    }

}
