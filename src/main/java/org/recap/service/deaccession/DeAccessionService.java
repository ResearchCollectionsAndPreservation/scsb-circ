package org.recap.service.deaccession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.controller.RequestItemController;
import org.recap.las.LASImsLocationConnectorFactory;
import org.recap.las.model.GFAPwdDsItemRequest;
import org.recap.las.model.GFAPwdDsItemResponse;
import org.recap.las.model.GFAPwdRequest;
import org.recap.las.model.GFAPwdResponse;
import org.recap.las.model.GFAPwdTtItemRequest;
import org.recap.las.model.GFAPwdTtItemResponse;
import org.recap.las.model.GFAPwiDsItemRequest;
import org.recap.las.model.GFAPwiDsItemResponse;
import org.recap.las.model.GFAPwiRequest;
import org.recap.las.model.GFAPwiResponse;
import org.recap.las.model.GFAPwiTtItemRequest;
import org.recap.las.model.GFAPwiTtItemResponse;
import org.recap.ils.model.response.ItemHoldResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.deaccession.DeAccessionDBResponseEntity;
import org.recap.model.deaccession.DeAccessionItem;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.model.deaccession.DeAccessionSolrRequest;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.DeaccesionItemChangeLogDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.las.GFALasService;
import org.recap.service.RestHeaderService;
import org.recap.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenchulakshmig on 28/9/16.
 */
@Component
public class DeAccessionService {

    private static final Logger logger = LoggerFactory.getLogger(DeAccessionService.class);

    private static final String EXCEPTION_CONSTANT = "Exception :";

    /**
     * The Bibliographic details repository.
     */
    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    /**
     * The Holdings details repository.
     */
    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    /**
     * The Item details repository.
     */
    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    /**
     * The Report detail repository.
     */
    @Autowired
    ReportDetailRepository reportDetailRepository;

    /**
     * The Request item details repository.
     */
    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    /**
     * The Request item status details repository.
     */
    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    /**
     * The Item change log details repository.
     */
    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    /**
     * The Request item controller.
     */
    @Autowired
    RequestItemController requestItemController;

    /**
     * The LAS Ims Location Connector Factory
     */
    @Autowired
    LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    /**
     * The Item Request Service.
     */
    @Autowired
    ItemRequestServiceUtil itemRequestServiceUtil;

    /**
     * The GFA Las  Service
     */
    @Autowired
    GFALasService gfaLasService;

    @Autowired
    RestHeaderService restHeaderService;

    @Autowired
    PropertyUtil propertyUtil;

    @Autowired
    private DeaccesionItemChangeLogDetailsRepository deaccesionItemChangeLogDetailsRepository;

    public RestHeaderService getRestHeaderService(){
        return restHeaderService;
    }

    /**
     * The Scsb solr client url.
     */
    @Value("${scsb.solr.doc.url}")
    String scsbSolrClientUrl;


    private String getRecapAssistanceEmailTo(String imsLocationCode) {
        return this.propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.email.assist.to");
    }


