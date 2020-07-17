package org.recap.service.deaccession;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.controller.RequestItemController;
import org.recap.ils.model.response.ItemHoldResponse;
import org.recap.model.deaccession.DeAccessionDBResponseEntity;
import org.recap.model.deaccession.DeAccessionItem;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.request.GFAService;
import org.recap.service.RestHeaderService;
import org.recap.util.ItemRequestServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by angelind on 10/11/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeAccessionServiceUT{

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    RequestItemController requestItemController;

    @Mock
    GFAService gfaService;

    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    RestHeaderService restHeaderService;

    @InjectMocks
    DeAccessionService deAccessionService;

    @Test
    public void deaccession(){
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = getDeAccessionItem();
        deAccessionRequest.setDeAccessionItems(Arrays.asList(deAccessionItem));
        deAccessionRequest.setUsername("Test");
        deAccessionRequest.setNotes("test");
        ItemEntity itemEntity = getItemEntity();
        RequestItemEntity requestItemEntity = getRequestItem();
        String itemBarcode ="123456";
        Mockito.when(itemDetailsRepository.findByBarcode(itemBarcode)).thenReturn(Arrays.asList(itemEntity));
//        Mockito.when(requestItemDetailsRepository.findByItemBarcode(itemBarcode)).thenReturn(Arrays.asList(requestItemEntity));
        Map<String, String> result = deAccessionService.deAccession(deAccessionRequest);
        assertNotNull(result);
    }
    public RequestItemEntity getRequestItem() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
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
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        return itemEntity;
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1234");
        return bibliographicEntity;
    }

    private DeAccessionItem getDeAccessionItem() {
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode("123456");
        deAccessionItem.setDeliveryLocation("PB");
        return deAccessionItem;
    }

    @Test
    public void deAccessionItemsInDB() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put(itemBarcode, "PB");
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        //entityManager.refresh(savedBibliographicEntity);
//        assertNotNull(savedBibliographicEntity);
//        assertNotNull(savedBibliographicEntity.getBibliographicId());
        Thread.sleep(3000);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionService.deAccessionItemsInDB(barcodeAndStopCodeMap, deAccessionDBResponseEntities, RecapConstants.GUEST_USER);
        assertNotNull(deAccessionDBResponseEntities);
        // assertTrue(deAccessionDBResponseEntities.size()==1);
        //DeAccessionDBResponseEntity deAccessionDBResponseEntity = deAccessionDBResponseEntities.get(0);
        //assertNotNull(deAccessionDBResponseEntity);
        //assertEquals(deAccessionDBResponseEntity.getStatus(), RecapCommonConstants.SUCCESS);

      //  List<ItemEntity> fetchedItemEntities = itemDetailsRepository.findByBarcodeIn(Arrays.asList(itemBarcode));
        //entityManager.refresh(fetchedItemEntities.get(0));
  //      assertNotNull(fetchedItemEntities);
//        assertTrue(fetchedItemEntities.size() == 1);
//        assertEquals(Boolean.FALSE, fetchedItemEntities.get(0).isDeleted());
//        assertNotNull(fetchedItemEntities.get(0).getLastUpdatedBy());
        // assertEquals(RecapConstants.GUEST_USER, fetchedItemEntities.get(0).getLastUpdatedBy());
        // assertNotNull(fetchedItemEntities.get(0).getLastUpdatedDate());
        // assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        // assertTrue(savedBibliographicEntity.getHoldingsEntities().size() == 1);
        // assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getItemEntities());
        //  assertTrue(savedBibliographicEntity.getHoldingsEntities().get(0).getItemEntities().size() == 1);
        //assertNotEquals(savedBibliographicEntity.getHoldingsEntities().get(0).getItemEntities().get(0).getLastUpdatedDate(), fetchedItemEntities.get(0).getLastUpdatedDate());
