package org.recap.ils;

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

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.ncip.AcceptItem;
import org.recap.ncip.CancelRequestItem;
import org.recap.ncip.CheckinItem;
import org.recap.ncip.CheckoutItem;
import org.recap.ncip.LookupUser;
import org.recap.ncip.RecallItem;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ils.model.nypl.BibLookupData;
import org.recap.ils.model.nypl.ItemLookupData;
import org.recap.ils.model.nypl.response.ItemLookupResponse;
import org.recap.ils.model.response.ItemLookUpInformationResponse;
import org.recap.ils.model.response.ItemCheckinResponse;
import org.recap.ils.model.response.ItemCheckoutResponse;
import org.recap.ils.model.response.ItemHoldResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.model.response.ItemRecallResponse;
import org.recap.ils.model.response.PatronInformationResponse;
import org.recap.ils.service.RestApiResponseUtil;
import org.recap.model.ILSConfigProperties;
import org.recap.model.AbstractResponseItem;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private String success = "Success";

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
        return RecapConstants.NCIP_PROTOCOL.equalsIgnoreCase(protocol);
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

    @Value("${ils.discharge.token}")
    private String dischargeToken;

    @Value("${ils.discharge.api.endpoint}")
    private String dischargeApiEndpoint;


    /**
     * Get rest template rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


    /**
     * Gets nypl api response util.
     *
     * @return the nypl api response util
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

    /**
     * Gets nypl data api url.
     *
     * @return the nypl data api url
     */
    public String getRestDataApiUrl() {
        return ilsConfigProperties.getIlsRestDataApi();
    }

    public String getBibDataApiUrl() {
        return ilsConfigProperties.getIlsBibdataApiEndpoint();
    }


    public String getApiUrl(String itemIdentifier) {
        return  getRestDataApiUrl() + "/items?item_barcode=" + itemIdentifier;
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
     //   itemInformationResponse.setCirculationStatus("CHARGED");
        // Commeneted for testing
        try {
            String owningInstitution = getRestApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier);

            String apiUrl = getApiUrl(itemIdentifier);

            HttpHeaders headers = getHttpHeader();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            org.springframework.http.HttpEntity requestEntity = getHttpEntity(headers);

            ResponseEntity<ItemLookupResponse> responseLookupEntity = restTemplate.exchange(apiUrl + "&apikey=" + ilsConfigProperties.getIlsApiKey(), HttpMethod.GET, requestEntity, ItemLookupResponse.class);
            ItemLookupResponse itemResponse = responseLookupEntity.getBody();
            log.info ("itemResponse {}" + itemResponse);

            ItemLookUpInformationResponse itemLookupData = buildResponse(itemResponse);
            itemInformationResponse.setItemBarcode(itemLookupData.getBarcode());
            itemInformationResponse.setCirculationStatus(itemLookupData.getCirculationStatus());
            itemInformationResponse.setBibID(itemLookupData.getBibId());
            itemInformationResponse.setTitleIdentifier(itemLookupData.getTitle());
            itemInformationResponse.setItemOwningInstitution(owningInstitution);

        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR, httpException);
            itemInformationResponse.setSuccess(false);
            itemInformationResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR, e);
            itemInformationResponse.setSuccess(false);
            itemInformationResponse.setScreenMessage(e.getMessage());
        }

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
            //transforms the NCIP xml response into NCIP Objects

            InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
            NCIPResponseData responseData = ncipToolkitUtil.translator.createResponseData(ncipToolkitUtil.serviceContext, stream);

            //transforms the NCIP Objects into a JSON response object
            CheckOutItemResponseData checkoutItemResponse = (CheckOutItemResponseData) responseData;

            responseObject = checkoutItem.getCheckoutResponse(checkoutItemResponse);

            if (!checkoutItemResponse.getProblems().isEmpty()) {
                itemCheckoutResponse.setSuccess(Boolean.FALSE);
                itemCheckoutResponse.setScreenMessage(failureReason + checkoutItemResponse.getProblems());
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
                itemCheckoutResponse.setScreenMessage(success);
                return itemCheckoutResponse;
            }

        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR, httpException);
            itemCheckoutResponse.setSuccess(false);
            itemCheckoutResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR, e);
            itemCheckoutResponse.setSuccess(false);
            itemCheckoutResponse.setScreenMessage(e.getMessage());
        }
        return itemCheckoutResponse;
    }

    @Override
    public Object checkInItem(ItemRequestInformation itemRequestInformation, String patronIdentifier) {
        log.info("Item barcode {} received for a checkin in" + getInstitution() + " for patron {}", itemRequestInformation.getItemBarcodes().get(0), patronIdentifier);
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        String responseString = null;
        JSONObject responseObject = new JSONObject();

        try {
            String behalfAgency = propertyUtil.getPropertyByInstitutionAndKey(getInstitution(), "ils.behalf.agency");
            if(getInstitution().equals(itemRequestInformation.getRequestingInstitution()) && behalfAgency.equals(RecapCommonConstants.ITEM))
            {
                behalfAgency = null;
            }
            String isCheckinInstitution = propertyUtil.getPropertyByInstitutionAndKey(itemRequestInformation.getRequestingInstitution(), "ils.checkin.institution");

            if ((itemRequestInformation.getRequestingInstitution() == null || !itemRequestInformation.getItemOwningInstitution().equalsIgnoreCase(itemRequestInformation.getRequestingInstitution())
                    ) || Boolean.FALSE.toString().equalsIgnoreCase(isCheckinInstitution)) {

                CheckinItem checkInItem = new CheckinItem();
                CheckInItemInitiationData checkInItemInitiationData = checkInItem.getCheckInItemInitiationData(itemRequestInformation.getItemBarcodes().get(0), getItemDetailsRepository(), behalfAgency, getNcipAgencyId(), getNcipScheme());
                NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();

                String requestBody = checkInItem.getRequestBody(ncipToolkitUtil, checkInItemInitiationData);

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
                CheckInItemResponseData checkinItemResponse = (CheckInItemResponseData) responseData;

                responseObject = checkInItem.getCheckInResponse(checkinItemResponse);

                if (!checkinItemResponse.getProblems().isEmpty()) {
                    itemCheckinResponse.setSuccess(Boolean.FALSE);
                    itemCheckinResponse.setScreenMessage(failureReason + checkinItemResponse.getProblems());
                    return itemCheckinResponse;
                }
            }
                String isDischargeInstitution = propertyUtil.getPropertyByInstitutionAndKey(itemRequestInformation.getItemOwningInstitution(), "ils.discharge.institution");

                if (Boolean.TRUE.toString().equalsIgnoreCase(isDischargeInstitution) && getInstitution().equals(itemRequestInformation.getItemOwningInstitution())) {
                    List<ItemEntity> itemEntities = getItemDetailsRepository().findByBarcode(itemRequestInformation.getItemBarcodes().get(0));
                    ItemEntity itemEntity = !itemEntities.isEmpty() ? itemEntities.get(0) : null;

                    String itemId = itemEntity != null ? itemEntity.getOwningInstitutionItemId() : null;
                    List<BibliographicEntity> bibliographicEntities = itemEntity != null ? itemEntity.getBibliographicEntities() : new ArrayList<>();
                    BibliographicEntity bibliographicEntity = !bibliographicEntities.isEmpty() ? bibliographicEntities.get(0) : null;
                    String bibId = bibliographicEntity != null ? bibliographicEntity.getOwningInstitutionBibId() : null;
                    List<HoldingsEntity> holdingsEntities = itemEntity != null ? itemEntity.getHoldingsEntities() : new ArrayList<>();
                    HoldingsEntity holdingsEntity = !holdingsEntities.isEmpty() ? holdingsEntities.get(0) : null;
                    String holdingId = holdingsEntity != null ? holdingsEntity.getOwningInstitutionHoldingsId() : null;
                    log.info("itemEntity >>> " + itemEntity.getId() + " <<<<<<<>>>>>  " + itemEntity.getBarcode());
                    log.info("bibliographicEntity.getOwningInstitutionBibId() >>> " + bibliographicEntity.getOwningInstitutionBibId());
                    log.info("holdingsEntity.getOwningInstitutionHoldingsId() >>> " + holdingsEntity.getOwningInstitutionHoldingsId());
                    log.info("itemEntity getOwningInstitutionItemId >>> " + itemEntity.getOwningInstitutionItemId());
                    Map<String, String> params = getParamsMap(bibId, holdingId, itemId);
                    HttpHeaders headers = getHttpHeader();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    JSONObject authJson = new JSONObject();
                    authJson.put("auth_token", dischargeToken);

                    org.springframework.http.HttpEntity requestEntity = getHttpEntity(authJson, headers);
                    ResponseEntity<String> respnseLookupEntity = restTemplate.exchange(dischargeApiEndpoint, HttpMethod.POST, requestEntity, String.class, params);
                    log.info("responseLookupEntity >>>>>>>>> " + respnseLookupEntity);
                }
                    log.info(responseObject.toString());
                    itemCheckinResponse.setSuccess(Boolean.TRUE);
                    itemCheckinResponse.setScreenMessage(success);
                    itemCheckinResponse.setItemOwningInstitution(getInstitution());

        }
        catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR, httpException);
            itemCheckinResponse.setSuccess(false);
            itemCheckinResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR, e);
            itemCheckinResponse.setSuccess(false);
            itemCheckinResponse.setScreenMessage(e.getMessage());
        }

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
        log.info("Inside placeHold >>>" + callInstitutionId +  " <<>>" + itemInstitutionId);
        log.info("Item barcode {} received for hold request in " + itemInstitutionId + " for patron {}", itemIdentifier, patronIdentifier);
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        if (callInstitutionId.equalsIgnoreCase(itemInstitutionId)) {
                itemHoldResponse.setSuccess(Boolean.TRUE);
            }
            else {
                itemHoldResponse = acceptItem(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
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
                return itemHoldResponse;
            }

            itemHoldResponse.setItemOwningInstitution(cancelItemResponse.getResponseHeader().getToAgencyId().getAgencyId().getValue());
            itemHoldResponse.setItemBarcode(cancelItemResponse.getItemId().getItemIdentifierValue());
            itemHoldResponse.setPatronIdentifier(cancelItemResponse.getRequestId().getAgencyId().getValue());
            itemHoldResponse.setSuccess(Boolean.TRUE);
            itemHoldResponse.setTitleIdentifier(cancelItemResponse.getItemId().getItemIdentifierValue());
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR, httpException);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR, e);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(e.getMessage());
        }
        return itemHoldResponse;
    }

    @Override
    public Object createBib(String itemIdentifier, String patronIdentifier, String institutionId, String titleIdentifier) {
        return null;
    }

    @Override
    public boolean patronValidation(String institutionId, String patronIdentifier) {
        return false;
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

                return patronInformationResponse;
            } else {
                log.info("responseobject toString >>> " + responseObject.toString());

                patronInformationResponse.setPatronName(responseObject.getString("name"));
                patronInformationResponse.setSuccess(Boolean.TRUE);
                patronInformationResponse.setScreenMessage(success);
                patronInformationResponse.setHomeAddress(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getStreet().concat(",").concat(
                        lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getLocality()).concat(",").
                        concat(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getRegion().concat(",").
                                concat(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(0).getPhysicalAddress().getStructuredAddress().getPostalCode())));

                patronInformationResponse.setEmail(lookupUserResponseData.getUserOptionalFields().getUserAddressInformation(1).getElectronicAddress().getElectronicAddressData());
                patronInformationResponse.setPatronIdentifier(patronIdentifier);

                return patronInformationResponse;
            }
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR, httpException);
            patronInformationResponse.setSuccess(false);
            patronInformationResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR, e);
            patronInformationResponse.setSuccess(false);
            patronInformationResponse.setScreenMessage(e.getMessage());
        }
        return patronInformationResponse;
    }

    @Override
    public Object recallItem(String itemIdentifier, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocation) {
        log.info("Lookup for patron {}", patronIdentifier);
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
                return itemRecallResponse;
            }
            log.info(responseObject.toString());

            itemRecallResponse.setPatronIdentifier(recallItemResponse.getUserId() != null ? recallItemResponse.getUserId().getUserIdentifierValue() : "");
            itemRecallResponse.setItemBarcode(recallItemResponse.getItemId().getItemIdentifierValue());
            itemRecallResponse.setItemOwningInstitution(institutionId);
            Date expirationDateforRecall = DateUtils.addYears(new Date(), 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.DATE_FORMAT);
            itemRecallResponse.setExpirationDate(simpleDateFormat.format(expirationDateforRecall));

            itemRecallResponse.setSuccess(Boolean.TRUE);
            itemRecallResponse.setScreenMessage(success);

        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR, httpException);
            itemRecallResponse.setSuccess(false);
            itemRecallResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR, e);
            itemRecallResponse.setSuccess(false);
            itemRecallResponse.setScreenMessage(e.getMessage());
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

    private ItemHoldResponse acceptItem(String itemIdentifier, Integer requestId, String patronIdentifier, String callInstitutionId, String itemInstitutionId, String expirationDate, String bibId, String pickupLocation, String trackingId, String title, String author, String callNumber) {
        AcceptItem acceptItem = new AcceptItem();
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        String responseString = null;
        JSONObject responseObject;
        try {
            NCIPToolKitUtil ncipToolkitUtil = NCIPToolKitUtil.getInstance();
            CloseableHttpClient client = buildCloseableHttpClient();
            AcceptItemInitiationData acceptItemInitiationData = acceptItem.getAcceptItemInitiationData(itemIdentifier, requestId, patronIdentifier, title, author, callNumber, getNcipAgencyId(), getNcipScheme());
            String requestBody = acceptItem.getRequestBody(ncipToolkitUtil, acceptItemInitiationData);
            log.info("AcceptItem Request Body >>> " + requestBody);
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
            itemHoldResponse.setScreenMessage(success);
            itemHoldResponse.setTitleIdentifier(acceptItemResponse.getItemId().getItemIdentifierValue());
            itemHoldResponse.setPickupLocation(pickupLocation);
            itemHoldResponse.setInstitutionID(getInstitution());
            itemHoldResponse.setCreatedDate(new Date().toString());
            itemHoldResponse.setUpdatedDate(new Date().toString());
            Date expirationDateforHold = DateUtils.addYears(new Date(), 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.DATE_FORMAT);
            itemHoldResponse.setExpirationDate(simpleDateFormat.format(expirationDateforHold));
        }
        catch(HttpClientErrorException httpException){
            log.error(RecapCommonConstants.LOG_ERROR, httpException);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(httpException.getStatusText());
        } catch(Exception e){
            log.error(RecapCommonConstants.LOG_ERROR, e);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(e.getMessage());
        }
        return itemHoldResponse;

    }
}