    /**
     * De accession map.
     *
     * @param deAccessionRequest the de accession request
     * @return the map
     */
    public Map<String, String> deAccession(DeAccessionRequest deAccessionRequest) {
        Map<String, String> resultMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(deAccessionRequest.getDeAccessionItems())) {
            Map<String, String> barcodeAndStopCodeMap = new HashMap<>();
            List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
            String username = StringUtils.isNotBlank(deAccessionRequest.getUsername()) ? deAccessionRequest.getUsername() : RecapConstants.DISCOVERY;

            checkGfaItemStatus(deAccessionRequest.getDeAccessionItems(), deAccessionDBResponseEntities, barcodeAndStopCodeMap);
            checkAndCancelHolds(barcodeAndStopCodeMap, deAccessionDBResponseEntities, username);
            deAccessionItemsInDB(barcodeAndStopCodeMap, deAccessionDBResponseEntities, username);
            callGfaDeaccessionService(deAccessionDBResponseEntities, username);
            rollbackLASRejectedItems(deAccessionDBResponseEntities, username);
            deAccessionItemsInSolr(deAccessionDBResponseEntities, resultMap);
            processAndSaveReportEntities(deAccessionDBResponseEntities);
            processAndSaveDeaccessionChangeLog(deAccessionRequest,username,deAccessionDBResponseEntities);
        } else {
            resultMap.put("", RecapConstants.DEACCESSION_NO_BARCODE_ERROR);
            return resultMap;
        }
        return resultMap;
    }

    private void rollbackLASRejectedItems(List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities, String username) {
        if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
            Map<Integer, String> itemIdAndMessageMap = new HashMap<>();
            List<Integer> bibIds = new ArrayList<>();
            List<Integer> holdingsIds = new ArrayList<>();
            List<Integer> itemIds = new ArrayList<>();
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                if (deAccessionDBResponseEntity.getStatus().equalsIgnoreCase(RecapCommonConstants.FAILURE)
                        && (deAccessionDBResponseEntity.getReasonForFailure().contains(RecapCommonConstants.LAS_REJECTED)
                        || deAccessionDBResponseEntity.getReasonForFailure().contains(RecapCommonConstants.LAS_SERVER_NOT_REACHABLE))) {
                    bibIds.addAll(deAccessionDBResponseEntity.getBibliographicIds());
                    holdingsIds.addAll(deAccessionDBResponseEntity.getHoldingIds());
                    itemIds.add(deAccessionDBResponseEntity.getItemId());
                    itemIdAndMessageMap.put(deAccessionDBResponseEntity.getItemId(), deAccessionDBResponseEntity.getReasonForFailure());
                }
            }
            Date currentDate = new Date();
            if (CollectionUtils.isNotEmpty(bibIds)) {
                bibliographicDetailsRepository.markBibsAsNotDeleted(bibIds, username, currentDate);
            }
            if (CollectionUtils.isNotEmpty(holdingsIds)) {
                holdingsDetailsRepository.markHoldingsAsNotDeleted(holdingsIds, username, currentDate);
            }
            if (CollectionUtils.isNotEmpty(itemIds)) {
                itemDetailsRepository.markItemsAsNotDeleted(itemIds, username, currentDate);
                saveDeAccessionItemChangeLogEntities(itemIds, username, RecapConstants.DEACCESSION_ROLLBACK, currentDate, RecapConstants.DEACCESSION_ROLLBACK_NOTES, itemIdAndMessageMap);
            }
        }
    }

    private void checkGfaItemStatus(List<DeAccessionItem> deAccessionItems, List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities, Map<String, String> barcodeAndStopCodeMap) {
        try {
            for (DeAccessionItem deAccessionItem : deAccessionItems) {
                logger.info("Deaccession Item Barcode = {} Delivery Location = {}", deAccessionItem.getItemBarcode(), deAccessionItem.getDeliveryLocation());
                String itemBarcode = deAccessionItem.getItemBarcode();
                if (StringUtils.isNotBlank(itemBarcode)) {
                    List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemBarcode.trim());
                    if (CollectionUtils.isNotEmpty(itemEntities)) {
                        ItemEntity itemEntity = itemEntities.get(0);
                        if (itemEntity.isDeleted()) {
                            deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deAccessionItem.getDeliveryLocation(), RecapCommonConstants.REQUESTED_ITEM_DEACCESSIONED, itemEntity));
                        } else if (!itemEntity.isComplete()) {
                            deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deAccessionItem.getDeliveryLocation(), RecapCommonConstants.ITEM_BARCDE_DOESNOT_EXIST, itemEntity));
                        } else {
                            String scsbItemStatus = itemEntity.getItemStatusEntity().getStatusCode();
                            String recapAssistanceEmailTo = getRecapAssistanceEmailTo(itemEntity.getImsLocationEntity().getImsLocationCode());
                            logger.info("SCSB Item Status : {}", scsbItemStatus);
                            String gfaItemStatus = gfaLasService.callGfaItemStatus(itemBarcode);
                            logger.info("GFA Item Status : {}", gfaItemStatus);
                            if (StringUtils.isNotBlank(gfaItemStatus)) {
                                gfaItemStatus = gfaItemStatus.toUpperCase();
                                gfaItemStatus = gfaItemStatus.contains(":") ? gfaItemStatus.substring(0, gfaItemStatus.indexOf(':') + 1) : gfaItemStatus;
                                logger.info("GFA Item Status    after trimming : {}", gfaItemStatus);
                                if((StringUtils.isNotBlank(gfaItemStatus) && RecapConstants.GFA_STATUS_SCH_ON_REFILE_WORK_ORDER.equals(gfaItemStatus))){
                                    deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deAccessionItem.getDeliveryLocation(), "Cannot Deaccession as Item is awaiting for Refile.Please try again later or contact ReCAP staff for further assistance.", itemEntity));
                                }
                                else if ((StringUtils.isNotBlank(gfaItemStatus) && !RecapConstants.GFA_STATUS_NOT_ON_FILE.equalsIgnoreCase(gfaItemStatus))
                                        && ((RecapCommonConstants.AVAILABLE.equals(scsbItemStatus) && RecapConstants.getGFAStatusAvailableList().contains(gfaItemStatus))
                                        || (RecapCommonConstants.NOT_AVAILABLE.equals(scsbItemStatus) && RecapConstants.getGFAStatusNotAvailableList().contains(gfaItemStatus)))) {
                                    barcodeAndStopCodeMap.put(itemBarcode.trim(), deAccessionItem.getDeliveryLocation());
                                } else {
                                    deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deAccessionItem.getDeliveryLocation(), MessageFormat.format(RecapConstants.GFA_ITEM_STATUS_MISMATCH, recapAssistanceEmailTo, recapAssistanceEmailTo), itemEntity));
                                }

                            } else {
                                deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deAccessionItem.getDeliveryLocation(), MessageFormat.format(RecapConstants.GFA_SERVER_DOWN, recapAssistanceEmailTo, recapAssistanceEmailTo), itemEntity));
                            }
                        }
                    } else {
                        deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deAccessionItem.getDeliveryLocation(), RecapCommonConstants.ITEM_BARCDE_DOESNOT_EXIST, null));
                    }
                } else {
                    deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deAccessionItem.getDeliveryLocation(), RecapConstants.DEACCESSION_NO_BARCODE_PROVIDED_ERROR, null));
                }
            }
        } catch (Exception e) {
            logger.error(EXCEPTION_CONSTANT, e);
        }
    }

    private void callGfaDeaccessionService(List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities, String username) {
        if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                String recapAssistanceEmailTo = getRecapAssistanceEmailTo(deAccessionDBResponseEntity.getImsLocationCode());
                if (RecapCommonConstants.SUCCESS.equalsIgnoreCase(deAccessionDBResponseEntity.getStatus()) && RecapCommonConstants.AVAILABLE.equalsIgnoreCase(deAccessionDBResponseEntity.getItemStatus())) {
                    GFAPwdRequest gfaPwdRequest = new GFAPwdRequest();
                    GFAPwdDsItemRequest gfaPwdDsItemRequest = new GFAPwdDsItemRequest();
                    GFAPwdTtItemRequest gfaPwdTtItemRequest = new GFAPwdTtItemRequest();
                    gfaPwdTtItemRequest.setCustomerCode(deAccessionDBResponseEntity.getCustomerCode());
                    gfaPwdTtItemRequest.setItemBarcode(deAccessionDBResponseEntity.getBarcode());
                    gfaPwdTtItemRequest.setDestination(deAccessionDBResponseEntity.getDeliveryLocation());
                    gfaPwdTtItemRequest.setRequestor(username);
                    gfaPwdDsItemRequest.setTtitem(Collections.singletonList(gfaPwdTtItemRequest));
                    gfaPwdRequest.setDsitem(gfaPwdDsItemRequest);
                    GFAPwdResponse gfaPwdResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(deAccessionDBResponseEntity.getImsLocationCode()).gfaPermanentWithdrawalDirect(gfaPwdRequest);
                    if (null != gfaPwdResponse) {
                        GFAPwdDsItemResponse gfaPwdDsItemResponse = gfaPwdResponse.getDsitem();
                        if (null != gfaPwdDsItemResponse) {
                            List<GFAPwdTtItemResponse> gfaPwdTtItemResponses = gfaPwdDsItemResponse.getTtitem();
                            if (CollectionUtils.isNotEmpty(gfaPwdTtItemResponses)) {
                                GFAPwdTtItemResponse gfaPwdTtItemResponse = gfaPwdTtItemResponses.get(0);
                                String errorCode = (String) gfaPwdTtItemResponse.getErrorCode();
                                String errorNote = (String) gfaPwdTtItemResponse.getErrorNote();
                                if (StringUtils.isNotBlank(errorCode) || StringUtils.isNotBlank(errorNote)) {
                                    deAccessionDBResponseEntity.setStatus(RecapCommonConstants.FAILURE);
                                    deAccessionDBResponseEntity.setReasonForFailure(MessageFormat.format(RecapConstants.LAS_DEACCESSION_REJECT_ERROR, RecapConstants.REQUEST_TYPE_PW_DIRECT, errorCode, errorNote));
                                }
                            }
                        }
                    } else {
                        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.FAILURE);
                        deAccessionDBResponseEntity.setReasonForFailure(MessageFormat.format(RecapConstants.LAS_SERVER_NOT_REACHABLE_ERROR, recapAssistanceEmailTo, recapAssistanceEmailTo));
                    }
                } else if (RecapCommonConstants.SUCCESS.equalsIgnoreCase(deAccessionDBResponseEntity.getStatus()) && RecapCommonConstants.NOT_AVAILABLE.equalsIgnoreCase(deAccessionDBResponseEntity.getItemStatus())) {
                    GFAPwiRequest gfaPwiRequest = new GFAPwiRequest();
                    GFAPwiDsItemRequest gfaPwiDsItemRequest = new GFAPwiDsItemRequest();
                    GFAPwiTtItemRequest gfaPwiTtItemRequest = new GFAPwiTtItemRequest();
                    gfaPwiTtItemRequest.setCustomerCode(deAccessionDBResponseEntity.getCustomerCode());
                    gfaPwiTtItemRequest.setItemBarcode(deAccessionDBResponseEntity.getBarcode());
                    gfaPwiDsItemRequest.setTtitem(Collections.singletonList(gfaPwiTtItemRequest));
                    gfaPwiRequest.setDsitem(gfaPwiDsItemRequest);
                    GFAPwiResponse gfaPwiResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(deAccessionDBResponseEntity.getImsLocationCode()).gfaPermanentWithdrawalInDirect(gfaPwiRequest);
                    if (null != gfaPwiResponse) {
                        GFAPwiDsItemResponse gfaPwiDsItemResponse = gfaPwiResponse.getDsitem();
                        if (null != gfaPwiDsItemResponse) {
                            List<GFAPwiTtItemResponse> gfaPwiTtItemResponses = gfaPwiDsItemResponse.getTtitem();
                            if (CollectionUtils.isNotEmpty(gfaPwiTtItemResponses)) {
                                GFAPwiTtItemResponse gfaPwiTtItemResponse = gfaPwiTtItemResponses.get(0);
                                String errorCode = gfaPwiTtItemResponse.getErrorCode();
                                String errorNote = gfaPwiTtItemResponse.getErrorNote();
                                if (StringUtils.isNotBlank(errorCode) || StringUtils.isNotBlank(errorNote)) {
                                    deAccessionDBResponseEntity.setStatus(RecapCommonConstants.FAILURE);
                                    deAccessionDBResponseEntity.setReasonForFailure(MessageFormat.format(RecapConstants.LAS_DEACCESSION_REJECT_ERROR, RecapConstants.REQUEST_TYPE_PW_INDIRECT, errorCode, errorNote));
                                }
                            }
                        }
                    } else {
                        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.FAILURE);
                        deAccessionDBResponseEntity.setReasonForFailure(MessageFormat.format(RecapConstants.LAS_SERVER_NOT_REACHABLE_ERROR, recapAssistanceEmailTo, recapAssistanceEmailTo));
                    }
                }
            }
        }
    }

    /**
     * Check and cancel holds.
     *
     * @param barcodeAndStopCodeMap         the barcode and stop code map
     * @param deAccessionDBResponseEntities the de accession db response entities
     * @param username                      the username
     */
    public void checkAndCancelHolds(Map<String, String> barcodeAndStopCodeMap, List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities, String username) {
        Set<String> itemBarcodeList = barcodeAndStopCodeMap.keySet();
        if (CollectionUtils.isNotEmpty(itemBarcodeList)) {
            String deliveryLocation = null;
            for (String itemBarcode : itemBarcodeList) {
                try {
                    deliveryLocation = barcodeAndStopCodeMap.get(itemBarcode);
                    List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByItemBarcode(itemBarcode);
                    if (CollectionUtils.isNotEmpty(requestItemEntities)) {
                        RequestItemEntity activeRetrievalRequest = null;
                        RequestItemEntity activeRecallRequest = null;
                        RequestItemEntity initialLoadRequest = null;
                        for (RequestItemEntity requestItemEntity : requestItemEntities) { // Get active retrieval and recall requests.
                            boolean isRequestTypeRetreivalAndFirstScan = RecapCommonConstants.RETRIEVAL.equals(requestItemEntity.getRequestTypeEntity().getRequestTypeCode()) && RecapConstants.LAS_REFILE_REQUEST_PLACED.equalsIgnoreCase(requestItemEntity.getRequestStatusEntity().getRequestStatusCode());
                            boolean isRequestTypeRecallAndFirstScan = RecapCommonConstants.REQUEST_TYPE_RECALL.equals(requestItemEntity.getRequestTypeEntity().getRequestTypeCode()) && RecapConstants.LAS_REFILE_REQUEST_PLACED.equalsIgnoreCase(requestItemEntity.getRequestStatusEntity().getRequestStatusCode());
                            if ((RecapCommonConstants.RETRIEVAL.equals(requestItemEntity.getRequestTypeEntity().getRequestTypeCode()) && RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED.equals(requestItemEntity.getRequestStatusEntity().getRequestStatusCode())) || isRequestTypeRetreivalAndFirstScan) {
                                activeRetrievalRequest = requestItemEntity;
                            }
                            if ((RecapCommonConstants.REQUEST_TYPE_RECALL.equals(requestItemEntity.getRequestTypeEntity().getRequestTypeCode()) && RecapCommonConstants.REQUEST_STATUS_RECALLED.equals(requestItemEntity.getRequestStatusEntity().getRequestStatusCode())) || isRequestTypeRecallAndFirstScan) {
                                activeRecallRequest = requestItemEntity;
                            }
                            if (RecapCommonConstants.RETRIEVAL.equals(requestItemEntity.getRequestTypeEntity().getRequestTypeCode()) && RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD.equals(requestItemEntity.getRequestStatusEntity().getRequestStatusCode())) {
                                initialLoadRequest = requestItemEntity;
                            }
                        }
                        if (initialLoadRequest != null) {
                            updateRequestAsCanceled(initialLoadRequest, username);
                            barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                        }
                        if (activeRetrievalRequest != null && activeRecallRequest != null) {
                            String retrievalRequestingInstitution = activeRetrievalRequest.getInstitutionEntity().getInstitutionCode();
                            String recallRequestingInstitution = activeRecallRequest.getInstitutionEntity().getInstitutionCode();
                            if (retrievalRequestingInstitution.equals(recallRequestingInstitution)) { // If retrieval order institution and recall order institution are same, cancel recall request.
                                ItemHoldResponse cancelRecallResponse = cancelRequest(activeRecallRequest, username);
                                if (cancelRecallResponse.isSuccess()) {
                                    barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                                } else {
                                    deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deliveryLocation, RecapConstants.REASON_CANCEL_REQUEST_FAILED + " - " + cancelRecallResponse.getScreenMessage(), null));
                                }
                            } else { // If retrieval order institution and recall order institution are different, cancel retrieval request and recall request.
                                ItemInformationResponse itemInformationResponse = getItemInformation(activeRetrievalRequest);
                                if (getHoldQueueLength(itemInformationResponse) > 0) {
                                    ItemHoldResponse cancelRetrievalResponse = cancelRequest(activeRetrievalRequest, username);
                                    if (cancelRetrievalResponse.isSuccess()) {
                                        ItemHoldResponse cancelRecallResponse = cancelRequest(activeRecallRequest, username);
                                        if (cancelRecallResponse.isSuccess()) {
                                            barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                                        } else {
                                            deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deliveryLocation, RecapConstants.REASON_CANCEL_REQUEST_FAILED + " - " + cancelRecallResponse.getScreenMessage(), null));
                                        }
                                    } else {
                                        deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deliveryLocation, RecapConstants.REASON_CANCEL_REQUEST_FAILED + " - " + cancelRetrievalResponse.getScreenMessage(), null));
                                    }
                                } else {
                                    barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                                }
                            }
                        } else if (activeRetrievalRequest != null && activeRecallRequest == null) {
                            ItemInformationResponse itemInformationResponse = getItemInformation(activeRetrievalRequest);
                            if (getHoldQueueLength(itemInformationResponse) > 0) {
                                ItemHoldResponse cancelRetrievalResponse = cancelRequest(activeRetrievalRequest, username);
                                if (cancelRetrievalResponse.isSuccess()) {
                                    barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                                } else {
                                    deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deliveryLocation, RecapConstants.REASON_CANCEL_REQUEST_FAILED + " - " + cancelRetrievalResponse.getScreenMessage(), null));
                                }
                            } else {
                                barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                            }
                        } else if (activeRetrievalRequest == null && activeRecallRequest != null) {
                            barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                        } else if (activeRetrievalRequest == null && activeRecallRequest == null) {
                            barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                        }
                    } else {
                        barcodeAndStopCodeMap.put(itemBarcode, deliveryLocation);
                    }
                } catch (Exception e) {
                    deAccessionDBResponseEntities.add(prepareFailureResponse(itemBarcode, deliveryLocation, RecapCommonConstants.FAILURE + " - " + e, null));
                    logger.error(EXCEPTION_CONSTANT, e);
                }
            }
        }
    }

    private ItemInformationResponse getItemInformation(RequestItemEntity activeRetrievalRequest) {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(activeRetrievalRequest);
        return (ItemInformationResponse) requestItemController.itemInformation(itemRequestInformation, itemRequestInformation.getRequestingInstitution());
    }

    private ItemRequestInformation getItemRequestInformation(RequestItemEntity activeRetrievalRequest) {
        ItemEntity itemEntity = activeRetrievalRequest.getItemEntity();
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.singletonList(itemEntity.getBarcode()));
        itemRequestInformation.setItemOwningInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
        itemRequestInformation.setBibId(itemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId());
        itemRequestInformation.setRequestingInstitution(activeRetrievalRequest.getInstitutionEntity().getInstitutionCode());
        itemRequestInformation.setPatronBarcode(activeRetrievalRequest.getPatronId());
        itemRequestInformation.setDeliveryLocation(activeRetrievalRequest.getStopCode());
        return itemRequestInformation;
    }

    /**
     * Cancel request item hold response.
     *
     * @param requestItemEntity the request item entity
     * @param username          the username
     * @return the item hold response
     */
    public ItemHoldResponse cancelRequest(RequestItemEntity requestItemEntity, String username) {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation(requestItemEntity);
        itemRequestInformation.setUsername(username);
        ItemHoldResponse itemCancelHoldResponse = (ItemHoldResponse) requestItemController.cancelHoldItem(itemRequestInformation, itemRequestInformation.getRequestingInstitution());
        logger.info("Deaccession Item - Cancel request status : {}", itemCancelHoldResponse.getScreenMessage());
        if (itemCancelHoldResponse.isSuccess()) {
            updateRequestAsCanceled(requestItemEntity, username);
            itemCancelHoldResponse.setSuccess(true);
            itemCancelHoldResponse.setScreenMessage(RecapConstants.REQUEST_CANCELLATION_SUCCCESS);
        }
        return itemCancelHoldResponse;
    }

    /**
     * Updates request status to canceled.
     *
     * @param requestItemEntity
     * @param username
     */
    private void updateRequestAsCanceled(RequestItemEntity requestItemEntity, String username) {
        RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_CANCELED);
        requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.getItemEntity().setItemAvailabilityStatusId(2);
        String requestNotes = requestItemEntity.getNotes();
        requestNotes = requestNotes + "\n" + "SCSB : " + RecapConstants.REQUEST_ITEM_CANCELED_FOR_DEACCESSION;
        requestItemEntity.setNotes(requestNotes);
        RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        saveDeAccessionItemChangeLogEntity(savedRequestItemEntity.getId(), username, RecapConstants.REQUEST_ITEM_CANCEL_DEACCESSION_ITEM, RecapConstants.REQUEST_ITEM_CANCELED_FOR_DEACCESSION + savedRequestItemEntity.getItemId());
        itemRequestServiceUtil.updateSolrIndex(savedRequestItemEntity.getItemEntity());
    }

    private void saveDeAccessionItemChangeLogEntity(Integer requestId, String deaccessionUser, String operationType, String notes) {
        DeaccessionItemChangeLog itemChangeLogEntity = new DeaccessionItemChangeLog();
        itemChangeLogEntity.setUpdatedBy(deaccessionUser);
        itemChangeLogEntity.setCreatedDate(new Date());
        itemChangeLogEntity.setOperationType(operationType);
        itemChangeLogEntity.setRecordId(requestId);
        itemChangeLogEntity.setNotes(notes);
        deaccesionItemChangeLogDetailsRepository.save(itemChangeLogEntity);
    }

    private void saveDeAccessionItemChangeLogEntities(List<Integer> itemIds, String deaccessionUser, String operationType, Date updatedDate, String notes, Map<Integer, String> itemIdAndMessageMap) {
        List<DeaccessionItemChangeLog> itemChangeLogEntities = new ArrayList<>();
        for (Integer itemId : itemIds) {
            DeaccessionItemChangeLog itemChangeLogEntity = new DeaccessionItemChangeLog();
            itemChangeLogEntity.setUpdatedBy(deaccessionUser);
            itemChangeLogEntity.setCreatedDate(updatedDate);
            itemChangeLogEntity.setOperationType(operationType);
            itemChangeLogEntity.setRecordId(itemId);
            itemChangeLogEntity.setNotes(itemIdAndMessageMap.get(itemId) + notes);
            itemChangeLogEntities.add(itemChangeLogEntity);
        }
        deaccesionItemChangeLogDetailsRepository.saveAll(itemChangeLogEntities);
    }

    private int getHoldQueueLength(ItemInformationResponse itemInformationResponse) {
        int iholdQueue = 0;
        if (StringUtils.isNotBlank(itemInformationResponse.getHoldQueueLength())) {
            iholdQueue = Integer.parseInt(itemInformationResponse.getHoldQueueLength());
        }
        return iholdQueue;
    }

    /**
     * De accession items in db.
     *
     * @param barcodeAndStopCodeMap         the barcode and stop code map
     * @param deAccessionDBResponseEntities the de accession db response entities
     * @param username                      the username
     */
    public void deAccessionItemsInDB(Map<String, String> barcodeAndStopCodeMap, List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities, String username) {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity;
        Date currentDate = new Date();
        Set<String> itemBarcodeList = barcodeAndStopCodeMap.keySet();
        List<ItemEntity> itemEntityList = itemDetailsRepository.findByBarcodeIn(new ArrayList<>(itemBarcodeList));
        try {
            String barcode = null;
            String deliveryLocation = null;
            for(ItemEntity itemEntity : itemEntityList) {
                try {
                    barcode = itemEntity.getBarcode();
                    deliveryLocation = barcodeAndStopCodeMap.get(barcode);
                    List<HoldingsEntity> holdingsEntities = itemEntity.getHoldingsEntities();
                    List<BibliographicEntity> bibliographicEntities = itemEntity.getBibliographicEntities();
                    Integer itemId = itemEntity.getId();
                    List<Integer> holdingsIds = processHoldings(holdingsEntities, username);
                    List<Integer> bibliographicIds = processBibs(bibliographicEntities, username);
                    itemDetailsRepository.markItemAsDeleted(itemId, username, currentDate);
                    deAccessionDBResponseEntity = prepareSuccessResponse(barcode, deliveryLocation, itemEntity, holdingsIds, bibliographicIds);
                    deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                } catch (Exception ex) {
                    logger.error(RecapCommonConstants.LOG_ERROR, ex);
                    deAccessionDBResponseEntity = prepareFailureResponse(barcode, deliveryLocation, "Exception" + ex, null);
                    deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                }
            }
        } catch (Exception ex) {
            logger.error(RecapCommonConstants.LOG_ERROR,ex);
        }
    }

    /**
     * Process and save list.
     *
     * @param deAccessionDBResponseEntities the de accession db response entities
     * @return the list
     */
    public List<ReportEntity> processAndSaveReportEntities(List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities) {
        List<ReportEntity> reportEntities = new ArrayList<>();
        ReportEntity reportEntity = null;
        if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                List<String> owningInstitutionBibIds = deAccessionDBResponseEntity.getOwningInstitutionBibIds();
                if (CollectionUtils.isNotEmpty(owningInstitutionBibIds)) {
                    for (String owningInstitutionBibId : owningInstitutionBibIds) {
                        reportEntity = generateReportEntity(deAccessionDBResponseEntity, owningInstitutionBibId);
                        reportEntities.add(reportEntity);
                    }
                } else {
                    reportEntity = generateReportEntity(deAccessionDBResponseEntity, null);
                    reportEntities.add(reportEntity);
                }
            }
            if (!CollectionUtils.isEmpty(reportEntities)) {
                reportDetailRepository.saveAll(reportEntities);
            }
        }
        return reportEntities;
    }

    private ReportEntity generateReportEntity(DeAccessionDBResponseEntity deAccessionDBResponseEntity, String owningInstitutionBibId) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapCommonConstants.DEACCESSION_REPORT);
        reportEntity.setType(RecapCommonConstants.DEACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity dateReportDataEntity = new ReportDataEntity();
        dateReportDataEntity.setHeaderName(RecapCommonConstants.DATE_OF_DEACCESSION);
        dateReportDataEntity.setHeaderValue(formatter.format(new Date()));
        reportDataEntities.add(dateReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getInstitutionCode())) {
            reportEntity.setInstitutionName(deAccessionDBResponseEntity.getInstitutionCode());

            ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();
            owningInstitutionReportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION);
            owningInstitutionReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getInstitutionCode());
            reportDataEntities.add(owningInstitutionReportDataEntity);
        } else {
            reportEntity.setInstitutionName("NA");
        }

        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
        barcodeReportDataEntity.setHeaderName(RecapCommonConstants.BARCODE);
        barcodeReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getBarcode());
        reportDataEntities.add(barcodeReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(owningInstitutionBibId)) {
            ReportDataEntity owningInstitutionBibIdReportDataEntity = new ReportDataEntity();
            owningInstitutionBibIdReportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INST_BIB_ID);
            owningInstitutionBibIdReportDataEntity.setHeaderValue(owningInstitutionBibId);
            reportDataEntities.add(owningInstitutionBibIdReportDataEntity);
        }

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getCollectionGroupCode())) {
            ReportDataEntity collectionGroupCodeReportDataEntity = new ReportDataEntity();
            collectionGroupCodeReportDataEntity.setHeaderName(RecapCommonConstants.COLLECTION_GROUP_CODE);
            collectionGroupCodeReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getCollectionGroupCode());
            reportDataEntities.add(collectionGroupCodeReportDataEntity);
        }

        ReportDataEntity statusReportDataEntity = new ReportDataEntity();
        statusReportDataEntity.setHeaderName(RecapCommonConstants.STATUS);
        statusReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getStatus());
        reportDataEntities.add(statusReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getReasonForFailure())) {
            ReportDataEntity reasonForFailureReportDataEntity = new ReportDataEntity();
            reasonForFailureReportDataEntity.setHeaderName(RecapCommonConstants.REASON_FOR_FAILURE);
            reasonForFailureReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getReasonForFailure());
            reportDataEntities.add(reasonForFailureReportDataEntity);
        }

        reportEntity.setReportDataEntities(reportDataEntities);
        return reportEntity;
    }

    private DeAccessionDBResponseEntity prepareSuccessResponse(String itemBarcode, String deliveryLocation, ItemEntity itemEntity, List<Integer> holdingIds, List<Integer> bibliographicIds) throws JSONException {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode(itemBarcode);
        deAccessionDBResponseEntity.setDeliveryLocation(deliveryLocation);
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.SUCCESS);
        populateDeAccessionDBResponseEntity(itemEntity, deAccessionDBResponseEntity);
        deAccessionDBResponseEntity.setHoldingIds(holdingIds);
        deAccessionDBResponseEntity.setBibliographicIds(bibliographicIds);
        return deAccessionDBResponseEntity;
    }

    private DeAccessionDBResponseEntity prepareFailureResponse(String itemBarcode, String deliveryLocation, String reasonForFailure, ItemEntity itemEntity) {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode(itemBarcode);
        deAccessionDBResponseEntity.setDeliveryLocation(deliveryLocation);
        deAccessionDBResponseEntity.setStatus(RecapCommonConstants.FAILURE);
        deAccessionDBResponseEntity.setReasonForFailure(reasonForFailure);
        if (itemEntity != null) {
            try {
                populateDeAccessionDBResponseEntity(itemEntity, deAccessionDBResponseEntity);
            } catch (JSONException e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
        }
        return deAccessionDBResponseEntity;
    }

    private void populateDeAccessionDBResponseEntity(ItemEntity itemEntity, DeAccessionDBResponseEntity deAccessionDBResponseEntity) throws JSONException {
        ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
        if (itemStatusEntity != null) {
            deAccessionDBResponseEntity.setItemStatus(itemStatusEntity.getStatusCode());
        }
        InstitutionEntity institutionEntity = itemEntity.getInstitutionEntity();
        if (institutionEntity != null) {
            deAccessionDBResponseEntity.setInstitutionCode(institutionEntity.getInstitutionCode());
        }
        ImsLocationEntity imsLocationEntity = itemEntity.getImsLocationEntity();
        if (imsLocationEntity != null) {
            deAccessionDBResponseEntity.setImsLocationCode(imsLocationEntity.getImsLocationCode());
        }
        CollectionGroupEntity collectionGroupEntity = itemEntity.getCollectionGroupEntity();
        if (collectionGroupEntity != null) {
            deAccessionDBResponseEntity.setCollectionGroupCode(collectionGroupEntity.getCollectionGroupCode());
        }
        deAccessionDBResponseEntity.setCustomerCode(itemEntity.getCustomerCode());
        deAccessionDBResponseEntity.setItemId(itemEntity.getId());
        List<BibliographicEntity> bibliographicEntities = itemEntity.getBibliographicEntities();
        List<String> owningInstitutionBibIds = new ArrayList<>();
        for (BibliographicEntity bibliographicEntity : bibliographicEntities) {
            String owningInstitutionBibId = bibliographicEntity.getOwningInstitutionBibId();
            owningInstitutionBibIds.add(owningInstitutionBibId);
        }
        deAccessionDBResponseEntity.setOwningInstitutionBibIds(owningInstitutionBibIds);
    }

    private List<Integer> processBibs(List<BibliographicEntity> bibliographicEntities, String username) throws JSONException {
        List<Integer> bibliographicIds = new ArrayList<>();
        for (BibliographicEntity bibliographicEntity : bibliographicEntities) {
            Integer owningInstitutionId = bibliographicEntity.getOwningInstitutionId();
            String owningInstitutionBibId = bibliographicEntity.getOwningInstitutionBibId();
            Long nonDeletedItemsCount = bibliographicDetailsRepository.getNonDeletedItemsCount(owningInstitutionId, owningInstitutionBibId);
            if (nonDeletedItemsCount == 1) {
                bibliographicIds.add(bibliographicEntity.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(bibliographicIds)) {
            bibliographicDetailsRepository.markBibsAsDeleted(bibliographicIds, username, new Date());
        }
        return bibliographicIds;
    }

    private List<Integer> processHoldings(List<HoldingsEntity> holdingsEntities, String username) throws JSONException {
        List<Integer> holdingIds = new ArrayList<>();
        for (HoldingsEntity holdingsEntity : holdingsEntities) {
            Integer owningInstitutionId = holdingsEntity.getOwningInstitutionId();
            String owningInstitutionHoldingsId = holdingsEntity.getOwningInstitutionHoldingsId();
            Long nonDeletedItemsCount = holdingsDetailsRepository.getNonDeletedItemsCount(owningInstitutionId, owningInstitutionHoldingsId);
            if (nonDeletedItemsCount == 1) {
                holdingIds.add(holdingsEntity.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(holdingIds)) {
            holdingsDetailsRepository.markHoldingsAsDeleted(holdingIds, username, new Date());
        }
        return holdingIds;
    }

    /**
     * De accession items in solr
     * @param deAccessionDBResponseEntities
     * @param resultMap
     */
    public void deAccessionItemsInSolr(List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities, Map<String, String> resultMap) {
        try {
            if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
                List<Integer> bibIds = new ArrayList<>();
                List<Integer> holdingsIds = new ArrayList<>();
                List<Integer> itemIds = new ArrayList<>();
                for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                    if (deAccessionDBResponseEntity.getStatus().equalsIgnoreCase(RecapCommonConstants.FAILURE)) {
                        resultMap.put(deAccessionDBResponseEntity.getBarcode(), deAccessionDBResponseEntity.getStatus() + " - " + deAccessionDBResponseEntity.getReasonForFailure());
                    } else if (deAccessionDBResponseEntity.getStatus().equalsIgnoreCase(RecapCommonConstants.SUCCESS)) {
                        resultMap.put(deAccessionDBResponseEntity.getBarcode(), deAccessionDBResponseEntity.getStatus());
                        bibIds.addAll(deAccessionDBResponseEntity.getBibliographicIds());
                        holdingsIds.addAll(deAccessionDBResponseEntity.getHoldingIds());
                        itemIds.add(deAccessionDBResponseEntity.getItemId());
                    }
                }
                if (CollectionUtils.isNotEmpty(bibIds) || CollectionUtils.isNotEmpty(holdingsIds) || CollectionUtils.isNotEmpty(itemIds)) {
                    String deAccessionSolrClientUrl = scsbSolrClientUrl + RecapConstants.DEACCESSION_IN_SOLR_URL;
                    DeAccessionSolrRequest deAccessionSolrRequest = new DeAccessionSolrRequest();
                    deAccessionSolrRequest.setBibIds(bibIds);
                    deAccessionSolrRequest.setHoldingsIds(holdingsIds);
                    deAccessionSolrRequest.setItemIds(itemIds);

                    RestTemplate restTemplate = new RestTemplate();
                    HttpEntity<DeAccessionSolrRequest> requestEntity = new HttpEntity<>(deAccessionSolrRequest, getRestHeaderService().getHttpHeaders());
                    ResponseEntity<String> responseEntity = restTemplate.exchange(deAccessionSolrClientUrl, HttpMethod.POST, requestEntity, String.class);
                    logger.info("Deaccession Item Solr update status : {}" , responseEntity.getBody());
                }
            }
        } catch (Exception e) {
            logger.error(EXCEPTION_CONSTANT, e);
        }
    }

    private void processAndSaveDeaccessionChangeLog(DeAccessionRequest deAccessionRequest, String userName, List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities) {
        if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
            for (DeAccessionDBResponseEntity deAccessionItem : deAccessionDBResponseEntities) {
                if (deAccessionItem.getStatus().contains(RecapCommonConstants.SUCCESS)) {
                    DeaccessionItemChangeLog itemChangeLogEntity = new DeaccessionItemChangeLog();
                    itemChangeLogEntity.setUpdatedBy(userName);
                    itemChangeLogEntity.setCreatedDate(new Date());
                    itemChangeLogEntity.setOperationType(RecapCommonConstants.DEACCESSION);
                    itemChangeLogEntity.setRecordId(deAccessionItem.getItemId());
                    String notes = deAccessionRequest.getNotes() != null ? deAccessionRequest.getNotes() : "";
                    itemChangeLogEntity.setNotes(notes);
                    deaccesionItemChangeLogDetailsRepository.save(itemChangeLogEntity);
                }
            }
        }
    }
}
