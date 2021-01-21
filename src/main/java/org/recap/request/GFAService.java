package org.recap.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.gfa.model.*;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.deaccession.DeAccessionDBResponseEntity;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.recap.model.jpa.*;
import org.recap.processor.LasItemStatusCheckPollingProcessor;
import org.recap.repository.jpa.*;
import org.recap.util.CommonUtil;
import org.recap.util.ItemRequestServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by sudhishk on 27/1/17.
 */
@Service
public class GFAService {

    private static final Logger logger = LoggerFactory.getLogger(GFAService.class);

    @Value("${gfa.item.status}")
    private String gfaItemStatus;

    @Value("${gfa.las.status}")
    private String gfaLasStatus;

    @Value("${gfa.item.retrieval.order}")
    private String gfaItemRetrival;

    @Value("${gfa.item.edd.retrieval.order}")
    private String gfaItemEDDRetrival;

    @Value("${gfa.item.permanent.withdrawl.direct}")
    private String gfaItemPermanentWithdrawlDirect;

    @Value("${gfa.item.permanent.withdrawl.indirect}")
    private String gfaItemPermanentWithdrawlInDirect;

    @Value("${las.use.queue}")
    private boolean useQueueLasCall;

    @Value("${gfa.server.response.timeout.milliseconds}")
    private Integer gfaServerResponseTimeOutMilliseconds;

    @Value("${recap-las.email.recap.assist.email.to}")
    private String recapAssistanceEmailTo;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    private LasItemStatusCheckPollingProcessor lasItemStatusCheckPollingProcessor;

    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    CommonUtil commonUtil;


    /**
     * Gets gfa item status.
     *
     * @return the gfa item status
     */
    public String getGfaItemStatus() {
        return gfaItemStatus;
    }

    /**
     * Gets gfa heart beat.
     *
     * @return the gfa heart beat
     */
    public String getGfaHeartBeat() {
        return gfaLasStatus;
    }

    /**
     * Gets gfa item retrival.
     *
     * @return the gfa item retrival
     */
    public String getGfaItemRetrival() {
        return gfaItemRetrival;
    }

    /**
     * Gets gfa item edd retrival.
     *
     * @return the gfa item edd retrival
     */
    public String getGfaItemEDDRetrival() {
        return gfaItemEDDRetrival;
    }

    /**
     * Gets gfa item permanent withdrawl direct.
     *
     * @return the gfa item permanent withdrawl direct
     */
    public String getGfaItemPermanentWithdrawlDirect() {
        return gfaItemPermanentWithdrawlDirect;
    }

    /**
     * Gets gfa item permanent withdrawl in direct.
     *
     * @return the gfa item permanent withdrawl in direct
     */
    public String getGfaItemPermanentWithdrawlInDirect() {
        return gfaItemPermanentWithdrawlInDirect;
    }

    /**
     * Get gfa retrieve edd item request gfa retrieve edd item request.
     *
     * @return the gfa retrieve edd item request
     */
    public GFARetrieveEDDItemRequest getGFARetrieveEDDItemRequest() {
        return new GFARetrieveEDDItemRequest();
    }

