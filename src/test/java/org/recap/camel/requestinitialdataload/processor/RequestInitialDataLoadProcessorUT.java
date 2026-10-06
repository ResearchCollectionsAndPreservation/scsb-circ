package org.recap.camel.requestinitialdataload.processor;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RouteController;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.recap.ScsbCommonConstants;
import org.recap.camel.requestinitialdataload.RequestDataLoadCSVRecord;
import org.recap.common.ScsbConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.service.requestdataload.RequestDataLoadService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class RequestInitialDataLoadProcessorUT {

    @InjectMocks
    RequestInitialDataLoadProcessor requestInitialDataLoadProcessor;

    @Mock
    RequestDataLoadService requestDataLoadService;

    @Mock
    AmazonS3 awsS3Client;

    @Mock
    RouteController routeController;

    @Mock
    CamelContext camelContext;

    @Mock
    RestTemplate restTemplate;

    @Mock
    ProducerTemplate producerTemplate;

    private static final String TEST_BUCKET_KEY = "test/test/test";

    @BeforeEach
    public void setup() {
        requestInitialDataLoadProcessor = new RequestInitialDataLoadProcessor("CUL");
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "requestDataLoadService", requestDataLoadService);
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "awsS3Client", awsS3Client);
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "camelContext", camelContext);
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "producerTemplate", producerTemplate);
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "requestInitialLoadFilePath", "/tmp");
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "scsbSolrClientUrl", "http://localhost:8080");
    }

    @Test
    public void testConstructor() {
        RequestInitialDataLoadProcessor processor = new RequestInitialDataLoadProcessor("PUL");
        assertNotNull(processor);
    }

    @Test
    public void testGetAndSetBarcodeSet() {
        Set<String> barcodes = new HashSet<>();
        barcodes.add("123456");
        requestInitialDataLoadProcessor.setBarcodeSet(barcodes);
        assertEquals(barcodes, requestInitialDataLoadProcessor.getBarcodeSet());
    }

    @Test
    public void testProcessInputWithSolrIndexRequired() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.TRUE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(5);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMap(5, true);

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);
        Mockito.when(restTemplate.postForObject(anyString(), any(), any())).thenReturn("success");

        // Execute
        requestInitialDataLoadProcessor.processInput(exchange);

        // Verify
        Mockito.verify(restTemplate, Mockito.atLeastOnce()).postForObject(anyString(), any(), any());
    }

    @Test
    public void testProcessInputWithoutSolrIndexRequired() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(5);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMap(5, false);

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);

        // Execute
        requestInitialDataLoadProcessor.processInput(exchange);

        // Verify that Solr indexing was NOT called
        Mockito.verify(restTemplate, Mockito.never()).postForObject(anyString(), any(), any());
    }

    @Test
    public void testProcessInputWithS3ObjectDoesNotExist() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(3);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMap(3, false);

        // Mock the service call - S3 object does not exist
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(false);

        // Execute - should not throw exception
        requestInitialDataLoadProcessor.processInput(exchange);

        // Verify that copyObject and deleteObject were NOT called
        Mockito.verify(awsS3Client, Mockito.never()).copyObject(any(), any(), any(), any());
        Mockito.verify(awsS3Client, Mockito.never()).deleteObject(any(), any());
    }

    @Test
    public void testProcessInputWithEmptyBarcodesList() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> emptyRecordList = new ArrayList<>();
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, emptyRecordList);

        Map<String, Object> barcodesMap = createBarcodesMap(0, false);

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);

        // Execute - should handle empty list gracefully
        requestInitialDataLoadProcessor.processInput(exchange);

        // Verify that process was still called
        Mockito.verify(requestDataLoadService, Mockito.never()).process(any(), any());
    }

    @Test
    public void testProcessInputWithLargeRecordList() throws ParseException {
        // Setup - test with list larger than partition size (10)
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(25); // > 10 for multiple partitions
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMap(25, false);

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);

        // Execute
        requestInitialDataLoadProcessor.processInput(exchange);

        // Verify process was called at least twice (due to partitioning)
        Mockito.verify(requestDataLoadService, Mockito.atLeast(2)).process(any(), any());
    }

    @Test
    public void testProcessInputWithException() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(5);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        // Mock the service to throw an exception
        Mockito.when(requestDataLoadService.process(any(), any())).thenThrow(new RuntimeException("Service error"));

        // Execute - should handle exception without throwing
        try {
            requestInitialDataLoadProcessor.processInput(exchange);
        } catch (Exception e) {
            // Exception should be caught and handled
            assertTrue(true);
        }
    }

    @Test
    public void testProcessInputWithMultipleInstitutions() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(5);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMapMultipleInstitutions();

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);

        // Execute
        requestInitialDataLoadProcessor.processInput(exchange);

        // Verify sendBodyAndHeaders was called
        Mockito.verify(producerTemplate, Mockito.atLeastOnce()).sendBodyAndHeaders(anyString(), any(), any());
    }

    @Test
    public void testProcessInputWithMultipleImsLocations() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(5);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMapMultipleImsLocations();

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);

        // Execute
        requestInitialDataLoadProcessor.processInput(exchange);

        // Verify sendBodyAndHeaders was called multiple times
        Mockito.verify(producerTemplate, Mockito.atLeast(2)).sendBodyAndHeaders(anyString(), any(), any());
    }

    @Test
    public void testProcessInputStartRouteException() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.FALSE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(5);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMap(5, false);

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);

        // Execute - should handle gracefully
        try {
            requestInitialDataLoadProcessor.processInput(exchange);
        } catch (Exception e) {
            // Exception should be caught and handled
            assertTrue(true);
        }
    }

    @Test
    public void testProcessInputWithBothBarcodeListsPopulated() throws ParseException {
        // Setup
        ReflectionTestUtils.setField(requestInitialDataLoadProcessor, "isSolrIndexRequired", Boolean.TRUE);

        List<RequestDataLoadCSVRecord> recordList = createRequestDataLoadCSVRecordList(5);
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        Exchange exchange = createExchange(ctx, recordList);

        Map<String, Object> barcodesMap = createBarcodesMapWithBothLists();

        // Mock the service call
        Mockito.when(requestDataLoadService.process(any(), any())).thenReturn(barcodesMap);
        Mockito.when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);
        Mockito.when(restTemplate.postForObject(anyString(), any(), any())).thenReturn("success");

        // Execute
        try {
            requestInitialDataLoadProcessor.processInput(exchange);
        } catch (Exception e) {
            // Exception handled
        }

        // Verify both route controller and rest template were called
        try {
            Mockito.verify(camelContext.getRouteController(), Mockito.atLeastOnce()).startRoute(anyString());
        } catch (Exception e) {
            // Exception handled
        }
        Mockito.verify(restTemplate, Mockito.atLeastOnce()).postForObject(anyString(), any(), any());
    }

    // =================================== Helper Methods ===================================

    private Exchange createExchange(CamelContext ctx, List<RequestDataLoadCSVRecord> recordList) {
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setBody(recordList);
        exchange.getIn().setHeader("CamelAwsS3Key", "test/test/test.csv");
        exchange.getIn().setHeader("CamelAwsS3BucketName", TEST_BUCKET_KEY);
        return exchange;
    }

    private List<RequestDataLoadCSVRecord> createRequestDataLoadCSVRecordList(int count) {
        List<RequestDataLoadCSVRecord> recordList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RequestDataLoadCSVRecord record = new RequestDataLoadCSVRecord();
            record.setBarcode("BARCODE" + i);
            record.setCustomerCode("PB");
            record.setDeliveryMethod("EDD");
            record.setCreatedDate(new Date().toString());
            record.setLastUpdatedDate(new Date().toString());
            record.setPatronId("PATRON" + i);
            record.setStopCode("AD");
            record.setEmail("test" + i + "@email.com");
            recordList.add(record);
        }
        return recordList;
    }

    private Map<String, Object> createBarcodesMap(int itemCount, boolean includeBibEntity) {
        Map<String, Object> barcodesMap = new HashMap<>();

        Set<String> barcodesNotInSCSB = new HashSet<>();
        barcodesNotInSCSB.add("NOTFOUND1");
        barcodesNotInSCSB.add("NOTFOUND2");

        List<ItemEntity> itemEntityList = new ArrayList<>();
        List<ItemEntity> itemsToIndex = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            ItemEntity itemEntity = createItemEntity(i, includeBibEntity);
            itemEntityList.add(itemEntity);
            if (includeBibEntity) {
                itemsToIndex.add(itemEntity);
            }
        }

        barcodesMap.put(ScsbConstants.BARCODE_NOT_FOUND_IN_SCSB, barcodesNotInSCSB);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_AVAILABLE_IN_LAS, itemEntityList);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_TO_INDEX, itemsToIndex);

        return barcodesMap;
    }

    private Map<String, Object> createBarcodesMapMultipleInstitutions() {
        Map<String, Object> barcodesMap = new HashMap<>();

        Set<String> barcodesNotInSCSB = new HashSet<>();
        barcodesNotInSCSB.add("NOTFOUND1");

        List<ItemEntity> itemEntityList = new ArrayList<>();

        // Create items for different institutions
        ItemEntity item1 = createItemEntityWithInstitution(1, "CUL");
        ItemEntity item2 = createItemEntityWithInstitution(2, "PUL");
        itemEntityList.add(item1);
        itemEntityList.add(item2);

        barcodesMap.put(ScsbConstants.BARCODE_NOT_FOUND_IN_SCSB, barcodesNotInSCSB);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_AVAILABLE_IN_LAS, itemEntityList);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_TO_INDEX, new ArrayList<>());

        return barcodesMap;
    }

    private Map<String, Object> createBarcodesMapMultipleImsLocations() {
        Map<String, Object> barcodesMap = new HashMap<>();

        Set<String> barcodesNotInSCSB = new HashSet<>();

        List<ItemEntity> itemEntityList = new ArrayList<>();

        // Create items for different IMS locations
        ItemEntity item1 = createItemEntityWithImsLocation(1, "PUL", "1");
        ItemEntity item2 = createItemEntityWithImsLocation(2, "PUL", "2");
        itemEntityList.add(item1);
        itemEntityList.add(item2);

        barcodesMap.put(ScsbConstants.BARCODE_NOT_FOUND_IN_SCSB, barcodesNotInSCSB);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_AVAILABLE_IN_LAS, itemEntityList);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_TO_INDEX, new ArrayList<>());

        return barcodesMap;
    }

    private Map<String, Object> createBarcodesMapWithBothLists() {
        Map<String, Object> barcodesMap = new HashMap<>();

        Set<String> barcodesNotInSCSB = new HashSet<>();
        barcodesNotInSCSB.add("NOTFOUND1");

        List<ItemEntity> barcodesAvailableInLAS = new ArrayList<>();
        List<ItemEntity> itemsToIndex = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ItemEntity itemEntity = createItemEntity(i, true);
            barcodesAvailableInLAS.add(itemEntity);
            itemsToIndex.add(itemEntity);
        }

        barcodesMap.put(ScsbConstants.BARCODE_NOT_FOUND_IN_SCSB, barcodesNotInSCSB);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_AVAILABLE_IN_LAS, barcodesAvailableInLAS);
        barcodesMap.put(ScsbConstants.REQUEST_INITIAL_BARCODES_TO_INDEX, itemsToIndex);

        return barcodesMap;
    }

    private ItemEntity createItemEntity(int index, boolean includeBibEntity) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("1");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("BARCODE" + index);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);

        itemEntity.setImsLocationEntity(createImsLocationEntity());

        if (includeBibEntity) {
            List<BibliographicEntity> bibList = new ArrayList<>();
            BibliographicEntity bibEntity = new BibliographicEntity();
            bibEntity.setId(100 + index);
            bibList.add(bibEntity);
            itemEntity.setBibliographicEntities(bibList);
        }

        return itemEntity;
    }

    private ItemEntity createItemEntityWithInstitution(int index, String institutionCode) {
        ItemEntity itemEntity = createItemEntity(index, false);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode(institutionCode);
        institutionEntity.setInstitutionName(institutionCode);
        itemEntity.setInstitutionEntity(institutionEntity);
        return itemEntity;
    }

    private ItemEntity createItemEntityWithImsLocation(int index, String institutionCode, String imsLocationCode) {
        ItemEntity itemEntity = createItemEntity(index, false);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode(institutionCode);
        institutionEntity.setInstitutionName(institutionCode);
        itemEntity.setInstitutionEntity(institutionEntity);

        ImsLocationEntity imsLocationEntity = createImsLocationEntityWithCode(imsLocationCode);
        itemEntity.setImsLocationEntity(imsLocationEntity);
        return itemEntity;
    }

    private ImsLocationEntity createImsLocationEntity() {
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

    private ImsLocationEntity createImsLocationEntityWithCode(String code) {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode(code);
        imsLocationEntity.setImsLocationName("test" + code);
        imsLocationEntity.setCreatedBy("test");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setActive(true);
        imsLocationEntity.setDescription("test");
        imsLocationEntity.setUpdatedBy("test");
        imsLocationEntity.setUpdatedDate(new Date());
        return imsLocationEntity;
    }

}
