package org.recap.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.util.CommonUtil;
import org.recap.util.SecurityUtil;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by hemalathas on 21/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRequestDBServiceUT{

    @InjectMocks
    ItemRequestDBService itemRequestDBService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

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
        int requestId = itemRequestDBService.updateRecapRequestItem(itemRequestInformation,itemEntity,requestStatusCode,bulkRequestItemEntity);
        assertNotNull(requestId);
    }
    @Test
    public void updateRecapRequestItemException(){
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        String requestStatusCode = "REFILED" ;
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity() ;
//        Mockito.when(requestItemDetailsRepository.findById(itemRequestInformation.getRequestId())).thenReturn(Optional.of(requestItemEntity));
//        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        itemRequestDBService.updateRecapRequestItem(itemRequestInformation,itemEntity,requestStatusCode,bulkRequestItemEntity);
    }
    @Test
    public void updateRecapRequestItemWithoutRequestId(){
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        ItemEntity itemEntity = getItemEntity();
        String requestStatusCode = "REFILED" ;
        RequestStatusEntity requestStatusEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestStatusEntity();
        RequestTypeEntity requestTypeEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0).getRequestTypeEntity();
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity() ;
        Mockito.when(commonUtil.getUser(itemRequestInformation.getUsername())).thenReturn("userName");
        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(requestStatusCode)).thenReturn(requestStatusEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(itemEntity.getInstitutionEntity());
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemRequestInformation.getRequestType())).thenReturn(requestTypeEntity);
//        Mockito.when(requestItemDetailsRepository.findById(itemRequestInformation.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        itemRequestDBService.updateRecapRequestItem(itemRequestInformation,itemEntity,requestStatusCode,bulkRequestItemEntity);
    }
    @Test
    public void updateRecapRequestItemWithFailureItemResponse(){
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
//        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
//        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_EXCEPTION)).thenReturn(requestStatusEntity);
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
//        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
//        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_EXCEPTION)).thenReturn(requestStatusEntity);
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
//        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
//        Mockito.when(requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_EXCEPTION)).thenReturn(requestStatusEntity);
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(itemInformationResponse.getRequestType())).thenReturn(requestTypeEntity);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(itemInformationResponse.getRequestingInstitution())).thenReturn(getBulkRequestItemEntity().getInstitutionEntity());
        Mockito.when(securityUtil.getEncryptedValue(itemInformationResponse.getEmailAddress())).thenReturn("test@gmail.com");
        Mockito.when(requestItemDetailsRepository.saveAndFlush(requestItemEntity)).thenReturn(requestItemEntity);
        ItemInformationResponse itemInformationResponse1 = itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(itemInformationResponse1);
    }
    @Test
    public void updateRecapRequestStatusForRetrieval(){
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
    public void updateItemAvailabilityStatus(){
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(getItemEntity());
        String userName = "userName";
        ItemStatusEntity itemStatusEntity = getItemEntity().getItemStatusEntity();
        Mockito.when(commonUtil.getUser(userName)).thenReturn("userName");
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        Mockito.doNothing().when(commonUtil).saveItemChangeLogEntity(itemEntities.get(0).getId(),userName, RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_UPDATE, RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_UPDATE);
        itemRequestDBService.updateItemAvailabilityStatus(itemEntities,userName);
    }
    @Test
    public void rollbackAfterGFA(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        RequestItemEntity requestItemEntity = getBulkRequestItemEntity().getRequestItemEntities().get(0);
        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setOwningInstitutionId(1);
        customerCodeEntity.setInstitutionEntity(getBulkRequestItemEntity().getInstitutionEntity());
        customerCodeEntity.setCustomerCode("PA");
        customerCodeEntity.setId(1);
        customerCodeEntity.setDescription("test");
        Mockito.when(requestItemDetailsRepository.findById(itemInformationResponse.getRequestId())).thenReturn(Optional.of(requestItemEntity));
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(requestItemEntity.getItemEntity().getCustomerCode())).thenReturn(customerCodeEntity);
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
        //requestItemEntity.setR
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
    /*@Test
    public void testUpdateRecapRequestItem() throws Exception {

        String requestStatusCode = "REFILED";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setExpirationDate("2017-02-21");
        itemRequestInformation.setRequestingInstitution("NYPL");
        itemRequestInformation.setUsername("john");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setPatronBarcode("426598712");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setRequestNotes("test");

        Integer response = itemRequestDBService.updateRecapRequestItem(itemRequestInformation,saveBibSingleHoldingsSingleItem().getItemEntities().get(0),requestStatusCode, null);
        assertNotNull(response);


    }*/

    public RequestTypeEntity createRequestType(){
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");
       // RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        return requestTypeEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(entity.getId());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("6027");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        //BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        //entityManager.refresh(savedBibliographicEntity);
        return bibliographicEntity;

    }

}
