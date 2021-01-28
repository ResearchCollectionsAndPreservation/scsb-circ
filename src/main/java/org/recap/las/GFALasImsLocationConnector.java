package org.recap.las;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.las.model.*;
import org.recap.model.IMSConfigProperties;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by rajeshbabuk on 20/Jan/2021
 */
@Service
@Slf4j
public class GFALasImsLocationConnector extends AbstractLASImsLocationConnector {

    @Autowired
    private GFALasServiceUtil gfaLasServiceUtil;

    @Override
    public boolean supports(String imsLocationCode) {
        return true;
    }

    @Override
    public void setImsLocationCode(String imsLocationCode) {
        this.imsLocationCode = imsLocationCode;
    }

    @Override
    public void setImsConfigProperties(IMSConfigProperties imsConfigProperties) {
        this.imsConfigProperties = imsConfigProperties;
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
     * Gets Http Headers with ContentType Json.
     *
     * @return the HttpHeaders
     */
    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Item status check gfa heart beat check response.
     *
     * @param gfaLasStatusCheckRequest the gfa heart beat check request
     * @return the gfa heart beat check response
     */
    @Override
    public GFALasStatusCheckResponse heartBeatCheck(GFALasStatusCheckRequest gfaLasStatusCheckRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String filterParamValue = "";
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = null;
        try {
            filterParamValue = objectMapper.writeValueAsString(gfaLasStatusCheckRequest);
            log.info("Las Heart Beat Request at {} : {}", this.imsLocationCode, filterParamValue);

            RestTemplate restTemplate = getRestTemplate();
            HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(new HttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.imsConfigProperties.getLasServerStatusEndpoint()).queryParam(RecapConstants.GFA_SERVICE_PARAM, filterParamValue);
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ResponseEntity<GFALasStatusCheckResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFALasStatusCheckResponse.class);
            if (responseEntity.getBody() != null) {
                gfaLasStatusCheckResponse = responseEntity.getBody();
                log.info("Las Heart Beat Response at {} : {}", this.imsLocationCode, gfaLasStatusCheckResponse);
            }
        } catch (JsonProcessingException e) {
            log.error(RecapConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION + "{}", e.getMessage());
        }
        return gfaLasStatusCheckResponse;
    }

