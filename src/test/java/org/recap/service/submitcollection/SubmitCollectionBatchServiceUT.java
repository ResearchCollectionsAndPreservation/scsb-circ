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
import org.recap.model.jpa.InstitutionEntity;
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

    private String inputRecords = "\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n\" +\n" +
            "            \"<collection>\\n\" +\n" +
            "            \"   <record>\\n\" +\n" +
            "            \"      <leader>01302cas a2200361 a 4500</leader>\\n\" +\n" +
            "            \"      <controlfield tag=\\\"001\\\">202304</controlfield>\\n\" +\n" +
            "            \"      <controlfield tag=\\\"005\\\">20160526232735.0</controlfield>\\n\" +\n" +
            "            \"      <controlfield tag=\\\"008\\\">830323c19819999iluqx p   gv  0    0eng d</controlfield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"010\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">82640039</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"z\\\">81640039</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"z\\\">sn 81001329</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\" \\\" tag=\\\"022\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">0276-9948</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"035\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">(OCoLC)7466281</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"035\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">(CStRLIN)NJPG83-S372</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"035\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"9\\\">ABB7255TS-test</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"040\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">NSDP</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"d\\\">NjP</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"042\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">nsdp</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">lc</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"043\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">n-us-il</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\"0\\\" tag=\\\"050\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">K25</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">.N63</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\"0\\\" tag=\\\"222\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">University of Illinois law review</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\"0\\\" tag=\\\"245\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">University of Michigan.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"3\\\" ind2=\\\"0\\\" tag=\\\"246\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Law review</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"260\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Champaign, IL :</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">University of Illinois at Urbana-Champaign, College of Law,</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"c\\\">c1981-</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"300\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">v. ;</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"c\\\">27 cm.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"310\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">5 times a year,</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">2001-&amp;lt;2013&amp;gt;</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"321\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Quarterly,</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">1981-2000</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\" \\\" tag=\\\"362\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Vol. 1981, no. 1-</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"588\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Title from cover.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"588\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Latest issue consulted: Vol. 2013, no. 5.</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\"0\\\" tag=\\\"650\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">Law reviews</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"z\\\">Illinois.</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"0\\\">(uri)http://id.loc.gov/authorities/subjects/sh2009129243</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"2\\\" ind2=\\\" \\\" tag=\\\"710\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">University of Illinois at Urbana-Champaign.</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">College of Law.</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"0\\\">(uri)http://id.loc.gov/authorities/names/n50049213</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\"0\\\" ind2=\\\"0\\\" tag=\\\"780\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"t\\\">University of Illinois law forum</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"x\\\">0041-963X</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"998\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">09/09/94</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"s\\\">9110</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"n\\\">NjP</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"w\\\">DCLC82640039S</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"d\\\">03/23/83</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"c\\\">DLJ</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"b\\\">SZF</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"i\\\">940909</subfield>\\n\" +\n" +
            "            \"         <subfield code=\\\"l\\\">NJPG</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"911\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">19940916</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"      <datafield ind1=\\\" \\\" ind2=\\\" \\\" tag=\\\"912\\\">\\n\" +\n" +
            "            \"         <subfield code=\\\"a\\\">19970731060735.8</subfield>\\n\" +\n" +
            "            \"      </datafield>\\n\" +\n" +
            "            \"   </record>\\n\" +\n" +
            "            \"</collection>\";\n";
    @Test
    public void processMarc(){
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
        record.setId(1l);
        leader.setId(1l);
        leader.setBaseAddressOfData(1);
        record.setLeader(leader);
        record.setType("Submit");
        List<Record> recordList = new ArrayList<>();
        recordList.add(record);
        String result = submitCollectionBatchService.processMarc(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
            ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        //assertNotNull(result);
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
    public  void processSCSBException() throws JAXBException {
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
        BibRecords bibRecords = new BibRecords();
        //Mockito.when((BibRecords) jaxbHandler.getInstance().unmarshal(inputRecords, BibRecords.class)).thenReturn(bibRecords);
        String result = submitCollectionBatchService.processSCSB(inputRecords, processedBibIds,submitCollectionReportInfoMap,idMapToRemoveIndexList,bibIdMapToRemoveIndexList,checkLimit
                ,isCGDProtection,institutionEntity,updatedDummyRecordOwnInstBibIdSet);
        assertNotNull(result);
    }
}
