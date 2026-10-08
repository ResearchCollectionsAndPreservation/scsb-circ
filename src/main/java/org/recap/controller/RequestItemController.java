package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.PropertyKeyConstants;
import org.recap.common.ScsbConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ils.connector.factory.ILSProtocolConnectorFactory;
import org.recap.model.AbstractResponseItem;
import org.recap.model.BulkRequestInformation;
import org.recap.model.ItemRefileRequest;
import org.recap.model.request.RequestStatusRequest;
import org.recap.model.response.ItemCheckinResponse;
import org.recap.model.response.ItemCheckoutResponse;
import org.recap.model.response.ItemCreateBibResponse;
import org.recap.model.response.ItemHoldResponse;
import org.recap.model.response.ItemInformationResponse;
import org.recap.model.response.ItemRecallResponse;
import org.recap.model.response.PatronInformationResponse;
import org.recap.model.response.ItemRefileResponse;
import  org.recap.model.response.RequestStatusResponse;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.request.ReplaceRequest;
import org.recap.request.service.ItemRequestService;
import org.recap.service.RestHeaderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sudhishk on 16/11/16.
 * Class for all service part of Requesting Item Functionality
 */
@Slf4j
@RestController
@RequestMapping("/requestItem")
public class RequestItemController {



    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private RestHeaderService restHeaderService;

    /**
     * Gets ItemRequestService object.
     *
     * @return the item request service
     */
    public ItemRequestService getItemRequestService() {
        return itemRequestService;
    }

    /**
     * Gets JSIPConectorFactory object.
     *
     * @return the jsip conector factory
     */
    public ILSProtocolConnectorFactory getIlsProtocolConnectorFactory() {
        return ilsProtocolConnectorFactory;
    }

    public RestHeaderService getRestHeaderService(){
        return restHeaderService;
    }

