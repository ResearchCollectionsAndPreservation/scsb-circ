package org.recap.service.submitcollection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.service.common.RepositoryService;
import org.recap.service.common.SetupDataService;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by premkb on 18/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionReportHelperServiceUT{

    @InjectMocks
    private SubmitCollectionReportHelperService submitCollectionReportHelperService;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SetupDataService setupDataService;

    @Mock
    private SubmitCollectionHelperService submitCollectionHelperService;

    @Mock
    private CommonUtil commonUtil;

    @Test
    public  void setSubmitCollectionExceptionReportInfo(){
        List<ItemEntity> itemEntityList = new ArrayList<>();
        itemEntityList.add(getItemEntity());
        List<SubmitCollectionReportInfo> submitCollectionExceptionInfos = new ArrayList<>();
        submitCollectionExceptionInfos.add(getSubmitCollectionReportInfo());
        String message = "SUBMIT COLLECTION";
        submitCollectionReportHelperService.setSubmitCollectionExceptionReportInfo(itemEntityList,submitCollectionExceptionInfos,message);
    }
    @Test
    public void setSubmitCollectionReportInfoForOwningInstitutionBibIdMismatch(){
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        incomingBibliographicEntity.setOwningInstitutionBibId("2345");
        Map<String,List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(1,"PUL");
        List<SubmitCollectionReportInfo> submitCollectionExceptionInfos = new ArrayList<>();
        submitCollectionExceptionInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("submitCollectionFailureList",submitCollectionExceptionInfos);
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(fetchedBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        submitCollectionReportHelperService.setSubmitCollectionReportInfoForOwningInstitutionBibIdMismatch(fetchedBibliographicEntity,incomingBibliographicEntity,submitCollectionReportInfoMap);
    }
    @Test
    public  void setSubmitCollectionReportInfoForOwningInstitutionBibIdMismatchForBoundWith(){
        BibliographicEntity fetchedBibliographicEntity = getBibliographicEntity();
        Map institutionEntityMap = new HashMap();
        institutionEntityMap.put(1,"PUL");
        List<String> notMatchedIncomingOwnInstBibId = new ArrayList<>();
        notMatchedIncomingOwnInstBibId.add("345677");
        List<String> notMatchedFetchedOwnInstBibId = new ArrayList<>();
        notMatchedFetchedOwnInstBibId.add("123456");
        ItemEntity incomingItemEntity = getItemEntity();
        ItemEntity fetchedItemEntity = getItemEntity();
        fetchedItemEntity.setHoldingsEntities(Arrays.asList(getHoldingsEntity()));
        List<SubmitCollectionReportInfo> submitCollectionExceptionInfos = new ArrayList<>();
        submitCollectionExceptionInfos.add(getSubmitCollectionReportInfo());
        Mockito.when(setupDataService.getInstitutionIdCodeMap().get(fetchedBibliographicEntity.getOwningInstitutionId())).thenReturn(institutionEntityMap);
        submitCollectionReportHelperService.setSubmitCollectionReportInfoForOwningInstitutionBibIdMismatchForBoundWith(notMatchedIncomingOwnInstBibId,notMatchedFetchedOwnInstBibId,incomingItemEntity,fetchedItemEntity,submitCollectionExceptionInfos);
    }

    @Test
    public void setSubmitCollectionReportInfoForInvalidDummyRecordBasedOnOwnInstItemId(){
        BibliographicEntity incomingBibliographicEntity = getBibliographicEntity();
        List<SubmitCollectionReportInfo> submitCollectionReportInfoList = new ArrayList<>();
        submitCollectionReportInfoList.add(getSubmitCollectionReportInfo());
        List<ItemEntity> fetchedCompleteItem = new ArrayList<>();
        ItemEntity itemEntity =  getItemEntity();
        itemEntity.setHoldingsEntities(Arrays.asList(getHoldingsEntity()));
        fetchedCompleteItem.add(itemEntity);
        submitCollectionReportHelperService.setSubmitCollectionReportInfoForInvalidDummyRecordBasedOnOwnInstItemId(incomingBibliographicEntity,submitCollectionReportInfoList,fetchedCompleteItem);
    }

    @Test
    public void setSubmitCollectionReportInfoForInvalidXml(){
        String institutionCode = "PUL";
        List<SubmitCollectionReportInfo> submitCollectionExceptionInfos = new ArrayList<>();
        submitCollectionExceptionInfos.add(getSubmitCollectionReportInfo());
        String message = "SUBMIT COLLECTION";
        submitCollectionReportHelperService.setSubmitCollectionReportInfoForInvalidXml(institutionCode,submitCollectionExceptionInfos,message);
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
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
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
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }
    private SubmitCollectionReportInfo getSubmitCollectionReportInfo(){
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setOwningInstitution("PUL");
        submitCollectionReportInfo.setItemBarcode("123456");
        submitCollectionReportInfo.setCustomerCode("PA");
        submitCollectionReportInfo.setMessage("SUCCESS");
        return submitCollectionReportInfo;
    }

    private ItemEntity getItemEntity(){
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
        itemEntity.setBibliographicEntities(Arrays.asList(getBibliographicEntity()));
        return itemEntity;
    }

    /*@Test
    public void buildSubmitCollectionReportInfoForValidRecord() throws Exception {
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportMap();
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem(1, "32101095533293", "PA", "24252", "PUL", "9919400", "9734816", "7453441", true);
        BibliographicEntity incomingBibliographicEntity = getConvertedBibliographicEntity("MarcRecord.xml", "PUL");
        submitCollectionReportHelperService.buildSubmitCollectionReportInfo(submitCollectionReportInfoMap, bibliographicEntity, incomingBibliographicEntity);
        List<SubmitCollectionReportInfo> submitCollectionReportInfoList = submitCollectionReportInfoMap.get(RecapConstants.SUBMIT_COLLECTION_SUCCESS_LIST);
        assertEquals(RecapConstants.SUBMIT_COLLECTION_SUCCESS_RECORD, submitCollectionReportInfoList.get(0).getMessage());
    }

    @Test
    public void buildSubmitCollectionReportInfoForValidRecord1BibMultipleItems() throws Exception {
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportMap();
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem(1, "32101095533293", "PA", "24252", "PUL", "9919400", "9734816", "7453441", true);
        BibliographicEntity incomingBibliographicEntity = getConvertedBibliographicEntity("MarcRecord.xml", "PUL");
        submitCollectionReportHelperService.buildSubmitCollectionReportInfo(submitCollectionReportInfoMap, bibliographicEntity, incomingBibliographicEntity);
        List<SubmitCollectionReportInfo> submitCollectionReportInfoList = submitCollectionReportInfoMap.get(RecapConstants.SUBMIT_COLLECTION_SUCCESS_LIST);
        assertEquals(RecapConstants.SUBMIT_COLLECTION_SUCCESS_RECORD, submitCollectionReportInfoList.get(0).getMessage());
    }

    @Test
    public void buildSubmitCollectionReportInfoForInvalidOwningInstHoldingId() throws Exception {
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportMap();
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem(1, "32101095533293", "PA", "24252", "PUL", "9919400", "222420", "7453441", true);
        BibliographicEntity incomingBibliographicEntity = getConvertedBibliographicEntity("MarcRecord.xml", "PUL");
        submitCollectionReportHelperService.buildSubmitCollectionReportInfo(submitCollectionReportInfoMap, bibliographicEntity, incomingBibliographicEntity);
        List<SubmitCollectionReportInfo> submitCollectionReportInfoList = submitCollectionReportInfoMap.get(RecapConstants.SUBMIT_COLLECTION_FAILURE_LIST);
        assertTrue(true);
    }

    @Test
    public void buildSubmitCollectionReportInfoForInvalidRecordOwningInstItemId() throws Exception {
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportMap();
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem(1, "32101095533293", "PA", "24252", "PUL", "9919400", "9734816", "7453442", true);
        BibliographicEntity incomingBibliographicEntity = getConvertedBibliographicEntity("MarcRecord.xml", "PUL");
        submitCollectionReportHelperService.buildSubmitCollectionReportInfo(submitCollectionReportInfoMap, bibliographicEntity, incomingBibliographicEntity);
        List<SubmitCollectionReportInfo> submitCollectionReportInfoList = submitCollectionReportInfoMap.get(RecapConstants.SUBMIT_COLLECTION_FAILURE_LIST);
        assertTrue(true);
    }

    @Test
    public void buildSubmitCollectionReportInfoForRejectionRecord() throws Exception {
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = getSubmitCollectionReportMap();
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem(1, "32101095533293", "PA", "24252", "PUL", "9919400", "9734816", "7453441", false);
        //BibliographicEntity incomingBibliographicEntity = getConvertedBibliographicEntity("MarcRecord.xml", "PUL");
       // submitCollectionReportHelperService.buildSubmitCollectionReportInfo(submitCollectionReportInfoMap, bibliographicEntity, incomingBibliographicEntity);
       // List<SubmitCollectionReportInfo> submitCollectionReportInfoList = submitCollectionReportInfoMap.get(RecapConstants.SUBMIT_COLLECTION_REJECTION_LIST);
       // assertEquals(RecapConstants.SUBMIT_COLLECTION_REJECTION_RECORD, submitCollectionReportInfoList.get(0).getMessage());
    }*/

    public BibliographicEntity saveBibSingleHoldingsSingleItem(Integer itemCount, String itemBarcode, String customerCode, String callnumber, String institution, String owningInstBibId, String owningInstHoldingId, String owningInstItemId, boolean availableItem) throws Exception {
        File bibContentFile = getBibContentFile(institution);
        File holdingsContentFile = getHoldingsContentFile(institution);
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(owningInstBibId);
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(owningInstHoldingId));

        List<ItemEntity> itemEntityList = getItemEntityList(itemCount, itemBarcode, customerCode, callnumber, owningInstItemId, availableItem);
        for (ItemEntity itemEntity1 : itemEntityList) {
            itemEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));
            itemEntity1.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        }
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(itemEntityList);
        holdingsEntity.setItemEntities(itemEntityList);

       // BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
       // entityManager.refresh(savedBibliographicEntity);
        return bibliographicEntity;

    }

    public List<ItemEntity> getItemEntityList(Integer itemCount, String itemBarcode, String customerCode, String callnumber, String owningInstItemId, boolean availableItem) {
        List<ItemEntity> itemEntityList = new ArrayList<>();
        for (int count = 0; count < itemCount; count++) {
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setLastUpdatedDate(new Date());
            if (count == 0) {
                itemEntity.setOwningInstitutionItemId(owningInstItemId);
                itemEntity.setBarcode(itemBarcode);
            } else {
                itemEntity.setOwningInstitutionItemId(owningInstItemId + count);
                itemEntity.setBarcode(itemBarcode + count);
            }
            itemEntity.setOwningInstitutionId(1);
            itemEntity.setCallNumber(callnumber);
            itemEntity.setCollectionGroupId(1);
            itemEntity.setCallNumberType("1");
            itemEntity.setCustomerCode(customerCode);
            itemEntity.setCatalogingStatus("Complete");
            if (availableItem) {
                itemEntity.setItemAvailabilityStatusId(1);
            } else {
                itemEntity.setItemAvailabilityStatusId(2);
            }
            itemEntity.setCreatedDate(new Date());
            itemEntity.setCreatedBy("tst");
            itemEntity.setLastUpdatedBy("tst");
            itemEntityList.add(itemEntity);
        }
        return itemEntityList;
    }

    private File getBibContentFile(String institution) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile(String institution) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-HoldingsContent.xml");
        return new File(resource.toURI());
    }

    private Map getSubmitCollectionReportMap() {
        List<SubmitCollectionReportInfo> submitCollectionSuccessInfoList = new ArrayList<>();
        List<SubmitCollectionReportInfo> submitCollectionFailureInfoList = new ArrayList<>();
        List<SubmitCollectionReportInfo> submitCollectionRejectionInfoList = new ArrayList<>();
        List<SubmitCollectionReportInfo> submitCollectionExceptionInfoList = new ArrayList<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        submitCollectionReportInfoMap.put(RecapConstants.SUBMIT_COLLECTION_SUCCESS_LIST, submitCollectionSuccessInfoList);
        submitCollectionReportInfoMap.put(RecapConstants.SUBMIT_COLLECTION_FAILURE_LIST, submitCollectionFailureInfoList);
        submitCollectionReportInfoMap.put(RecapConstants.SUBMIT_COLLECTION_REJECTION_LIST, submitCollectionRejectionInfoList);
        submitCollectionReportInfoMap.put(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_LIST, submitCollectionExceptionInfoList);
        return submitCollectionReportInfoMap;
    }

    /*private BibliographicEntity getConvertedBibliographicEntity(String xmlFileName, String institutionCode) throws URISyntaxException, IOException {
        File bibContentFile = getXmlContent(xmlFileName);
        String marcXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<Record> records = readMarcXml(marcXmlString);
       // InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(institutionCode);
      //  Map map = marcToBibEntityConverter.convert(records.get(0), institutionEntity);
      //  assertNotNull(map);
        BibliographicEntity convertedBibliographicEntity = (BibliographicEntity) map.get("bibliographicEntity");
        StringBuilder errorMessage = new StringBuilder();
        return convertedBibliographicEntity;
    }*/

    private File getXmlContent(String fileName) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource(fileName);
        return new File(resource.toURI());
    }

    private List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
        }
        return recordList;
    }
}