    /**
     * Gets rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Gets producer.
     *
     * @return the producer
     */
    public ProducerTemplate getProducer() {
        return producer;
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
    public boolean isUseQueueLasCall() {
        return useQueueLasCall;
    }

    /**
     * Gets request item details repository.
     *
     * @return the request item details repository
     */
    public RequestItemDetailsRepository getRequestItemDetailsRepository() {
        return requestItemDetailsRepository;
    }

    /**
     * Gets item details repository.
     *
     * @return the item details repository
     */
    public ItemDetailsRepository getItemDetailsRepository() {
        return itemDetailsRepository;
    }

    /**
     * Gets item status details repository.
     *
     * @return the item status details repository
     */
    public ItemStatusDetailsRepository getItemStatusDetailsRepository() {
        return itemStatusDetailsRepository;
    }

    /**
     * Gets item change log details repository.
     *
     * @return the item change log details repository
     */
    public ItemChangeLogDetailsRepository getItemChangeLogDetailsRepository() {
        return itemChangeLogDetailsRepository;
    }

    /**
     * Gets gfa server response time out milliseconds.
     *
     * @return the gfa server response time out milliseconds
     */
    public Integer getGfaServerResponseTimeOutMilliseconds() {
        return gfaServerResponseTimeOutMilliseconds;
    }

    /**
     * Item status check gfa item status check response.
     *
     * @param gfaItemStatusCheckRequest the gfa item status check request
     * @return the gfa item status check response
     */
    public GFAItemStatusCheckResponse itemStatusCheck(GFAItemStatusCheckRequest gfaItemStatusCheckRequest) {

        ObjectMapper objectMapper = new ObjectMapper();
        String filterParamValue = "";
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = null;
        try {
            filterParamValue = objectMapper.writeValueAsString(gfaItemStatusCheckRequest);
            logger.info(filterParamValue);

            RestTemplate restTemplate = getRestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(new HttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getGfaItemStatus()).queryParam(RecapConstants.GFA_SERVICE_PARAM, filterParamValue);
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(getGfaServerResponseTimeOutMilliseconds());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(getGfaServerResponseTimeOutMilliseconds());
            ResponseEntity<GFAItemStatusCheckResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFAItemStatusCheckResponse.class);
            if (responseEntity != null && responseEntity.getBody() != null) {
                gfaItemStatusCheckResponse = responseEntity.getBody();
            }
            if (responseEntity != null && responseEntity.getStatusCode() != null) {
                logger.info(String.format("Item status check response : %s" , responseEntity.getStatusCode()));
            }
        } catch (JsonProcessingException e) {
            logger.error(RecapConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION + " " , e.getMessage());
        }

        return gfaItemStatusCheckResponse;
    }

