package org.recap.request.service;


import lombok.extern.slf4j.Slf4j;
import org.recap.PropertyKeyConstants;
import org.recap.common.ScsbConstants;
import org.recap.ScsbCommonConstants;
import org.recap.controller.ItemController;
import org.recap.model.request.ItemRequestInformation;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hemalathas on 3/11/16.
 */
@Slf4j
@Component
public class RequestParamaterValidatorService {


    /**
     * The Scsb solr client url.
     */
    @Value("${" + PropertyKeyConstants.SCSB_SOLR_DOC_URL + "}")
    String scsbSolrClientUrl;

    /**
     * The Item controller.
     */
    @Autowired
    ItemController itemController;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    CommonUtil commonUtil;

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
        if (itemRequestInformation != null && itemRequestInformation.getPatronBarcode() != null && !StringUtils.isEmpty(itemRequestInformation.getPatronBarcode()) && itemRequestInformation.getPatronBarcode().length() > ScsbConstants.PATRON_CODE_MAX_LENGTH) {
            errorMessageMap.put(errorCount, ScsbConstants.INVALID_PATRON_CODE);
            errorCount++;
        }
        if (CollectionUtils.isEmpty(itemRequestInformation.getItemBarcodes())) {
            errorMessageMap.put(errorCount, ScsbConstants.ITEM_BARCODE_IS_REQUIRED);
            errorCount++;
        }
        if (StringUtils.isEmpty(itemRequestInformation.getRequestingInstitution()) || !institutionDetailsRepository.existsByInstitutionCode(itemRequestInformation.getRequestingInstitution())) {
            errorMessageMap.put(errorCount, MessageFormat.format(ScsbConstants.INVALID_REQUEST_INSTITUTION, String.join(",", commonUtil.findAllInstitutionCodesExceptSupportInstitution())));
            errorCount++;
        }
        if (!validateEmailAddress(itemRequestInformation.getEmailAddress())) {
            errorMessageMap.put(errorCount, ScsbConstants.INVALID_EMAIL_ADDRESS);
            errorCount++;
        }

        if ((itemRequestInformation.getRequestType() == null || itemRequestInformation.getRequestType().trim().length() <= 0) || (!ScsbConstants.getRequestTypeList().contains(itemRequestInformation.getRequestType()))) {
            errorMessageMap.put(errorCount, ScsbConstants.INVALID_REQUEST_TYPE);
            errorCount++;
        } else {
            if (itemRequestInformation.getRequestType().equalsIgnoreCase(ScsbConstants.EDD_REQUEST)) {
                if (!CollectionUtils.isEmpty(itemRequestInformation.getItemBarcodes())) {
                    if (itemController.splitStringAndGetList(itemRequestInformation.getItemBarcodes().toString()).size() > 1) {
                        errorMessageMap.put(errorCount, ScsbConstants.MULTIPLE_ITEMS_NOT_ALLOWED_FOR_EDD);
                        errorCount++;
                    }
                } else {
                    errorMessageMap.put(errorCount, ScsbConstants.ITEM_BARCODE_IS_REQUIRED);
                    errorCount++;
                }
                if (StringUtils.isEmpty(itemRequestInformation.getChapterTitle())) {
                    errorMessageMap.put(errorCount, ScsbConstants.CHAPTER_TITLE_IS_REQUIRED);
                    errorCount++;
                }
                if (itemRequestInformation.getStartPage() == null || itemRequestInformation.getEndPage() == null) {
                    errorMessageMap.put(errorCount, ScsbConstants.START_PAGE_AND_END_PAGE_REQUIRED);
                    errorCount++;
                }
            } else if ((itemRequestInformation.getRequestType().equalsIgnoreCase(ScsbCommonConstants.REQUEST_TYPE_RECALL) || itemRequestInformation.getRequestType().equalsIgnoreCase(ScsbCommonConstants.RETRIEVAL)) &&
                    (StringUtils.isEmpty(itemRequestInformation.getDeliveryLocation()))) {
                errorMessageMap.put(errorCount, ScsbConstants.DELIVERY_LOCATION_REQUIRED);
                errorCount++;
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
                String regex = ScsbCommonConstants.REGEX_FOR_EMAIL_ADDRESS;
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(toEmailAddress);
                bSuccess = matcher.matches();
            } else {
                bSuccess = true;
            }
        } catch (RuntimeException e) {
            log.error(ScsbCommonConstants.LOG_ERROR,e);
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
        responseHeaders.add(ScsbCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    private static String buildErrorMessage(Map<Integer, String> erroMessageMap) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        erroMessageMap.forEach((key, value) -> errorMessageBuilder.append(value).append("\n"));
        return errorMessageBuilder.toString();
    }
}
