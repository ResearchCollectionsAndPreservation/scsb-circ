package org.recap.service.requestdataload;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.common.ScsbConstants;
import org.recap.camel.requestinitialdataload.RequestDataLoadCSVRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 21/7/17.
 */
public class RequestDataLoadServiceIT extends BaseTestCase{

    @Autowired
    RequestDataLoadService requestDataLoadService;

    @Test
    public void testRequestDataService() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = new RequestDataLoadCSVRecord();
        requestDataLoadCSVRecord.setBarcode(bibliographicEntity.getItemEntities().get(0).getBarcode());
        requestDataLoadCSVRecord.setCustomerCode("PB");
        requestDataLoadCSVRecord.setDeliveryMethod(ScsbConstants.REQUEST_DATA_LOAD_REQUEST_TYPE);
        requestDataLoadCSVRecord.setCreatedDate("05/12/2017 00:00:27.124");
        requestDataLoadCSVRecord.setLastUpdatedDate("05/12/2017 00:00:27.124");
        requestDataLoadCSVRecord.setPatronId("0000000");
        requestDataLoadCSVRecord.setStopCode("AD");
        requestDataLoadCSVRecord.setEmail("test@email.com");
        Set<String> barcodeSet = new HashSet<>();

        Map<String,Object> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord),barcodeSet);
        assertTrue(response.size() == 3);

    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");


        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(institutionEntity.getId());
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
        itemEntity.setBarcode("41234213");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(2);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

}