    /**
     * Checkout item method is for processing SIP2 protocol function check out, This function converts SIP data to JSON format.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/checkoutItem", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem checkoutItem(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        String itemBarcode;
        try {
            String callInst = callingInstitution(callInstitution, itemRequestInformation);
            if (!itemRequestInformation.getItemBarcodes().isEmpty()) {
                itemBarcode = itemRequestInformation.getItemBarcodes().get(0);
                itemCheckoutResponse = (ItemCheckoutResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).checkOutItem(itemBarcode, itemRequestInformation.getRequestId(), itemRequestInformation.getPatronBarcode());
            } else {
                itemCheckoutResponse.setSuccess(false);
                itemCheckoutResponse.setScreenMessage("Item Id not found");
            }
        } catch (RuntimeException e) {
            itemCheckoutResponse.setSuccess(false);
            itemCheckoutResponse.setScreenMessage(e.getMessage());
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemCheckoutResponse;
    }

    /**
     * This method checkinItem is for processing SIP2 protocol function check in. This function converts SIP data to JSON format.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/checkinItem", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem checkinItem(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        ItemCheckinResponse itemCheckinResponse;
        try {
            String callInst = callingInstitution(callInstitution, itemRequestInformation);
            if (!itemRequestInformation.getItemBarcodes().isEmpty()) {
                log.info("Patron barcode and Institution info before CheckIn call : patron - {} , institution - {} ",itemRequestInformation.getPatronBarcode(),callInstitution);
                itemCheckinResponse = (ItemCheckinResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).checkInItem(itemRequestInformation, itemRequestInformation.getPatronBarcode());
                log.info("CheckIn Response Message : {}",itemCheckinResponse.getScreenMessage());
            } else {
                itemCheckinResponse = new ItemCheckinResponse();
                itemCheckinResponse.setSuccess(false);
                itemCheckinResponse.setScreenMessage("Item Id not found");
            }
        } catch (Exception e) {
            itemCheckinResponse = new ItemCheckinResponse();
            itemCheckinResponse.setSuccess(false);
            itemCheckinResponse.setScreenMessage(e.getMessage());
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemCheckinResponse;
    }

    /**
     * Hold item abstract response item.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/holdItem", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem holdItem(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        try {
            String callInst = callingInstitution(callInstitution, itemRequestInformation);
            String itembarcode = itemRequestInformation.getItemBarcodes().get(0);
            itemHoldResponse = (ItemHoldResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).placeHold(itembarcode, itemRequestInformation.getRequestId(),
                    itemRequestInformation.getPatronBarcode(),
                    itemRequestInformation.getRequestingInstitution(),
                    itemRequestInformation.getItemOwningInstitution(),
                    itemRequestInformation.getExpirationDate(),
                    itemRequestInformation.getBibId(),
                    getPickupLocationDB(itemRequestInformation, callInst),
                    itemRequestInformation.getTrackingId(),
                    itemRequestInformation.getTitleIdentifier(),
                    itemRequestInformation.getAuthor(),
                    itemRequestInformation.getCallNumber());

        } catch (RuntimeException e) {
            log.info(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage("ILS returned a invalid response");
        }
        return itemHoldResponse;
    }

    /**
     * Cancel hold item abstract response item.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/cancelHoldItem", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem cancelHoldItem(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        ItemHoldResponse itemHoldCancelResponse = null;
        String callInst = callingInstitution(callInstitution, itemRequestInformation);
        if (CollectionUtils.isNotEmpty(itemRequestInformation.getItemBarcodes()) && !itemRequestInformation.getItemBarcodes().get(0).equals("string")) {
            String itembarcode = itemRequestInformation.getItemBarcodes().get(0);
            itemHoldCancelResponse = (ItemHoldResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).cancelHold(itembarcode, itemRequestInformation.getRequestId(), itemRequestInformation.getPatronBarcode(),
                    itemRequestInformation.getRequestingInstitution(),
                    itemRequestInformation.getExpirationDate(),
                    itemRequestInformation.getBibId(),
                    getPickupLocationDB(itemRequestInformation, callInst), itemRequestInformation.getTrackingId());
        }
        else {
            itemHoldCancelResponse = new ItemHoldResponse();
            itemHoldCancelResponse.setSuccess(false);
            itemHoldCancelResponse.setScreenMessage("Please check the Item Barcode provided");
        }
        return itemHoldCancelResponse;
    }

    /**
     * Create bibliogrphic item abstract response item.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/createBib", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem createBibliogrphicItem(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        ItemCreateBibResponse itemCreateBibResponse;
        String itemBarcode;
        log.info("ESIP CALL FOR CREATE BIB -> {}" , callInstitution);
        String callInst = callingInstitution(callInstitution, itemRequestInformation);
        if (!itemRequestInformation.getItemBarcodes().isEmpty() && !itemRequestInformation.getItemBarcodes().get(0).equals("string")) {
            itemBarcode = itemRequestInformation.getItemBarcodes().get(0);
            ItemInformationResponse itemInformation = (ItemInformationResponse) itemInformation(itemRequestInformation, itemRequestInformation.getRequestingInstitution());
            if (itemInformation.getScreenMessage().toUpperCase().contains(ScsbConstants.REQUEST_ITEM_BARCODE_NOT_FOUND)) {
                itemCreateBibResponse = (ItemCreateBibResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).createBib(itemBarcode, itemRequestInformation.getPatronBarcode(), itemRequestInformation.getRequestingInstitution(), itemRequestInformation.getTitleIdentifier());
            } else {
                itemCreateBibResponse = new ItemCreateBibResponse();
                itemCreateBibResponse.setSuccess(true);
                itemCreateBibResponse.setScreenMessage("Item Barcode already Exist");
                itemCreateBibResponse.setItemBarcode(itemBarcode);
                itemCreateBibResponse.setBibId(itemInformation.getBibID());
            }
        } else {
            itemCreateBibResponse = new ItemCreateBibResponse();
            itemCreateBibResponse.setSuccess(false);
            itemCreateBibResponse.setScreenMessage("Please check the Item Barcode provided");
        }
        return itemCreateBibResponse;
    }

    /**
     * Item information abstract response item.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/itemInformation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem itemInformation(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        AbstractResponseItem itemInformationResponse;
        String callInst = callingInstitution(callInstitution, itemRequestInformation);
        String itemBarcode = itemRequestInformation.getItemBarcodes().get(0);
        itemInformationResponse = ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).lookupItem(itemBarcode);
        return itemInformationResponse;
    }

    /**
     * Recall item abstract response item.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/recallItem", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem recallItem(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        ItemRecallResponse itemRecallResponse;
        log.info("ESIP CALL FOR RECALL ITEM -> {}" , callInstitution);
        String callInst = callingInstitution(callInstitution, itemRequestInformation);
        String itembarcode = itemRequestInformation.getItemBarcodes().get(0);
        itemRecallResponse = (ItemRecallResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).recallItem(itembarcode, itemRequestInformation.getPatronBarcode(),
                itemRequestInformation.getRequestingInstitution(),
                itemRequestInformation.getExpirationDate(),
                itemRequestInformation.getBibId(),
                getPickupLocationDB(itemRequestInformation, callInst));
        return itemRecallResponse;
    }

    /**
     * Patron information abstract response item.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/patronInformation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem patronInformation(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        PatronInformationResponse patronInformationResponse;
        String callInst = callingInstitution(callInstitution, itemRequestInformation);
        patronInformationResponse = (PatronInformationResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).lookupPatron(itemRequestInformation.getPatronBarcode());
        return patronInformationResponse;
    }

    /**
     * Refile item item refile response.
     *
     * @param itemRefileRequest the item refile request
     * @return the item refile response
     */
    @PostMapping(value = "/refile",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemRefileResponse refileItem(@RequestBody ItemRefileRequest itemRefileRequest) {
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse = getItemRequestService().reFileItem(itemRefileRequest,itemRefileResponse);

        if (itemRefileResponse.isSuccess()) {
            itemRefileResponse.setScreenMessage("Successfully Refiled");
        } else {
            if(itemRefileResponse.getScreenMessage()==null){
                itemRefileResponse.setScreenMessage("Cannot process Refile request");
            }
        }
        log.info("Refile Response: {}",itemRefileResponse.getScreenMessage());
        return itemRefileResponse;
    }


    @PostMapping("/patronValidationBulkRequest")
    public Boolean patronValidationBulkRequest(@RequestBody BulkRequestInformation bulkRequestInformation) {
        return ilsProtocolConnectorFactory.getIlsProtocolConnector(bulkRequestInformation.getRequestingInstitution()).patronValidation(bulkRequestInformation.getRequestingInstitution(), bulkRequestInformation.getPatronBarcode());
    }

    /**
     * This method refiles the item in ILS. Currently only NYPL has the refile endpoint.
     *
     * @param itemRequestInformation the item request information
     * @param callInstitution        the call institution
     * @return the abstract response item
     */
    @PostMapping(value = "/refileItemInILS", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbstractResponseItem refileItemInILS(@RequestBody ItemRequestInformation itemRequestInformation, String callInstitution) {
        ItemRefileResponse itemRefileResponse;
        String itemBarcode;
        try {
            String callInst = callingInstitution(callInstitution, itemRequestInformation);
            if (!itemRequestInformation.getItemBarcodes().isEmpty()) {
                itemBarcode = itemRequestInformation.getItemBarcodes().get(0);
                itemRefileResponse = (ItemRefileResponse) ilsProtocolConnectorFactory.getIlsProtocolConnector(callInst).refileItem(itemBarcode);
            } else {
                itemRefileResponse = new ItemRefileResponse();
                itemRefileResponse.setSuccess(false);
                itemRefileResponse.setScreenMessage(ScsbConstants.REQUEST_ITEM_BARCODE_NOT_FOUND);
            }
        } catch (Exception e) {
            itemRefileResponse = new ItemRefileResponse();
            itemRefileResponse.setSuccess(false);
            itemRefileResponse.setScreenMessage(e.getMessage());
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemRefileResponse;
    }

    /**
     * This method will replace the requests to LAS queue.
     *
     * @param replaceRequest the replace request
     * @return the string response
     */
    @PostMapping(value = "/replaceRequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> replaceRequest(@RequestBody ReplaceRequest replaceRequest) {
        return itemRequestService.replaceRequestsToLASQueue(replaceRequest);
    }

    /**
     * Gets pickup location.
     *
     * @param institution the institution
     * @return the pickup location
     */
    public String getPickupLocation(String institution) {
        return propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_DEFAULT_PICKUP_LOCATION);
    }

    /**
     * Log messages.
     *
     * @param logger    the logger
     * @param clsObject the cls object
     */
    public void logMessages(Logger logger, Object clsObject) {
        try {
            for (Field field : clsObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(clsObject);
                if (!StringUtils.isBlank(name) && value != null) {
                    logger.info("Field name: {} Filed Value : {} ", name, value);
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("", e);
        }
    }

    private String callingInstitution(String callingInst, ItemRequestInformation itemRequestInformation) {
        String inst;
        if (callingInst == null) {
            inst = itemRequestInformation.getItemOwningInstitution();
        } else {
            inst = callingInst;
        }
        return inst;
    }

    private String getPickupLocationDB(ItemRequestInformation itemRequestInformation, String callInstitution) {
        String useDeliveryLocationAsPickupLocation = propertyUtil.getPropertyByInstitutionAndKey(callInstitution, PropertyKeyConstants.ILS.ILS_USE_DELIVERY_LOCATION_AS_PICKUP_LOCATION);
        if (Boolean.TRUE.toString().equalsIgnoreCase(useDeliveryLocationAsPickupLocation)) {
            return itemRequestInformation.getDeliveryLocation();
        }
        return (StringUtils.isBlank(itemRequestInformation.getPickupLocation())) ? getPickupLocation(callInstitution) : itemRequestInformation.getPickupLocation();
    }

    @PostMapping(value = "/requestStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity requestStatus(@RequestBody RequestStatusRequest requestStatusRequest) {
        log.info("Inside requestStatus >>>>>>> ");
        List<RequestStatusResponse> requestStatusResponses = new ArrayList<>();
        ResponseEntity responseEntity;
        try {
            requestStatusResponses = getItemRequestService().getItemRequestStatusByBarcode(requestStatusRequest.getBarcodes());
        } catch (Exception exception) {
            responseEntity = new ResponseEntity(org.recap.common.ScsbConstants.SCSB_PERSISTENCE_SERVICE_IS_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            log.error(org.recap.common.ScsbConstants.EXCEPTION, exception);
            return responseEntity;
        }
        responseEntity = new ResponseEntity(requestStatusResponses, getHttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ScsbCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

}
