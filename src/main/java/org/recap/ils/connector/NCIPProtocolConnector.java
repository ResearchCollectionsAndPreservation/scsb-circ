package org.recap.ils.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.extensiblecatalog.ncip.v2.service.AcceptItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.AcceptItemResponseData;
import org.extensiblecatalog.ncip.v2.service.CancelRequestItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CancelRequestItemResponseData;
import org.extensiblecatalog.ncip.v2.service.CheckInItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CheckInItemResponseData;
import org.extensiblecatalog.ncip.v2.service.CheckOutItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CheckOutItemResponseData;
import org.extensiblecatalog.ncip.v2.service.LookupUserInitiationData;
import org.extensiblecatalog.ncip.v2.service.LookupUserResponseData;
import org.extensiblecatalog.ncip.v2.service.NCIPResponseData;
import org.extensiblecatalog.ncip.v2.service.RecallItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.RecallItemResponseData;
import org.json.JSONObject;

import org.recap.PropertyKeyConstants;
import org.recap.ils.protocol.ncip.util.NCIPToolKitUtil;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.request.ItemRequestInformation;
import org.recap.ils.protocol.ncip.AcceptItem;
import org.recap.ils.protocol.ncip.CancelRequestItem;
import org.recap.ils.protocol.ncip.CheckinItem;
import org.recap.ils.protocol.ncip.CheckoutItem;
import org.recap.ils.protocol.ncip.LookupUser;
import org.recap.ils.protocol.ncip.RecallItem;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.ils.protocol.rest.model.BibLookupData;
import org.recap.ils.protocol.rest.model.ItemLookupData;
import org.recap.ils.protocol.rest.model.response.ItemLookupResponse;
import org.recap.model.response.ItemLookUpInformationResponse;
import org.recap.model.response.ItemCheckinResponse;
import org.recap.model.response.ItemCheckoutResponse;
import org.recap.model.response.ItemHoldResponse;
import org.recap.model.response.ItemInformationResponse;
import org.recap.model.response.ItemRecallResponse;
import org.recap.model.response.PatronInformationResponse;
import org.recap.ils.protocol.rest.util.RestApiResponseUtil;
import org.recap.model.ILSConfigProperties;
import org.recap.model.AbstractResponseItem;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RefreshScope
public class NCIPProtocolConnector extends AbstractProtocolConnector {

    private String ncipRequest = "NCIP2 request sent: ";
    private String ncipResponse = "NCIP2 response received: ";
    private String httpCallTo = "Http call to ";
    private String returnedResponseCode = " returned response code ";
    private String responseBody = ".  Response body: ";
    private String failureReason = "Failure due to ";

    @Autowired
    RestApiResponseUtil restApiResponseUtil;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    PropertyUtil propertyUtil;

    @Override
    public boolean supports(String protocol) {
        return ScsbConstants.NCIP_PROTOCOL.equalsIgnoreCase(protocol);
    }

