package org.recap.service.requestdataload;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.requestinitialdataload.RequestDataLoadCSVRecord;
import org.recap.ims.service.GFALasService;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.recap.util.CommonUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

/**
 * Created by hemalathas on 21/7/17.
 */
public class RequestDataLoadServiceUT extends BaseTestCaseUT {

    @InjectMocks
    RequestDataLoadService requestDataLoadService;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    GFALasService gfaLasService;

    @Mock
    CommonUtil commonUtil;

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
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("HD");
        itemEntity.setImsLocationEntity(imsLocationEntity);
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(getItemStatusEntity());
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(gfaLasService.callGfaItemStatus(any())).thenReturn("Test");
        Mockito.when(gfaLasService.getGfaItemStatusInUpperCase(any())).thenReturn("test");
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.FALSE);
        ReflectionTestUtils.setField(requestDataLoadService,"requestInitialLoadGfaCheck",Boolean.TRUE);
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(ScsbCommonConstants.RETRIEVAL)).thenReturn(requestTypeEntity);
        Map<String,Object> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord), barcodeSet);
        assertNotNull(response);
    }

    @Test
    public void testRequestDataServiceItemNotAvailable() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        RequestTypeEntity requestTypeEntity = getRequestTypeEntity();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(getItemStatusEntity());
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(requestTypeDetailsRepository.findByrequestTypeCode(ScsbCommonConstants.RETRIEVAL)).thenReturn(requestTypeEntity);
        Map<String,Object> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord), barcodeSet);
        assertNotNull(response);
    }

    @Test
    public void testRequestDataServiceWithoutRequestIdItemId() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(getItemStatusEntity());
        Map<String,Object> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord), barcodeSet);
        assertTrue(response.size() == 3);
    }

    @Test
    public void testRequestDataServiceWithDuplicateBarcodes() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        RequestDataLoadCSVRecord requestDataLoadCSVRecord = getRequestDataLoadCSVRecord(bibliographicEntity);
        Set<String> barcodeSet = new HashSet<>();
        barcodeSet.add("41234213");
        Mockito.when(itemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.NOT_AVAILABLE)).thenReturn(getItemStatusEntity());
        Map<String,Object> response = requestDataLoadService.process(Arrays.asList(requestDataLoadCSVRecord), barcodeSet);
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
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("HD");
        itemEntity.setImsLocationEntity(imsLocationEntity);
        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setOwningInstitutionId(4);
        itemEntity2.setItemStatusEntity(getItemStatusEntity());
        List<ItemEntity> itemEntityList = new ArrayList<>();
        itemEntityList.add(itemEntity);
        itemEntityList.add(itemEntity2);
        ItemStatusEntity itemStatusEntity = getItemStatusEntity();
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(itemEntityList);
        Mockito.when(gfaLasService.callGfaItemStatus(any())).thenReturn("Test");
        Mockito.when(gfaLasService.getGfaItemStatusInUpperCase(any())).thenReturn("test");
        Mockito.when(commonUtil.checkIfImsItemStatusIsAvailableOrNotAvailable(any(), any(), anyBoolean())).thenReturn(Boolean.TRUE);
        ReflectionTestUtils.setField(requestDataLoadService,"requestInitialLoadGfaCheck",Boolean.TRUE);
        ReflectionTestUtils.invokeMethod(requestDataLoadService, "getItemInfo", barcode,itemStatusEntity);
    }

    private ItemStatusEntity getItemStatusEntity() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode(ScsbConstants.ITEM_STATUS_AVAILABLE);
        itemStatusEntity.setStatusDescription(ScsbConstants.ITEM_STATUS_AVAILABLE);
        return itemStatusEntity;
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
        requestDataLoadCSVRecord.setEmail("test@email.com");
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
        itemEntity.setItemStatusEntity(getItemStatusEntity());
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
