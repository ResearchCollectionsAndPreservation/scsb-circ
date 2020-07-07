package org.recap.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.model.ItemRefileRequest;
import org.recap.controller.RequestItemController;
import org.recap.gfa.model.Dsitem;
import org.recap.gfa.model.GFAItemStatus;
import org.recap.gfa.model.GFAItemStatusCheckRequest;
import org.recap.gfa.model.GFAItemStatusCheckResponse;
import org.recap.gfa.model.Ttitem;
import org.recap.ils.model.response.ItemCreateBibResponse;
import org.recap.ils.model.response.ItemHoldResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.model.response.ItemRecallResponse;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.ReplaceRequest;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.jpa.SearchResultRow;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.service.RestHeaderService;
import org.recap.util.CommonUtil;
import org.recap.util.ItemRequestServiceUtil;
import org.recap.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class for Request Item Service
 * Created by sudhishk on 1/12/16.
 */
@Component
public class ItemRequestService {


    private static final Logger logger = LoggerFactory.getLogger(ItemRequestService.class);

    @Value("${scsb.solr.client.url}")
    private String scsbSolrClientUrl;

    //EDD values
    @Value("${ils.princeton.cul.patron.edd}")
    private String princetonCULEDDPatron;

    @Value("${ils.princeton.nypl.patron.edd}")
    private String princetonNYPLEDDPatron;

    @Value("${ils.columbia.pul.patron.edd}")
    private String columbiaPULEDDPatron;

    @Value("${ils.columbia.nypl.patron.edd}")
    private String columbiaNYPLEDDPatron;

    @Value("${ils.nypl.princeton.patron.edd}")
    private String nyplPrincetonEDDPatron;

    @Value("${ils.nypl.columbia.patron.edd}")
    private String nyplColumbiaEDDPatron;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private RequestItemController requestItemController;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    private GFAService gfaService;

    @Autowired
    private ItemRequestDBService itemRequestDBService;

    @Autowired
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private RestHeaderService restHeaderService;

    @Autowired
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private RequestParamaterValidatorService requestParamaterValidatorService;

    @Autowired
    private ItemValidatorService itemValidatorService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ItemEDDRequestService itemEDDRequestService;

    /**
     * @return
     */
    public RestHeaderService getRestHeaderService() {
        return restHeaderService;
    }

    /**
     * @return
     */
    public EmailService getEmailService() {
        return emailService;
    }

    /**
     * @return
     */
    public GFAService getGfaService() {
        return gfaService;
    }

