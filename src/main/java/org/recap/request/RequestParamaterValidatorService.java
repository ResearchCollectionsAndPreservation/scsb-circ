package org.recap.request;


import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.controller.ItemController;
import org.recap.model.jpa.ItemRequestInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hemalathas on 3/11/16.
 */
@Component
public class RequestParamaterValidatorService {

    private static final Logger logger = LoggerFactory.getLogger(RequestParamaterValidatorService.class);

    /**
     * The Scsb solr client url.
     */
    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    /**
     * The Item controller.
     */
    @Autowired
    ItemController itemController;

    /**
     * Validate item request parameters response entity.
     *
     * @param itemRequestInformation the item request information
     * @return the response entity
     */
    public ResponseEntity validateItemRequestParameters(ItemRequestInformation itemRequestInformation) {
        ResponseEntity responseEntity = null;
        Map<Integer, String> errorMessageMap = new HashMap<>();
        Integer errorCount = 1;
        if (CollectionUtils.isEmpty(itemRequestInformation.getItemBarcodes())) {
            errorMessageMap.put(errorCount, RecapConstants.ITEM_BARCODE_IS_REQUIRED);
            errorCount++;
        }
        if (StringUtils.isEmpty(itemRequestInformation.getRequestingInstitution()) || !itemRequestInformation.getRequestingInstitution().equalsIgnoreCase(RecapCommonConstants.PRINCETON) && !itemRequestInformation.getRequestingInstitution().equalsIgnoreCase(RecapCommonConstants.COLUMBIA)
                && !itemRequestInformation.getRequestingInstitution().equalsIgnoreCase(RecapCommonConstants.NYPL)) {
            errorMessageMap.put(errorCount, RecapConstants.INVALID_REQUEST_INSTITUTION);
            errorCount++;
        }
        if (!validateEmailAddress(itemRequestInformation.getEmailAddress())) {
            errorMessageMap.put(errorCount, RecapConstants.INVALID_EMAIL_ADDRESS);
            errorCount++;
        }

        if ((itemRequestInformation.getRequestType() == null || itemRequestInformation.getRequestType().trim().length() <= 0) || (!RecapConstants.getRequestTypeList().contains(itemRequestInformation.getRequestType()))) {
            errorMessageMap.put(errorCount, RecapConstants.INVALID_REQUEST_TYPE);
            errorCount++;
        } else {
            if (itemRequestInformation.getRequestType().equalsIgnoreCase(RecapConstants.EDD_REQUEST)) {
                if (!CollectionUtils.isEmpty(itemRequestInformation.getItemBarcodes())) {
                    if (itemController.splitStringAndGetList(itemRequestInformation.getItemBarcodes().toString()).size() > 1) {
                        errorMessageMap.put(errorCount, RecapConstants.MULTIPLE_ITEMS_NOT_ALLOWED_FOR_EDD);
                        errorCount++;
                    }
                } else {
                    errorMessageMap.put(errorCount, RecapConstants.ITEM_BARCODE_IS_REQUIRED);
                    errorCount++;
                }
                if (StringUtils.isEmpty(itemRequestInformation.getChapterTitle())) {
                    errorMessageMap.put(errorCount, RecapConstants.CHAPTER_TITLE_IS_REQUIRED);
                    errorCount++;
                }
                if (itemRequestInformation.getStartPage() == null || itemRequestInformation.getEndPage() == null) {
                    errorMessageMap.put(errorCount, RecapConstants.START_PAGE_AND_END_PAGE_REQUIRED);
                    errorCount++;
                }
            } else if (itemRequestInformation.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RECALL) || itemRequestInformation.getRequestType().equalsIgnoreCase(RecapCommonConstants.RETRIEVAL)) {
                if (StringUtils.isEmpty(itemRequestInformation.getDeliveryLocation())) {
                    errorMessageMap.put(errorCount, RecapConstants.DELIVERY_LOCATION_REQUIRED);
                    errorCount++;
                }
            }
        }

        if (errorMessageMap.size() > 0) {
            return new ResponseEntity(buildErrorMessage(errorMessageMap), getHttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }

    private boolean validateEmailAddress(String toEmailAddress) {
        boolean bSuccess = false;
        try {
            if (!StringUtils.isEmpty(toEmailAddress)) {
                String regex = RecapCommonConstants.REGEX_FOR_EMAIL_ADDRESS;
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(toEmailAddress);
                bSuccess = matcher.matches();
            } else {
                bSuccess = true;
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
        return bSuccess;
    }

    /**
     * Gets http headers.
     *
     * @return the http headers
     */
    public HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    private String buildErrorMessage(Map<Integer, String> erroMessageMap) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        erroMessageMap.entrySet().forEach(entry -> errorMessageBuilder.append(entry.getValue()).append("\n"));
        return errorMessageBuilder.toString();
    }
}