    @Override
    public void setInstitution(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInstitution() {
        return institutionCode;
    }

    public String getNcipAgencyId() {
        return ilsConfigProperties.getNcipAgencyId();
    }

    public String getNcipScheme() {
        return ilsConfigProperties.getNcipScheme();
    }

    public ItemDetailsRepository getItemDetailsRepository() {
        return itemDetailsRepository;
    }

    @Override
    public void setIlsConfigProperties(ILSConfigProperties ilsConfigProperties) {
        this.ilsConfigProperties = ilsConfigProperties;
    }

    /**
     * Get rest template rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


    /**
     * Gets rest api response util.
     *
     * @return the rest api response util
     */
    public RestApiResponseUtil getRestApiResponseUtil() {
        return restApiResponseUtil;
    }

    /**
     * Get http header http headers.
     *
     * @return the http headers
     */
    public HttpHeaders getHttpHeader() {
        return new HttpHeaders();
    }

    /**
     * Get http entity http entity.
     *
     * @param headers the headers
     * @return the http entity
     */
    public org.springframework.http.HttpEntity getHttpEntity(JSONObject body, HttpHeaders headers){
        return new org.springframework.http.HttpEntity<>(body.toString(), headers);
    }

    public org.springframework.http.HttpEntity getHttpEntity(HttpHeaders headers){
        return new org.springframework.http.HttpEntity<>(headers);
    }


    public String getBibDataApiUrl() {
        return ilsConfigProperties.getIlsBibdataApiEndpoint();
    }

    public CloseableHttpClient buildCloseableHttpClient(){
        return HttpClients.custom().build();
    }

    @Override
    public AbstractResponseItem lookupItem(String itemIdentifier) {
        log.info("NCIP Connector Host: {}", getHost());
        log.info("NCIP Connector Port: {}", getPort());
        log.info("NCIP Connector Location: {}", getOperatorLocation());
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setHoldQueueLength("1");
        itemInformationResponse.setSuccess(false);
        itemInformationResponse.setScreenMessage("Lookup Item API not implemented");
        return itemInformationResponse;
    }

    @Override
    public ItemCheckoutResponse checkOutItem(String itemIdentifier, Integer requestId, String patronIdentifier) {
        log.info("Item barcode {} received for a checkout in " + getInstitution() + "for patron {}", itemIdentifier, patronIdentifier);
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        String responseString = null;
        JSONObject responseObject = new JSONObject();
        try {
            CheckoutItem checkoutItem = new CheckoutItem();

            CheckOutItemInitiationData checkOutItemInitiationData = checkoutItem.getCheckOutItemInitiationData(itemIdentifier, requestId, patronIdentifier, getNcipAgencyId());
            NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();
            InputStream requestMessageStream = ncipToolkitUtil.translator.createInitiationMessageStream(ncipToolkitUtil.serviceContext, checkOutItemInitiationData);
            String requestBody = IOUtils.toString(requestMessageStream, StandardCharsets.UTF_8);
            CloseableHttpClient client = buildCloseableHttpClient();

            HttpUriRequest request = getHttpRequest(requestBody);

            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString( entity, StandardCharsets.UTF_8);

            log.info(ncipRequest);
            log.info(requestBody);
            log.info(ncipResponse);
            log.info(responseString);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode > 399) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST , httpCallTo + getEndPointUrl() + returnedResponseCode + responseCode + responseBody + responseString);
            }

            InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
            NCIPResponseData responseData = ncipToolkitUtil.translator.createResponseData(ncipToolkitUtil.serviceContext, stream);

            //transforms the NCIP Objects into a JSON response object
            CheckOutItemResponseData checkoutItemResponse = (CheckOutItemResponseData) responseData;

            responseObject = checkoutItem.getCheckoutResponse(checkoutItemResponse);