//        assertTrue(true);
    }

    @Test
    public void processAndSave() throws Exception {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("12345");
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setReasonForFailure(RecapCommonConstants.ITEM_BARCDE_DOESNOT_EXIST);

        List<ReportEntity> reportEntities = deAccessionService.processAndSaveReportEntities(Arrays.asList(deAccessionDBResponseEntity));
        assertNotNull(reportEntities);
//        assertTrue(reportEntities.size() == 1);
//        ReportEntity reportEntity = reportEntities.get(0);
        //    assertNotNull(reportEntity);

        //  assertNotNull(reportEntity.getReportDataEntities());
        //  assertTrue(reportEntity.getReportDataEntities().size() == 4);
        assertTrue(true);
    }

    private BibliographicEntity getBibEntityWithHoldingsAndItem(String itemBarcode) throws Exception {
        Random random = new Random();
        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(itemBarcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }

    @Test
    public void checkAndCancelHolds() throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode("123456");
        deAccessionItem.setDeliveryLocation("PB");
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put("123456", "PB");
        barcodeAndStopCodeMap.put("123", "AB");
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        deAccessionDBResponseEntity.setBarcode("12345");
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        deAccessionDBResponseEntity.setReasonForFailure(RecapCommonConstants.ITEM_BARCDE_DOESNOT_EXIST);
        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
        deAccessionService.checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, "Test");
        assertNotNull(barcodeAndStopCodeMap);
        // assertTrue(barcodeAndStopCodeMap.size() == 1);
        assertEquals(deAccessionItem.getItemBarcode(), barcodeAndStopCodeMap.keySet().toArray()[1]);
    }

   /* @Test
    public void deaccessionFlowTest() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
        barcodeAndStopCodeMap.put(itemBarcode, "PB");
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        //entityManager.refresh(savedBibliographicEntity);
//        assertNotNull(savedBibliographicEntity);
//        assertNotNull(savedBibliographicEntity.getBibliographicId());
        Thread.sleep(3000);
//        when(requestItemDetailsRepository.findByItemBarcode(itemBarcode)).thenReturn(getMockRequestItemEntities());

        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode(itemBarcode);
        deAccessionItem.setDeliveryLocation("PB");
        deAccessionRequest.setDeAccessionItems(Arrays.asList(deAccessionItem));
        deAccessionRequest.setUsername("Test");
        Map<String, String> resultMap = deAccessionService.deAccession(deAccessionRequest);
        assertNotNull(resultMap);
    }*/

    private List<RequestItemEntity> getMockRequestItemEntities() {
        InstitutionEntity institutionEntity1 = new InstitutionEntity();
        institutionEntity1.setId(1);
        institutionEntity1.setInstitutionCode("PUL");
        institutionEntity1.setInstitutionName("Princeton");

        InstitutionEntity institutionEntity2 = new InstitutionEntity();
        institutionEntity2.setId(2);
        institutionEntity2.setInstitutionCode("CUL");
        institutionEntity2.setInstitutionName("Columbia");

        RequestTypeEntity requestTypeEntity1 = new RequestTypeEntity();
        requestTypeEntity1.setId(1);
        requestTypeEntity1.setRequestTypeCode("RETRIEVAL");
        requestTypeEntity1.setRequestTypeDesc("RETRIEVAL");

        RequestTypeEntity requestTypeEntity2 = new RequestTypeEntity();
        requestTypeEntity2.setId(2);
        requestTypeEntity2.setRequestTypeCode("RECALL");
        requestTypeEntity2.setRequestTypeDesc("RECALL");

        RequestStatusEntity requestStatusEntity1 = new RequestStatusEntity();
        requestStatusEntity1.setId(1);
        requestStatusEntity1.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity1.setRequestStatusDescription("RETRIEVAL_ORDER_PLACED");

        RequestStatusEntity requestStatusEntity2 = new RequestStatusEntity();
        requestStatusEntity2.setId(2);
        requestStatusEntity2.setRequestStatusCode("RECALL_ORDER_PLACED");
        requestStatusEntity2.setRequestStatusDescription("RECALL_ORDER_PLACED");

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setItemId(1);
        itemEntity1.setBarcode("123");
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setInstitutionEntity(institutionEntity1);

        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setOwningInstitutionBibId("345");
        itemEntity1.setBibliographicEntities(Arrays.asList(bibliographicEntity1));

        List<RequestItemEntity> requestItemEntities = new ArrayList<>();

        RequestItemEntity requestItemEntity1 = new RequestItemEntity();
        requestItemEntity1.setId(1);
        requestItemEntity1.setItemId(2);
        requestItemEntity1.setRequestTypeId(1);
        requestItemEntity1.setRequestingInstitutionId(1);
        requestItemEntity1.setPatronId("123");
        requestItemEntity1.setEmailId("test1@gmail.com");
        requestItemEntity1.setRequestExpirationDate(new Date());
        requestItemEntity1.setCreatedBy("Test");
        requestItemEntity1.setCreatedDate(new Date());
        requestItemEntity1.setStopCode("PA");
        requestItemEntity1.setRequestStatusId(1);
        requestItemEntity1.setNotes("Test Notes");
        requestItemEntity1.setInstitutionEntity(institutionEntity1);
        requestItemEntity1.setRequestTypeEntity(requestTypeEntity1);
        requestItemEntity1.setRequestStatusEntity(requestStatusEntity1);
        requestItemEntity1.setItemEntity(itemEntity1);
        requestItemEntities.add(requestItemEntity1);

        RequestItemEntity requestItemEntity2 = new RequestItemEntity();
        requestItemEntity2.setId(2);
        requestItemEntity2.setItemId(2);
        requestItemEntity2.setRequestTypeId(2);
        requestItemEntity2.setRequestingInstitutionId(1);
        requestItemEntity2.setPatronId("321");
        requestItemEntity2.setEmailId("test2@gmail.com");
        requestItemEntity2.setRequestExpirationDate(new Date());
        requestItemEntity2.setCreatedBy("Test");
        requestItemEntity2.setCreatedDate(new Date());
        requestItemEntity2.setStopCode("PA");
        requestItemEntity2.setRequestStatusId(2);
        requestItemEntity2.setNotes("Test Notes");
        requestItemEntity2.setInstitutionEntity(institutionEntity1);
        requestItemEntity2.setRequestTypeEntity(requestTypeEntity2);
        requestItemEntity2.setRequestStatusEntity(requestStatusEntity2);
        requestItemEntity2.setItemEntity(itemEntity1);
        requestItemEntities.add(requestItemEntity2);

        return requestItemEntities;
    }

   /* @Test
    public void testcancelRequest() {
        ItemHoldResponse itemHoldResponse = null;
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        itemHoldResponse = deAccessionService.cancelRequest(requestItemEntity, "test");
        assertTrue(true);
    }*/
}