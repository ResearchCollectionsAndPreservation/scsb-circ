package org.recap.service.submitcollection;


import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.model.submitcollection.BoundWithBibliographicEntityObject;
import org.recap.model.submitcollection.NonBoundWithBibliographicEntityObject;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.service.common.RepositoryService;
import org.recap.service.common.SetupDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionDAOServiceUT {

    @InjectMocks
    SubmitCollectionDAOService submitCollectionDAOService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SetupDataService setupDataService;

    @Mock
    private SubmitCollectionReportHelperService submitCollectionReportHelperService;

    @Mock
    private SubmitCollectionValidationService submitCollectionValidationService;

    @Mock
    private SubmitCollectionHelperService submitCollectionHelperService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(submitCollectionDAOService, "nonHoldingIdInstitution", "NYPL");
    }


    @Test
    public void updateBibliographicEntityInBatchForNonBoundWith() throws Exception{
        List<NonBoundWithBibliographicEntityObject> nonBoundWithBibliographicEntityObjectList = new ArrayList<>();
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = getNonBoundWithBibliographicEntityObject();
        nonBoundWithBibliographicEntityObjectList.add(nonBoundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        Mockito.when(submitCollectionValidationService.isExistingBoundWithItem(itemEntity.get(0))).thenReturn(false);
        List<BibliographicEntity> bibliographicEntities =submitCollectionDAOService.updateBibliographicEntityInBatchForNonBoundWith(nonBoundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }
    @Test
    public void updateBibliographicEntityInBatchForBoundWith(){
        List<BoundWithBibliographicEntityObject> boundWithBibliographicEntityObjectList = new ArrayList<>();
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = getBoundWithBibliographicEntityObject();
        boundWithBibliographicEntityObjectList.add(boundWithBibliographicEntityObject);
        Integer owningInstitutionId = 1;
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportInfoMap();
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        Set<String> processedBarcodeSetForDummyRecords = new HashSet<>();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("123456");
        List<ItemEntity> itemEntity = getBibliographicEntity().getItemEntities();
        Map<String,BibliographicEntity> bibliographicEntityMap = new HashMap<>();
        bibliographicEntityMap.put("1",getBibliographicEntity());
        Map<String, ItemEntity> fetchedBarcodeItemEntityMap = new HashMap<>();
        fetchedBarcodeItemEntityMap.put("123456",getBibliographicEntity().getItemEntities().get(0));
        List<BibliographicEntity> fetchedBibliographicEntityList = new ArrayList<>();
        fetchedBibliographicEntityList.add(getBibliographicEntity());
        //Mockito.when(submitCollectionValidationService.validateIncomingItemHavingBibCountIsSameAsExistingItem(submitCollectionReportInfoMap,fetchedBarcodeItemEntityMap, boundWithBibliographicEntityObject.getBibliographicEntityList())).thenReturn(true);
//        Mockito.when(submitCollectionValidationService.getOwnInstBibIdBibliographicEntityMap(fetchedBibliographicEntityList)).thenReturn(bibliographicEntityMap);
        Mockito.when(repositoryService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(repositoryService.getItemDetailsRepository().findByBarcodeInAndOwningInstitutionId(itemBarcodeList,owningInstitutionId)).thenReturn(itemEntity);
        List<BibliographicEntity> bibliographicEntities = submitCollectionDAOService.updateBibliographicEntityInBatchForBoundWith(boundWithBibliographicEntityObjectList,owningInstitutionId,submitCollectionReportInfoMap,processedBibIds,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,processedBarcodeSetForDummyRecords);
        assertNotNull(bibliographicEntities);
    }
    private BoundWithBibliographicEntityObject getBoundWithBibliographicEntityObject(){
        BoundWithBibliographicEntityObject boundWithBibliographicEntityObject = new BoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        boundWithBibliographicEntityObject.setBarcode("123456");
        boundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        return boundWithBibliographicEntityObject;
    }
    private NonBoundWithBibliographicEntityObject getNonBoundWithBibliographicEntityObject(){
        NonBoundWithBibliographicEntityObject nonBoundWithBibliographicEntityObject = new NonBoundWithBibliographicEntityObject();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        nonBoundWithBibliographicEntityObject.setBibliographicEntityList(Arrays.asList(bibliographicEntity));
        nonBoundWithBibliographicEntityObject.setOwningInstitutionBibId("1577261074");
        return nonBoundWithBibliographicEntityObject;
    }
    private Map<String, List< SubmitCollectionReportInfo >> getSubmitCollectionReportInfoMap(){
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setMessage("SUCCESS");
        submitCollectionReportInfo.setCustomerCode("PA");
        submitCollectionReportInfo.setItemBarcode("123456");
        submitCollectionReportInfo.setOwningInstitution("PUL");
        submitCollectionReportInfoMap.put("1",Arrays.asList(submitCollectionReportInfo));
        return submitCollectionReportInfoMap;
    }
    private BibliographicEntity getBibliographicEntity(){
        Random random = new Random();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(123456);
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
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

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
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

}
