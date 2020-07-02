package org.recap.request;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.ItemRefileRequest;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.Normalizer;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 20/3/17.
 */
public class ItemRequestServiceUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ItemRequestServiceUT.class);

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    ItemRequestDBService itemRequestDBService;

    @Autowired
    ItemRequestService itemRequestService;

    @Mock
    ItemRequestService mockedItemRequestService;

    @Autowired
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    Exchange exchange;

    @Test // Test Cases RequestIds
    public void testUpdateRecapRequestItem() throws Exception {
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        Integer response = itemRequestService.updateRecapRequestItem(getItemRequestInformation(), bibliographicEntity.getItemEntities().get(0), "REFILED");
        assertTrue(response != 0);
        itemRequestService.getEmailService();
        itemRequestService.getGfaService();
        //updateItemAvailabilutyStatus
        Random random = new Random();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);

        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(entity.getId());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setCatalogingStatus("Complete");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity = new ItemEntity();
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("Not Available");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("7020");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        List<ItemEntity> list = new ArrayList<ItemEntity>();
        list.add(itemEntity);
        try {
            boolean status = itemRequestService.updateItemAvailabilutyStatus(list, "recap");
            assertTrue(status);
        } catch (Exception e) {
        }
        try {
            ItemInformationResponse itemInformationResponse = itemRequestService.recallItem(getItemRequestInformation(), exchange);
            assertNotNull(itemInformationResponse);
        } catch (Exception e) {
        }

        ItemInformationResponse itemInformationResponse = itemRequestService.updateGFA(getItemRequestInformation(), getItemInformationResponse());
        assertNotNull(itemInformationResponse);
        String body = getItemInformationResponse().toString();
        try {
            itemRequestService.processLASRetrieveResponse(body);
            itemRequestService.processLASEddRetrieveResponse(body);
            itemRequestService.removeDiacritical("tests");
        } catch (Exception e) {

        }
        try {
            boolean bstatus = itemRequestService.executeLasitemCheck(getItemRequestInformation(), getItemInformationResponse());
            assertTrue(bstatus);
        } catch (Exception e) {}
        ReplaceRequest replaceRequest = new ReplaceRequest();
        replaceRequest.setReplaceRequestByType("RequestStatus");
        replaceRequest.setEndRequestId("320");
        replaceRequest.setFromDate(new Date().toString());
        replaceRequest.setToDate(new Date().toString());
        replaceRequest.setRequestIds("2");
        replaceRequest.setStartRequestId("1");
        replaceRequest.setRequestStatus("test");
        Map<String, String> listMap = new HashMap<>();
        listMap = itemRequestService.replaceRequestsToLASQueue(replaceRequest);
        assertNotNull(listMap);

        try { itemRequestService.sendMessageToTopic("PUL","RETRIEVAL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("PUL","EDD",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("PUL","RECALL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("PUL","BORROW DIRECT",getItemInformationResponse(),exchange);} catch (Exception e) {}

        try { itemRequestService.sendMessageToTopic("CUL","RETRIEVAL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("CUL","EDD",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("CUL","RECALL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("CUL","BORROW DIRECT",getItemInformationResponse(),exchange);} catch (Exception e) {}

        try { itemRequestService.sendMessageToTopic("NYPLL","RETRIEVAL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("NYPLL","EDD",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("NYPLL","RECALL",getItemInformationResponse(),exchange);} catch (Exception e) {}
        try { itemRequestService.sendMessageToTopic("NYPLL","BORROW DIRECT",getItemInformationResponse(),exchange);} catch (Exception e) {}

    }

    @Test
    public void testUpdateRecapRqstItem() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestId(requestItemEntity.getId());
        ItemInformationResponse response = itemRequestService.updateRecapRequestItem(itemInformationResponse);
        assertNotNull(response);
    }

    @Test
    public void testUpdateRecapRequestStatus() throws Exception {
        RequestItemEntity requestItemEntity = createRequestItem();
        ItemInformationResponse itemInformationResponse = getItemInformationResponse();
        itemInformationResponse.setRequestId(requestItemEntity.getId());
        ItemInformationResponse response = itemRequestService.updateRecapRequestStatus(itemInformationResponse);
        assertNotNull(response);
    }

   /* @Test
    public void testupdateChangesToDb() {
        itemRequestService.updateChangesToDb(getItemInformationResponse(), "test");
        assertTrue(true);
        try {
            camelContext.getRouteController().startRoute(RecapConstants.SUBMIT_COLLECTION_FTP_CGD_PROTECTED_PUL_ROUTE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Endpoint endpoint = camelContext.getEndpoint(RecapConstants.SUBMIT_COLLECTION_COMPLETION_QUEUE_TO);
        PollingConsumer consumer = null;
        try {
            consumer = endpoint.createPollingConsumer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Exchange exchange = consumer.receive();

        ItemInformationResponse itemInformationResponse = itemRequestService.recallItem(getItemRequestInformation(), exchange);
        assertNotNull(itemInformationResponse);
    }*/

    @Test
    public void testRequestItem() throws Exception {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        itemRequestInformation.setItemBarcodes(Arrays.asList(bibliographicEntity.getItemEntities().get(0).getBarcode()));
        ItemInformationResponse response = itemRequestService.requestItem(itemRequestInformation, exchange);
        assertNotNull(response);
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123"));
        List<Integer> requestIds = new ArrayList<>();
        requestIds.add(1);
        requestIds.add(2);
        itemRefileRequest.setRequestIds(requestIds);

        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setSuccess(false);
        itemRefileResponse.setScreenMessage(RecapConstants.REQUEST_ITEM_BARCODE_NOT_FOUND);
        ItemRefileResponse refileResponse = itemRequestService.reFileItem(itemRefileRequest, itemRefileResponse);
        assertNotNull(refileResponse);
    }

    public ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setUsername("Discovery");
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setCallNumber("X");
        itemRequestInformation.setAuthor("John");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setExpirationDate("30-03-2017 00:00:00");
        itemRequestInformation.setCustomerCode("PB");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setRequestType("RETRIEVAL");
        return itemRequestInformation;
    }

    public ItemInformationResponse getItemInformationResponse() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setCirculationStatus("test");
        itemInformationResponse.setSecurityMarker("test");
        itemInformationResponse.setFeeType("test");
        itemInformationResponse.setTransactionDate(new Date().toString());
        itemInformationResponse.setHoldQueueLength("10");
        itemInformationResponse.setTitleIdentifier("test");
        itemInformationResponse.setBibID("1223");
        itemInformationResponse.setDueDate(new Date().toString());
        itemInformationResponse.setExpirationDate("30-03-2017 00:00:00");
        itemInformationResponse.setRecallDate(new Date().toString());
        itemInformationResponse.setCurrentLocation("test");
        itemInformationResponse.setHoldPickupDate(new Date().toString());
        itemInformationResponse.setItemBarcode("32101077423406");
        itemInformationResponse.setRequestType("RECALL");
        itemInformationResponse.setRequestingInstitution("CUL");
        itemInformationResponse.setRequestId(2);
        return itemInformationResponse;
    }

    public BibliographicEntity getBibliographicEntity() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);
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
        bibliographicEntity.setCatalogingStatus("Complete");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("7020");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    public RequestItemEntity createRequestItem() throws Exception {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UOC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);
        assertNotNull(entity);

        BibliographicEntity bibliographicEntity = getBibliographicEntity();

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");
        RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        assertNotNull(savedRequestTypeEntity);

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("REFILE");
        requestStatusEntity.setRequestStatusDescription("REFILE");

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getItemId());
        requestItemEntity.setRequestTypeId(savedRequestTypeEntity.getId());
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setPatronId("123");
        requestItemEntity.setStopCode("test");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(4);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        return savedRequestItemEntity;
    }

    @Test
    public void removeDia() {
        String input = "[No Restrictions] Afghānistān / |c nivīsandah, Aḥmad Shāh Farzān [RECAP] أَبَنَ فُلانًا: عَابَه ورَمَاه بخَلَّة سَوء.";
        logger.info(input);

        logger.info(input.replaceAll("[^\\p{ASCII}]", ""));

        logger.info(input.replaceAll("[^\\u0000-\\uFFFF]", ""));
        logger.info(input.replaceAll("[^\\x20-\\x7e]", ""));

        String normailzed = Normalizer.normalize(input, Normalizer.Form.NFD);

        logger.info("Normailzed : " + normailzed);
        logger.info(normailzed.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));

        normailzed = Normalizer.normalize(input, Normalizer.Form.NFKD);
        logger.info(normailzed.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));

        logger.info(normailzed.replaceAll("[^\\x20-\\x7e]", ""));

        logger.info("removeDiacritical: " + itemRequestService.removeDiacritical(input));

        logger.info(Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));


    }


}