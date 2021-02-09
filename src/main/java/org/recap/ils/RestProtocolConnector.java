package org.recap.ils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.ils.model.nypl.*;
import org.recap.ils.model.nypl.request.*;
import org.recap.ils.model.nypl.response.*;
import org.recap.ils.model.response.*;
import org.recap.ils.service.RestApiResponseUtil;
import org.recap.ils.service.RestOauthTokenApiService;
import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.processor.RestProtocolJobResponsePollingProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RestProtocolConnector extends AbstractProtocolConnector {
    
    @Autowired
    RestOauthTokenApiService restOauthTokenApiService;
  
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RestApiResponseUtil restApiResponseUtil;
   
    @Autowired
    RestProtocolJobResponsePollingProcessor restProtocolJobResponsePollingProcessor;


    /**
     * Gets nypl data api url.
     *
     * @return the nypl data api url
     */
    public String getRestDataApiUrl() {
        return ilsConfigProperties.getIlsRestDataApi();
    }

    /**
     * Gets oauth token api url.
     *
     * @return the oauth token api url
     */
    public String getOauthTokenApiUrl() {
        return ilsConfigProperties.getOauthTokenApiUrl();
    }

    /**
     * Get rest template rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    /**
     * Get http header http headers.
     *
     * @return the http headers
     */
    public HttpHeaders getHttpHeader(){
        return new HttpHeaders();
    }

    /**
     * Get http entity http entity.
     *
     * @param headers the headers
     * @return the http entity
     */
    public HttpEntity getHttpEntity(HttpHeaders headers){
        return new HttpEntity<>(headers);
    }

    /**
     * Get api url string.
     *
     * @param source         the source
     * @param itemIdentifier the item identifier
     * @return the string
     */
    public String getApiUrl(String source,String itemIdentifier) {
        if (StringUtils.isBlank(source)) {
            return getRestDataApiUrl() + "/items/" + itemIdentifier;
        }
        return getRestDataApiUrl() + "/items/" + source + "/" + itemIdentifier;
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
     * Gets nypl oauth token api service.
     *
     * @return the nypl oauth token api service
     */
    public RestOauthTokenApiService getRestOauthTokenApiService() {
        return restOauthTokenApiService;
    }

    /**
     * Gets nypl job response polling processor.
     *
     * @return the nypl job response polling processor
     */
    public RestProtocolJobResponsePollingProcessor getRestProtocolJobResponsePollingProcessor() {
        return restProtocolJobResponsePollingProcessor;
    }

    @Override
    public boolean supports(String protocol) {
        return RecapConstants.REST_PROTOCOL.equalsIgnoreCase(protocol);
    }

    @Override
    public void setInstitution(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    @Override
    public void setIlsConfigProperties(ILSConfigProperties ilsConfigProperties) {
        this.ilsConfigProperties = ilsConfigProperties;
    }

    /**
     * Get check out request checkout request.
     *
     * @return the checkout request
     */
    public CheckoutRequest getCheckOutRequest(){
        return new CheckoutRequest();
    }

    /**
     * Get check in request checkin request.
     *
     * @return the checkin request
     */
    public CheckinRequest getCheckInRequest(){
        return new CheckinRequest();
    }

    /**
     * Get create hold request create hold request.
     *
     * @return the create hold request
     */
    public CreateHoldRequest getCreateHoldRequest(){
        return new CreateHoldRequest();
    }

    /**
     * Get cancel hold request cancel hold request.
     *
     * @return the cancel hold request
     */
    public CancelHoldRequest getCancelHoldRequest(){
        return new CancelHoldRequest();
    }

    /**
     * Look up item in REST protocol using Institution for the given item identifier.
     *
     * @param itemIdentifier the item identifier
     * @return
     */
    @Override
    public ItemInformationResponse lookupItem(String itemIdentifier) {
        log.info("Lookup item in {} for barcode : {}", this.institutionCode, itemIdentifier);
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        try {
            String owningInstitution = getRestApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier);
            String source = getRestApiResponseUtil().getRestApiSourceForInstitution(this.institutionCode, owningInstitution);
            itemIdentifier = getRestApiResponseUtil().getNormalizedItemIdForRestProtocolApi(itemIdentifier);
            String apiUrl = getApiUrl(source, itemIdentifier);
            String authorization = "Bearer " + getRestOauthTokenApiService().generateAccessTokenForRestApi(getOauthTokenApiUrl(), getOperatorUserId(), getOperatorPassword());

            HttpHeaders headers = getHttpHeader();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", authorization);

            HttpEntity requestEntity = getHttpEntity(headers);
            ResponseEntity<ItemResponse> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ItemResponse.class);
            ItemResponse itemResponse = responseEntity.getBody();
            itemInformationResponse = getRestApiResponseUtil().buildItemInformationResponse(itemResponse);
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR,httpException);
            itemInformationResponse.setSuccess(false);
            itemInformationResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR,e);
            itemInformationResponse.setSuccess(false);
            itemInformationResponse.setScreenMessage(e.getMessage());
        }
        return itemInformationResponse;
    }

    /**
     * Checks out item in REST protocol using Institution for the given patron.
     *
     * @param itemIdentifier   the item identifier
     * @param requestId the request identifier
     * @param patronIdentifier the patron identifier
     * @return
     */
    @Override
    public ItemCheckoutResponse checkOutItem(String itemIdentifier, String requestId, String patronIdentifier) {
        log.info("Item barcode {} received for a checkout in {} for patron {}", itemIdentifier, this.institutionCode, patronIdentifier);
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        try {
            String apiUrl = getRestDataApiUrl() + RecapConstants.REST_CHECKOUT_REQUEST_URL;
            CheckoutRequest checkoutRequest = getCheckOutRequest();
            checkoutRequest.setPatronBarcode(patronIdentifier);
            checkoutRequest.setItemBarcode(itemIdentifier);
            checkoutRequest.setDesiredDateDue(getRestApiResponseUtil().getExpirationDateForRest());

            HttpEntity<CheckoutRequest> requestEntity = new HttpEntity<>(checkoutRequest, getHttpHeaders());
            ResponseEntity<CheckoutResponse> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, CheckoutResponse.class);
            CheckoutResponse checkoutResponse = responseEntity.getBody();
            itemCheckoutResponse = getRestApiResponseUtil().buildItemCheckoutResponse(checkoutResponse);
            CheckoutData checkoutData = checkoutResponse.getData();
            if (null != checkoutData) {
                String jobId = checkoutData.getJobId();
                itemCheckoutResponse.setJobId(jobId);
                log.info("Initiated checkout on {}", this.institutionCode);
                log.info("{} checkout job id -> {} " , this.institutionCode, jobId);
                JobResponse jobResponse = getRestProtocolJobResponsePollingProcessor().pollRestApiRequestItemJobResponse(itemCheckoutResponse.getJobId(), institutionCode);
                String statusMessage = jobResponse.getStatusMessage();
                itemCheckoutResponse.setScreenMessage(statusMessage);
                JobData jobData = jobResponse.getData();
                if (null != jobData) {
                    itemCheckoutResponse.setSuccess(jobData.getSuccess());
                    log.info("Checkout Finished ->  {} " , jobData.getFinished());
                    log.info("Checkout Success -> {}", jobData.getSuccess());
                    log.info(statusMessage);
                } else {
                    itemCheckoutResponse.setSuccess(false);
                    log.info("Checkout Finished -> {}" , false);
                    log.info("Checkout Success -> {}", false);
                    log.info(statusMessage);
                }
            }
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR,httpException);
            itemCheckoutResponse.setSuccess(false);
            itemCheckoutResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR,e);
            itemCheckoutResponse.setSuccess(false);
            itemCheckoutResponse.setScreenMessage(e.getMessage());
        }
        return itemCheckoutResponse;
    }

    /**
     * Checks in item in REST protocol using Institution for the given patron.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @return
     */
    @Override
    public ItemCheckinResponse checkInItem(String itemIdentifier, String patronIdentifier) {
        log.info("Item barcode {} received for a checkin in {} for patron {}", itemIdentifier, this.institutionCode, patronIdentifier);
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        try {
            String apiUrl = getRestDataApiUrl() + RecapConstants.REST_CHECKIN_REQUEST_URL;

            CheckinRequest checkinRequest = getCheckInRequest();
            checkinRequest.setItemBarcode(itemIdentifier);

            HttpEntity<CheckinRequest> requestEntity = new HttpEntity<>(checkinRequest, getHttpHeaders());
            ResponseEntity<CheckinResponse> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, CheckinResponse.class);
            CheckinResponse checkinResponse = responseEntity.getBody();
            itemCheckinResponse = getRestApiResponseUtil().buildItemCheckinResponse(checkinResponse);
            CheckinData checkinData = checkinResponse.getData();
            if (null != checkinData) {
                String jobId = checkinData.getJobId();
                itemCheckinResponse.setJobId(jobId);
                log.info("Initiated checkin on {}", this.institutionCode);
                log.info("{} checkin job id -> {} " , this.institutionCode, jobId);
                JobResponse jobResponse = getRestProtocolJobResponsePollingProcessor().pollRestApiRequestItemJobResponse(itemCheckinResponse.getJobId(), institutionCode);
                String statusMessage = jobResponse.getStatusMessage();
                itemCheckinResponse.setScreenMessage(statusMessage);
                JobData jobData = jobResponse.getData();
                if (null != jobData) {
                    itemCheckinResponse.setSuccess(jobData.getSuccess());
                    log.info("Checkin Finished -> {}" , jobData.getFinished());
                    log.info("Checkin Success -> {}" , jobData.getSuccess());
                    log.info(statusMessage);
                } else {
                    itemCheckinResponse.setSuccess(false);
                    log.info("Checkin Finished -> " + false);
                    log.info("Checkin Success -> " + false);
                    log.info(statusMessage);
                }
            }
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR,httpException);
            itemCheckinResponse.setSuccess(false);
            itemCheckinResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR,e);
            itemCheckinResponse.setSuccess(false);
            itemCheckinResponse.setScreenMessage(e.getMessage());
        }
        return itemCheckinResponse;
    }

    /**
     * Creates a hold request in REST protocol using Institution with the provided information.
     *
     * @param itemIdentifier    the item identifier
     * @param patronIdentifier  the patron identifier
     * @param callInstitutionId the call institution id
     * @param itemInstitutionId the item institution id
     * @param expirationDate    the expiration date
     * @param bibId             the bib id
     * @param deliveryLocation
     * @param trackingId        the tracking id
     * @param title             the title
     * @param author            the author
     * @param callNumber        the call number
     * @return
     */
    @Override
    public AbstractResponseItem placeHold(String itemIdentifier, String requestId, String patronIdentifier, String callInstitutionId, String itemInstitutionId, String expirationDate, String bibId, String deliveryLocation, String trackingId, String title, String author, String callNumber) {
        log.info("Item barcode {} received for hold request in {} for patron {}", itemIdentifier, this.institutionCode, patronIdentifier);
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        try {
            String recapHoldApiUrl = getRestDataApiUrl() + RecapConstants.REST_RECAP_HOLD_REQUEST_URL;
            if (StringUtils.isBlank(trackingId)) {
                trackingId = initiateHoldRequest(itemIdentifier, patronIdentifier, itemInstitutionId, deliveryLocation);
            }
            CreateHoldRequest createHoldRequest = getCreateHoldRequest();
            createHoldRequest.setTrackingId(trackingId);
            createHoldRequest.setOwningInstitutionId(itemInstitutionId);
            createHoldRequest.setItemBarcode(itemIdentifier);
            createHoldRequest.setPatronBarcode(patronIdentifier);
            Description description = new Description();
            String titleIdentifier = title.replace("[" + RecapConstants.REQUEST_USE_RESTRICTIONS + "]", "[" + MessageFormat.format(RecapConstants.REST_NO_RESTRICTIONS, this.institutionCode) + "]");
            log.info("{} title identifier : {} ", this.institutionCode, titleIdentifier);
            description.setTitle(titleIdentifier);
            description.setAuthor(author);
            description.setCallNumber(callNumber);
            createHoldRequest.setDescription(description);

            HttpEntity<CreateHoldRequest> requestEntity = new HttpEntity<>(createHoldRequest, getHttpHeaders());
            ResponseEntity<CreateHoldResponse> responseEntity = restTemplate.exchange(recapHoldApiUrl, HttpMethod.POST, requestEntity, CreateHoldResponse.class);
            CreateHoldResponse createHoldResponse = responseEntity.getBody();
            itemHoldResponse = getRestApiResponseUtil().buildItemHoldResponse(createHoldResponse);
            CreateHoldData createHoldData = createHoldResponse.getData();
            if (null != createHoldData) {
                String responseTrackingId = createHoldData.getTrackingId();
                NYPLHoldResponse nyplHoldResponse = queryHoldResponseByTrackingId(responseTrackingId);
                NYPLHoldData nyplHoldData = nyplHoldResponse.getData();
                if (null != nyplHoldData) {
                    String jobId = nyplHoldData.getJobId();
                    itemHoldResponse.setJobId(jobId);
                    log.info("Initiated recap hold request on {}", this.institutionCode);
                    log.info("{} Hold request job id -> {} " , this.institutionCode, jobId);
                    JobResponse jobResponse = getRestProtocolJobResponsePollingProcessor().pollRestApiRequestItemJobResponse(itemHoldResponse.getJobId(), institutionCode);
                    String statusMessage = jobResponse.getStatusMessage();
                    itemHoldResponse.setScreenMessage(statusMessage);
                    JobData jobData = jobResponse.getData();
                    if (null != jobData) {
                        itemHoldResponse.setSuccess(jobData.getSuccess());
                        log.info("Hold Finished -> {}" , jobData.getFinished());
                        log.info("Hold Success -> {}" , jobData.getSuccess());
                        log.info(statusMessage);
                    } else {
                        itemHoldResponse.setSuccess(false);
                        log.info("Hold Finished -> " + false);
                        log.info("Hold Success -> " + false);
                        log.info(statusMessage);
                    }
                }
            }
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR,httpException);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR,e);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(e.getMessage());
        }
        return itemHoldResponse;
    }

    /**
     * Cancels hold request in REST protocol using Institution for the given item.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @param institutionId    the institution id
     * @param expirationDate   the expiration date
     * @param bibId            the bib id
     * @param pickupLocation   the pickup location
     * @param trackingId       the tracking id
     * @return
     */
    @Override
    public AbstractResponseItem cancelHold(String itemIdentifier, String requestId, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocation, String trackingId) {
        log.info("Item barcode {} received for a canceling a hold in {} for patron {}", itemIdentifier, this.institutionCode, patronIdentifier);
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        try {
            String apiUrl = getRestDataApiUrl() + RecapConstants.REST_RECAP_CANCEL_HOLD_REQUEST_URL;

            CancelHoldRequest cancelHoldRequest = getCancelHoldRequest();
            cancelHoldRequest.setTrackingId(trackingId);
            cancelHoldRequest.setOwningInstitutionId(getRestApiResponseUtil().getItemOwningInstitutionByItemBarcode(itemIdentifier));
            cancelHoldRequest.setItemBarcode(itemIdentifier);
            cancelHoldRequest.setPatronBarcode(patronIdentifier);

            HttpEntity<CancelHoldRequest> requestEntity = new HttpEntity<>(cancelHoldRequest, getHttpHeaders());
            ResponseEntity<CancelHoldResponse> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, CancelHoldResponse.class);
            CancelHoldResponse cancelHoldResponse = responseEntity.getBody();
            itemHoldResponse = getRestApiResponseUtil().buildItemCancelHoldResponse(cancelHoldResponse);
            CancelHoldData cancelHoldData = cancelHoldResponse.getData();
            if (null != cancelHoldData) {
                String jobId = cancelHoldData.getJobId();
                itemHoldResponse.setJobId(jobId);
                log.info("Initiated cancel hold request on {}", this.institutionCode);
                log.info("{} cancel hold request job id -> {}" , this.institutionCode, jobId);
                JobResponse jobResponse = getRestProtocolJobResponsePollingProcessor().pollRestApiRequestItemJobResponse(itemHoldResponse.getJobId(), institutionCode);
                String statusMessage = jobResponse.getStatusMessage();
                itemHoldResponse.setScreenMessage(statusMessage);
                JobData jobData = jobResponse.getData();
                if (null != jobData) {
                    itemHoldResponse.setSuccess(jobData.getSuccess());
                    log.info("Cancel hold Finished -> {}" , jobData.getFinished());
                    log.info("Cancel hold Success -> {}" , jobData.getSuccess());
                    log.info(statusMessage);
                } else {
                    itemHoldResponse.setSuccess(false);
                    log.info("Cancel hold Finished -> " + false);
                    log.info("Cancel hold Success -> " + false);
                    log.info(statusMessage);
                }
            }
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR,httpException);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR,e);
            itemHoldResponse.setSuccess(false);
            itemHoldResponse.setScreenMessage(e.getMessage());
        }
        return itemHoldResponse;
    }

    /**
     * Query for job response by job id.
     *
     * @param jobId the job id
     * @return the job response
     * @throws Exception the exception
     */
    public JobResponse queryForJob(String jobId) throws Exception {
        String apiUrl = getRestDataApiUrl() + "/jobs/" + jobId;
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        ResponseEntity<JobResponse> jobResponseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, JobResponse.class);
        return jobResponseEntity.getBody();
    }

    /**
     * Gets the hold response information by the tracking id.
     *
     * @param trackingId
     * @return
     * @throws Exception
     */
    private NYPLHoldResponse queryHoldResponseByTrackingId(String trackingId) throws Exception {
        String apiUrl = getRestDataApiUrl() + "/hold-requests/" + trackingId;
        HttpHeaders headers = getHttpHeaders();
        HttpEntity requestEntity = getHttpEntity(headers);
        ResponseEntity<NYPLHoldResponse> jobResponseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, NYPLHoldResponse.class);
        return jobResponseEntity.getBody();
    }

    /**
     * Build Http headers to access NYPL API.
     *
     * @return
     * @throws Exception
     */
    private HttpHeaders getHttpHeaders() throws Exception {
        String authorization = "Bearer " + getRestOauthTokenApiService().generateAccessTokenForRestApi(getOauthTokenApiUrl(), getOperatorUserId(), getOperatorPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", authorization);
        return headers;
    }

    /**
     * This method initiates the hold request on NYPL end to get the tracking id and use it in recap hold request subsequently.
     *
     * @param itemIdentifier
     * @param patronIdentifier
     * @param itemInstitutionId
     * @param deliveryLocation
     * @return
     * @throws Exception
     */
    private String initiateHoldRequest(String itemIdentifier, String patronIdentifier, String itemInstitutionId, String deliveryLocation) throws Exception {
        String trackingId = null;
        String nyplHoldApiUrl = getRestDataApiUrl() + RecapConstants.REST_HOLD_REQUEST_URL;
        String nyplSource = restApiResponseUtil.getRestApiSourceForInstitution(this.institutionCode, itemInstitutionId);
        NyplHoldRequest nyplHoldRequest = new NyplHoldRequest();
        nyplHoldRequest.setRecord(restApiResponseUtil.getNormalizedItemIdForRestProtocolApi(itemIdentifier));
        nyplHoldRequest.setPatron(getPatronIdByPatronBarcode(patronIdentifier));
        nyplHoldRequest.setNyplSource(nyplSource);
        nyplHoldRequest.setRecordType(RecapConstants.REST_RECORD_TYPE);
        nyplHoldRequest.setPickupLocation("");
        nyplHoldRequest.setDeliveryLocation(deliveryLocation);
        nyplHoldRequest.setNumberOfCopies(1);
        nyplHoldRequest.setNeededBy(restApiResponseUtil.getExpirationDateForRest());

        HttpEntity<NyplHoldRequest> requestEntity = new HttpEntity<>(nyplHoldRequest, getHttpHeaders());
        ResponseEntity<NYPLHoldResponse> responseEntity = restTemplate.exchange(nyplHoldApiUrl, HttpMethod.POST, requestEntity, NYPLHoldResponse.class);
        NYPLHoldResponse nyplHoldResponse = responseEntity.getBody();
        NYPLHoldData nyplHoldData = nyplHoldResponse.getData();
        if (null != nyplHoldData) {
            trackingId = String.valueOf(nyplHoldData.getId());
        }
        return trackingId;
    }

    /**
     * Gets patron id by the given patron barcode from NYPL.
     *
     * @param patronBarcode
     * @return
     * @throws Exception
     */
    private String getPatronIdByPatronBarcode(String patronBarcode) throws Exception {
        String patronId = null;
        NyplPatronResponse nyplPatronResponse = queryForPatronResponse(patronBarcode);
        List<NyplPatronData> nyplPatronDatas = nyplPatronResponse.getData();
        if (CollectionUtils.isNotEmpty(nyplPatronDatas)) {
            NyplPatronData nyplPatronData = nyplPatronDatas.get(0);
            patronId = nyplPatronData.getId();
        }
        return patronId;
    }

    /**
     * Get patron response information based on the patron id from REST API.
     *
     * @param patronIdentifier
     * @return
     * @throws Exception
     */
    private NyplPatronResponse queryForPatronResponse(String patronIdentifier) throws Exception {
        String apiUrl = getRestDataApiUrl() + RecapConstants.REST_PATRON_BY_BARCODE_URL + patronIdentifier;
        log.info("{} patron response url : {}" , this.institutionCode, apiUrl);
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        ResponseEntity<NyplPatronResponse> jobResponseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, NyplPatronResponse.class);
        return jobResponseEntity.getBody();
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
        return null;
    }

    /**
     * Creates a recall request for the item in REST protocol using Institution.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @param institutionId    the institution id
     * @param expirationDate   the expiration date
     * @param bibId            the bib id
     * @param pickupLocation   the pickup location
     * @return
     */
    @Override
    public AbstractResponseItem recallItem(String itemIdentifier, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocation) {
        log.info("Item barcode {} received for a recall request in REST protocol using Institution for patron {}", itemIdentifier, patronIdentifier);
       /* ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        try {
            String apiUrl = getRestDataApiUrl() + RecapConstants.NYPL_RECAP_RECALL_REQUEST_URL;

            RecallRequest recallRequest = new RecallRequest();
            recallRequest.setOwningInstitutionId(nyplApiResponseUtil.getItemOwningInstitutionByItemBarcode(itemIdentifier));
            recallRequest.setItemBarcode(itemIdentifier);

            HttpEntity<RecallRequest> requestEntity = new HttpEntity(recallRequest, getHttpHeaders());
            ResponseEntity<RecallResponse> responseEntity = getRestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, RecallResponse.class);
            RecallResponse recallResponse = responseEntity.getBody();
            itemRecallResponse = getNyplApiResponseUtil().buildItemRecallResponse(recallResponse);
            RecallData recallData = recallResponse.getData();
            if (null != recallData) {
                String jobId = recallData.getJobId();
                itemRecallResponse.setJobId(jobId);
                log.info("Initiated recall request on NYPL");
                log.info("Nypl recall request job id -> {}" , jobId);
                JobResponse jobResponse = getRestProtocolJobResponsePollingProcessor().pollNyplRequestItemJobResponse(itemRecallResponse.getJobId());
                String statusMessage = jobResponse.getStatusMessage();
                itemRecallResponse.setScreenMessage(statusMessage);
                JobData jobData = jobResponse.getData();
                if (null != jobData) {
                    itemRecallResponse.setSuccess(jobData.getSuccess());
                    log.info("Recall request Finished -> {} " , jobData.getFinished());
                    log.info("Recall request Success -> {} " , jobData.getSuccess());
                    log.info(statusMessage);
                } else {
                    itemRecallResponse.setSuccess(false);
                    log.info("Recall request Finished -> " + false);
                    log.info("Recall request Success -> " + false);
                    log.info(statusMessage);
                }
            }
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR,httpException);
            itemRecallResponse.setSuccess(false);
            itemRecallResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR,e);
            itemRecallResponse.setSuccess(false);
            itemRecallResponse.setScreenMessage(e.getMessage());
        }*/
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setSuccess(true);
        itemRecallResponse.setScreenMessage("Success");
        return itemRecallResponse;
    }

    /**
     * Creates a refile request for the item in REST protocol using Institution.
     *
     * @param itemIdentifier the item identifier
     * @return ItemRefileResponse
     */
    @Override
    public ItemRefileResponse refileItem(String itemIdentifier) {
        log.info("Item barcode {} received for refiling in {} ", itemIdentifier, this.institutionCode);
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        try {
            String apiUrl = getRestDataApiUrl() + RecapConstants.REST_RECAP_REFILE_REQUEST_URL;

            RefileRequest refileRequest = new RefileRequest();
            refileRequest.setItemBarcode(itemIdentifier);

            HttpEntity<RefileRequest> requestEntity = new HttpEntity<>(refileRequest, getHttpHeaders());
            ResponseEntity<RefileResponse> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, RefileResponse.class);
            RefileResponse refileResponse = responseEntity.getBody();
            itemRefileResponse = getRestApiResponseUtil().buildItemRefileResponse(refileResponse, this.institutionCode);
            RefileData refileData = refileResponse.getData();
            if (null != refileData) {
                String jobId = refileData.getJobId();
                itemRefileResponse.setJobId(jobId);
                log.info("Initiated refile request on {}", this.institutionCode);
                log.info("{} refile request job id -> {}" , this.institutionCode, jobId);
                JobResponse jobResponse = getRestProtocolJobResponsePollingProcessor().pollRestApiRequestItemJobResponse(itemRefileResponse.getJobId(), institutionCode);
                String statusMessage = jobResponse.getStatusMessage();
                itemRefileResponse.setScreenMessage(statusMessage);
                JobData jobData = jobResponse.getData();
                if (null != jobData) {
                    itemRefileResponse.setSuccess(jobData.getSuccess());
                    log.info("Refile request Finished -> {}" , jobData.getFinished());
                    log.info("Refile request Success -> {}" , jobData.getSuccess());
                    log.info(statusMessage);
                } else {
                    itemRefileResponse.setSuccess(false);
                    log.info("Refile request Finished -> " + false);
                    log.info("Refile request Success -> " + false);
                    log.info(statusMessage);
                }
            }
        } catch (HttpClientErrorException httpException) {
            log.error(RecapCommonConstants.LOG_ERROR,httpException);
            itemRefileResponse.setSuccess(false);
            itemRefileResponse.setScreenMessage(httpException.getStatusText());
        } catch (Exception e) {
            log.error(RecapCommonConstants.LOG_ERROR,e);
            itemRefileResponse.setSuccess(false);
            itemRefileResponse.setScreenMessage(e.getMessage());
        }
        return itemRefileResponse;
    }
}
