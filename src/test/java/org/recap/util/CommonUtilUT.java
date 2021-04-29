package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.*;

public class CommonUtilUT extends BaseTestCaseUT {
    @InjectMocks
    CommonUtil commonUtil;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    private ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    private PropertyUtil propertyUtil;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void buildHoldingsEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        Date currentDate = new Date();
        StringBuilder errorMessage = new StringBuilder();
        String holdingsContent = "PUL";
        HoldingsEntity holdingsEntity = commonUtil.buildHoldingsEntity(bibliographicEntity, currentDate, errorMessage, holdingsContent);
        assertNotNull(holdingsEntity);
    }

    @Test
    public void buildHoldingsEntitywithErrorMessage() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        Date currentDate = new Date();
        StringBuilder errorMessage = new StringBuilder();
        String holdingsContent = null;
        HoldingsEntity holdingsEntity = commonUtil.buildHoldingsEntity(bibliographicEntity, currentDate, errorMessage, holdingsContent);
        assertNotNull(holdingsEntity);
    }

    @Test
    public void addItemAndReportEntities() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        List<ReportEntity> reportEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setId(1);
        boolean processHoldings = false;
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("itemEntity", getBibliographicEntity().getItemEntities().get(0));
        itemMap.put("itemReportEntity", reportEntity);
        commonUtil.addItemAndReportEntities(itemEntities, reportEntities, processHoldings, holdingsEntity, itemMap);
    }

    @Test
    public void addItemAndReportEntitiesWithoutitemReportEntity() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        List<ReportEntity> reportEntities = new ArrayList<>();
        boolean processHoldings = true;
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("itemEntity", getBibliographicEntity().getItemEntities().get(0));
        commonUtil.addItemAndReportEntities(itemEntities, reportEntities, processHoldings, holdingsEntity, itemMap);
    }

    @Test
    public void rollbackUpdateItemAvailabilityStatus() {
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        String userName = "Test";
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.AVAILABLE)).thenReturn(getItemStatusEntity());
        commonUtil.rollbackUpdateItemAvailabilityStatus(itemEntity, userName);
    }

    @Test
    public void getFTPPropertiesMap() {
        commonUtil.getFTPPropertiesMap();
    }

    @Test
    public void getItemStatusMap() {
        List<ItemStatusEntity> itemStatusEntities = new ArrayList<>();
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        itemStatusEntities.add(itemStatusEntity);
        Mockito.when(itemStatusDetailsRepository.findAll()).thenReturn(itemStatusEntities);
        commonUtil.getItemStatusMap();
        commonUtil.getCollectionGroupMap();
        commonUtil.getInstitutionEntityMap();
        CommonUtil commonUtil = new CommonUtil();
        commonUtil.getItemStatusMap();
    }

    @Test
    public void getInstitutionEntityMap() {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(Arrays.asList(institutionEntity));
        Map map = commonUtil.getInstitutionEntityMap();
        assertNotNull(map);
    }

    @Test
    public void findAllInstitutionCodesExceptHTC() {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList(institutionEntity.getInstitutionName()));
        List<String> result = commonUtil.findAllInstitutionCodesExceptHTC();
        assertNotNull(result);
    }

    @Test
    public void findAllImsLocationCodeExceptUN() {
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(imsLocationDetailsRepository.findAllImsLocationCodeExceptUN()).thenReturn(Arrays.asList(institutionEntity.getInstitutionName()));
        List<String> result = commonUtil.findAllImsLocationCodeExceptUN();
        assertNotNull(result);
    }

    @Test
    public void isImsItemStatusAvailable() {
        String imsLocationCode = "HD";
        String imsItemStatus = "RECAP";
        String imsAvailableCodes = "HD,HTC,UN,RECAP";
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.available.item.status.codes")).thenReturn(imsAvailableCodes);
        boolean result = commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(imsLocationCode, imsItemStatus, true);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void isImsItemStatusNotAvailable() {
        String imsLocationCode = "HD";
        String imsItemStatus = "RECAP";
        String imsNotAvailableCodes = "HD,HTC,UN,RECAP";
        Mockito.when(propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.not.available.item.status.codes")).thenReturn(imsNotAvailableCodes);
        boolean result = commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(imsLocationCode, imsItemStatus, false);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void getInstitutionEntityMapException() {
        Mockito.when(institutionDetailsRepository.findAll()).thenThrow(new NullPointerException());
        Map map = commonUtil.getInstitutionEntityMap();
        assertNotNull(map);
    }

    @Test
    public void getCollectionGroupMap() {
        CollectionGroupEntity collectionGroupEntity = getCollectionGroupEntity();
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(Arrays.asList(collectionGroupEntity));
        Map map = commonUtil.getCollectionGroupMap();
        assertNotNull(map);
    }

    @Test
    public void getCollectionGroupMapException() {
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenThrow(new NullPointerException());
        Map map = commonUtil.getCollectionGroupMap();
        assertNotNull(map);
    }

    @Test
    public void getImsLocationCodeByItemBarcode() {
        String itemBarcode = "32959";
        Mockito.when(itemDetailsRepository.findByBarcode(itemBarcode)).thenReturn(getBibliographicEntity().getItemEntities());
        String imsLocationCode = commonUtil.getImsLocationCodeByItemBarcode(itemBarcode);
        assertNotNull(imsLocationCode);
    }

    /*@Test
    public void getExistingItemEntityOwningInstItemId() {
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        ItemEntity incomingItemEntity = getBibliographicEntity().getItemEntities().get(0);
        incomingItemEntity.setOwningInstitutionItemId("55555");
        ReflectionTestUtils.invokeMethod(commonUtil, "getExistingItemEntityOwningInstItemId", fetchedBibliographicEntity, incomingItemEntity);
    }*/

    @Test
    public void getBarcodesList() {
        List<ItemEntity> itemEntities = getBibliographicEntity().getItemEntities();
        List<String> itemBarcodes = commonUtil.getBarcodesList(itemEntities);
        assertNotNull(itemBarcodes);
    }
    @Test
    public void getItemRequestInformation(){
        ItemEntity itemEntity = getBibliographicEntity().getItemEntities().get(0);
        ItemRequestInformation itemRequestInformation = commonUtil.getItemRequestInformation(itemEntity);
        assertNotNull(itemRequestInformation);
    }

    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCreatedDate(new Date());
        collectionGroupEntity.setCollectionGroupCode("others");
        collectionGroupEntity.setCollectionGroupDescription("others");
        collectionGroupEntity.setLastUpdatedDate(new Date());
        return collectionGroupEntity;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        return institutionEntity;
    }

    @Test
    public void getUser() {
        String user1 = commonUtil.getUser("1");
        String user2 = commonUtil.getUser("");
        assertNotNull(user1);
        assertEquals("1", user1);
        assertNotNull(user2);
        assertEquals("Discovery", user2);
    }

    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("SUCCESS");
        itemStatusEntity.setStatusDescription("AVAILABLE");
        return itemStatusEntity;
    }

    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("12345");
        holdingsEntity.setDeleted(false);
        return holdingsEntity;
    }

    private BibliographicEntity getBibliographicEntity() {

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(123456);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1577261074");
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
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
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);
        itemEntity.setImsLocationEntity(getImsLocationEntity());
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

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
