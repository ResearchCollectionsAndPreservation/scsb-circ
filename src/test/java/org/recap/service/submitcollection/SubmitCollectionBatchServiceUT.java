package org.recap.service.submitcollection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.util.MarcUtil;

import javax.xml.bind.JAXBException;
import java.util.*;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionBatchServiceUT {

    @InjectMocks
    SubmitCollectionBatchService submitCollectionBatchService;

    @Mock
    Record record;

    @Mock
    JAXBHandler jaxbHandler;

    @Mock
    Leader leader;

    @Mock
    private MarcUtil marcUtil;

    private String inputRecords = "<?xml version=\"1.0\" ?>\n" +
            "<bibRecords>\n" +
            "    <bibRecord>\n" +
            "        <bib>\n" +
            "            <owningInstitutionId>NYPL</owningInstitutionId>\n" +
            "            <owningInstitutionBibId>.b153286131</owningInstitutionBibId>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                    <record>\n" +
            "                        <controlfield tag=\"001\">47764496</controlfield>\n" +
            "                        <controlfield tag=\"003\">OCoLC</controlfield>\n" +
            "                        <controlfield tag=\"005\">20021018083242.7</controlfield>\n" +
            "                        <controlfield tag=\"008\">010604s2000 it a bde 000 0cita</controlfield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "                            <subfield code=\"a\">2001386785</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "                            <subfield code=\"a\">8880898620</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "                            <subfield code=\"a\">DLC</subfield>\n" +
            "                            <subfield code=\"c\">DLC</subfield>\n" +
            "                            <subfield code=\"d\">NYP</subfield>\n" +
            "                            <subfield code=\"d\">OCoLC</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"042\">\n" +
            "                            <subfield code=\"a\">pcc</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"043\">\n" +
            "                            <subfield code=\"a\">e-it---</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"049\">\n" +
            "                            <subfield code=\"a\">NYPG</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
            "                            <subfield code=\"a\">GV942.7.A1</subfield>\n" +
            "                            <subfield code=\"b\">D59 2000</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"0\" ind2=\"0\" tag=\"245\">\n" +
            "                            <subfield code=\"a\">Dizionario biografico enciclopedico di un secolo del calcio italiano /\n" +
            "                            </subfield>\n" +
            "                            <subfield code=\"c\">a cura di Marco Sappino.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"1\" ind2=\"4\" tag=\"246\">\n" +
            "                            <subfield code=\"a\">Dizionario del calcio italiano</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "                            <subfield code=\"a\">Milano :</subfield>\n" +
            "                            <subfield code=\"b\">Baldini &amp; Castoldi,</subfield>\n" +
            "                            <subfield code=\"c\">c2000.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "                            <subfield code=\"a\">2 v., (2147 p.) :</subfield>\n" +
            "                            <subfield code=\"b\">ill. ;</subfield>\n" +
            "                            <subfield code=\"c\">22 cm.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"3\" tag=\"440\">\n" +
            "                            <subfield code=\"a\">Le boe ;</subfield>\n" +
            "                            <subfield code=\"v\">43</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "                            <subfield code=\"a\">Includes bibliographical references (p. [2004]-2038).</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"1\" ind2=\" \" tag=\"505\">\n" +
            "                            <subfield code=\"a\">1. Protagonisti -- 2. club e trofei.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                            <subfield code=\"a\">Soccer players</subfield>\n" +
            "                            <subfield code=\"z\">Italy</subfield>\n" +
            "                            <subfield code=\"v\">Biography</subfield>\n" +
            "                            <subfield code=\"v\">Dictionaries.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                            <subfield code=\"a\">Soccer</subfield>\n" +
            "                            <subfield code=\"z\">Italy</subfield>\n" +
            "                            <subfield code=\"v\">Biography</subfield>\n" +
            "                            <subfield code=\"v\">Dictionaries.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                            <subfield code=\"a\">Soccer</subfield>\n" +
            "                            <subfield code=\"z\">Italy</subfield>\n" +
            "                            <subfield code=\"v\">Encyclopedias.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n" +
            "                            <subfield code=\"a\">Sappino, Marco.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"907\">\n" +
            "                            <subfield code=\"a\">.b153286131</subfield>\n" +
            "                            <subfield code=\"c\">m</subfield>\n" +
            "                            <subfield code=\"d\">a</subfield>\n" +
            "                            <subfield code=\"e\">-</subfield>\n" +
            "                            <subfield code=\"f\">ita</subfield>\n" +
            "                            <subfield code=\"g\">it</subfield>\n" +
            "                            <subfield code=\"h\">0</subfield>\n" +
            "                            <subfield code=\"i\">2</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"952\">\n" +
            "                            <subfield code=\"h\">JFD 02-22709</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "                            <subfield code=\"a\">(OCoLC)47764496</subfield>\n" +
            "                        </datafield>\n" +
            "                        <leader>01184nam a22003494a 4500</leader>\n" +
            "                    </record>\n" +
            "                </collection>\n" +
            "            </content>\n" +
            "        </bib>\n" +
            "        <holdings>\n" +
            "            <holding>\n" +
            "                <owningInstitutionHoldingsId/>\n" +
            "                <content>\n" +
            "                    <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                        <record>\n" +
            "                            <datafield ind1=\" \" ind2=\"8\" tag=\"852\">\n" +
            "                                <subfield code=\"b\">rc2ma</subfield>\n" +
            "                                <subfield code=\"h\">JFD 02-22709</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\" \" ind2=\" \" tag=\"866\">\n" +
            "                                <subfield code=\"a\">v. 1</subfield>\n" +
            "                            </datafield>\n" +
            "                        </record>\n" +
            "                    </collection>\n" +
            "                </content>\n" +
            "                <items>\n" +
            "                    <content>\n" +
            "                        <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                            <record>\n" +
            "                                <datafield ind1=\" \" ind2=\" \" tag=\"876\">\n" +
            "                                    <subfield code=\"p\">33433031684909</subfield>\n" +
            "                                    <subfield code=\"h\">In Library Use</subfield>\n" +
            "                                    <subfield code=\"a\">.i116690355</subfield>\n" +
            "                                    <subfield code=\"j\">Available</subfield>\n" +
            "                                    <subfield code=\"t\">1</subfield>\n" +
            "                                    <subfield code=\"3\">v. 1</subfield>\n" +
            "                                </datafield>\n" +
            "                                <datafield ind1=\" \" ind2=\" \" tag=\"900\">\n" +
            "                                    <subfield code=\"a\">Shared</subfield>\n" +
            "                                    <subfield code=\"b\">NA</subfield>\n" +
            "                                </datafield>\n" +
            "                            </record>\n" +
            "                        </collection>\n" +
            "                    </content>\n" +
            "                </items>\n" +
            "            </holding>\n" +
            "        </holdings>\n" +
            "    </bibRecord>\n" +
            "</bibRecords>\n";
    @Test
    public void processMarc(){
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        processedBibIds.add(2);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        List<SubmitCollectionReportInfo> submitCollectionReportInfos = new ArrayList<>();
        submitCollectionReportInfos.add(getSubmitCollectionReportInfo());
        submitCollectionReportInfoMap.put("1",submitCollectionReportInfos);
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("1","1");
        idMapToRemoveIndexList.add(stringMap);
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        bibIdMapToRemoveIndexList.add(stringMap);
        boolean checkLimit = true;
        boolean isCGDProtection = true;
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        updatedDummyRecordOwnInstBibIdSet.add("123456");
        InstitutionEntity institutionEntity = getInstitutionEntity();
        record.setId(1l);
        leader.setId(1l);
        leader.setBaseAddressOfData(1);
        record.setLeader(leader);
        record.setType("Submit");
        List<Record> recordList = new ArrayList<>();
        recordList.add(record);
       // Mockito.when(submitCollectionBatchService.getMarcUtil()).thenReturn(marcUtil);
//        Mockito.doCallRealMethod().when(marcUtil).convertAndValidateXml(any(),any(),any());
        String result = submitCollectionBatchService.processMarc(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
            ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
    }

    @Test
    public void processMarcWithInvalidMessage(){
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        processedBibIds.add(2);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        boolean checkLimit = true;
        boolean isCGDProtection = true;
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        List<Record> recordList = new ArrayList<>();
        Mockito.when(marcUtil.convertAndValidateXml(inputRecords, checkLimit, recordList)).thenReturn("Maximum allowed input record");
        String result = submitCollectionBatchService.processMarc(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
                ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertNotNull(result);
    }
    @Test
    public void processSCSB() throws JAXBException {
        //String inputRecords = "test";
        Set<Integer> processedBibIds = new HashSet<>();
        Map<String, List<SubmitCollectionReportInfo>> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        boolean checkLimit = true;
        boolean isCGDProtected = true;
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        BibRecords bibRecords = new BibRecords();
//        Mockito.when((BibRecords)jaxbHandler.getInstance().unmarshal(inputRecords, BibRecords.class)).thenReturn(bibRecords);
        submitCollectionBatchService.processSCSB(inputRecords,processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit,isCGDProtected,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
    }
    @Test
    public  void processSCSBException() throws JAXBException {
        //String inputRecords = "/home/jancy.roach/Workspace/Recap-4jdk11/Phase4-SCSB-Circ/src/test/resources";
        Set<Integer> processedBibIds = new HashSet<>();
        processedBibIds.add(1);
        processedBibIds.add(2);
        Map<String, List< SubmitCollectionReportInfo >> submitCollectionReportInfoMap = new HashMap<>();
        List<Map<String, String>> idMapToRemoveIndexList = new ArrayList<>();
        List<Map<String, String>> bibIdMapToRemoveIndexList = new ArrayList<>();
        boolean checkLimit = true;
        boolean isCGDProtection = true;
        Set<String> updatedDummyRecordOwnInstBibIdSet = new HashSet<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        BibRecords bibRecords = new BibRecords();
        //Mockito.when((BibRecords) jaxbHandler.getInstance().unmarshal(inputRecords, BibRecords.class)).thenReturn(bibRecords);
        String result = submitCollectionBatchService.processSCSB(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
                ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertNotNull(result);
    }

    private InstitutionEntity getInstitutionEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }
    private BibliographicEntity getBibliographicEntity(){
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

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");

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
        itemEntity.setInstitutionEntity(institutionEntity);
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
        itemEntity.setBibliographicEntities(Arrays.asList(getBibliographicEntity()));
        return itemEntity;
    }

}
