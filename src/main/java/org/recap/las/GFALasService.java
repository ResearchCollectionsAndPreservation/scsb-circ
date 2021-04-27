package org.recap.las;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.las.model.GFAEddItemResponse;
import org.recap.las.model.GFAItemStatus;
import org.recap.las.model.GFAItemStatusCheckRequest;
import org.recap.las.model.GFARetrieveEDDItemRequest;
import org.recap.las.model.GFARetrieveItemRequest;
import org.recap.las.model.GFARetrieveItemResponse;
import org.recap.las.model.RetrieveItemEDDRequest;
import org.recap.las.model.RetrieveItemRequest;
import org.recap.las.model.TtitemEDDResponse;
import org.recap.las.model.TtitemRequest;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.ScsbLasItemStatusCheckModel;
import org.recap.model.gfa.Ttitem;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.RequestInformation;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.jpa.SearchResultRow;
import org.recap.processor.LasItemStatusCheckPollingProcessor;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.request.ItemRequestService;
import org.recap.util.CommonUtil;
import org.recap.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sudhishk on 27/1/17.
 */
@Service
@Slf4j
public class GFALasService {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    private LasItemStatusCheckPollingProcessor lasItemStatusCheckPollingProcessor;

    @Autowired
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Autowired
    private GFALasServiceUtil gfaLasServiceUtil;

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * Gets rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Get object mapper object mapper.
     *
     * @return the object mapper
     */
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * Is use queue las call boolean.
     *
     * @return the boolean
     */
    public boolean isUseQueueLasCall(String imsLocationCode) {
        return Boolean.parseBoolean(this.propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.use.queue"));
    }

    /**
     * Execute retrive order item information response.
     *
     * @param itemRequestInfo         the item request info
     * @param itemResponseInformation the item response information
     * @return the item information response
     */
    public ItemInformationResponse executeRetrieveOrder(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse;
        String itemStatus;
        String gfaOnlyStatus;
        try {
            GFAItemStatus gfaItemStatus001 = new GFAItemStatus();
            gfaItemStatus001.setItemBarCode(itemRequestInfo.getItemBarcodes().get(0));
            List<GFAItemStatus> gfaItemStatuses = new ArrayList<>();
            gfaItemStatuses.add(gfaItemStatus001);
            gfaItemStatusCheckRequest.setItemStatus(gfaItemStatuses);
            gfaItemStatusCheckResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(itemRequestInfo.getImsLocationCode()).itemStatusCheck(gfaItemStatusCheckRequest);
            if (gfaItemStatusCheckResponse != null
                    && gfaItemStatusCheckResponse.getDsitem() != null
                    && gfaItemStatusCheckResponse.getDsitem().getTtitem() != null && !gfaItemStatusCheckResponse.getDsitem().getTtitem().isEmpty()) {

                itemStatus = gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).getItemStatus();
                if (itemStatus.contains(":")) {
                    gfaOnlyStatus = itemStatus.substring(0, itemStatus.indexOf(':') + 1).toUpperCase();
                } else {
                    gfaOnlyStatus = itemStatus.toUpperCase();
                }
                log.info("Item status check before executing {} Order. Status received : {}", itemRequestInfo.getRequestType(), gfaOnlyStatus);
                // Call Retrieval Order
                if (StringUtils.isBlank(itemRequestInfo.getImsLocationCode())) {
                    itemResponseInformation.setSuccess(false);
                    itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.IMS_LOCATION_CODE_BLANK_ERROR);
                } else if (commonUtil.isImsItemStatusAvailable(itemRequestInfo.getImsLocationCode(), gfaOnlyStatus)) {
                    if (itemRequestInfo.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)) {
                        itemResponseInformation = callItemRetrievable(itemRequestInfo, itemResponseInformation);
                    } else if (itemRequestInfo.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)) {
                        itemResponseInformation = callItemEDDRetrievable(itemRequestInfo, itemResponseInformation);
                    }
                } else if (RecapConstants.GFA_STATUS_SCH_ON_REFILE_WORK_ORDER.toLowerCase().contains(gfaOnlyStatus.toLowerCase())) {
                    log.info("Request Received while GFA status is Sch on Refile WO");
                    itemResponseInformation.setRequestTypeForScheduledOnWO(true);
                    itemResponseInformation.setSuccess(true);
                    itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
                } else {
                    itemResponseInformation.setSuccess(false);
                    itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ITEM_NOT_AVAILABLE);
                }
            } else {
                lasPolling(itemRequestInfo, itemResponseInformation);
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage("");
            }
        } catch (Exception e) {
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse callItemRetrievable(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {
        itemRequestInfo.setRequestId(itemResponseInformation.getRequestId());
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        TtitemRequest ttitem001 = new TtitemRequest();
        try {
            ttitem001.setCustomerCode(itemRequestInfo.getCustomerCode());
            ttitem001.setItemBarcode(itemRequestInfo.getItemBarcodes().get(0));
            ttitem001.setDestination(itemRequestInfo.getTranslatedDeliveryLocation());
            ttitem001.setRequestId(itemRequestInfo.getRequestId().toString());
            ttitem001.setRequestor(itemRequestInfo.getPatronBarcode());

            List<TtitemRequest> ttitems = new ArrayList<>();
            ttitems.add(ttitem001);
            RetrieveItemRequest retrieveItem = new RetrieveItemRequest();
            retrieveItem.setTtitem(ttitems);
            gfaRetrieveItemRequest.setDsitem(retrieveItem);

            if (isUseQueueLasCall(itemRequestInfo.getImsLocationCode())) { // Queue
                producerTemplate.sendBodyAndHeader(RecapConstants.SCSB_LAS_OUTGOING_QUEUE_PREFIX + itemRequestInfo.getImsLocationCode() + RecapConstants.OUTGOING_QUEUE_SUFFIX, itemRequestInfo, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                GFARetrieveItemResponse gfaRetrieveItemResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(itemRequestInfo.getImsLocationCode()).itemRetrieval(gfaRetrieveItemRequest);
                if (gfaRetrieveItemResponse.isSuccess()) {
                    itemResponseInformation.setSuccess(true);
                    itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
                } else {
                    itemResponseInformation.setSuccess(false);
                    itemResponseInformation.setScreenMessage(gfaRetrieveItemResponse.getScreenMessage());
                }
            }
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
        }
        return itemResponseInformation;
    }

    public ItemInformationResponse gfaItemRequestProcessor(Exchange exchange) {
        ItemInformationResponse itemInformationResponse = null;
        ItemRequestInformation itemRequestInfo = (ItemRequestInformation) exchange.getIn().getBody();
        log.info("Message Processing from LAS OUTGOING QUEUE at {} : {}", itemRequestInfo.getImsLocationCode(), itemRequestInfo.toString());
        if (RecapCommonConstants.REQUEST_TYPE_RETRIEVAL.equalsIgnoreCase(itemRequestInfo.getRequestType())) {
            itemInformationResponse = callItemRetrieveApi(itemRequestInfo);
        } else if (RecapCommonConstants.REQUEST_TYPE_EDD.equalsIgnoreCase(itemRequestInfo.getRequestType())) {
            itemInformationResponse = callItemEDDRetrieveApi(itemRequestInfo);
        }
        return itemInformationResponse;
    }

    private ItemInformationResponse callItemRetrieveApi(ItemRequestInformation itemRequestInfo) {
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        TtitemRequest ttitem001 = new TtitemRequest();
        try {
            ttitem001.setCustomerCode(itemRequestInfo.getCustomerCode());
            ttitem001.setItemBarcode(itemRequestInfo.getItemBarcodes().get(0));
            ttitem001.setDestination(itemRequestInfo.getTranslatedDeliveryLocation());

            ttitem001.setRequestId(itemRequestInfo.getRequestId().toString());
            ttitem001.setRequestor(itemRequestInfo.getPatronBarcode());

            List<TtitemRequest> ttitems = new ArrayList<>();
            ttitems.add(ttitem001);
            RetrieveItemRequest retrieveItem = new RetrieveItemRequest();
            retrieveItem.setTtitem(ttitems);
            gfaRetrieveItemRequest.setDsitem(retrieveItem);

            GFARetrieveItemResponse gfaRetrieveItemResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(itemRequestInfo.getImsLocationCode()).itemRetrieval(gfaRetrieveItemRequest);
            if (gfaRetrieveItemResponse.isSuccess()) {
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                itemResponseInformation.setSuccess(false);
                itemResponseInformation.setScreenMessage(gfaRetrieveItemResponse.getScreenMessage());
            }

            if (isUseQueueLasCall(itemRequestInfo.getImsLocationCode())) { // Queue
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(gfaRetrieveItemResponse);
                producerTemplate.sendBodyAndHeader(RecapConstants.LAS_INCOMING_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
            }
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
        }
        return itemResponseInformation;
    }

    /**
     * Call item edd retrivate item information response.
     *
     * @param itemRequestInfo         the item request info
     * @param itemResponseInformation the item response information
     * @return the item information response
     */
    public ItemInformationResponse callItemEDDRetrievable(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = new GFARetrieveEDDItemRequest();
        GFAEddItemResponse gfaEddItemResponse;
        TtitemEDDResponse ttitem001 = new TtitemEDDResponse();
        try {
            ttitem001.setCustomerCode(itemRequestInfo.getCustomerCode());
            ttitem001.setItemBarcode(itemRequestInfo.getItemBarcodes().get(0));
            ttitem001.setRequestId(itemRequestInfo.getRequestId());
            ttitem001.setRequestor(itemRequestInfo.getPatronBarcode());
            ttitem001.setRequestorEmail(itemRequestInfo.getEmailAddress());

            ttitem001.setStartPage(itemRequestInfo.getStartPage());
            ttitem001.setEndPage(itemRequestInfo.getEndPage());

            ttitem001.setArticleTitle(itemRequestInfo.getChapterTitle());
            ttitem001.setArticleAuthor(itemRequestInfo.getAuthor());
            ttitem001.setArticleVolume(itemRequestInfo.getVolume() + ", " + itemRequestInfo.getIssue());
            ttitem001.setArticleIssue(itemRequestInfo.getIssue());

            ttitem001.setNotes(itemRequestInfo.getEddNotes());

            ttitem001.setBiblioTitle(itemRequestInfo.getTitleIdentifier());
            ttitem001.setBiblioAuthor(itemRequestInfo.getItemAuthor());
            ttitem001.setBiblioVolume(itemRequestInfo.getItemVolume());
            ttitem001.setBiblioLocation(itemRequestInfo.getCallNumber());

            List<TtitemEDDResponse> ttitems = new ArrayList<>();
            ttitems.add(ttitem001);
            RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
            retrieveItemEDDRequest.setTtitem(ttitems);
            gfaRetrieveEDDItemRequest.setDsitem(retrieveItemEDDRequest);
            if (isUseQueueLasCall(itemRequestInfo.getImsLocationCode())) { // Queue
                producerTemplate.sendBodyAndHeader(RecapConstants.SCSB_LAS_OUTGOING_QUEUE_PREFIX + itemRequestInfo.getImsLocationCode() + RecapConstants.OUTGOING_QUEUE_SUFFIX, itemRequestInfo, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                gfaEddItemResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(itemRequestInfo.getImsLocationCode()).itemEDDRetrieval(gfaRetrieveEDDItemRequest);
                if (gfaEddItemResponse.isSuccess()) {
                    itemResponseInformation.setSuccess(true);
                    itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
                } else {
                    itemResponseInformation.setSuccess(false);
                    itemResponseInformation.setScreenMessage(gfaEddItemResponse.getScreenMessage());
                }
            }
        } catch (Exception e) {
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse callItemEDDRetrieveApi(ItemRequestInformation itemRequestInfo) {
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = new GFARetrieveEDDItemRequest();
        GFAEddItemResponse gfaEddItemResponse;
        TtitemEDDResponse ttitem001 = new TtitemEDDResponse();
        try {
            ttitem001.setCustomerCode(itemRequestInfo.getCustomerCode());
            ttitem001.setItemBarcode(itemRequestInfo.getItemBarcodes().get(0));
            ttitem001.setRequestId(itemRequestInfo.getRequestId());
            ttitem001.setRequestor(itemRequestInfo.getPatronBarcode());
            ttitem001.setRequestorEmail(itemRequestInfo.getEmailAddress());

            ttitem001.setStartPage(itemRequestInfo.getStartPage());
            ttitem001.setEndPage(itemRequestInfo.getEndPage());

            ttitem001.setArticleTitle(itemRequestInfo.getChapterTitle());
            ttitem001.setArticleAuthor(itemRequestInfo.getAuthor());
            ttitem001.setArticleVolume(itemRequestInfo.getVolume() + ", " + itemRequestInfo.getIssue());
            ttitem001.setArticleIssue(itemRequestInfo.getIssue());

            ttitem001.setNotes(itemRequestInfo.getEddNotes());

            ttitem001.setBiblioTitle(itemRequestInfo.getTitleIdentifier());
            ttitem001.setBiblioAuthor(itemRequestInfo.getItemAuthor());
            ttitem001.setBiblioVolume(itemRequestInfo.getItemVolume());
            ttitem001.setBiblioLocation(itemRequestInfo.getCallNumber());

            List<TtitemEDDResponse> ttitems = new ArrayList<>();
            ttitems.add(ttitem001);
            RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
            retrieveItemEDDRequest.setTtitem(ttitems);
            gfaRetrieveEDDItemRequest.setDsitem(retrieveItemEDDRequest);

            gfaEddItemResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(itemRequestInfo.getImsLocationCode()).itemEDDRetrieval(gfaRetrieveEDDItemRequest);
            if (gfaEddItemResponse.isSuccess()) {
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                itemResponseInformation.setSuccess(false);
                itemResponseInformation.setScreenMessage(gfaEddItemResponse.getScreenMessage());
            }

            if (isUseQueueLasCall(itemRequestInfo.getImsLocationCode())) { // Queue
                ObjectMapper objectMapper = getObjectMapper();
                String json = objectMapper.writeValueAsString(gfaEddItemResponse);
                producerTemplate.sendBodyAndHeader(RecapConstants.LAS_INCOMING_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
            }
        } catch (Exception e) {
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    /**
     * Process las retrieve response item information response.
     *
     * @param body the body
     * @return the item information response
     */
    public ItemInformationResponse processLASRetrieveResponse(String body) {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ObjectMapper om = new ObjectMapper();
        try {
            GFARetrieveItemResponse gfaRetrieveItemResponse = om.readValue(body, GFARetrieveItemResponse.class);
            gfaRetrieveItemResponse = gfaLasServiceUtil.getLASRetrieveResponse(gfaRetrieveItemResponse);
            if (gfaRetrieveItemResponse.isSuccess()) {
                itemInformationResponse.setRequestId(gfaRetrieveItemResponse.getDsitem().getTtitem().get(0).getRequestId());
                itemInformationResponse.setSuccess(true);
                itemInformationResponse.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                itemInformationResponse.setRequestId(gfaRetrieveItemResponse.getDsitem().getTtitem().get(0).getRequestId());
                itemInformationResponse.setSuccess(false);
                itemInformationResponse.setScreenMessage(gfaRetrieveItemResponse.getScreenMessage());
            }
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemInformationResponse;
    }

    /**
     * Process las EDD response item information response.
     *
     * @param body the body
     * @return item information response
     */
    public ItemInformationResponse processLASEDDRetrieveResponse(String body) {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ObjectMapper om = new ObjectMapper();
        try {
            GFAEddItemResponse gfaEddItemResponse = om.readValue(body, GFAEddItemResponse.class);
            gfaEddItemResponse = getLASEddRetrieveResponse(gfaEddItemResponse);
            if (null != gfaEddItemResponse && gfaEddItemResponse.isSuccess()) {
                itemInformationResponse.setRequestId(gfaEddItemResponse.getDsitem().getTtitem().get(0).getRequestId());
                itemInformationResponse.setSuccess(true);
                itemInformationResponse.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                itemInformationResponse.setRequestId(gfaEddItemResponse != null ? gfaEddItemResponse.getDsitem().getTtitem().get(0).getRequestId() : 0);
                itemInformationResponse.setSuccess(false);
                itemInformationResponse.setScreenMessage(gfaEddItemResponse != null ? gfaEddItemResponse.getScreenMessage() : "");
            }
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemInformationResponse;
    }

    private GFAEddItemResponse getLASEddRetrieveResponse(GFAEddItemResponse gfaEddItemResponse) {
        GFAEddItemResponse gfaRetrieveItemResponse = gfaEddItemResponse;
        if (gfaRetrieveItemResponse != null && gfaRetrieveItemResponse.getDsitem() != null && gfaRetrieveItemResponse.getDsitem().getTtitem() != null && !gfaRetrieveItemResponse.getDsitem().getTtitem().isEmpty()) {
            List<TtitemEDDResponse> titemList = gfaRetrieveItemResponse.getDsitem().getTtitem();
            for (TtitemEDDResponse ttitemEDDRequest : titemList) {
                if (!ttitemEDDRequest.getErrorCode().isEmpty()) {
                    gfaRetrieveItemResponse.setSuccess(false);
                    gfaRetrieveItemResponse.setScreenMessage(ttitemEDDRequest.getErrorNote());
                } else {
                    gfaRetrieveItemResponse.setSuccess(true);
                }
            }
        } else {
            if (gfaRetrieveItemResponse != null) {
                gfaRetrieveItemResponse.setSuccess(true);
            }
        }
        return gfaRetrieveItemResponse;
    }

    private void lasPolling(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {
        // Update Request_item_t table with new status - each Item
        try {
            RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_LAS_ITEM_STATUS_PENDING);
            RequestItemEntity requestItemEntity = requestItemDetailsRepository.findRequestItemById(itemRequestInfo.getRequestId());
            requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
            requestItemEntity.setLastUpdatedDate(new Date());
            requestItemDetailsRepository.save(requestItemEntity);
            log.info("lasPolling Saved {}", requestItemEntity.getRequestStatusEntity().getRequestStatusCode());
            ObjectMapper objectMapper = getObjectMapper();
            String json = null;
            RequestInformation requestInformation = new RequestInformation();
            requestInformation.setItemRequestInfo(itemRequestInfo);
            requestInformation.setItemResponseInformation(itemResponseInformation);
            json = objectMapper.writeValueAsString(requestInformation);
            log.info(json);
            log.info("Rest Service Status -> {}", RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS);
            if (RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS == 0) {
                producerTemplate.getCamelContext().getRouteController().stopRoute(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID);
            }
            producerTemplate.sendBodyAndHeader(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
            itemRequestServiceUtil.updateSolrIndex(requestItemEntity.getItemEntity());
            if (RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS == 0) {
                // Start Polling program - Once
                startPolling(itemRequestInfo.getItemBarcodes().get(0), itemRequestInfo.getImsLocationCode());
            }
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException ", e);
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }

    public void startPolling(String barcode, String imsLocationCode) {
        try {
            log.info("Start Polling Process Once");
            RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS = 1;
            lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse(barcode, imsLocationCode, producerTemplate.getCamelContext());
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }

    /**
     * Builds request order info based on request type and replaces into LAS queue.
     *
     * @param requestItemEntity
     * @return
     */
    public String buildRequestInfoAndReplaceToLAS(RequestItemEntity requestItemEntity) {
        try {
            ItemRequestInformation itemRequestInformation = itemRequestService.getItemRequestInformationByRequestEntity(requestItemEntity, requestItemEntity.getItemEntity());
            String itemStatus = callGfaItemStatus(requestItemEntity.getItemEntity().getBarcode());
            String imsLocationCode = commonUtil.getImsLocationCodeByItemBarcode(requestItemEntity.getItemEntity().getBarcode());
            if (commonUtil.isImsItemStatusAvailable(imsLocationCode, itemStatus)) {
                producerTemplate.sendBodyAndHeader(RecapConstants.SCSB_LAS_OUTGOING_QUEUE_PREFIX + imsLocationCode + RecapConstants.OUTGOING_QUEUE_SUFFIX, itemRequestInformation, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, requestItemEntity.getRequestTypeEntity().getRequestTypeCode());
            } else if (StringUtils.isNotBlank(itemStatus)) {
                RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.LAS_REFILE_REQUEST_PLACED);
                requestItemEntity.setRequestStatusEntity(requestStatusEntity);
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                requestItemDetailsRepository.save(requestItemEntity);
            }
        } catch (Exception exception) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, exception);
            return RecapCommonConstants.FAILURE + ":" + exception.getMessage();
        }
        return RecapCommonConstants.SUCCESS;
    }

    /**
     * Builds retrieval request order info.
     *
     * @param requestItemEntity RequestItemEntity
     * @return GFARetrieveItemRequest
     */
    private GFARetrieveItemRequest buildGFARetrieveItemRequest(RequestItemEntity requestItemEntity) {
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        TtitemRequest ttitem001 = new TtitemRequest();
        ttitem001.setCustomerCode(requestItemEntity.getItemEntity().getCustomerCode());
        ttitem001.setItemBarcode(requestItemEntity.getItemEntity().getBarcode());
        ttitem001.setDestination(requestItemEntity.getStopCode());
        ttitem001.setRequestId(String.valueOf(requestItemEntity.getId()));
        ttitem001.setRequestor(requestItemEntity.getPatronId());
        RetrieveItemRequest retrieveItem = new RetrieveItemRequest();
        retrieveItem.setTtitem(Collections.singletonList(ttitem001));
        gfaRetrieveItemRequest.setDsitem(retrieveItem);
        return gfaRetrieveItemRequest;
    }

    /**
     * Builds edd request order info.
     *
     * @param requestItemEntity RequestItemEntity
     * @return GFARetrieveEDDItemRequest
     */
    private GFARetrieveEDDItemRequest buildGFAEddItemRequest(RequestItemEntity requestItemEntity) {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = new GFARetrieveEDDItemRequest();
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        TtitemEDDResponse ttitem001 = new TtitemEDDResponse();
        ttitem001.setCustomerCode(itemEntity.getCustomerCode());
        ttitem001.setItemBarcode(itemEntity.getBarcode());
        ttitem001.setRequestId(requestItemEntity.getId());
        ttitem001.setRequestor(requestItemEntity.getPatronId());
        ttitem001.setRequestorEmail(requestItemEntity.getEmailId());

        String notes = requestItemEntity.getNotes();
        ttitem001.setNotes(notes);
        new BufferedReader(new StringReader(notes)).lines().forEach(line -> itemRequestServiceUtil.setEddInfoToGfaRequest(line, ttitem001));
        ttitem001.setArticleVolume(ttitem001.getArticleVolume() + ", " + ttitem001.getArticleIssue());

        SearchResultRow searchResultRow = itemRequestService.searchRecords(itemEntity);
        ttitem001.setBiblioTitle(itemRequestService.getTitle(null, itemEntity, searchResultRow));
        ttitem001.setBiblioAuthor(searchResultRow.getAuthor());
        ttitem001.setBiblioVolume(itemEntity.getVolumePartYear());
        ttitem001.setBiblioLocation(itemEntity.getCallNumber());

        RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
        retrieveItemEDDRequest.setTtitem(Collections.singletonList(ttitem001));
        gfaRetrieveEDDItemRequest.setDsitem(retrieveItemEDDRequest);
        return gfaRetrieveEDDItemRequest;
    }

    /**
     * Get LAS Item Status by Item Barcode
     * @param itemBarcode the Item Barcode
     * @return the Las Status
     */
    public String callGfaItemStatus(String itemBarcode) {
        String gfaItemStatusValue = null;
        String imsLocationCode = commonUtil.getImsLocationCodeByItemBarcode(itemBarcode);
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatusCall = new GFAItemStatus();
        gfaItemStatusCall.setItemBarCode(itemBarcode);
        gfaItemStatusCheckRequest.setItemStatus(Collections.singletonList(gfaItemStatusCall));
        if (StringUtils.isNotBlank(imsLocationCode)) {
            GFAItemStatusCheckResponse gfaItemStatusCheckResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(imsLocationCode).itemStatusCheck(gfaItemStatusCheckRequest);
            if (null != gfaItemStatusCheckResponse) {
                Dsitem dsitem = gfaItemStatusCheckResponse.getDsitem();
                if (null != dsitem) {
                    List<Ttitem> ttitems = dsitem.getTtitem();
                    if (CollectionUtils.isNotEmpty(ttitems)) {
                        gfaItemStatusValue = ttitems.get(0).getItemStatus();
                    }
                }
            }
        }
        return gfaItemStatusValue;
    }

    /**
     * For the given item barcodes this method checks status with LAS.
     *
     * @param itemsStatusCheckModel the item status requests
     * @return the gfa item status check response
     */
    public List<ScsbLasItemStatusCheckModel> getGFAItemStatusCheckResponseByBarcodesAndImsLocationList(List<ScsbLasItemStatusCheckModel> itemsStatusCheckModel) {
        List<ScsbLasItemStatusCheckModel> itemsStatusCheckModelResponse = new ArrayList<>();
        if (itemsStatusCheckModel != null) {
            Map<String, List<GFAItemStatus>> itemStatusCheckRequestMap = new HashMap<>();
            for (ScsbLasItemStatusCheckModel itemsStatusCheckModelObj : itemsStatusCheckModel) {
                String imsLocation = itemsStatusCheckModelObj.getImsLocation();
                GFAItemStatus gfaItemStatusRequest = new GFAItemStatus();
                gfaItemStatusRequest.setItemBarCode(itemsStatusCheckModelObj.getItemBarcode());
                if (itemStatusCheckRequestMap.containsKey(imsLocation)) {
                    itemStatusCheckRequestMap.get(imsLocation).add(gfaItemStatusRequest);
                } else {
                    List<GFAItemStatus> gfaItemStatusList = new ArrayList<>();
                    gfaItemStatusList.add(gfaItemStatusRequest);
                    itemStatusCheckRequestMap.put(imsLocation, gfaItemStatusList);
                }
            }
            for (Map.Entry<String, List<GFAItemStatus>> mapEntry : itemStatusCheckRequestMap.entrySet()) {
                String imsLocation = mapEntry.getKey();
                GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
                gfaItemStatusCheckRequest.setItemStatus(mapEntry.getValue());
                GFAItemStatusCheckResponse gfaItemStatusCheckResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(imsLocation).itemStatusCheck(gfaItemStatusCheckRequest);
                if (gfaItemStatusCheckResponse != null && gfaItemStatusCheckResponse.getDsitem() != null && gfaItemStatusCheckResponse.getDsitem().getTtitem() != null) {
                    List<Ttitem> ttitemList = gfaItemStatusCheckResponse.getDsitem().getTtitem();
                    for (Ttitem ttitem : ttitemList) {
                        ScsbLasItemStatusCheckModel itemStatusCheckModelResponseItem = new ScsbLasItemStatusCheckModel();
                        itemStatusCheckModelResponseItem.setItemBarcode(ttitem.getItemBarcode());
                        itemStatusCheckModelResponseItem.setImsLocation(imsLocation);
                        itemStatusCheckModelResponseItem.setItemStatus(ttitem.getItemStatus());
                        itemsStatusCheckModelResponse.add(itemStatusCheckModelResponseItem);
                    }
                }
            }
        }
        return itemsStatusCheckModelResponse;
    }

}