    /**
     * Request item item information response.
     *
     * @param itemRequestInfo the item request info
     * @param exchange        the exchange
     * @return the item information response
     */
    public ItemInformationResponse requestItem(ItemRequestInformation itemRequestInfo, Exchange exchange) {

        List<ItemEntity> itemEntities;
        ItemEntity itemEntity;
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        try {
            itemEntities = itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes());

            if (itemEntities != null && !itemEntities.isEmpty()) {
                itemEntity = itemEntities.get(0);
                CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCode(itemRequestInfo.getDeliveryLocation());
                if (StringUtils.isBlank(itemRequestInfo.getBibId())) {
                    itemRequestInfo.setBibId(itemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId());
                }
                itemRequestInfo.setItemOwningInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
                SearchResultRow searchResultRow = searchRecords(itemEntity); //Solr

                itemRequestInfo.setTitleIdentifier(getTitle(itemRequestInfo.getTitleIdentifier(), itemEntity, searchResultRow));
                itemRequestInfo.setAuthor(searchResultRow.getAuthor());
                itemRequestInfo.setCustomerCode(itemEntity.getCustomerCode());
                itemRequestInfo.setPickupLocation(customerCodeEntity.getPickupLocation());
                itemResponseInformation.setItemId(itemEntity.getItemId());

                boolean isItemStatusAvailable;
                synchronized (this) {
                    // Change Item Availablity
                    isItemStatusAvailable = updateItemAvailabilutyStatus(itemEntities, itemRequestInfo.getUsername());
                }

                Integer requestId = updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PROCESSING);
                itemRequestInfo.setRequestId(requestId);
                itemResponseInformation.setRequestId(requestId);

                if (requestId == 0) {
                    rollbackUpdateItemAvailabilutyStatus(itemEntity, itemRequestInfo.getUsername());
                    itemResponseInformation.setScreenMessage(RecapCommonConstants.REQUEST_EXCEPTION + RecapConstants.INTERNAL_ERROR_DURING_REQUEST);
                    itemResponseInformation.setSuccess(false);
                } else if (!isItemStatusAvailable) {
                    itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.RETRIEVAL_NOT_FOR_UNAVAILABLE_ITEM);
                    itemResponseInformation.setSuccess(false);
                } else {
                    // Process
                    itemResponseInformation = checkOwningInstitution(itemRequestInfo, itemResponseInformation, itemEntity);
                }
            } else {
                itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.WRONG_ITEM_BARCODE);
                itemResponseInformation.setSuccess(false);
            }
            itemResponseInformation = setItemResponseInformation(itemRequestInfo, itemResponseInformation);

            if (isUseQueueLasCall() && (StringUtils.containsIgnoreCase(itemResponseInformation.getScreenMessage(), RecapConstants.REQUEST_ILS_EXCEPTION)
                    || StringUtils.containsIgnoreCase(itemResponseInformation.getScreenMessage(), RecapConstants.REQUEST_SCSB_EXCEPTION)
                    || StringUtils.containsIgnoreCase(itemResponseInformation.getScreenMessage(), RecapConstants.REQUEST_LAS_EXCEPTION))) {
                updateChangesToDb(itemResponseInformation, RecapConstants.REQUEST_RETRIEVAL + "-" + itemResponseInformation.getRequestingInstitution());
            }
            // Update Topics
            sendMessageToTopic(itemRequestInfo.getRequestingInstitution(), itemRequestInfo.getRequestType(), itemResponseInformation, exchange);
            logger.info(RecapConstants.FINISH_PROCESSING);
        } catch (RestClientException ex) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION_REST, ex);
        } catch (Exception ex) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, ex);
        }
        return itemResponseInformation;
    }

    /**
     * Update request status based on success message from ILS
     *
     * @param itemResponseInformation
     * @param operationType
     */
    public void updateChangesToDb(ItemInformationResponse itemResponseInformation, String operationType) {
        if (itemResponseInformation.getRequestId() != null && itemResponseInformation.getRequestId() > 0) {
            saveItemChangeLogEntity(itemResponseInformation.getRequestId(), getUser(itemResponseInformation.getUsername()), operationType, itemResponseInformation.getRequestNotes());
            updateRecapRequestItem(itemResponseInformation);
        }
    }

    /**
     * Recall item item information response.
     *
     * @param itemRequestInfo the item request info
     * @param exchange        the exchange
     * @return the item information response
     */
    public ItemInformationResponse recallItem(ItemRequestInformation itemRequestInfo, Exchange exchange) {

        List<ItemEntity> itemEntities;
        ItemEntity itemEntity;
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        try {
            itemEntities = itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes());
            RequestItemEntity requestItemEntity = requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemRequestInfo.getItemBarcodes().get(0), RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
            if (requestItemEntity != null) {
                if (itemEntities != null && !itemEntities.isEmpty()) {
                    itemEntity = itemEntities.get(0);
                    SearchResultRow searchResultRow = searchRecords(itemEntity); //Solr

                    itemRequestInfo.setTitleIdentifier(getTitle(itemRequestInfo.getTitleIdentifier(), itemEntity, searchResultRow));
                    itemRequestInfo.setAuthor(searchResultRow.getAuthor());
                    itemRequestInfo.setBibId(itemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId());
                    itemRequestInfo.setItemOwningInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
                    itemRequestInfo.setPickupLocation(getPickupLocation(itemRequestInfo.getDeliveryLocation()));
                    itemResponseInformation.setItemId(itemEntity.getItemId());
                    Integer requestId = updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PROCESSING);
                    itemRequestInfo.setRequestId(requestId);
                    itemResponseInformation.setRequestId(requestId);

                    if (requestId == 0) {
                        itemResponseInformation.setScreenMessage(RecapCommonConstants.REQUEST_EXCEPTION + RecapConstants.INTERNAL_ERROR_DURING_REQUEST);
                        itemResponseInformation.setSuccess(false);
                    } else {
                        itemResponseInformation = checkOwningInstitutionRecall(itemRequestInfo, itemResponseInformation, itemEntity);
                    }
                } else {
                    itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.WRONG_ITEM_BARCODE);
                    itemResponseInformation.setSuccess(false);
                }
            } else {
                itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.CANNOT_REFILE_FIRST_SCAN_REQUEST);
                itemResponseInformation.setSuccess(false);
            }
            logger.info(RecapConstants.FINISH_PROCESSING);
            itemResponseInformation = setItemResponseInformation(itemRequestInfo, itemResponseInformation);

            if (isUseQueueLasCall()) {
                updateChangesToDb(itemResponseInformation, RecapConstants.REQUEST_RECALL + "-" + itemResponseInformation.getRequestingInstitution());
            }
            // Update Topics
            sendMessageToTopic(itemRequestInfo.getItemOwningInstitution(), itemRequestInfo.getRequestType(), itemResponseInformation, exchange);
        } catch (RestClientException ex) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION_REST, ex);
        } catch (Exception ex) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, ex);
        }
        return itemResponseInformation;
    }

    /**
     * Re file item boolean.
     *
     * @param itemRefileRequest the item refile request
     * @return the boolean
     */
    public ItemRefileResponse reFileItem(ItemRefileRequest itemRefileRequest, ItemRefileResponse itemRefileResponse) {

        logger.info("Processing received Refile request");
        logger.info("Refile Request Information : Barcodes {} , Request Id's : {}", itemRefileRequest.getItemBarcodes(), itemRefileRequest.getRequestIds());
        // Change Response for this Method
        String itemBarcode;
        ItemEntity itemEntity;
        List<String> requestItemStatusList = Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, RecapCommonConstants.REQUEST_STATUS_EDD, RecapCommonConstants.REQUEST_STATUS_CANCELED, RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD);
        List<RequestItemEntity> requestEntities = requestItemDetailsRepository.findByIdsAndStatusCodes(itemRefileRequest.getRequestIds(), requestItemStatusList);
        List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByItemBarcodes(itemRefileRequest.getItemBarcodes());
        if (requestEntities != null && !requestEntities.isEmpty()) {
            for (RequestItemEntity requestItemEntity : requestEntities) {
                itemEntity = requestItemEntity.getItemEntity();
                RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_REFILED);
                String gfaItemStatus = gfaService.callGfaItemStatus(itemEntity.getBarcode());
                logger.info("GFA Item Status {} for the barcode {} received on Refile", gfaItemStatus, itemEntity.getBarcode());
                if (itemEntity.getItemAvailabilityStatusId() == 2) { // Only Item Not Availability, Status is Processed
                    itemBarcode = itemEntity.getBarcode();
                    ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
                    itemRequestInfo.setItemBarcodes(Collections.singletonList(itemBarcode));
                    itemRequestInfo.setItemOwningInstitution(requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
                    itemRequestInfo.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
                    itemRequestInfo.setRequestType(requestItemEntity.getRequestTypeEntity().getRequestTypeCode());
                    RequestItemEntity requestItemEntityRecalled = requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemBarcode, RecapCommonConstants.REQUEST_STATUS_RECALLED);
                    if (requestItemEntityRecalled == null) { // Recall Request Does not Exist
                        requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                        requestItemEntity.setLastUpdatedDate(new Date());
                        requestItemDetailsRepository.save(requestItemEntity);
                        rollbackUpdateItemAvailabilutyStatus(itemEntity, RecapConstants.GUEST_USER);
                        itemRequestServiceUtil.updateSolrIndex(itemEntity);
                        itemRefileResponse.setSuccess(true);
                    } else { // Recall Request Exist
                        if (requestItemEntityRecalled.getRequestingInstitutionId().intValue() == requestItemEntity.getRequestingInstitutionId().intValue()) { // Borrowed Inst same as Recall Requesting Inst
                            requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                            requestItemEntity.setLastUpdatedDate(new Date());
                            requestItemEntityRecalled.setRequestStatusId(requestStatusEntity.getId());
                            requestItemEntityRecalled.setLastUpdatedDate(new Date());
                            requestItemDetailsRepository.save(requestItemEntity);
                            requestItemDetailsRepository.save(requestItemEntityRecalled);
                            rollbackUpdateItemAvailabilutyStatus(requestItemEntity.getItemEntity(), RecapConstants.GUEST_USER);
                            itemRequestServiceUtil.updateSolrIndex(requestItemEntity.getItemEntity());
                            itemRefileResponse.setSuccess(true);
                            itemRefileResponse.setScreenMessage("Successfully Refiled");
                        } else { // Borrowed Inst not same as Recall Requesting Inst, Change Retrieval Order Status to Refiled.
                            requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                            requestItemDetailsRepository.save(requestItemEntity);
                            // Checkout the item based on the institution Princeton,Columbia or NYPL for the Recall order
                            if(itemRequestInfo.getRequestType().equalsIgnoreCase(RecapConstants.EDD_REQUEST)) {
                                //Checkout for EDD patron
                                itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getRequestingInstitution() ,itemRequestInfo.getItemOwningInstitution(), RecapCommonConstants.REQUEST_TYPE_EDD));
                                requestItemController.checkoutItem(itemRequestInfo, itemRequestInfo.getItemOwningInstitution());
                            }else {
                                itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getRequestingInstitution(), itemRequestInfo.getItemOwningInstitution(), RecapCommonConstants.REQUEST_TYPE_RETRIEVAL));
                                requestItemController.checkoutItem(itemRequestInfo, itemRequestInfo.getItemOwningInstitution());
                            }
                            setItemRequestInfoForRequest(itemEntity, itemRequestInfo, requestItemEntityRecalled);
                            ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
                            // Put back the Recall order to LAS. On success from LAS, recall order is updated to retrieval.
                            updateScsbAndGfa(itemRequestInfo, itemInformationResponse, itemEntity);
                            itemRefileResponse.setSuccess(true);
                        }
                    }
                    logger.info("Refile Request Id = {} Refile Barcode = {}", requestItemEntity.getId(), itemBarcode);
                    if (itemRequestInfo.getRequestingInstitution().equalsIgnoreCase(RecapCommonConstants.PRINCETON) || itemRequestInfo.getRequestingInstitution().equalsIgnoreCase(RecapCommonConstants.COLUMBIA)) {
                        //TODO - Check if EDD and change Patron accordingly to checkIn in RequestingInstitution
                        if(itemRequestInfo.getRequestType().equalsIgnoreCase(RecapConstants.EDD_REQUEST)){
                            if(itemRequestInfo.isOwningInstitutionItem()) {
                                itemRequestInfo.setPatronBarcode(itemEDDRequestService.getPatronIdForOwningInstitutionOnEdd(itemRequestInfo.getItemOwningInstitution()));
                            }else {
                                //Interchanging the arguments since the checkin call is made based on Requesting Institution.
                                itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getItemOwningInstitution(),itemRequestInfo.getRequestingInstitution(), RecapCommonConstants.REQUEST_TYPE_EDD));
                            }
                        }
                        else {
                            itemRequestInfo.setPatronBarcode(requestItemEntity.getPatronId());
                        }
                        requestItemController.checkinItem(itemRequestInfo, itemRequestInfo.getRequestingInstitution());
                    } else if (itemRequestInfo.getRequestingInstitution().equalsIgnoreCase(RecapCommonConstants.NYPL)) {
                        //TODO - Check if EDD and change Patron accordingly to checkIn in RequestingInstitution
                        if(itemRequestInfo.getRequestType().equalsIgnoreCase(RecapConstants.EDD_REQUEST)){
                            if(itemRequestInfo.isOwningInstitutionItem()) {
                                itemRequestInfo.setPatronBarcode(itemEDDRequestService.getPatronIdForOwningInstitutionOnEdd(itemRequestInfo.getItemOwningInstitution()));
                            }else {
                                //Interchanging the arguments since the checkin call is made based on Requesting Institution
                                itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getItemOwningInstitution(),itemRequestInfo.getRequestingInstitution(), RecapCommonConstants.REQUEST_TYPE_EDD));
                            }
                        }
                        requestItemController.getJsipConectorFactory().getJSIPConnector(itemRequestInfo.getRequestingInstitution()).refileItem(itemBarcode);
                    }
                    if (!itemRequestInfo.isOwningInstitutionItem()) {
                        //TODO - Check if EDD and change Patron accordingly to checkIn in ItemOwningInstitution
                        if(itemRequestInfo.getRequestType().equalsIgnoreCase(RecapConstants.EDD_REQUEST)){
                            itemRequestInfo.setPatronBarcode(getPatronIDForEDDBorrowingInstitution(itemRequestInfo.getRequestingInstitution(),itemRequestInfo.getItemOwningInstitution()));
                        }
                        else {
                            itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getRequestingInstitution(), itemRequestInfo.getItemOwningInstitution(), RecapCommonConstants.REQUEST_TYPE_RETRIEVAL));
                        }
                        requestItemController.checkinItem(itemRequestInfo, itemRequestInfo.getItemOwningInstitution());
                    }
                }
                itemRefileResponse.setScreenMessage("Cannot refile a already available Item");
            }
        }
        else {
            for (RequestItemEntity requestItemEntity : requestItemEntities) {
                if (requestItemEntity.getRequestStatusEntity().getRequestStatusCode().equalsIgnoreCase(RecapConstants.LAS_REFILE_REQUEST_PLACED)) {
                    ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
                    itemEntity = requestItemEntity.getItemEntity();
                    itemBarcode = itemEntity.getBarcode();
                    String gfaItemStatus = gfaService.callGfaItemStatus(itemBarcode);
                    logger.info("Gfa status received during refile : {}",gfaItemStatus);
                    logger.info("GFA Item Status {} for the barcode {} received on Refile where Request Id : {}", gfaItemStatus, itemEntity.getBarcode(),requestItemEntity.getId());
                    logger.info("Rejecting the Refile for the barcode {} where Request ID : {} and Request Status : {}", itemEntity.getBarcode(), requestItemEntity.getId(), requestItemEntity.getRequestStatusEntity().getRequestStatusCode());
                    if (gfaItemStatus.contains(":")) {
                        gfaItemStatus = gfaItemStatus.substring(0, gfaItemStatus.indexOf(':') + 1).toUpperCase();
                    } else {
                        gfaItemStatus = gfaItemStatus.toUpperCase();
                    }
                    logger.info("Gfa status After modifying : {}",gfaItemStatus);
                    logger.info("Condition satified {}", RecapConstants.getGFAStatusAvailableList().contains(gfaItemStatus));
                    if(RecapConstants.getGFAStatusAvailableList().contains(gfaItemStatus)) {
                        itemRequestInfo.setItemBarcodes(Collections.singletonList(itemBarcode));
                        itemRequestInfo.setItemOwningInstitution(requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
                        itemRequestInfo.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
                        itemRequestInfo.setPatronBarcode(requestItemEntity.getPatronId());
                        setItemRequestInfoForRequest(itemEntity, itemRequestInfo, requestItemEntity);
                        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
                        updateScsbAndGfa(itemRequestInfo, itemInformationResponse, itemEntity);
                        logger.info("Successfully placed the request to Queue");
                        requestItemDetailsRepository.save(requestItemEntity);
                        itemRefileResponse.setSuccess(false);
                        itemRefileResponse.setScreenMessage("Refile ignored as request is placed after first scan and successfully placed request to queue");
                    }
                    else {
                        logger.info("Cannot Refile. LAS status is Not Available");
                    }
                }
            }
            itemRefileResponse.setScreenMessage("Cannot Refile.Please check the provided barcode(s) and requestId(s)");
        }
        return itemRefileResponse;
    }

    private void setItemRequestInfoForRequest(ItemEntity itemEntity, ItemRequestInformation itemRequestInfo, RequestItemEntity requestItemEntity) {
        if(requestItemEntity.getRequestTypeEntity().getRequestTypeCode().equalsIgnoreCase(RecapConstants.EDD_REQUEST)){
            ItemEntity entityItemEntity = requestItemEntity.getItemEntity();
            String notes = requestItemEntity.getNotes();
            String[] eddInformation = notes.split("\n");
            HashMap<String,String> eddNotesMap=new HashMap<String, String>();
            for (String eddInfo : eddInformation) {
                String[] eddInfoInPairs = eddInfo.split(":",2);
                if(!(eddInfoInPairs[0].isEmpty()))
                eddNotesMap.put(eddInfoInPairs[0],eddInfoInPairs[1]);
            }
            if (itemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId().trim().length() <= 0) {
                itemRequestInfo.setBibId(itemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId());
            }
            SearchResultRow searchResultRow = searchRecords(itemEntity);
            itemRequestInfo.setTitleIdentifier(removeDiacritical(searchResultRow.getTitle().replaceAll("[^\\x00-\\x7F]", "?")));
            itemRequestInfo.setItemAuthor(removeDiacritical(searchResultRow.getAuthor()));
            itemRequestInfo.setEmailAddress(securityUtil.getDecryptedValue(requestItemEntity.getEmailId()));
            itemRequestInfo.setRequestType(RecapConstants.EDD_REQUEST);
            if(itemRequestInfo.isOwningInstitutionItem()) {
                itemRequestInfo.setPatronBarcode(itemEDDRequestService.getPatronIdForOwningInstitutionOnEdd(itemRequestInfo.getItemOwningInstitution()));
            }
            else {
                itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getRequestingInstitution(), itemRequestInfo.getItemOwningInstitution(), RecapCommonConstants.REQUEST_TYPE_EDD));
            }
            setEddInformation(itemRequestInfo, eddNotesMap);
        }
        else {
            itemRequestInfo.setRequestType(RecapCommonConstants.RETRIEVAL);
        }
        itemRequestInfo.setRequestNotes(requestItemEntity.getNotes());
        itemRequestInfo.setRequestId(requestItemEntity.getId());
        itemRequestInfo.setUsername(requestItemEntity.getCreatedBy());
        itemRequestInfo.setDeliveryLocation(requestItemEntity.getStopCode());
        itemRequestInfo.setCustomerCode(itemEntity.getCustomerCode());
    }

    private void setEddInformation(ItemRequestInformation itemRequestInfo, HashMap<String, String> eddNotesMap) {
        for (Map.Entry<String, String> eddNotes : eddNotesMap.entrySet()) {
         if(eddNotes.getKey().contains("Start Page")){
             itemRequestInfo.setStartPage(eddNotes.getValue());
            }
         if(eddNotes.getKey().contains("End Page")){
                itemRequestInfo.setEndPage(eddNotes.getValue());
            }
         if(eddNotes.getKey().contains("Chapter")){
             itemRequestInfo.setChapterTitle(eddNotes.getValue());
         }
            if(eddNotes.getKey().contains("Article Author")){
                itemRequestInfo.setAuthor(eddNotes.getValue());
            }
            if(eddNotes.getKey().contains("Volume Number")){
                itemRequestInfo.setVolume(eddNotes.getValue());
            }
            if(eddNotes.getKey().contains("Article/Chapter Title")){
                itemRequestInfo.setChapterTitle(eddNotes.getValue());
            }
            if(eddNotes.getKey().contains("Issue")){
                itemRequestInfo.setIssue(eddNotes.getValue());
            }
            if(eddNotes.getKey().contains("User")){
                itemRequestInfo.setEddNotes(eddNotes.getValue());
            }
        }
    }

    /**
     * Send message to topic.
     *
     * @param owningInstituteId the owning institute id
     * @param requestType       the request type
     * @param itemResponseInfo  the item response info
     * @param exchange          the exchange
     */
    public void sendMessageToTopic(String owningInstituteId, String requestType, ItemInformationResponse itemResponseInfo, Exchange exchange) {
        String selectTopic = RecapConstants.PUL_REQUEST_TOPIC;
        if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.PRINCETON) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)) {
            selectTopic = RecapConstants.PUL_REQUEST_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.PRINCETON) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)) {
            selectTopic = RecapConstants.PUL_EDD_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.PRINCETON) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RECALL)) {
            selectTopic = RecapConstants.PUL_RECALL_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.PRINCETON) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_BORROW_DIRECT)) {
            selectTopic = RecapConstants.PUL_BORROW_DIRECT_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.COLUMBIA) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)) {
            selectTopic = RecapConstants.CUL_REQUEST_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.COLUMBIA) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)) {
            selectTopic = RecapConstants.CUL_EDD_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.COLUMBIA) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RECALL)) {
            selectTopic = RecapConstants.CUL_RECALL_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.COLUMBIA) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_BORROW_DIRECT)) {
            selectTopic = RecapConstants.CUL_BORROW_DIRECT_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.NYPL) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)) {
            selectTopic = RecapConstants.NYPL_REQUEST_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.NYPL) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)) {
            selectTopic = RecapConstants.NYPL_EDD_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.NYPL) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RECALL)) {
            selectTopic = RecapConstants.NYPL_RECALL_TOPIC;
        } else if (owningInstituteId.equalsIgnoreCase(RecapCommonConstants.NYPL) && requestType.equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_BORROW_DIRECT)) {
            selectTopic = RecapConstants.NYPL_BORROW_DIRECT_TOPIC;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(itemResponseInfo);
            logger.info("Topic logs : " + json);
        } catch (JsonProcessingException e) {
            logger.error(RecapConstants.REQUEST_PARSE_EXCEPTION, e);
        }
        FluentProducerTemplate fluentProducerTemplate = DefaultFluentProducerTemplate.on(exchange.getContext());
        fluentProducerTemplate
                .to(selectTopic)
                .withBody(json);
        fluentProducerTemplate.send();
    }

    private ItemInformationResponse setItemResponseInformation(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInfo) {
        ItemInformationResponse itemResponseInformation = itemResponseInfo;
        itemResponseInformation.setRequestId(itemRequestInfo.getRequestId());
        itemResponseInformation.setDueDate(itemRequestInfo.getExpirationDate());
        itemResponseInformation.setBibID(itemRequestInfo.getBibId());
        itemResponseInformation.setItemOwningInstitution(itemRequestInfo.getItemOwningInstitution());
        itemResponseInformation.setRequestingInstitution(itemRequestInfo.getRequestingInstitution());
        itemResponseInformation.setPatronBarcode(itemRequestInfo.getPatronBarcode());
        itemResponseInformation.setRequestType(itemRequestInfo.getRequestType());
        itemResponseInformation.setEmailAddress(itemRequestInfo.getEmailAddress());
        itemResponseInformation.setDeliveryLocation(itemRequestInfo.getDeliveryLocation());
        itemResponseInformation.setRequestNotes(getNotes(itemResponseInformation.isSuccess(), itemResponseInformation.getScreenMessage(), itemRequestInfo.getRequestNotes()));
        itemResponseInformation.setItemBarcode(itemRequestInfo.getItemBarcodes().get(0));
        itemResponseInformation.setTitleIdentifier(itemRequestInfo.getTitleIdentifier());
        itemResponseInformation.setExpirationDate(itemRequestInfo.getExpirationDate());
        itemResponseInformation.setUsername(itemRequestInfo.getUsername());
        return itemResponseInformation;
    }

    /**
     * Update recap request item integer.
     *
     * @param itemRequestInformation the item request information
     * @param itemEntity             the item entity
     * @param requestStatusCode      the request status code
     * @return the integer
     */
    public Integer updateRecapRequestItem(ItemRequestInformation itemRequestInformation, ItemEntity itemEntity, String requestStatusCode) {
        return itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, requestStatusCode, null);
    }

    /**
     * Update recap request item item information response.
     *
     * @param itemInformationResponse the item information response
     * @return the item information response
     */
    public ItemInformationResponse updateRecapRequestItem(ItemInformationResponse itemInformationResponse) {
        return itemRequestDBService.updateRecapRequestItem(itemInformationResponse);
    }

    /**
     * Update recap request status item information response.
     *
     * @param itemInformationResponse the item information response
     * @return the item information response
     */
    public ItemInformationResponse updateRecapRequestStatus(ItemInformationResponse itemInformationResponse) {
        return itemRequestDBService.updateRecapRequestStatus(itemInformationResponse);
    }

    public boolean updateItemAvailabilutyStatus(List<ItemEntity> itemEntities, String username) {
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.NOT_AVAILABLE);
        for (ItemEntity itemEntity : itemEntities) {
            ItemEntity itemEntityByItemId = itemDetailsRepository.findByItemId(itemEntity.getItemId());
            logger.info("Item status : " + itemEntityByItemId.getItemStatusEntity().getStatusCode());
            if (itemStatusEntity.getId() == itemEntityByItemId.getItemAvailabilityStatusId()) {  //Condition should be checked with equals not == ?
                return false;
            }
        }
        itemRequestDBService.updateItemAvailabilutyStatus(itemEntities, username);
        return true;
    }

    public void rollbackUpdateItemAvailabilutyStatus(ItemEntity itemEntity, String username) {
        commonUtil.rollbackUpdateItemAvailabilutyStatus(itemEntity, username);
    }

    /**
     * Save item change log entity.
     *
     * @param recordId      the record id
     * @param userName      the user name
     * @param operationType the operation type
     * @param notes         the notes
     */
    public void saveItemChangeLogEntity(Integer recordId, String userName, String operationType, String notes) {
        commonUtil.saveItemChangeLogEntity(recordId, userName, operationType, notes);
    }

    /**
     * Gets user.
     *
     * @param userId the user id
     * @return the user
     */
    public String getUser(String userId) {
        return commonUtil.getUser(userId);
    }

    /**
     * Update gfa item information response.
     *
     * @param itemRequestInfo         the item request info
     * @param itemResponseInformation the item response information
     * @return the item information response
     */
    protected ItemInformationResponse updateGFA(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {

        try {
            if (itemRequestInfo.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL) || itemRequestInfo.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)) {
                itemResponseInformation = gfaService.executeRetriveOrder(itemRequestInfo, itemResponseInformation);
            } else {
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.RETRIVAL_ORDER_NOT_REQUIRED_FOR_RECALL);
            }
        } catch (Exception e) {
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + e.getMessage());
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse checkOwningInstitution(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation, ItemEntity itemEntity) {
        try {
            if (itemRequestInfo.isOwningInstitutionItem()) {
                itemResponseInformation = holdItem(itemRequestInfo.getItemOwningInstitution(), itemRequestInfo, itemResponseInformation, itemEntity);
            } else {// Not the Owning Institute
                // Get Temporary bibId from SCSB DB
                ItemCreateBibResponse createBibResponse;
                if (!RecapCommonConstants.NYPL.equalsIgnoreCase(itemRequestInfo.getRequestingInstitution())) {
                    createBibResponse = (ItemCreateBibResponse) requestItemController.createBibliogrphicItem(itemRequestInfo, itemRequestInfo.getRequestingInstitution());
                } else {
                    createBibResponse = new ItemCreateBibResponse();
                    createBibResponse.setSuccess(true);
                }
                if (createBibResponse.isSuccess()) {
                    itemRequestInfo.setBibId(createBibResponse.getBibId());
                    itemResponseInformation = holdItem(itemRequestInfo.getRequestingInstitution(), itemRequestInfo, itemResponseInformation, itemEntity);
                } else {
                    itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_ILS_EXCEPTION + RecapConstants.CREATING_A_BIB_RECORD_FAILED_IN_ILS);
                    itemResponseInformation.setSuccess(createBibResponse.isSuccess());
                    rollbackUpdateItemAvailabilutyStatus(itemEntity, itemRequestInfo.getUsername());
                    saveItemChangeLogEntity(itemEntity.getItemId(), getUser(itemRequestInfo.getUsername()), RecapConstants.REQUEST_ITEM_HOLD_FAILURE, createBibResponse.getBibId() + " - " + createBibResponse.getScreenMessage());
                }
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + e.getMessage());
            itemResponseInformation.setSuccess(false);
            saveItemChangeLogEntity(itemEntity.getItemId(), getUser(itemRequestInfo.getUsername()), RecapConstants.REQUEST_ITEM_ITEM_CHANGE_LOG_EXCEPTION, itemRequestInfo.getItemBarcodes() + " - " + e.getMessage());
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse checkInstAfterPlacingRequest(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation, ItemEntity itemEntity) {
        if (itemRequestInfo.isOwningInstitutionItem()) {
            itemResponseInformation = updateScsbAndGfa(itemRequestInfo, itemResponseInformation, itemEntity);
        } else { // Item does not belong to requesting Institute
            itemResponseInformation = updateScsbAndGfa(itemRequestInfo, itemResponseInformation, itemEntity);
            logger.info("GFA Response for Retrieval request : {}",itemResponseInformation.isSuccess());
            if(itemResponseInformation.isSuccess()){
                itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getRequestingInstitution(), itemRequestInfo.getItemOwningInstitution(), RecapCommonConstants.REQUEST_TYPE_RETRIEVAL));
                logger.info("Performing CheckOut using the generic patron : {}",itemRequestInfo.getPatronBarcode());
                requestItemController.checkoutItem(itemRequestInfo, itemRequestInfo.getItemOwningInstitution());
            }
        }
        if (itemResponseInformation.isSuccess()) {
            itemRequestServiceUtil.updateSolrIndex(itemEntity);
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse holdItem(String callingInst, ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation, ItemEntity itemEntity) {
        ItemHoldResponse itemHoldResponse = (ItemHoldResponse) requestItemController.holdItem(itemRequestInfo, callingInst);
        if (itemHoldResponse.isSuccess()) { // IF Hold command is successfully
            itemResponseInformation.setExpirationDate(itemHoldResponse.getExpirationDate());
            itemRequestInfo.setExpirationDate(itemHoldResponse.getExpirationDate());
            itemResponseInformation = checkInstAfterPlacingRequest(itemRequestInfo, itemResponseInformation, itemEntity);
        } else { // If Hold command Failure
            itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_ILS_EXCEPTION + itemHoldResponse.getScreenMessage());
            itemResponseInformation.setSuccess(itemHoldResponse.isSuccess());
            rollbackUpdateItemAvailabilutyStatus(itemEntity, itemRequestInfo.getUsername());
            saveItemChangeLogEntity(itemEntity.getItemId(), getUser(itemRequestInfo.getUsername()), RecapConstants.REQUEST_ITEM_HOLD_FAILURE, itemHoldResponse.getPatronIdentifier() + " - " + itemHoldResponse.getScreenMessage());
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse updateScsbAndGfa(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation, ItemEntity itemEntity) {
        Integer requestId = 0;
        if (gfaService.isUseQueueLasCall()) {
            requestId = updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PENDING);
        }
        itemResponseInformation.setRequestId(requestId);
        itemResponseInformation = updateGFA(itemRequestInfo, itemResponseInformation);
        if(itemResponseInformation.isRequestTypeForScheduledOnWO()){
            logger.info("Request Received on first scan");
            requestId = updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.LAS_REFILE_REQUEST_PLACED);
            logger.info("Updated the request id {} on first scan",requestId);
        }
        if (itemResponseInformation.isSuccess()) {
            itemResponseInformation.setScreenMessage(RecapConstants.SUCCESSFULLY_PROCESSED_REQUEST_ITEM);
        } else {
            rollbackAfterGFA(itemEntity, itemRequestInfo, itemResponseInformation);
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse checkOwningInstitutionRecall(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation, ItemEntity itemEntity) {
        String messagePublish;
        boolean bsuccess;
        RequestItemEntity requestItemEntity = requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(itemRequestInfo.getItemBarcodes().get(0), RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
        logger.info("Owning     Inst = " + requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
        logger.info("Borrowed   Inst = " + requestItemEntity.getInstitutionEntity().getInstitutionCode());
        logger.info("Requesting Inst = " + itemRequestInfo.getRequestingInstitution());
        ItemInformationResponse itemInformation = (ItemInformationResponse) requestItemController.itemInformation(itemRequestInfo, requestItemEntity.getInstitutionEntity().getInstitutionCode());
        if (itemInformation.getCirculationStatus().equalsIgnoreCase(RecapConstants.CIRCULATION_STATUS_CHARGED)
                || itemInformation.getCirculationStatus().equalsIgnoreCase(RecapConstants.CIRCULATION_STATUS_ON_HOLDSHELF)
                || itemInformation.getCirculationStatus().equalsIgnoreCase(RecapConstants.CIRCULATION_STATUS_IN_TRANSIT_NYPL)) {
            if (requestItemEntity.getInstitutionEntity().getInstitutionCode().equalsIgnoreCase(itemRequestInfo.getRequestingInstitution())) {
                ItemRecallResponse itemRecallResponse = (ItemRecallResponse) requestItemController.recallItem(itemRequestInfo, requestItemEntity.getInstitutionEntity().getInstitutionCode());
                if (itemRecallResponse.isSuccess()) {
                    // Update Recap DB
                    itemRequestInfo.setExpirationDate(itemRecallResponse.getExpirationDate());
                    sendEmail(requestItemEntity.getItemEntity().getCustomerCode(), itemRequestInfo.getItemBarcodes().get(0), itemRequestInfo.getPatronBarcode(), requestItemEntity.getInstitutionEntity().getInstitutionCode());
                    messagePublish = RecapConstants.SUCCESSFULLY_PROCESSED_REQUEST_ITEM;
                    bsuccess = true;
                } else {
                    messagePublish = recallError(itemRecallResponse);
                    bsuccess = false;
                }
            } else {
                itemResponseInformation = createBibAndHold(itemRequestInfo, itemResponseInformation, itemEntity);
                if (itemResponseInformation.isSuccess()) { // IF Hold command is successfully
                    itemRequestInfo.setExpirationDate(itemRequestInfo.getExpirationDate());
                    String requestingPatron = itemRequestInfo.getPatronBarcode();
                    itemRequestInfo.setPatronBarcode(itemRequestServiceUtil.getPatronIdBorrowingInstitution(itemRequestInfo.getRequestingInstitution(), requestItemEntity.getInstitutionEntity().getInstitutionCode(), RecapCommonConstants.REQUEST_TYPE_RETRIEVAL));
                    itemRequestInfo.setPickupLocation(getPickupLocation(requestItemEntity.getStopCode()));
                    itemRequestInfo.setBibId(itemInformation.getBibID());
                    ItemRecallResponse itemRecallResponse = (ItemRecallResponse) requestItemController.recallItem(itemRequestInfo, requestItemEntity.getInstitutionEntity().getInstitutionCode());
                    itemRequestInfo.setPatronBarcode(requestingPatron);
                    if (itemRecallResponse.isSuccess()) {
                        sendEmail(requestItemEntity.getItemEntity().getCustomerCode(), itemRequestInfo.getItemBarcodes().get(0), itemRequestInfo.getPatronBarcode(), requestItemEntity.getInstitutionEntity().getInstitutionCode());
                        messagePublish = RecapConstants.SUCCESSFULLY_PROCESSED_REQUEST_ITEM;
                        bsuccess = true;
                    } else {
                        messagePublish = recallError(itemRecallResponse);
                        bsuccess = false;
                        requestItemController.cancelHoldItem(itemRequestInfo, itemRequestInfo.getRequestingInstitution());
                        saveItemChangeLogEntity(itemEntity.getItemId(), getUser(itemRequestInfo.getUsername()), RecapConstants.REQUEST_ITEM_HOLD_FAILURE, itemRequestInfo.getPatronBarcode() + " - " + itemResponseInformation.getScreenMessage());
                    }
                } else { // If Hold command Failure
                    messagePublish = itemResponseInformation.getScreenMessage();
                    bsuccess = false;
                }
            }
        } else {
            messagePublish = RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.RECALL_CANNOT_BE_PROCESSED_THE_ITEM_IS_NOT_CHECKED_OUT_IN_ILS;
            bsuccess = false;
        }
        itemResponseInformation.setScreenMessage(messagePublish);
        itemResponseInformation.setSuccess(bsuccess);
        return itemResponseInformation;
    }

    private String recallError(ItemRecallResponse itemRecallResponse) {
        if (itemRecallResponse.getScreenMessage() != null && itemRecallResponse.getScreenMessage().trim().length() > 0) {
            return RecapConstants.REQUEST_SCSB_EXCEPTION + itemRecallResponse.getScreenMessage();
        } else {
            return RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.RECALL_FAILED_NO_MESSAGE_RETURNED;
        }
    }

    private String getPatronIDForEDDBorrowingInstitution(String requestingInstitution, String owningInstitution) {
        String patronId = "";
        if (owningInstitution.equalsIgnoreCase(RecapCommonConstants.PRINCETON)) {
            if (requestingInstitution.equalsIgnoreCase(RecapCommonConstants.COLUMBIA)) {
                patronId = princetonCULEDDPatron;
            } else if (requestingInstitution.equalsIgnoreCase(RecapCommonConstants.NYPL)) {
                patronId = princetonNYPLEDDPatron;
            }
        } else if (owningInstitution.equalsIgnoreCase(RecapCommonConstants.COLUMBIA)) {
            if (requestingInstitution.equalsIgnoreCase(RecapCommonConstants.PRINCETON)) {
                patronId = columbiaPULEDDPatron;
            } else if (requestingInstitution.equalsIgnoreCase(RecapCommonConstants.NYPL)) {
                patronId = columbiaNYPLEDDPatron;
            }
        } else if (owningInstitution.equalsIgnoreCase(RecapCommonConstants.NYPL)) {
            if (requestingInstitution.equalsIgnoreCase(RecapCommonConstants.PRINCETON)) {
                patronId = nyplPrincetonEDDPatron;
            } else if (requestingInstitution.equalsIgnoreCase(RecapCommonConstants.COLUMBIA)) {
                patronId = nyplColumbiaEDDPatron;
            }
        }
        logger.info(patronId);
        return patronId;
    }

    private ItemInformationResponse createBibAndHold(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation, ItemEntity itemEntity) {
        ItemCreateBibResponse createBibResponse;
        if (!RecapCommonConstants.NYPL.equalsIgnoreCase(itemRequestInfo.getRequestingInstitution())) {
            createBibResponse = (ItemCreateBibResponse) requestItemController.createBibliogrphicItem(itemRequestInfo, itemRequestInfo.getRequestingInstitution());
        } else {
            createBibResponse = new ItemCreateBibResponse();
            createBibResponse.setSuccess(true);
        }
        if (createBibResponse.isSuccess()) {
            itemRequestInfo.setBibId(createBibResponse.getBibId());
            ItemHoldResponse itemHoldResponse = (ItemHoldResponse) requestItemController.holdItem(itemRequestInfo, itemRequestInfo.getRequestingInstitution());
            itemResponseInformation.setScreenMessage(itemHoldResponse.getScreenMessage());
            itemResponseInformation.setSuccess(itemHoldResponse.isSuccess());
        } else {
            itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_ILS_EXCEPTION + RecapConstants.CREATING_A_BIB_RECORD_FAILED_IN_ILS);
            itemResponseInformation.setSuccess(createBibResponse.isSuccess());
            if (itemRequestInfo.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)) {
                rollbackUpdateItemAvailabilutyStatus(itemEntity, itemRequestInfo.getUsername());
            }
        }
        return itemResponseInformation;
    }

    /**
     * Search records search result row.
     *
     * @param itemEntity the item entity
     * @return the search result row
     */
    protected SearchResultRow searchRecords(ItemEntity itemEntity) {
        List<SearchResultRow> statusResponse;
        SearchResultRow searchResultRow = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(getRestHeaderService().getHttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + RecapConstants.SEARCH_RECORDS_SOLR)
                    .queryParam(RecapConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME, RecapConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_NAME_VALUE)
                    .queryParam(RecapConstants.SEARCH_RECORDS_SOLR_PARAM_FIELD_VALUE, itemEntity.getBarcode());
            ResponseEntity<List<SearchResultRow>> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<SearchResultRow>>() {
            });
            statusResponse = responseEntity.getBody();
            if (statusResponse != null && !statusResponse.isEmpty()) {
                searchResultRow = statusResponse.get(0);
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return searchResultRow;
    }

    /**
     * Gets title.
     *
     * @param title           the title
     * @param itemEntity      the item entity
     * @param searchResultRow the search result row
     * @return the title
     */
    protected String getTitle(String title, ItemEntity itemEntity, SearchResultRow searchResultRow) {
        String titleIdentifier = "";
        String useRestrictions = RecapConstants.REQUEST_USE_RESTRICTIONS;
        String lTitle;
        String returnTitle = "";
        try {
            if (itemEntity != null && StringUtils.isNotBlank(itemEntity.getUseRestrictions())) {
                useRestrictions = itemEntity.getUseRestrictions();
            }
            if (!(title != null && title.trim().length() > 0)) {
                if (searchResultRow != null) {
                    lTitle = searchResultRow.getTitle();
                } else {
                    lTitle = "";
                }
            } else {
                lTitle = title;
            }
            if (lTitle != null && lTitle.trim().length() > 126) {
                lTitle = lTitle.toUpperCase().substring(0, 126);
            } else if (lTitle != null && lTitle.trim().length() <= 0) {
                lTitle = "";
            }
            if (lTitle != null) {
                titleIdentifier = String.format("[%s] %s%s", useRestrictions, lTitle.toUpperCase(), RecapConstants.REQUEST_ITEM_TITLE_SUFFIX);
            }
            returnTitle = removeDiacritical(titleIdentifier);
            logger.info(returnTitle);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return returnTitle;
    }

    private void rollbackAfterGFA(ItemEntity itemEntity, ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {
        if (!itemResponseInformation.getScreenMessage().equalsIgnoreCase(RecapConstants.GFA_ITEM_STATUS_CHECK_FAILED)) {
            rollbackUpdateItemAvailabilutyStatus(itemEntity, itemRequestInfo.getUsername());
            saveItemChangeLogEntity(itemEntity.getItemId(), getUser(itemRequestInfo.getUsername()), RecapConstants.REQUEST_ITEM_GFA_FAILURE, itemRequestInfo.getPatronBarcode() + " - " + itemResponseInformation.getScreenMessage());
        }
        requestItemController.cancelHoldItem(itemRequestInfo, itemRequestInfo.getRequestingInstitution());
    }

    private void rollbackAfterGFA(ItemInformationResponse itemResponseInformation) {
        ItemRequestInformation itemRequestInformation = itemRequestDBService.rollbackAfterGFA(itemResponseInformation);
        Optional<RequestItemEntity> requestItemEntity = requestItemDetailsRepository.findById(itemResponseInformation.getRequestId());
        if (null != requestItemEntity) {
            itemRequestServiceUtil.updateSolrIndex(requestItemEntity.get().getItemEntity());
        }
        if (itemResponseInformation.isBulk()) {
            requestItemController.checkinItem(itemRequestInformation, itemRequestInformation.getRequestingInstitution());
        } else {
            requestItemController.cancelHoldItem(itemRequestInformation, itemRequestInformation.getRequestingInstitution());
        }
    }

    /**
     * Gets notes.
     *
     * @param success       the success
     * @param screenMessage the screen message
     * @param userNotes     the user notes
     * @return the notes
     */
    protected String getNotes(boolean success, String screenMessage, String userNotes) {
        String notes = "";
        if (!StringUtils.isBlank(userNotes)) {
            notes = String.format("User: %s", userNotes);
        }
        if (!success && !StringUtils.isBlank(screenMessage)) {
            if (!StringUtils.isBlank(notes)) {
                notes += "\n";
            }
            notes += screenMessage;
        }
        return notes;
    }

    /**
     * Process las retrieve response.
     *
     * @param body the body
     */
    public void processLASRetrieveResponse(String body) {
        ItemInformationResponse itemInformationResponse = gfaService.processLASRetrieveResponse(body);
        itemInformationResponse = updateRecapRequestStatus(itemInformationResponse);
        if (!itemInformationResponse.isSuccess()) {
            rollbackAfterGFA(itemInformationResponse);
        }
    }

    /**
     * @param body
     */
    public void processLASEddRetrieveResponse(String body) {
        ItemInformationResponse itemInformationResponse = gfaService.processLASEDDRetrieveResponse(body);
        if (itemInformationResponse.isSuccess()) {
            updateRecapRequestStatus(itemInformationResponse);
        } else {
            updateRecapRequestStatus(itemInformationResponse);
            rollbackAfterGFA(itemInformationResponse);
        }
    }

    /**
     * @param text
     * @return
     */
    public String removeDiacritical(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private void sendEmail(String customerCode, String itemBarcode, String patronBarcode, String toInstitution) {
        emailService.sendEmail(customerCode, itemBarcode, RecapConstants.REQUEST_RECALL_TO_BORRWER, patronBarcode, toInstitution, RecapConstants.REQUEST_RECALL_SUBJECT);
    }

    private String getPickupLocation(String deliveryLocation) {
        CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCode(deliveryLocation);
        return customerCodeEntity.getPickupLocation();
    }

    /**
     * Is use queue las call boolean.
     *
     * @return the boolean
     */
    public boolean isUseQueueLasCall() {
        return gfaService.isUseQueueLasCall();
    }

    public boolean executeLasitemCheck(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {
        RequestStatusEntity requestStatusEntity = null;
        Optional<RequestItemEntity> requestItemEntity = requestItemDetailsRepository.findById(itemRequestInfo.getRequestId());
        itemResponseInformation = gfaService.executeRetriveOrder(itemRequestInfo, itemResponseInformation);
        logger.info("itemResponseInformation-> " + itemResponseInformation.isSuccess());
        if (itemResponseInformation.isSuccess()) {
            requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_PENDING);
            requestItemEntity.get().setRequestStatusId(requestStatusEntity.getId());
            requestItemEntity.get().setLastUpdatedDate(new Date());
            requestItemDetailsRepository.save(requestItemEntity.get());
        } else {
            return false;
        }
        return true;
    }

    /**
     * Replaces the requests to LAS Queue.
     * @param replaceRequest
     * @return
     */
    public Map<String, String> replaceRequestsToLASQueue(ReplaceRequest replaceRequest) {
        Map<String, String> resultMap = new HashMap<>();
        String replaceRequestByType = replaceRequest.getReplaceRequestByType();
        try {
            if (StringUtils.isNotBlank(replaceRequestByType)) {
                resultMap = replaceRequestToLASQueueByType(replaceRequest, replaceRequestByType);
            } else {
                resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_REPLACE_BY_TYPE_NOT_SELECTED);
            }
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, exception);
            resultMap.put(RecapCommonConstants.FAILURE, exception.getMessage());
        }
        return resultMap;
    }

    /**
     * Replaces the requests to LAS Queue based on the replace request type.
     * @param replaceRequest
     * @param replaceRequestByType
     * @return
     * @throws ParseException
     */
    private Map<String, String> replaceRequestToLASQueueByType(ReplaceRequest replaceRequest, String replaceRequestByType) throws ParseException {
        Map<String, String> resultMap = new HashMap<>();
        if (RecapCommonConstants.REQUEST_STATUS.equalsIgnoreCase(replaceRequestByType)) {
            String requestStatus = replaceRequest.getRequestStatus();
            if (StringUtils.isNotBlank(requestStatus)) {
                if (RecapConstants.REQUEST_STATUS_PENDING.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByRequestStatusCode(Arrays.asList(RecapConstants.REQUEST_STATUS_PENDING));
                    resultMap = buildRequestInfoAndReplaceToLAS(requestItemEntities);
                } else if (RecapConstants.REQUEST_STATUS_EXCEPTION.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByRequestStatusCode(Arrays.asList(RecapConstants.REQUEST_STATUS_EXCEPTION));
                    resultMap = buildRequestInfoAndReplaceToSCSB(requestItemEntities);
                } else {
                    resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_STATUS_INVALID);
                }
            } else {
                resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_STATUS_INVALID);
            }
        } else if (RecapCommonConstants.REQUEST_IDS.equalsIgnoreCase(replaceRequestByType)) {
            if (StringUtils.isNotBlank(replaceRequest.getRequestIds()) && StringUtils.isNotBlank(replaceRequest.getRequestStatus())) {
                String requestStatus = replaceRequest.getRequestStatus();
                List<Integer> requestIds = new ArrayList<>();
                Arrays.stream(replaceRequest.getRequestIds().split(",")).forEach(requestId -> requestIds.add(Integer.valueOf(requestId.trim())));
                resultMap.put(RecapCommonConstants.TOTAL_REQUESTS_IDS, String.valueOf(requestIds.size()));
                if (RecapConstants.REQUEST_STATUS_PENDING.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByIdsAndStatusCodes(requestIds, Arrays.asList(RecapConstants.REQUEST_STATUS_PENDING));
                    resultMap = buildRequestInfoAndReplaceToLAS(requestItemEntities);
                } else if (RecapConstants.REQUEST_STATUS_EXCEPTION.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByIdsAndStatusCodes(requestIds, Arrays.asList(RecapConstants.REQUEST_STATUS_EXCEPTION));
                    resultMap = buildRequestInfoAndReplaceToSCSB(requestItemEntities);
                } else {
                    resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_STATUS_INVALID);
                }
            } else {
                resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_IDS_INVALID);
            }
        } else if (RecapConstants.REQUEST_IDS_RANGE.equalsIgnoreCase(replaceRequestByType)) {
            if (StringUtils.isNotBlank(replaceRequest.getStartRequestId()) && StringUtils.isNotBlank(replaceRequest.getEndRequestId()) && StringUtils.isNotBlank(replaceRequest.getRequestStatus())) {
                String requestStatus = replaceRequest.getRequestStatus();
                Integer startRequestId = Integer.valueOf(replaceRequest.getStartRequestId());
                Integer endRequestId = Integer.valueOf(replaceRequest.getEndRequestId());
                if (RecapConstants.REQUEST_STATUS_PENDING.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.getRequestsBasedOnRequestIdRangeAndRequestStatusCode(startRequestId, endRequestId, RecapConstants.REQUEST_STATUS_PENDING);
                    resultMap = buildRequestInfoAndReplaceToLAS(requestItemEntities);
                } else if (RecapConstants.REQUEST_STATUS_EXCEPTION.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.getRequestsBasedOnRequestIdRangeAndRequestStatusCode(startRequestId, endRequestId, RecapConstants.REQUEST_STATUS_EXCEPTION);
                    resultMap = buildRequestInfoAndReplaceToSCSB(requestItemEntities);
                } else {
                    resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_STATUS_INVALID);
                }
            } else {
                resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_START_END_IDS_INVALID);
            }
        } else if (RecapConstants.REQUEST_DATES_RANGE.equalsIgnoreCase(replaceRequestByType)) {
            if (StringUtils.isNotBlank(replaceRequest.getFromDate()) && StringUtils.isNotBlank(replaceRequest.getToDate()) && StringUtils.isNotBlank(replaceRequest.getRequestStatus())) {
                String requestStatus = replaceRequest.getRequestStatus();
                SimpleDateFormat dateFormatter = new SimpleDateFormat(RecapConstants.DEFAULT_DATE_FORMAT);
                Date fromDate = dateFormatter.parse(replaceRequest.getFromDate());
                Date toDate = dateFormatter.parse(replaceRequest.getToDate());
                if (RecapConstants.REQUEST_STATUS_PENDING.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.getRequestsBasedOnDateRangeAndRequestStatusCode(fromDate, toDate, RecapConstants.REQUEST_STATUS_PENDING);
                    resultMap = buildRequestInfoAndReplaceToLAS(requestItemEntities);
                } else if (RecapConstants.REQUEST_STATUS_EXCEPTION.equalsIgnoreCase(requestStatus)) {
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.getRequestsBasedOnDateRangeAndRequestStatusCode(fromDate, toDate, RecapConstants.REQUEST_STATUS_EXCEPTION);
                    resultMap = buildRequestInfoAndReplaceToSCSB(requestItemEntities);
                } else {
                    resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_STATUS_INVALID);
                }
            } else {
                resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_DATES_INVALID);
            }
        } else {
            resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.REQUEST_REPLACE_BY_TYPE_INVALID);
        }
        return resultMap;
    }

    /**
     * Builds request information and replaces them to LAS queue.
     * @param requestItemEntities
     * @return
     */
    private Map<String, String> buildRequestInfoAndReplaceToLAS(List<RequestItemEntity> requestItemEntities) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(RecapCommonConstants.TOTAL_REQUESTS_FOUND, String.valueOf(requestItemEntities.size()));
        if (CollectionUtils.isNotEmpty(requestItemEntities)) {
            String message;
            for (RequestItemEntity requestItemEntity : requestItemEntities) {
                String requestTypeCode = requestItemEntity.getRequestTypeEntity().getRequestTypeCode();
                if (RecapCommonConstants.RETRIEVAL.equalsIgnoreCase(requestTypeCode) || RecapConstants.EDD_REQUEST.equalsIgnoreCase(requestTypeCode)) {
                    message = gfaService.buildRequestInfoAndReplaceToLAS(requestItemEntity);
                } else {
                    message = RecapConstants.IGNORE_REQUEST_TYPE_NOT_VALID + requestTypeCode;
                }
                resultMap.put(String.valueOf(requestItemEntity.getId()), message);
            }
        } else {
            resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.NO_REQUESTS_FOUND);
        }
        return resultMap;
    }

    /**
     * Builds request information and replaces them to SCSB queue.
     * @param requestItemEntities
     * @return
     */
    private Map<String, String> buildRequestInfoAndReplaceToSCSB(List<RequestItemEntity> requestItemEntities) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(RecapCommonConstants.TOTAL_REQUESTS_FOUND, String.valueOf(requestItemEntities.size()));
        if (CollectionUtils.isNotEmpty(requestItemEntities)) {
            String message;
            for (RequestItemEntity requestItemEntity : requestItemEntities) {
                String requestTypeCode = requestItemEntity.getRequestTypeEntity().getRequestTypeCode();
                if (RecapCommonConstants.RETRIEVAL.equalsIgnoreCase(requestTypeCode)) {
                    message = buildRetrieveRequestInfoAndReplaceToSCSB(requestItemEntity);
                } else if (RecapConstants.EDD_REQUEST.equalsIgnoreCase(requestTypeCode)) {
                    message = buildEddRequestInfoAndReplaceToSCSB(requestItemEntity);
                } else {
                    message = RecapConstants.IGNORE_REQUEST_TYPE_NOT_VALID + requestTypeCode;
                }
                resultMap.put(String.valueOf(requestItemEntity.getId()), message);
            }
        } else {
            resultMap.put(RecapCommonConstants.INVALID_REQUEST, RecapConstants.NO_REQUESTS_FOUND);
        }
        return resultMap;
    }

    /**
     * Validates the request information and returns message.
     * @param itemRequestInformation
     * @return
     */
    private String validateItemRequest(ItemRequestInformation itemRequestInformation) {
        ResponseEntity responseEntity = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInformation);
        if (responseEntity == null) {
            responseEntity = itemValidatorService.itemValidation(itemRequestInformation);
        }
        return responseEntity.getBody().toString();
    }

    /**
     * Builds retrieval request information and replaces them to SCSB queue.
     * @param requestItemEntity
     * @return
     */
    private String buildRetrieveRequestInfoAndReplaceToSCSB(RequestItemEntity requestItemEntity) {
        try {
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setUsername(requestItemEntity.getCreatedBy());
            itemRequestInformation.setItemBarcodes(Arrays.asList(requestItemEntity.getItemEntity().getBarcode()));
            itemRequestInformation.setPatronBarcode(requestItemEntity.getPatronId());
            itemRequestInformation.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
            itemRequestInformation.setEmailAddress(securityUtil.getDecryptedValue(requestItemEntity.getEmailId()));
            itemRequestInformation.setItemOwningInstitution(requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
            itemRequestInformation.setRequestType(requestItemEntity.getRequestTypeEntity().getRequestTypeCode());
            itemRequestInformation.setDeliveryLocation(requestItemEntity.getStopCode());

            String notes = requestItemEntity.getNotes();
            new BufferedReader(new StringReader(notes)).lines().forEach(line -> itemRequestServiceUtil.setEddInfoToScsbRequest(line, itemRequestInformation));

            String validationMessage = validateItemRequest(itemRequestInformation);
            if (!RecapCommonConstants.VALID_REQUEST.equals(validationMessage)) {
                return RecapCommonConstants.FAILURE + " : " + validationMessage;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(itemRequestInformation);
            String itemStatus=gfaService.callGfaItemStatus(requestItemEntity.getItemEntity().getBarcode());
            if(RecapConstants.getGFAStatusAvailableList().contains(itemStatus)) {
                producerTemplate.sendBodyAndHeader(RecapConstants.REQUEST_ITEM_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
            }
            else {
                RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.LAS_REFILE_REQUEST_PLACED);
                requestItemEntity.setRequestStatusEntity(requestStatusEntity);
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                requestItemDetailsRepository.save(requestItemEntity);
            }
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, exception);
            return RecapCommonConstants.FAILURE + ":" + exception.getMessage();
        }
        return RecapCommonConstants.SUCCESS + " : " + RecapCommonConstants.REQUEST_MESSAGE_RECEVIED;
    }

    /**
     * Builds EDD request information and replaces them to SCSB queue.
     * @param requestItemEntity
     * @return
     */
    private String buildEddRequestInfoAndReplaceToSCSB(RequestItemEntity requestItemEntity) {
        try {
            ItemEntity itemEntity = requestItemEntity.getItemEntity();
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setUsername(requestItemEntity.getCreatedBy());
            itemRequestInformation.setItemBarcodes(Arrays.asList(itemEntity.getBarcode()));
            itemRequestInformation.setPatronBarcode(requestItemEntity.getPatronId());
            itemRequestInformation.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
            itemRequestInformation.setEmailAddress(securityUtil.getDecryptedValue(requestItemEntity.getEmailId()));
            itemRequestInformation.setItemOwningInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
            itemRequestInformation.setRequestType(requestItemEntity.getRequestTypeEntity().getRequestTypeCode());
            itemRequestInformation.setDeliveryLocation(requestItemEntity.getStopCode());

            String notes = requestItemEntity.getNotes();
            new BufferedReader(new StringReader(notes)).lines().forEach(line -> itemRequestServiceUtil.setEddInfoToScsbRequest(line, itemRequestInformation));

            SearchResultRow searchResultRow = searchRecords(itemEntity);
            itemRequestInformation.setTitleIdentifier(searchResultRow.getTitle());

            String validationMessage = validateItemRequest(itemRequestInformation);
            if (!RecapCommonConstants.VALID_REQUEST.equals(validationMessage)) {
                return RecapCommonConstants.FAILURE + ":" + validationMessage;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(itemRequestInformation);
            String itemStatus=gfaService.callGfaItemStatus(requestItemEntity.getItemEntity().getBarcode());
            if(RecapConstants.getGFAStatusAvailableList().contains(itemStatus)) {
                producerTemplate.sendBodyAndHeader(RecapConstants.REQUEST_ITEM_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
            }
            else {
                RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.LAS_REFILE_REQUEST_PLACED);
                requestItemEntity.setRequestStatusEntity(requestStatusEntity);
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                requestItemDetailsRepository.save(requestItemEntity);
            }
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, exception);
            return RecapCommonConstants.FAILURE + ":" + exception.getMessage();
        }
        return RecapCommonConstants.SUCCESS + " : " + RecapCommonConstants.REQUEST_MESSAGE_RECEVIED;
    }
}