    /**
     * Item status check gfa item status check response.
     *
     * @param gfaItemStatusCheckRequest the gfa item status check request
     * @return the gfa item status check response
     */
    @Override
    public GFAItemStatusCheckResponse itemStatusCheck(GFAItemStatusCheckRequest gfaItemStatusCheckRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String filterParamValue = "";
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = null;
        try {
            filterParamValue = objectMapper.writeValueAsString(gfaItemStatusCheckRequest);
            log.info("Las Item Status Request at {} : {}", this.imsLocationCode, filterParamValue);

            RestTemplate restTemplate = getRestTemplate();
            HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(new HttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.imsConfigProperties.getLasItemStatusEndpoint()).queryParam(RecapConstants.GFA_SERVICE_PARAM, filterParamValue);
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ResponseEntity<GFAItemStatusCheckResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFAItemStatusCheckResponse.class);
            if (responseEntity.getBody() != null) {
                gfaItemStatusCheckResponse = responseEntity.getBody();
            }
            log.info("Las Item status check response at {} : {}", this.imsLocationCode, responseEntity.getStatusCode());
        } catch (JsonProcessingException e) {
            log.error(RecapConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION + "{}", e.getMessage());
        }
        return gfaItemStatusCheckResponse;
    }

    /**
     * Item retrival gfa retrieve item response.
     *
     * @param gfaRetrieveItemRequest the gfa retrieve item request
     * @return the gfa retrieve item response
     */
    @Override
    public GFARetrieveItemResponse itemRetrieval(GFARetrieveItemRequest gfaRetrieveItemRequest) {
        GFARetrieveItemResponse gfaRetrieveItemResponse = null;
        try {
            log.info("Las Item Retrieval Request at {} : {}", this.imsLocationCode, gfaRetrieveItemRequest);
            HttpEntity<GFARetrieveItemRequest> requestEntity = new HttpEntity<>(gfaRetrieveItemRequest, getHttpHeaders());
            ResponseEntity<GFARetrieveItemResponse> responseEntity = getRestTemplate().exchange(this.imsConfigProperties.getLasItemRetrievalOrderEndpoint(), HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                gfaRetrieveItemResponse = responseEntity.getBody();
                gfaRetrieveItemResponse = gfaLasServiceUtil.getLASRetrieveResponse(gfaRetrieveItemResponse);
            } else {
                gfaRetrieveItemResponse = new GFARetrieveItemResponse();
                gfaRetrieveItemResponse.setSuccess(false);
            }
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            gfaRetrieveItemResponse = new GFARetrieveItemResponse();
            gfaRetrieveItemResponse.setSuccess(false);
            gfaRetrieveItemResponse.setScreenMessage(RecapConstants.REQUEST_LAS_EXCEPTION + RecapCommonConstants.LAS_SERVER_NOT_REACHABLE);
        } catch (Exception e) {
            gfaRetrieveItemResponse = new GFARetrieveItemResponse();
            gfaRetrieveItemResponse.setSuccess(false);
            gfaRetrieveItemResponse.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        log.info("Las Item Retrieval Response at {} : {}", this.imsLocationCode, gfaRetrieveItemResponse);
        return gfaRetrieveItemResponse;
    }

    /**
     * Item edd retrival gfa retrieve item response.
     *
     * @param gfaRetrieveEDDItemRequest the gfa retrieve edd item request
     * @return the gfa retrieve item response
     */
    @Override
    public GFAEddItemResponse itemEDDRetrieval(GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest) {
        GFAEddItemResponse gfaEddItemResponse = null;
        try {
            log.info("Las Item EDD Request at {} : {}", this.imsLocationCode, gfaRetrieveEDDItemRequest);
            HttpEntity<GFARetrieveEDDItemRequest> requestEntity = new HttpEntity<>(gfaRetrieveEDDItemRequest, getHttpHeaders());
            ResponseEntity<GFAEddItemResponse> responseEntity = getRestTemplate().exchange(this.imsConfigProperties.getLasItemEddOrderEndpoint(), HttpMethod.POST, requestEntity, GFAEddItemResponse.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                gfaEddItemResponse = responseEntity.getBody();
                gfaEddItemResponse = gfaLasServiceUtil.getLASEddResponse(gfaEddItemResponse);
            } else {
                gfaEddItemResponse = new GFAEddItemResponse();
                gfaEddItemResponse.setSuccess(false);
                gfaEddItemResponse.setScreenMessage(RecapConstants.REQUEST_LAS_EXCEPTION + "HTTP Error response from LAS");
            }
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            gfaEddItemResponse = new GFAEddItemResponse();
            gfaEddItemResponse.setSuccess(false);
            gfaEddItemResponse.setScreenMessage(RecapConstants.REQUEST_LAS_EXCEPTION + RecapCommonConstants.LAS_SERVER_NOT_REACHABLE);
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        } catch (Exception e) {
            gfaEddItemResponse = new GFAEddItemResponse();
            gfaEddItemResponse.setSuccess(false);
            gfaEddItemResponse.setScreenMessage(RecapConstants.SCSB_REQUEST_EXCEPTION + e.getMessage());
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        log.info("Las Item EDD Response at {} : {}", this.imsLocationCode, gfaEddItemResponse);
        return gfaEddItemResponse;
    }

    /**
     * Gfa permanent withdrawal direct gfa pwd response.
     *
     * @param gfaPwdRequest the gfa pwd request
     * @return the gfa pwd response
     */
    @Override
    public GFAPwdResponse gfaPermanentWithdrawalDirect(GFAPwdRequest gfaPwdRequest) {
        GFAPwdResponse gfaPwdResponse = null;
        try {
            HttpEntity<GFAPwdRequest> requestEntity = new HttpEntity<>(gfaPwdRequest, getHttpHeaders());
            log.info("GFA PWD Request at {} : {}", this.imsLocationCode, gfaLasServiceUtil.convertJsonToString(requestEntity.getBody()));
            RestTemplate restTemplate = getRestTemplate();
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ResponseEntity<GFAPwdResponse> responseEntity = restTemplate.exchange(this.imsConfigProperties.getLasPermanentWithdrawalDirectEndpoint(), HttpMethod.POST, requestEntity, GFAPwdResponse.class);
            gfaPwdResponse = responseEntity.getBody();
            log.info("GFA PWD Response Status Code at {} : {}", this.imsLocationCode, responseEntity.getStatusCode());
            log.info("GFA PWD Response at {} : {}", this.imsLocationCode, gfaLasServiceUtil.convertJsonToString(responseEntity.getBody()));
            log.info("GFA PWD item status processed at {}", this.imsLocationCode);
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaPwdResponse;
    }

    /**
     * Gfa permanent withdrawal in direct gfa pwi response.
     *
     * @param gfaPwiRequest the gfa pwi request
     * @return the gfa pwi response
     */
    @Override
    public GFAPwiResponse gfaPermanentWithdrawalInDirect(GFAPwiRequest gfaPwiRequest) {
        GFAPwiResponse gfaPwiResponse = null;
        try {
            HttpEntity<GFAPwiRequest> requestEntity = new HttpEntity<>(gfaPwiRequest, getHttpHeaders());
            log.info("GFA PWI Request at {} : {}", this.imsLocationCode, gfaLasServiceUtil.convertJsonToString(requestEntity.getBody()));
            RestTemplate restTemplate = getRestTemplate();
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(Integer.parseInt(this.imsConfigProperties.getLasServerResponseTimeoutMillis()));
            ResponseEntity<GFAPwiResponse> responseEntity = restTemplate.exchange(this.imsConfigProperties.getLasPermanentWithdrawalIndirectEndpoint(), HttpMethod.POST, requestEntity, GFAPwiResponse.class);
            gfaPwiResponse = responseEntity.getBody();
            log.info("GFA PWI Response Status Code at {} : {}", this.imsLocationCode, responseEntity.getStatusCode());
            log.info("GFA PWI Response at {} : {}", this.imsLocationCode, gfaLasServiceUtil.convertJsonToString(responseEntity.getBody()));
            log.info("GFA PWI item status processed at {}", this.imsLocationCode);
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaPwiResponse;
    }
}