            if (!checkoutItemResponse.getProblems().isEmpty()) {
                itemCheckoutResponse.setSuccess(Boolean.FALSE);
                itemCheckoutResponse.setScreenMessage(failureReason + checkoutItemResponse.getProblems());
                log.error("checkOutItem Response >>> " + checkoutItemResponse.getProblems());
                log.error("checkOutItem Response message >>> " + itemCheckoutResponse.getScreenMessage());

                return itemCheckoutResponse;
            } else {
                log.info(responseObject.toString());

                itemCheckoutResponse.setItemBarcode(checkoutItemResponse.getItemId().getItemIdentifierValue());
                itemCheckoutResponse.setPatronIdentifier(checkoutItemResponse.getUserId().getUserIdentifierValue());
                if (checkoutItemResponse.getItemOptionalFields() != null) {
                    itemCheckoutResponse.setTitleIdentifier(checkoutItemResponse.getItemOptionalFields().getBibliographicDescription().getTitle());
                }
                itemCheckoutResponse.setPatronIdentifier(checkoutItemResponse.getUserId().getUserIdentifierValue());
                itemCheckoutResponse.setDueDate(responseObject.getString("dueDate"));
                itemCheckoutResponse.setSuccess(Boolean.TRUE);
                itemCheckoutResponse.setScreenMessage(ScsbCommonConstants.SUCCESS);
            log.info("checkOutItem Response >>> " + checkoutItemResponse.getProblems());
                log.info("checkOutItem Response message >>> " + itemCheckoutResponse.getScreenMessage());

                return itemCheckoutResponse;
            }

        } catch (HttpClientErrorException httpException) {
            log.error(ScsbCommonConstants.LOG_ERROR, httpException);
            itemCheckoutResponse.setSuccess(false);
            itemCheckoutResponse.setScreenMessage(httpException.getStatusText());
            log.error("checkOutItem Response message >>> " + itemCheckoutResponse.getScreenMessage());
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
            itemCheckoutResponse.setSuccess(false);
            itemCheckoutResponse.setScreenMessage(e.getMessage());
            log.error("checkOutItem Response message >>> " + itemCheckoutResponse.getScreenMessage());
        }
        return itemCheckoutResponse;
    }

    public CheckInItemResponseData getCheckinResponse(CheckinItem checkInItem, CheckInItemInitiationData checkInItemInitiationData)
    {
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        CheckInItemResponseData checkinItemResponse = new CheckInItemResponseData();
        try {
            NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();
            String requestBody = checkInItem.getRequestBody(ncipToolkitUtil, checkInItemInitiationData);
            HttpUriRequest request = getHttpRequest(requestBody);
            CloseableHttpClient client = buildCloseableHttpClient();

            String responseString = null;

            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.info(ncipRequest);
            log.info(requestBody);
            log.info(ncipResponse);
            log.info(responseString);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode > 399) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, httpCallTo + getEndPointUrl() + returnedResponseCode + responseCode + responseBody + responseString);
            }

            //transforms the NCIP xml response into NCIP Objects
            InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
            NCIPResponseData responseData = ncipToolkitUtil.translator.createResponseData(ncipToolkitUtil.serviceContext, stream);

            //transforms the NCIP Objects into a JSON response object
            checkinItemResponse = (CheckInItemResponseData) responseData;
            checkInItem.getCheckInResponse(checkinItemResponse);
                   }
        catch (HttpClientErrorException httpException) {
            log.error(ScsbCommonConstants.LOG_ERROR, httpException);
            itemCheckinResponse.setSuccess(false);
            itemCheckinResponse.setScreenMessage(httpException.getStatusText());
            log.error("CheckinItem Response message >>> " + itemCheckinResponse.getScreenMessage());
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
            itemCheckinResponse.setSuccess(false);
            itemCheckinResponse.setScreenMessage(e.getMessage());
            log.error("CheckinItem Response message >>> " + itemCheckinResponse.getScreenMessage());
        }
        log.info("CheckinItem Response >>> " + checkinItemResponse);
        log.info("CheckinItem Response message >>> " + itemCheckinResponse.getScreenMessage());
        return checkinItemResponse;
    }
    @Override
    public Object checkInItem(ItemRequestInformation itemRequestInformation, String patronIdentifier) {
        log.info("Item barcode {} received for a checkin in" + getInstitution() + " for patron {}", itemRequestInformation.getItemBarcodes().get(0), patronIdentifier);
        CheckinItem checkInItem = new CheckinItem();
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        String itemIdentifier = itemRequestInformation.getItemBarcodes().get(0);
        List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemIdentifier);
        ItemEntity itemEntity = !itemEntities.isEmpty() ? itemEntities.get(0) : null;
        String imsLocation = itemEntity != null ? itemEntity.getImsLocationEntity().getImsLocationCode() : null;
        Boolean isRemoteCheckin = Boolean.FALSE;

        String remoteCheckin = propertyUtil.getPropertyByInstitutionAndKey(getInstitution(), PropertyKeyConstants.ILS.ILS_REMOTE_CHECKIN);
           if(Boolean.TRUE.toString().equalsIgnoreCase(remoteCheckin) && (
                    getInstitution().equals(itemRequestInformation.getItemOwningInstitution())
              || itemRequestInformation.getRequestingInstitution().equals(itemRequestInformation.getItemOwningInstitution()))) {
                isRemoteCheckin = Boolean.TRUE;
            }
                if (isRemoteCheckin.booleanValue()) {
                    String remoteProfileType = propertyUtil.getPropertyByInstitutionAndLocationAndKey(getInstitution(), imsLocation, PropertyKeyConstants.ILS.ILS_REMOTE_PROFILE_TYPE);
                    if (!itemRequestInformation.getRequestingInstitution().equals(itemRequestInformation.getItemOwningInstitution()) || itemRequestInformation.getRequestType().equals(ScsbCommonConstants.REQUEST_TYPE_EDD)) {
                        CheckInItemInitiationData checkInItemInitiationData = checkInItem.getCheckInItemInitiationData(itemIdentifier,  getNcipAgencyId());
                        CheckInItemResponseData checkinItemResponse = getCheckinResponse(checkInItem, checkInItemInitiationData);
                        if (!checkinItemResponse.getProblems().isEmpty()) {
                            itemCheckinResponse.setSuccess(Boolean.FALSE);
                            itemCheckinResponse.setScreenMessage(failureReason + checkinItemResponse.getProblems());
                            log.error("CheckinItem Response >>> " + checkinItemResponse.getProblems());
                            log.error("CheckinItem Response message >>> " + itemCheckinResponse.getScreenMessage());
                            return itemCheckinResponse;
                        }
                        checkInItemInitiationData = checkInItem.getCheckInItemInitiationRemoteData(itemIdentifier, itemEntity, imsLocation, remoteProfileType, getNcipAgencyId(), getNcipScheme());
                        checkinItemResponse = getCheckinResponse(checkInItem, checkInItemInitiationData);
                        if (!checkinItemResponse.getProblems().isEmpty()) {
                            itemCheckinResponse.setSuccess(Boolean.FALSE);
                            itemCheckinResponse.setScreenMessage(failureReason + checkinItemResponse.getProblems());
                            log.error("CheckinItem Response >>> " + checkinItemResponse.getProblems());
                            log.error("CheckinItem Response message >>> " + itemCheckinResponse.getScreenMessage());
                            return itemCheckinResponse;
                        }
                    } else {
                        CheckInItemInitiationData checkInItemInitiationData = checkInItem.getCheckInItemInitiationRemoteData(itemIdentifier, itemEntity, imsLocation, remoteProfileType, getNcipAgencyId(), getNcipScheme());
                        CheckInItemResponseData checkinItemResponse = getCheckinResponse(checkInItem, checkInItemInitiationData);
                        if (!checkinItemResponse.getProblems().isEmpty()) {
                            itemCheckinResponse.setSuccess(Boolean.FALSE);
                            itemCheckinResponse.setScreenMessage(failureReason + checkinItemResponse.getProblems());
                            log.error("CheckinItem Response >>> " + checkinItemResponse.getProblems());
                            log.error("CheckinItem Response message >>> " + itemCheckinResponse.getScreenMessage());
                            return itemCheckinResponse;
                        }
                      }

                } else {
                        CheckInItemInitiationData checkInItemInitiationData = checkInItem.getCheckInItemInitiationData(itemRequestInformation.getItemBarcodes().get(0), getNcipAgencyId());
                        CheckInItemResponseData checkinItemResponse = getCheckinResponse(checkInItem, checkInItemInitiationData);
                        if (!checkinItemResponse.getProblems().isEmpty()) {
                            itemCheckinResponse.setSuccess(Boolean.FALSE);
                            itemCheckinResponse.setScreenMessage(failureReason + checkinItemResponse.getProblems());
                            log.info("itemCheckinResponse Response message >>> " + itemCheckinResponse.getScreenMessage());
                            log.info("itemCheckinResponse problems >>> " + checkinItemResponse.getProblems());
                            return itemCheckinResponse;
                        }
                    log.info("itemCheckinResponse Response message >>> " + itemCheckinResponse.getScreenMessage());
                }
                    itemCheckinResponse.setSuccess(Boolean.TRUE);
                    itemCheckinResponse.setScreenMessage(ScsbCommonConstants.SUCCESS);
                    itemCheckinResponse.setItemOwningInstitution(getInstitution());
                    log.info("itemCheckinResponse Response message >>> " + itemCheckinResponse.getScreenMessage());
        return itemCheckinResponse;
    }

    public Map<String, String> getParamsMap(String bibId, String holdingId, String itemId) {
        Map<String, String> params = new HashMap<>();
        params.put("bibId", bibId);
        params.put("holdingId", holdingId);
        params.put("itemId", itemId);
        return params;
    }

    @Override
    public Object placeHold(String itemIdentifier, Integer requestId, String patronIdentifier, String callInstitutionId, String itemInstitutionId, String expirationDate, String bibId, String pickupLocation, String trackingId, String title, String author, String callNumber) {
        log.info("Item barcode {} received for hold request in " + callInstitutionId + " for patron {}", itemIdentifier, patronIdentifier);
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        if (callInstitutionId.equalsIgnoreCase(itemInstitutionId)) {
                itemHoldResponse.setSuccess(Boolean.TRUE);
            }
            else {
                itemHoldResponse = acceptItem(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, pickupLocation, title, author, callNumber);
            }
        return itemHoldResponse;
    }

    @Override
    public Object cancelHold(String itemIdentifier, Integer requestId, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocation, String trackingId) {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        String responseString = null;
        JSONObject responseObject = new JSONObject();

        try {
            CancelRequestItem cancelRequestItem = new CancelRequestItem();
            CancelRequestItemInitiationData cancelRequestItemInitiationData = cancelRequestItem.getCancelRequestItemInitiationData(requestId, patronIdentifier, getNcipAgencyId(), getNcipScheme());

            NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();
            InputStream requestMessageStream = ncipToolkitUtil.translator.createInitiationMessageStream(ncipToolkitUtil.serviceContext, cancelRequestItemInitiationData);
            String requestBody = IOUtils.toString(requestMessageStream, StandardCharsets.UTF_8);

            CloseableHttpClient client = buildCloseableHttpClient();
            HttpUriRequest request = getHttpRequest(requestBody);
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.info(ncipRequest);
            log.info(requestBody);
            log.info(ncipResponse);
            log.info(responseString);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode > 399) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, httpCallTo + getEndPointUrl() + returnedResponseCode + responseCode + responseBody + responseString);
            }
            //transforms the NCIP xml response into NCIP Objects
            InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
            NCIPResponseData responseData = ncipToolkitUtil.translator.createResponseData(ncipToolkitUtil.serviceContext, stream);

            //transforms the NCIP Objects into a JSON response object
            CancelRequestItemResponseData cancelItemResponse = (CancelRequestItemResponseData) responseData;
            responseObject = cancelRequestItem.getCancelRequestItemResponse(cancelItemResponse);

            log.info(responseObject.toString());

            if (!cancelItemResponse.getProblems().isEmpty()) {
                itemHoldResponse.setSuccess(Boolean.FALSE);
                itemHoldResponse.setScreenMessage(failureReason + cancelItemResponse.getProblems());
                log.error("cancelHold Response >>> " + cancelItemResponse.getProblems());
                log.error("cancelHold Response message >>> " + itemHoldResponse.getScreenMessage());
                return itemHoldResponse;
            }

            itemHoldResponse.setItemOwningInstitution(cancelItemResponse.getResponseHeader().getToAgencyId().getAgencyId().getValue());
            itemHoldResponse.setItemBarcode(cancelItemResponse.getItemId().getItemIdentifierValue());
            itemHoldResponse.setPatronIdentifier(cancelItemResponse.getRequestId().getAgencyId().getValue());
            itemHoldResponse.setSuccess(Boolean.TRUE);
            itemHoldResponse.setScreenMessage(ScsbCommonConstants.SUCCESS);
            itemHoldResponse.setTitleIdentifier(cancelItemResponse.getItemId().getItemIdentifierValue());
        } catch (HttpClientErrorException httpException) {
            log.error(ScsbCommonConstants.LOG_ERROR, httpException);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(httpException.getStatusText());
            log.error("cancelHold Response message >>> " + itemHoldResponse.getScreenMessage());
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(e.getMessage());
            log.error("cancelHold Response message >>> " + itemHoldResponse.getScreenMessage());
        }
        log.info("cancelHold Response >>> " + itemHoldResponse);
        log.info("cancelHold Response message >>> " + itemHoldResponse.getScreenMessage());
        return itemHoldResponse;
    }

    @Override
    public Object createBib(String itemIdentifier, String patronIdentifier, String institutionId, String titleIdentifier) {
        return null;
    }

    @Override
    public boolean patronValidation(String institutionId, String patronIdentifier) {
        return true;
    }

    @Override
    public AbstractResponseItem lookupPatron(String patronIdentifier) {
        log.info("Lookup for patron {}", patronIdentifier);
        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        String responseString;
        JSONObject responseObject;

        try {
            LookupUser lookupUser = new LookupUser();
            LookupUserInitiationData lookupUserInitiationData = lookupUser.getLookupUserInitiationData(patronIdentifier, getNcipAgencyId());
            NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();
            InputStream requestMessageStream = ncipToolkitUtil.translator.createInitiationMessageStream(ncipToolkitUtil.serviceContext, lookupUserInitiationData);
            String requestBody = IOUtils.toString(requestMessageStream, StandardCharsets.UTF_8);
            CloseableHttpClient client = buildCloseableHttpClient();

            HttpUriRequest request = getHttpRequest(requestBody);
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.info(ncipRequest);
            log.info(requestBody);
            log.info(ncipResponse);
            log.info(responseString);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode > 399) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, httpCallTo + getEndPointUrl() + returnedResponseCode + responseCode + responseBody + responseString);
            }
            //transforms the NCIP xml response into  NCIP Objects
            InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
            NCIPResponseData responseData = ncipToolkitUtil.translator.createResponseData(ncipToolkitUtil.serviceContext, stream);
            //transforms the  NCIP Objects into a JSON response object
            LookupUserResponseData lookupUserResponseData = (LookupUserResponseData) responseData;

            responseObject = lookupUser.getLookupUserResponse(lookupUserResponseData);

            if (!lookupUserResponseData.getProblems().isEmpty()) {
                patronInformationResponse.setSuccess(Boolean.FALSE);
                patronInformationResponse.setScreenMessage(failureReason + lookupUserResponseData.getProblems());
                log.error("patronInformation Response >>> " + lookupUserResponseData.getProblems());
                log.error("patronInformation Response message >>> " + patronInformationResponse.getScreenMessage());

                return patronInformationResponse;
            } else {
                patronInformationResponse.setPatronName(responseObject.getString("name"));
                patronInformationResponse.setSuccess(Boolean.TRUE);
                patronInformationResponse.setScreenMessage(ScsbCommonConstants.SUCCESS);
                patronInformationResponse.setHomeAddress(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getStreet().concat(",").concat(
                        lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getLocality()).concat(",").
                        concat(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getRegion().concat(",").
                                concat(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getPostalCode())));

                patronInformationResponse.setEmail(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(1).getElectronicAddress().getElectronicAddressData());
                patronInformationResponse.setPatronIdentifier(patronIdentifier);
                log.info("patronInformation Response >>> " + lookupUserResponseData);
                log.info("patronInformation Response message >>> " + patronInformationResponse.getScreenMessage());

                return patronInformationResponse;
            }
        } catch (HttpClientErrorException httpException) {
            log.error(ScsbCommonConstants.LOG_ERROR, httpException);
            patronInformationResponse.setSuccess(false);
            patronInformationResponse.setScreenMessage(httpException.getStatusText());
            log.error("patronInformation Response message >>> " + patronInformationResponse.getScreenMessage());
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
            patronInformationResponse.setSuccess(false);
            patronInformationResponse.setScreenMessage(e.getMessage());
            log.error("patronInformation Response message >>> " + patronInformationResponse.getScreenMessage());
        }
        return patronInformationResponse;
    }

    @Override
    public Object recallItem(String itemIdentifier, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocation) {
        log.info("recallItem for Item {}", itemIdentifier);
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        String responseString;
        JSONObject responseObject;

        try {
            RecallItem recallItem = new RecallItem();
            RecallItemInitiationData recallItemInitiationData = recallItem.getRecallItemInitiationData(itemIdentifier, patronIdentifier, getNcipAgencyId());
            NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();
            InputStream requestMessageStream = ncipToolkitUtil.translator.createInitiationMessageStream(ncipToolkitUtil.serviceContext, recallItemInitiationData);
            String requestBody = IOUtils.toString(requestMessageStream, StandardCharsets.UTF_8);
            CloseableHttpClient client = buildCloseableHttpClient();

            HttpUriRequest request = getHttpRequest(requestBody);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.info(ncipRequest);
            log.info(requestBody);
            log.info(ncipResponse);
            log.info(responseString);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode > 399) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, httpCallTo + getEndPointUrl() + returnedResponseCode + responseCode + responseBody + responseString);
            }
            //transforms the NCIP xml response into NCIP Objects
            InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
            NCIPResponseData responseData = ncipToolkitUtil.translator.createResponseData(ncipToolkitUtil.serviceContext, stream);
            //transforms the NCIP Objects into a JSON response object
            RecallItemResponseData recallItemResponse = (RecallItemResponseData) responseData;
            responseObject = recallItem.getRecallItemResponse(recallItemResponse);

            if (!recallItemResponse.getProblems().isEmpty()) {
                itemRecallResponse.setSuccess(false);
                itemRecallResponse.setScreenMessage("Faiure due to " + recallItemResponse.getProblems());
                log.error("recallItem Response >>> " + recallItemResponse.getProblems());
                log.error("recallItem Response message >>> " + itemRecallResponse.getScreenMessage());
                return itemRecallResponse;
            }
            log.info(responseObject.toString());

            itemRecallResponse.setPatronIdentifier(recallItemResponse.getUserId() != null ? recallItemResponse.getUserId().getUserIdentifierValue() : "");
            itemRecallResponse.setItemBarcode(recallItemResponse.getItemId().getItemIdentifierValue());
            itemRecallResponse.setItemOwningInstitution(institutionId);
            Date expirationDateforRecall = DateUtils.addYears(new Date(), 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ScsbConstants.DATE_FORMAT);
            itemRecallResponse.setExpirationDate(simpleDateFormat.format(expirationDateforRecall));

            itemRecallResponse.setSuccess(Boolean.TRUE);
            itemRecallResponse.setScreenMessage(ScsbCommonConstants.SUCCESS);
            log.error("recallItem Response >>> " + recallItemResponse.getProblems());
            log.error("recallItem Response message >>> " + itemRecallResponse.getScreenMessage());

        } catch (HttpClientErrorException httpException) {
            log.error(ScsbCommonConstants.LOG_ERROR, httpException);
            itemRecallResponse.setSuccess(false);
            itemRecallResponse.setScreenMessage(httpException.getStatusText());
            log.error("recallItem Response message >>> " + itemRecallResponse.getScreenMessage());
      } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
            itemRecallResponse.setSuccess(false);
            itemRecallResponse.setScreenMessage(e.getMessage());
            log.error("recallItem Response message >>> " + itemRecallResponse.getScreenMessage());
        }
        return itemRecallResponse;
    }

    @Override
    public Object refileItem(String itemIdentifier) {
        return null;
    }

    public HttpUriRequest getHttpRequest(String requestBody) {
        return RequestBuilder.post()
                .setUri(getEndPointUrl())
                .setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8))
                .setHeader("Content-Type", "application/xml")
                .build();
    }

    public String getEndPointUrl() {
        return ilsConfigProperties.getHost();
    }

    public ItemLookUpInformationResponse buildResponse(ItemLookupResponse itemResponse) {
        ItemLookUpInformationResponse itemLookUpInformationResponse = new ItemLookUpInformationResponse();

        ItemLookupData itemData = itemResponse.getItemLookupData();
        BibLookupData bibData = itemResponse.getBibLookupData();
        itemLookUpInformationResponse.setBarcode((String)itemData.getBarcode());
        HashMap<String, Object> itemProcessMap = (HashMap) itemData.getProcesstype();
        itemLookUpInformationResponse.setProcesstype((HashMap) itemData.getProcesstype());
        itemLookUpInformationResponse.setCirculationStatus((String) itemProcessMap.get("value"));
        itemLookUpInformationResponse.setBibId((String) bibData.getBibId());
        itemLookUpInformationResponse.setTitle((String) bibData.getTitle());

        return  itemLookUpInformationResponse;
    }

    private ItemHoldResponse acceptItem(String itemIdentifier, Integer requestId, String patronIdentifier, String callInstitutionId, String itemInstitutionId,  String pickupLocation, String title, String author, String callNumber) {
        AcceptItem acceptItem = new AcceptItem();
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        String responseString = null;
        String itemAgencyId = null;
        JSONObject responseObject;
        try {
            NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();
            CloseableHttpClient client = buildCloseableHttpClient();
            AcceptItemInitiationData acceptItemInitiationData = new AcceptItemInitiationData();
                    List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemIdentifier);
            ItemEntity itemEntity = !itemEntities.isEmpty() ? itemEntities.get(0) : null;
            String useRestrictions = itemEntity != null ? itemEntity.getUseRestrictions() : null;
            if(useRestrictions != null) {
                itemAgencyId = propertyUtil.getPropertyByInstitutionAndKey(callInstitutionId, PropertyKeyConstants.ILS.ILS_RESTRICTED_ACCEPT_ITEM_AGENCY_ID);
                acceptItemInitiationData = acceptItem.getAcceptItemInitiationData(itemIdentifier, requestId, patronIdentifier, title, author, pickupLocation, callNumber, getNcipAgencyId(), getNcipScheme(), itemAgencyId);
            }
            else {
                itemAgencyId = propertyUtil.getPropertyByInstitutionAndKey(callInstitutionId, PropertyKeyConstants.ILS.ILS_UNRESTRICTED_ACCEPT_ITEM_AGENCY_ID);
                acceptItemInitiationData = acceptItem.getAcceptItemInitiationData(itemIdentifier, requestId, patronIdentifier, title, author, pickupLocation, callNumber, getNcipAgencyId(), getNcipScheme(), itemAgencyId);
            }
            String requestBody = acceptItem.getRequestBody(ncipToolkitUtil, acceptItemInitiationData);
            HttpUriRequest request = getHttpRequest(requestBody);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);


            log.info(ncipRequest);
            log.info(requestBody);
            log.info(ncipResponse);
            log.info(responseString);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode > 399) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, getEndPointUrl() + returnedResponseCode + responseCode + responseBody + responseString);
            }
            //transforms the NCIP xml response into NCIP Objects
            InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
            NCIPResponseData responseData = ncipToolkitUtil.translator.createResponseData(ncipToolkitUtil.serviceContext, stream);

            //transforms the  NCIP Objects into a JSON response object
            AcceptItemResponseData acceptItemResponse = (AcceptItemResponseData) responseData;
            responseObject = acceptItem.getAcceptItemResponse(acceptItemResponse);
            log.info(responseObject.toString());

            if (!acceptItemResponse.getProblems().isEmpty()) {
                itemHoldResponse.setSuccess(Boolean.FALSE);
                itemHoldResponse.setScreenMessage(failureReason + acceptItemResponse.getProblems());
                return itemHoldResponse;
            }

            itemHoldResponse.setItemOwningInstitution(itemInstitutionId);
            itemHoldResponse.setItemBarcode(acceptItemResponse.getItemId().getItemIdentifierValue());
            itemHoldResponse.setPatronIdentifier(patronIdentifier);
            itemHoldResponse.setSuccess(Boolean.TRUE);
            itemHoldResponse.setScreenMessage(ScsbCommonConstants.SUCCESS);
            itemHoldResponse.setTitleIdentifier(acceptItemResponse.getItemId().getItemIdentifierValue());
            itemHoldResponse.setPickupLocation(pickupLocation);
            itemHoldResponse.setInstitutionID(getInstitution());
            itemHoldResponse.setCreatedDate(new Date().toString());
            itemHoldResponse.setUpdatedDate(new Date().toString());
            Date expirationDateforHold = DateUtils.addYears(new Date(), 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ScsbConstants.DATE_FORMAT);
            itemHoldResponse.setExpirationDate(simpleDateFormat.format(expirationDateforHold));
        } catch (HttpClientErrorException httpException) {
            log.error(ScsbCommonConstants.LOG_ERROR, httpException);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(e.getMessage());
        }
        return itemHoldResponse;
    }
}
