package org.recap.service.requestdataload;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.requestinitialdataload.RequestDataLoadCSVRecord;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 21/7/17.
 */
public class RequestDataLoadServiceUT extends BaseTestCaseUT {

    @InjectMocks
    RequestDataLoadService requestDataLoadService;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Test
    public void testRequestDataService() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        RequestTypeEntity requestTypeEntity = getRequestTypeEntity();
        Mockito.when(itemDetailsRepository.findByBarcodeAndItemStatusEntity_StatusCode(requestDataLoadCSVRecord.getBarcode(), RecapCommonConstants.NOT_AVAILABLE)).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(RecapCommonConstants.RETRIEVAL)).thenReturn(requestTypeEntity);
        Set<String> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord),barcodeSet);
        assertNotNull(response);
    }
    @Test
    public void testRequestDataServiceWithoutRequestIdItemId() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        Mockito.when(itemDetailsRepository.findByBarcodeAndItemStatusEntity_StatusCode(requestDataLoadCSVRecord.getBarcode(), RecapCommonConstants.NOT_AVAILABLE)).thenReturn(null);
        Set<String> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord),barcodeSet);
        assertTrue(response.size() == 1);
    }
    @Test
    public void testRequestDataServiceWithDuplicateBarcodes() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        barcodeSet.add("41234213");
        Set<String> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord),barcodeSet);
        assertNotNull(response);
    }
    private RequestDataLoadCSVRecord getRequestDataLoadCSVRecord(BibliographicEntity bibliographicEntity) {
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = new RequestDataLoadCSVRecord();
        requestDataLoadCSVRecord.setBarcode(bibliographicEntity.getItemEntities().get(0).getBarcode());
        requestDataLoadCSVRecord.setCustomerCode("PB");
        requestDataLoadCSVRecord.setDeliveryMethod(RecapConstants.REQUEST_DATA_LOAD_REQUEST_TYPE);
        requestDataLoadCSVRecord.setCreatedDate("05/12/2017 00:00:27.124");
        requestDataLoadCSVRecord.setLastUpdatedDate("05/12/2017 00:00:27.124");
        requestDataLoadCSVRecord.setPatronId("0000000");
        requestDataLoadCSVRecord.setStopCode("AD");
        requestDataLoadCSVRecord.setEmail("hemalatha.s@htcindia.com");
        return requestDataLoadCSVRecord;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() {

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
        itemEntity.setId(1);
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
    private RequestTypeEntity getRequestTypeEntity() {
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setId(1);
        requestTypeEntity.setRequestTypeCode("EDD");
        requestTypeEntity.setRequestTypeDesc("EDD");
        return requestTypeEntity;
    }

}
