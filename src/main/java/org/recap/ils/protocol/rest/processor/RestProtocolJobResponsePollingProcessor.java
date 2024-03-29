package org.recap.ils.protocol.rest.processor;

import lombok.extern.slf4j.Slf4j;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ils.protocol.rest.callable.RestJobResponsePollingCallable;
import org.recap.ils.connector.RestProtocolConnector;
import org.recap.ils.protocol.rest.model.JobData;
import org.recap.ils.protocol.rest.model.response.JobResponse;
import org.recap.ils.protocol.rest.util.RestApiResponseUtil;
import org.recap.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * Created by rajeshbabuk on 08/Jan/2021
 */
@Slf4j
@Component
public class RestProtocolJobResponsePollingProcessor {


    private Integer pollingMaxTimeOut;
    private Integer pollingTimeInterval;

    /**
     * The Rest api service connector.
     */
    @Autowired
    RestProtocolConnector restProtocolConnector;

    /**
     * The Rest api response util.
     */
    @Autowired
    RestApiResponseUtil restApiResponseUtil;

    @Autowired
    PropertyUtil propertyUtil;

    /**
     * Poll rest request item job response job response.
     *
     * @param jobId the job id
     * @return the job response
     */
    public JobResponse pollRestApiRequestItemJobResponse(String jobId, String institution) {
        JobResponse jobResponse = new JobResponse();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
            ilsConfigProperties.setIlsRestDataApi(propertyUtil.getPropertyByInstitutionAndKey(institution,PropertyKeyConstants.ILS.ILS_REST_DATA_API));
            ilsConfigProperties.setOauthTokenApiUrl(propertyUtil.getPropertyByInstitutionAndKey(institution,PropertyKeyConstants.ILS.ILS_OAUTH_TOKEN_API_URL));
            ilsConfigProperties.setOperatorUserId(propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_SERVER_OPERATOR_USER_ID));
            ilsConfigProperties.setOperatorPassword(propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_SERVER_OPERATOR_PASSWORD));
            pollingMaxTimeOut = Integer.valueOf(propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_REST_POLLING_MAX_TIMEOUT));
            pollingTimeInterval = Integer.valueOf(propertyUtil.getPropertyByInstitutionAndKey(institution,PropertyKeyConstants.ILS.ILS_REST_POLLING_TIME_INTERVAL));
            restProtocolConnector.setInstitution(institution);
            restProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
            Future<JobResponse> future = executor.submit(new RestJobResponsePollingCallable(jobId, pollingTimeInterval, restProtocolConnector));
            log.info("Polling on job id {} started", jobId);
            jobResponse = future.get(pollingMaxTimeOut, TimeUnit.SECONDS);
            JobData jobData = jobResponse.getData();
            if (null != jobData) {
                jobResponse.setStatusMessage(restApiResponseUtil.getJobStatusMessage(jobData));
            }
            executor.shutdown();
            return jobResponse;
        } catch (InterruptedException e) {
            log.error("{} job response interrupted for job id -> {} ", institution, jobId);
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            Thread.currentThread().interrupt();
            executor.shutdownNow();
            jobResponse.setStatusMessage(institution + " job response interrupted : " + e.getMessage());
            return jobResponse;
        } catch (ExecutionException e) {
            log.error("{} job response execution failed for job id -> {} ", institution, jobId);
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            executor.shutdownNow();
            jobResponse.setStatusMessage(institution + " job response execution failed : " + e.getMessage());
            return jobResponse;
        } catch (TimeoutException e) {
            log.error("{} job response polling timed out for job id -> {} ",institution, jobId);
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            executor.shutdownNow();
            jobResponse.setStatusMessage(institution + " job response polling timed out : " + e.getMessage());
            return jobResponse;
        } catch (Exception e) {
            log.error("{} job response polling failed for job id -> {} ", institution, jobId);
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            executor.shutdownNow();
            jobResponse.setStatusMessage(institution + " job response polling failed : " + e.getMessage());
            return jobResponse;
        }
    }
}
