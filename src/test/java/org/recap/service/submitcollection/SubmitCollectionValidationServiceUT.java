package org.recap.service.submitcollection;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SubmitCollectionValidationServiceUT extends BaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(SubmitCollectionValidationService.class);
    @Autowired
    SubmitCollectionValidationService submitCollectionValidationService;
    @Autowired
    InstitutionDetailsRepository institutionDetailRepository;

    @Test
    public void test() {
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setItemBarcode("33433001888415");
        submitCollectionReportInfo.setCustomerCode("PB");
        submitCollectionReportInfo.setOwningInstitution("CUL");
        submitCollectionReportInfo.setMessage("Rejection record - Only use restriction and cgd not updated because the item is in use");
        List<SubmitCollectionReportInfo> submitCollectionReportInfoList = new ArrayList<>();
        submitCollectionReportInfoList.add(submitCollectionReportInfo);
        Map<String, List<SubmitCollectionReportInfo>> data = new HashMap<>();
        data.put("Test", submitCollectionReportInfoList);

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
        List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
        bibliographicEntityList.add(bibliographicEntity);
        List<String> listholdings = new ArrayList<>();
        listholdings.add("test1");
        listholdings.add("test2");
        boolean datatest = false;
        try {
            datatest = submitCollectionValidationService.validateIncomingEntities(data, bibliographicEntity, bibliographicEntity);
        } catch (Exception e) {
            logger.info("Exception" + e);
        }
        try {
            submitCollectionValidationService.getOwningBibIdOwnInstHoldingsIdIfAnyHoldingMismatch(bibliographicEntityList, listholdings);
        } catch (Exception e) {
            logger.info("Exception" + e);
        }

        assertTrue(!datatest);
    }
}
