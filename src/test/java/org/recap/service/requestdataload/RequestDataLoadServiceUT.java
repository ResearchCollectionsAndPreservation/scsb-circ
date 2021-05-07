package org.recap.service.requestdataload;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.requestinitialdataload.RequestDataLoadCSVRecord;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

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
        Mockito.when(itemDetailsRepository.findByBarcodeAndItemStatusEntity_StatusCode(requestDataLoadCSVRecord.getBarcode(), ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(ScsbCommonConstants.RETRIEVAL)).thenReturn(requestTypeEntity);
        Set<String> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord), barcodeSet);
        assertNotNull(response);
    }

    @Test
    public void testRequestDataServiceWithoutRequestIdItemId() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        Mockito.when(itemDetailsRepository.findByBarcodeAndItemStatusEntity_StatusCode(requestDataLoadCSVRecord.getBarcode(), ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(null);
        Set<String> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord), barcodeSet);
        assertTrue(response.size() == 1);
    }

    @Test
    public void testRequestDataServiceWithDuplicateBarcodes() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        barcodeSet.add("41234213");
        Set<String> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord), barcodeSet);
        assertNotNull(response);
    }

    @Test
    public void getDateFormat() {
        String date = "";
        ReflectionTestUtils.invokeMethod(requestDataLoadService, "getDateFormat", date);
    }

    @Test
    public void getItemInfo() {
        String barcode = "23465634";
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        itemEntity.setOwningInstitutionId(5);
        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setOwningInstitutionId(4);
        List<ItemEntity> itemEntityList = new ArrayList<>();
        itemEntityList.add(itemEntity2);
        itemEntityList.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndItemStatusEntity_StatusCode(any(), any())).thenReturn(itemEntityList);
        ReflectionTestUtils.invokeMethod(requestDataLoadService, "getItemInfo", barcode);
    }

    private RequestDataLoadCSVRecord getRequestDataLoadCSVRecord(BibliographicEntity bibliographicEntity) {
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = new RequestDataLoadCSVRecord();
        requestDataLoadCSVRecord.setBarcode(bibliographicEntity.getItemEntities().get(0).getBarcode());
        requestDataLoadCSVRecord.setCustomerCode("PB");
        requestDataLoadCSVRecord.setDeliveryMethod(ScsbConstants.REQUEST_DATA_LOAD_REQUEST_TYPE);
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
