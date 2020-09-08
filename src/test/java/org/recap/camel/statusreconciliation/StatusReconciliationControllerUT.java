package org.recap.camel.statusreconciliation;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.gfa.model.Dsitem;
import org.recap.gfa.model.GFAItemStatusCheckResponse;
import org.recap.gfa.model.GFARetrieveItemResponse;
import org.recap.gfa.model.RetrieveItem;
import org.recap.gfa.model.Ttitem;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.request.GFAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 2/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class StatusReconciliationControllerUT{

    @InjectMocks
    private StatusReconciliationController statusReconciliationController;

    private static final Logger logger = LoggerFactory.getLogger(StatusReconciliationController.class);

    @Mock
    GFAService gfaService;


    private Integer batchSize = 100;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    private ProducerTemplate producer;

    @Mock
    private RequestItemDetailsRepository mockedRequestItemDetailsRepository;

    @Mock
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(statusReconciliationController,"batchSize",10);
        ReflectionTestUtils.setField(statusReconciliationController,"statusReconciliationDayLimit",2);
        ReflectionTestUtils.setField(statusReconciliationController,"statusReconciliationLasBarcodeLimit",1);
    }

    @Test
    public void testStatusReconciliation(){
        Ttitem ttitem = new Ttitem();
        ttitem.setItemBarcode("3321545824554545");
        ttitem.setItemStatus("IN");
        Dsitem dsitem = new Dsitem();
        dsitem.setTtitem(Arrays.asList(ttitem));
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = new GFAItemStatusCheckResponse();
        gfaItemStatusCheckResponse.setDsitem(dsitem);
        Map<String,Integer> itemCountAndStatusIdMap = new HashMap<>();
        itemCountAndStatusIdMap.put("itemAvailabilityStatusId",0);
        itemCountAndStatusIdMap.put("totalPagesCount",0);
        int totalPagesCount = itemCountAndStatusIdMap.get("totalPagesCount");
        int itemAvailabilityStatusId = itemCountAndStatusIdMap.get("itemAvailabilityStatusId");
        List<List<ItemEntity>> itemEntityChunkList = new ArrayList<>();
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("3321545824554545");
        itemEntity.setItemId(1);
        itemEntity.setItemAvailabilityStatusId(2);
        List<ItemEntity> itemEntityList = Arrays.asList(itemEntity);
        itemEntityChunkList = Arrays.asList(itemEntityList);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(2);
        List<RequestStatusEntity> requestStatusEntityList = new ArrayList<>();
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setId(3);
        requestStatusEntity.setRequestStatusCode("EDD");
        requestStatusEntity.setRequestStatusDescription("EDD");
        requestStatusEntityList.add(requestStatusEntity);
        List<String> requestStatusCodes = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        Mockito.when(statusReconciliationController.getRequestItemStatusDetailsRepository().findByRequestStatusCodeIn(requestStatusCodes)).thenReturn(Arrays.asList(requestStatusEntity));
        Mockito.when(statusReconciliationController.getGfaService().itemStatusComparison(Mockito.any(),Mockito.any())).thenCallRealMethod();
        Mockito.when(itemDetailsRepository.getNotAvailableItemsCount(2,Arrays.asList(3),itemStatusEntity.getId())).thenReturn((long) 1);
        Mockito.when(statusReconciliationController.getItemStatusDetailsRepository().findByStatusCode(RecapConstants.ITEM_STATUS_NOT_AVAILABLE)).thenReturn(itemStatusEntity);
        ResponseEntity responseEntity = statusReconciliationController.itemStatusReconciliation();
        List<Integer> requestStatusIds = new ArrayList<>();
        requestStatusIds.add(1);
        requestStatusIds.add(2);
        Map<String,Integer>  data= statusReconciliationController.getTotalPageCount(requestStatusIds,1);
        assertNotNull(responseEntity);
        assertNotNull(data);
        assertEquals(responseEntity.getBody().toString(),"Success");
    }

    @Test
    public void testTtitem(){
        Ttitem ttitem = new Ttitem();
        ttitem.setItemBarcode("332445645758458");
        ttitem.setCustomerCode("AD");
        ttitem.setRequestId(1);
        ttitem.setRequestor("Test");
        ttitem.setRequestorFirstName("test");
        ttitem.setRequestorLastName("test");
        ttitem.setRequestorMiddleName("test");
        ttitem.setRequestorEmail("hemalatha.s@htcindia.com");
        ttitem.setRequestorOther("test");
        ttitem.setBiblioTitle("test");
        ttitem.setBiblioLocation("Discovery");
        ttitem.setBiblioAuthor("John");
        ttitem.setBiblioVolume("V1");
        ttitem.setBiblioCode("A1");
        ttitem.setArticleTitle("Title");
        ttitem.setArticleDate(new Date().toString());
        ttitem.setArticleAuthor("john");
        ttitem.setArticleIssue("Test");
        ttitem.setArticleVolume("V1");
        ttitem.setStartPage("1");
        ttitem.setEndPage("10");
        ttitem.setPages("9");
        ttitem.setOther("test");
        ttitem.setPriority("test");
        ttitem.setNotes("notes");
        ttitem.setRequestDate(new Date().toString());
        ttitem.setRequestTime("06:05:00");
        ttitem.setErrorCode("test");
        ttitem.setErrorNote("test");
        ttitem.setItemStatus("Available");
        ttitem.setDestination("Discovery");
        ttitem.setDeliveryMethod("test");

        RetrieveItem retrieveItem = new RetrieveItem();
        retrieveItem.setTtitem(Arrays.asList(ttitem));

        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        gfaRetrieveItemResponse.setScrenMessage("Success");
        gfaRetrieveItemResponse.setSuccess(true);
        gfaRetrieveItemResponse.setRetrieveItem(retrieveItem);

        assertNotNull(ttitem.getItemBarcode());
        assertNotNull(ttitem.getCustomerCode());
        assertNotNull(ttitem.getRequestor());
        assertNotNull(ttitem.getRequestorFirstName());
        assertNotNull(ttitem.getRequestorLastName());
        assertNotNull(ttitem.getRequestorMiddleName());
        assertNotNull(ttitem.getRequestorEmail());
        assertNotNull(ttitem.getRequestorOther());
        assertNotNull(ttitem.getBiblioTitle());
        assertNotNull(ttitem.getBiblioLocation());
        assertNotNull(ttitem.getBiblioAuthor());
        assertNotNull(ttitem.getBiblioVolume());
        assertNotNull(ttitem.getBiblioCode());
        assertNotNull(ttitem.getArticleTitle());
        assertNotNull(ttitem.getArticleAuthor());
        assertNotNull(ttitem.getArticleVolume());
        assertNotNull(ttitem.getArticleIssue());
        assertNotNull(ttitem.getArticleDate());
        assertNotNull(ttitem.getStartPage());
        assertNotNull(ttitem.getEndPage());
        assertNotNull(ttitem.getPages());
        assertNotNull(ttitem.getOther());
        assertNotNull(ttitem.getPriority());
        assertNotNull(ttitem.getNotes());
        assertNotNull(ttitem.getRequestDate());
        assertNotNull(ttitem.getRequestTime());
        assertNotNull(ttitem.getErrorCode());
        assertNotNull(ttitem.getErrorNote());
        assertNotNull(ttitem.getRequestId());
        assertNotNull(ttitem.getItemStatus());
        assertNotNull(ttitem.getDeliveryMethod());
        assertNotNull(ttitem.getDestination());
        assertNotNull(retrieveItem.getTtitem());
        assertNotNull(gfaRetrieveItemResponse.getRetrieveItem());
        assertNotNull(gfaRetrieveItemResponse.getScrenMessage());
        assertNotNull(gfaRetrieveItemResponse.isSuccess());
    }
    @Test
    public void test(){
        statusReconciliationController.getBatchSize();
        statusReconciliationController.getFromDate(1);
        statusReconciliationController.getGfaService();
        statusReconciliationController.getItemDetailsRepository();
        statusReconciliationController.getItemStatusDetailsRepository();
        statusReconciliationController.getProducer();
        statusReconciliationController.getRequestItemStatusDetailsRepository();
        statusReconciliationController.getStatusReconciliationDayLimit();
        statusReconciliationController.getStatusReconciliationLasBarcodeLimit();
        assertTrue(true);
    }
}