    /**
     * Item status check gfa heart beat check response.
     *
     * @param gfaLasStatusCheckRequest the gfa heart beat check request
     * @return the gfa heart beat check response
     */
    public GFALasStatusCheckResponse heartBeatCheck(GFALasStatusCheckRequest gfaLasStatusCheckRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String filterParamValue = "";
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = null;
        try {
            filterParamValue = objectMapper.writeValueAsString(gfaLasStatusCheckRequest);
            logger.info("Heart Beat Request: {}", filterParamValue);

            RestTemplate restTemplate = getRestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(new HttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getGfaHeartBeat()).queryParam(RecapConstants.GFA_SERVICE_PARAM, filterParamValue);
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(getGfaServerResponseTimeOutMilliseconds());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(getGfaServerResponseTimeOutMilliseconds());
            ResponseEntity<GFALasStatusCheckResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFALasStatusCheckResponse.class);
            if (responseEntity != null && responseEntity.getBody() != null) {
                gfaLasStatusCheckResponse = responseEntity.getBody();
                logger.info("Heart Beat Response: {}", gfaLasStatusCheckResponse);
            }
        } catch (JsonProcessingException e) {
            logger.error(RecapConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION + "{}", e.getMessage());
        }
        return gfaLasStatusCheckResponse;
    }

    /**
     * For the given item entities this method prepares item barcodes to check status with LAS.
     *
     * @param itemEntities the item entities
     * @return the gfa item status check response
     */
    public GFAItemStatusCheckResponse getGFAItemStatusCheckResponse(List<ItemEntity> itemEntities) {
        return getGFAItemStatusCheckResponseByBarcodes(commonUtil.getBarcodesList(itemEntities));
    }

    /**
     * For the given item barcodes this method checks status with LAS.
     *
     * @param itemBarcodes the item entities
     * @return the gfa item status check response
     */
    public GFAItemStatusCheckResponse getGFAItemStatusCheckResponseByBarcodes(List<String> itemBarcodes) {
        if (itemBarcodes != null) {
            List<GFAItemStatus> gfaItemStatusList = new ArrayList<>();
            for (String itemBarcode : itemBarcodes) {
                GFAItemStatus gfaItemStatusResponse = new GFAItemStatus();
                gfaItemStatusResponse.setItemBarCode(itemBarcode);
                gfaItemStatusList.add(gfaItemStatusResponse);
            }
            GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
            gfaItemStatusCheckRequest.setItemStatus(gfaItemStatusList);
            return itemStatusCheck(gfaItemStatusCheckRequest);
        }
        return new GFAItemStatusCheckResponse();
    }

    /**
     * Item retrival gfa retrieve item response.
     *
     * @param gfaRetrieveItemRequest the gfa retrieve item request
     * @return the gfa retrieve item response
     */
    public GFARetrieveItemResponse itemRetrieval(GFARetrieveItemRequest gfaRetrieveItemRequest) {
        GFARetrieveItemResponse gfaRetrieveItemResponse = null;
        ResponseEntity<GFARetrieveItemResponse> responseEntity = null;
        try {
            HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveItemRequest, getHttpHeaders());
            responseEntity = getRestTemplate().exchange(getGfaItemRetrival(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                gfaRetrieveItemResponse = responseEntity.getBody();
                gfaRetrieveItemResponse = getLASRetrieveResponse(gfaRetrieveItemResponse);
            } else {
                gfaRetrieveItemResponse = new GFARetrieveItemResponse();
                gfaRetrieveItemResponse.setSuccess(false);
            }
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            gfaRetrieveItemResponse = new GFARetrieveItemResponse();
            gfaRetrieveItemResponse.setSuccess(false);
            gfaRetrieveItemResponse.setScreenMessage(RecapConstants.REQUEST_LAS_EXCEPTION + RecapCommonConstants.LAS_SERVER_NOT_REACHABLE);
        } catch (Exception e) {
            gfaRetrieveItemResponse = new GFARetrieveItemResponse();
            gfaRetrieveItemResponse.setSuccess(false);
            gfaRetrieveItemResponse.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaRetrieveItemResponse;
    }

    private GFARetrieveItemResponse getLASRetrieveResponse(GFARetrieveItemResponse gfaRetrieveItemResponseParam) {
        GFARetrieveItemResponse gfaRetrieveItemResponse = gfaRetrieveItemResponseParam;
        if (gfaRetrieveItemResponse != null && gfaRetrieveItemResponse.getDsitem() != null && gfaRetrieveItemResponse.getDsitem().getTtitem() != null && !gfaRetrieveItemResponse.getDsitem().getTtitem().isEmpty()) {
            List<Ttitem> titemList = gfaRetrieveItemResponse.getDsitem().getTtitem();
            for (Ttitem ttitem : titemList) {
                if (StringUtils.isNotBlank(ttitem.getErrorCode())) {
                    gfaRetrieveItemResponse.setSuccess(false);
                    gfaRetrieveItemResponse.setScreenMessage(ttitem.getErrorNote());
                } else {
                    gfaRetrieveItemResponse.setSuccess(true);
                }
            }
        } else {
            if (gfaRetrieveItemResponse == null) {
                gfaRetrieveItemResponse = new GFARetrieveItemResponse();
            }
            gfaRetrieveItemResponse.setSuccess(true);
        }
        return gfaRetrieveItemResponse;
    }

    private GFAEddItemResponse getLASEddResponse(GFAEddItemResponse gfaEddItemResponseParam) {
        GFAEddItemResponse gfaEddItemResponse = gfaEddItemResponseParam;
        if (gfaEddItemResponse != null && gfaEddItemResponse.getDsitem() != null && gfaEddItemResponse.getDsitem().getTtitem() != null && !gfaEddItemResponse.getDsitem().getTtitem().isEmpty()) {
            List<TtitemEDDResponse> titemList = gfaEddItemResponse.getDsitem().getTtitem();
            for (TtitemEDDResponse ttitem : titemList) {
                if (StringUtils.isNotBlank(ttitem.getErrorCode())) {
                    gfaEddItemResponse.setSuccess(false);
                    gfaEddItemResponse.setScreenMessage(ttitem.getErrorNote());
                } else {
                    gfaEddItemResponse.setSuccess(true);
                }
            }
        } else {
            if (gfaEddItemResponse == null) {
                gfaEddItemResponse = new GFAEddItemResponse();
            }
            gfaEddItemResponse.setSuccess(true);
        }
        return gfaEddItemResponse;
    }

    /**
     * Item edd retrival gfa retrieve item response.
     *
     * @param gfaRetrieveEDDItemRequest the gfa retrieve edd item request
     * @return the gfa retrieve item response
     */
    public GFAEddItemResponse itemEDDRetrieval(GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest) {
        GFAEddItemResponse gfaEddItemResponse = null;
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse;
        try {
            GFAItemStatus gfaItemStatus001 = new GFAItemStatus();
            gfaItemStatus001.setItemBarCode(gfaRetrieveEDDItemRequest.getDsitem().getTtitem().get(0).getItemBarcode());
            List<GFAItemStatus> gfaItemStatuses = new ArrayList<>();
            gfaItemStatuses.add(gfaItemStatus001);
            gfaItemStatusCheckRequest.setItemStatus(gfaItemStatuses);
            gfaItemStatusCheckResponse = itemStatusCheck(gfaItemStatusCheckRequest);
            if (gfaItemStatusCheckResponse != null
                    && gfaItemStatusCheckResponse.getDsitem() != null
                    && gfaItemStatusCheckResponse.getDsitem().getTtitem() != null && !gfaItemStatusCheckResponse.getDsitem().getTtitem().isEmpty()) {

                RestTemplate restTemplate = getRestTemplate();
                HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
                logger.info("{}" , convertJsontoString(requestEntity.getBody()));
                ResponseEntity<GFAEddItemResponse> responseEntity = restTemplate.exchange(getGfaItemEDDRetrival(), HttpMethod.POST, requestEntity, GFAEddItemResponse.class);
                logger.info("{}", responseEntity.getStatusCode() + " - " + convertJsontoString(responseEntity.getBody()));
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    gfaEddItemResponse = responseEntity.getBody();
                    gfaEddItemResponse = getLASEddResponse(gfaEddItemResponse);
                } else {
                    gfaEddItemResponse = new GFAEddItemResponse();
                    gfaEddItemResponse.setSuccess(false);
                    gfaEddItemResponse.setScreenMessage(RecapConstants.REQUEST_LAS_EXCEPTION + "HTTP Error response from LAS");
                }
            } else {
                gfaEddItemResponse = new GFAEddItemResponse();
                gfaEddItemResponse.setSuccess(false);
                gfaEddItemResponse.setScreenMessage(RecapConstants.GFA_ITEM_STATUS_CHECK_FAILED);
            }
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            gfaEddItemResponse = new GFAEddItemResponse();
            gfaEddItemResponse.setSuccess(false);
            gfaEddItemResponse.setScreenMessage(RecapConstants.REQUEST_LAS_EXCEPTION + RecapCommonConstants.LAS_SERVER_NOT_REACHABLE);
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        } catch (Exception e) {
            gfaEddItemResponse = new GFAEddItemResponse();
            gfaEddItemResponse.setSuccess(false);
            gfaEddItemResponse.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaEddItemResponse;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
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
            gfaItemStatusCheckResponse = itemStatusCheck(gfaItemStatusCheckRequest);
            if (gfaItemStatusCheckResponse != null
                    && gfaItemStatusCheckResponse.getDsitem() != null
                    && gfaItemStatusCheckResponse.getDsitem().getTtitem() != null && !gfaItemStatusCheckResponse.getDsitem().getTtitem().isEmpty()) {

                itemStatus = gfaItemStatusCheckResponse.getDsitem().getTtitem().get(0).getItemStatus();
                if (itemStatus.contains(":")) {
                    gfaOnlyStatus = itemStatus.substring(0, itemStatus.indexOf(':') + 1).toUpperCase();
                } else {
                    gfaOnlyStatus = itemStatus.toUpperCase();
                }

                logger.info("Item status check before executing {} Order. Status received : {}", itemRequestInfo.getRequestType(), gfaOnlyStatus);
                // Call Retrieval Order
                if (StringUtils.isBlank(itemRequestInfo.getImsLocationCode())) {
                    itemResponseInformation.setSuccess(false);
                    itemResponseInformation.setScreenMessage(RecapConstants.REQUEST_SCSB_EXCEPTION + RecapConstants.IMS_LOCATION_CODE_BLANK_ERROR);
                }
                else if (RecapConstants.getGFAStatusAvailableList().contains(gfaOnlyStatus)) {
                    if (itemRequestInfo.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)) {
                        itemResponseInformation = callItemEDDRetrievable(itemRequestInfo, itemResponseInformation);
                    } else if (itemRequestInfo.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)) {
                        itemResponseInformation = callItemRetrievable(itemRequestInfo, itemResponseInformation);
                    }
                }
                else if (RecapConstants.GFA_STATUS_SCH_ON_REFILE_WORK_ORDER.toLowerCase().contains(gfaOnlyStatus.toLowerCase())){
                    logger.info("Request Received while GFA status is Sch on Refile WO");
                    itemResponseInformation.setRequestTypeForScheduledOnWO(true);
                    itemResponseInformation.setSuccess(true);
                    itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
                }
                else {
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
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
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
            ttitem001.setDestination(itemRequestInfo.getDeliveryLocation());
            ttitem001.setRequestId(itemRequestInfo.getRequestId().toString());
            ttitem001.setRequestor(itemRequestInfo.getPatronBarcode());

            List<TtitemRequest> ttitems = new ArrayList<>();
            ttitems.add(ttitem001);
            RetrieveItemRequest retrieveItem = new RetrieveItemRequest();
            retrieveItem.setTtitem(ttitems);
            gfaRetrieveItemRequest.setDsitem(retrieveItem);

            if (isUseQueueLasCall()) { // Queue
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(gfaRetrieveItemRequest);
                getProducer().sendBodyAndHeader(RecapConstants.SCSB_OUTGOING_QUEUE, itemRequestInfo, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                GFARetrieveItemResponse gfaRetrieveItemResponse = itemRetrieval(gfaRetrieveItemRequest);
                if (gfaRetrieveItemResponse.isSuccess()) {
                    itemResponseInformation.setSuccess(true);
                    itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
                } else {
                    itemResponseInformation.setSuccess(false);
                    itemResponseInformation.setScreenMessage(gfaRetrieveItemResponse.getScreenMessage());
                }
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
        }
        return itemResponseInformation;
    }

    public ItemInformationResponse gfaItemRequestProcessor(Exchange exchange) {
        ItemInformationResponse itemInformationResponse = null;
        ItemRequestInformation itemRequestInfo = (ItemRequestInformation) exchange.getIn().getBody();
        logger.info("Message Processing from LAS OUTGOING QUEUE: {}", itemRequestInfo.toString());

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
            ttitem001.setDestination(itemRequestInfo.getDeliveryLocation());
            ttitem001.setRequestId(itemRequestInfo.getRequestId().toString());
            ttitem001.setRequestor(itemRequestInfo.getPatronBarcode());

            List<TtitemRequest> ttitems = new ArrayList<>();
            ttitems.add(ttitem001);
            RetrieveItemRequest retrieveItem = new RetrieveItemRequest();
            retrieveItem.setTtitem(ttitems);
            gfaRetrieveItemRequest.setDsitem(retrieveItem);

            GFARetrieveItemResponse gfaRetrieveItemResponse = itemRetrieval(gfaRetrieveItemRequest);
            if (gfaRetrieveItemResponse.isSuccess()) {
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                itemResponseInformation.setSuccess(false);
                itemResponseInformation.setScreenMessage(gfaRetrieveItemResponse.getScreenMessage());
            }

            if (isUseQueueLasCall()) { // Queue
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(gfaRetrieveItemResponse);
                getProducer().sendBodyAndHeader(RecapConstants.LAS_INCOMING_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
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
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
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
            if (isUseQueueLasCall()) { // Queue
                ObjectMapper objectMapper = getObjectMapper();
                String json = objectMapper.writeValueAsString(itemRequestInfo);
                getProducer().sendBodyAndHeader(RecapConstants.SCSB_OUTGOING_QUEUE, itemRequestInfo, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                gfaEddItemResponse = itemEDDRetrieval(gfaRetrieveEDDItemRequest);
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
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    private ItemInformationResponse callItemEDDRetrieveApi(ItemRequestInformation itemRequestInfo) {
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = getGFARetrieveEDDItemRequest();
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

            gfaEddItemResponse = itemEDDRetrieval(gfaRetrieveEDDItemRequest);
            if (gfaEddItemResponse.isSuccess()) {
                itemResponseInformation.setSuccess(true);
                itemResponseInformation.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                itemResponseInformation.setSuccess(false);
                itemResponseInformation.setScreenMessage(gfaEddItemResponse.getScreenMessage());
            }

            if (isUseQueueLasCall()) { // Queue
                ObjectMapper objectMapper = getObjectMapper();
                String json = objectMapper.writeValueAsString(gfaEddItemResponse);
                getProducer().sendBodyAndHeader(RecapConstants.LAS_INCOMING_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
            }
        } catch (Exception e) {
            itemResponseInformation.setSuccess(false);
            itemResponseInformation.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    /**
     * Gfa permanent withdrawl direct gfa pwd response.
     *
     * @param gfaPwdRequest the gfa pwd request
     * @return the gfa pwd response
     */
    public GFAPwdResponse gfaPermanentWithdrawlDirect(GFAPwdRequest gfaPwdRequest) {
        GFAPwdResponse gfaPwdResponse = null;
        try {
            HttpEntity<GFAPwdRequest> requestEntity = new HttpEntity<>(gfaPwdRequest, getHttpHeaders());
            logger.info("GFA PWD Request : {}", convertJsontoString(requestEntity.getBody()));
            RestTemplate restTemplate = getRestTemplate();
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(getGfaServerResponseTimeOutMilliseconds());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(getGfaServerResponseTimeOutMilliseconds());
            ResponseEntity<GFAPwdResponse> responseEntity = restTemplate.exchange(getGfaItemPermanentWithdrawlDirect(), HttpMethod.POST, requestEntity, GFAPwdResponse.class);
            gfaPwdResponse = responseEntity.getBody();
            logger.info("GFA PWD Response Status Code : {}", responseEntity.getStatusCode());
            logger.info("GFA PWD Response : {}", convertJsontoString(responseEntity.getBody()));
            logger.info("GFA PWD item status processed");
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaPwdResponse;
    }

    /**
     * Gfa permanent withdrawl in direct gfa pwi response.
     *
     * @param gfaPwiRequest the gfa pwi request
     * @return the gfa pwi response
     */
    public GFAPwiResponse gfaPermanentWithdrawlInDirect(GFAPwiRequest gfaPwiRequest) {
        GFAPwiResponse gfaPwiResponse = null;
        try {
            HttpEntity<GFAPwiRequest> requestEntity = new HttpEntity<>(gfaPwiRequest, getHttpHeaders());
            logger.info("GFA PWI Request : {}", convertJsontoString(requestEntity.getBody()));
            RestTemplate restTemplate = getRestTemplate();
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(getGfaServerResponseTimeOutMilliseconds());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(getGfaServerResponseTimeOutMilliseconds());
            ResponseEntity<GFAPwiResponse> responseEntity = restTemplate.exchange(getGfaItemPermanentWithdrawlInDirect(), HttpMethod.POST, requestEntity, GFAPwiResponse.class);
            gfaPwiResponse = responseEntity.getBody();
            logger.info("GFA PWI Response Status Code : {}", responseEntity.getStatusCode());
            logger.info("GFA PWI Response : {}", convertJsontoString(responseEntity.getBody()));
            logger.info("GFA PWI item status processed");
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaPwiResponse;
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
            gfaRetrieveItemResponse = getLASRetrieveResponse(gfaRetrieveItemResponse);
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
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
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
            if (gfaEddItemResponse.isSuccess()) {
                itemInformationResponse.setRequestId(gfaEddItemResponse.getDsitem().getTtitem().get(0).getRequestId());
                itemInformationResponse.setSuccess(true);
                itemInformationResponse.setScreenMessage(RecapConstants.GFA_RETRIVAL_ORDER_SUCCESSFUL);
            } else {
                itemInformationResponse.setRequestId(gfaEddItemResponse != null ? gfaEddItemResponse.getDsitem().getTtitem().get(0).getRequestId() : 0);
                itemInformationResponse.setSuccess(false);
                itemInformationResponse.setScreenMessage(gfaEddItemResponse != null ? gfaEddItemResponse.getScreenMessage() : "");
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
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
            if(gfaRetrieveItemResponse != null) {
                gfaRetrieveItemResponse.setSuccess(true);
            }
        }
        return gfaRetrieveItemResponse;
    }

    private String convertJsontoString(Object objJson) {
        String strJson = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            strJson = objectMapper.writeValueAsString(objJson);
        } catch (JsonProcessingException e) {
            logger.error("", e);
        }
        return strJson;
    }

    public LasItemStatusCheckPollingProcessor getLasItemStatusCheckPollingProcessor() {
        return lasItemStatusCheckPollingProcessor;
    }

    public void setLasItemStatusCheckPollingProcessor(LasItemStatusCheckPollingProcessor lasItemStatusCheckPollingProcessor) {
        this.lasItemStatusCheckPollingProcessor = lasItemStatusCheckPollingProcessor;
    }

    private void lasPolling(ItemRequestInformation itemRequestInfo, ItemInformationResponse itemResponseInformation) {
        // Update Request_item_t table with new status - each Item
        try {
            RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_LAS_ITEM_STATUS_PENDING);
            RequestItemEntity requestItemEntity = requestItemDetailsRepository.findRequestItemById(itemRequestInfo.getRequestId());
            requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
            requestItemEntity.setLastUpdatedDate(new Date());
            requestItemDetailsRepository.save(requestItemEntity);
            logger.info("lasPolling Saved {}" , requestItemEntity.getRequestStatusEntity().getRequestStatusCode());
            ObjectMapper objectMapper = getObjectMapper();
            String json = null;
            RequestInformation requestInformation = new RequestInformation();
            requestInformation.setItemRequestInfo(itemRequestInfo);
            requestInformation.setItemResponseInformation(itemResponseInformation);
            json = objectMapper.writeValueAsString(requestInformation);
            logger.info(json);
            logger.info("Rest Service Status -> {}" , RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS);
            if (RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS == 0) {
                getProducer().getCamelContext().getRouteController().stopRoute(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID);
            }
            getProducer().sendBodyAndHeader(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
            itemRequestServiceUtil.updateSolrIndex(requestItemEntity.getItemEntity());
            if (RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS == 0) {
                // Start Polling program - Once
                startPolling(itemRequestInfo.getItemBarcodes().get(0));
            }
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException ", e);
        } catch (Exception e) {
            logger.error("Exception ", e);
        }
    }

    public void startPolling(String barcode) {
        try {
            logger.info("Start Polling Process Once");
            RecapConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS = 1;
            getLasItemStatusCheckPollingProcessor().pollLasItemStatusJobResponse(barcode, getProducer().getCamelContext());
        } catch (Exception e) {
            logger.error("Exception ", e);
        }
    }

    /**
     * Builds request order info based on request type and replaces into LAS queue.
     * @param requestItemEntity
     * @return
     */
    public String buildRequestInfoAndReplaceToLAS(RequestItemEntity requestItemEntity) {
        try {
            String json = null;
            String requestTypeCode = requestItemEntity.getRequestTypeEntity().getRequestTypeCode();
            if (RecapCommonConstants.RETRIEVAL.equalsIgnoreCase(requestTypeCode)) {
                GFARetrieveItemRequest gfaRetrieveItemRequest = buildGFARetrieveItemRequest(requestItemEntity);
                ObjectMapper objectMapper = new ObjectMapper();
                json = objectMapper.writeValueAsString(gfaRetrieveItemRequest);
            } else if (RecapConstants.EDD_REQUEST.equalsIgnoreCase(requestTypeCode)) {
                GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = buildGFAEddItemRequest(requestItemEntity);
                ObjectMapper objectMapper = new ObjectMapper();
                json = objectMapper.writeValueAsString(gfaRetrieveEDDItemRequest);
            }
            String itemStatus = callGfaItemStatus(requestItemEntity.getItemEntity().getBarcode());
            if (RecapConstants.getGFAStatusAvailableList().contains(itemStatus)) {
                getProducer().sendBodyAndHeader(RecapConstants.SCSB_OUTGOING_QUEUE, json, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, requestItemEntity.getRequestTypeEntity().getRequestTypeCode());
            } else {
                RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.LAS_REFILE_REQUEST_PLACED);
                requestItemEntity.setRequestStatusEntity(requestStatusEntity);
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                requestItemDetailsRepository.save(requestItemEntity);
            }
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, exception);
            return RecapCommonConstants.FAILURE + ":" + exception.getMessage();
        }
        return RecapCommonConstants.SUCCESS;
    }

    /**
     * Builds retrieval request order info.
     * @param requestItemEntity
     * @return
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
     * @param requestItemEntity
     * @return
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

    public String callGfaItemStatus(String itemBarcode) {
        String gfaItemStatusValue = null;
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatusCall = new GFAItemStatus();
        gfaItemStatusCall.setItemBarCode(itemBarcode);
        gfaItemStatusCheckRequest.setItemStatus(Collections.singletonList(gfaItemStatusCall));
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = itemStatusCheck(gfaItemStatusCheckRequest);
        if (null != gfaItemStatusCheckResponse) {
            Dsitem dsitem = gfaItemStatusCheckResponse.getDsitem();
            if (null != dsitem) {
                List<Ttitem> ttitems = dsitem.getTtitem();
                if (CollectionUtils.isNotEmpty(ttitems)) {
                    gfaItemStatusValue = ttitems.get(0).getItemStatus();
                }
            }
        }
        return gfaItemStatusValue;
    }

    public void callGfaDeaccessionService(List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities, String username) {
        if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
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
                    GFAPwdResponse gfaPwdResponse = gfaPermanentWithdrawlDirect(gfaPwdRequest);
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
                    GFAPwiResponse gfaPwiResponse = gfaPermanentWithdrawlInDirect(gfaPwiRequest);
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

}
