package org.recap.processor;

import org.recap.RecapCommonConstants;
import org.recap.callable.RestJobResponsePollingCallable;
import org.recap.ils.RestProtocolConnector;
import org.recap.ils.model.nypl.JobData;
import org.recap.ils.model.nypl.response.JobResponse;
import org.recap.ils.service.RestApiResponseUtil;
import org.recap.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * Created by rajeshbabuk on 08/Jan/2021
 */
@Component
public class RestProtocolJobResponsePollingProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RestProtocolJobResponsePollingProcessor.class);

    private Integer pollingMaxTimeOut;
    private Integer pollingTimeInterval;

    /**
     * The Rest api service connector.
     */
    @Autowired
    RestProtocolConnector restProtocolConnector;

    /**
     * The Nypl api response util.
     */
    @Autowired
    RestApiResponseUtil restApiResponseUtil;

    @Autowired
    PropertyUtil propertyUtil;

    /**
     * Poll nypl request item job response job response.
     *
     * @param jobId the job id
     * @return the job response
     */
    public JobResponse pollRestApiRequestItemJobResponse(String jobId, String institution) {
        JobResponse jobResponse = new JobResponse();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
            ilsConfigProperties.setIlsRestDataApi(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.data.api"));
            ilsConfigProperties.setOauthTokenApiUrl(propertyUtil.getPropertyByInstitutionAndKey(institution, "oauth.token.api.url"));
            ilsConfigProperties.setOperatorUserId(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.server.operator.user.id"));
            ilsConfigProperties.setOperatorPassword(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.server.operator.password"));
            pollingMaxTimeOut = Integer.valueOf(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.max.timeout"));
            pollingTimeInterval = Integer.valueOf(propertyUtil.getPropertyByInstitutionAndKey(institution, "ils.rest.polling.time.interval"));
            restProtocolConnector.setInstitution(institution);
            restProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
            Future<JobResponse> future = executor.submit(new RestJobResponsePollingCallable(jobId, pollingTimeInterval, restProtocolConnector));
            logger.info("Polling on job id {} started", jobId);
            jobResponse = future.get(pollingMaxTimeOut, TimeUnit.SECONDS);
            JobData jobData = jobResponse.getData();
            if (null != jobData) {
                jobResponse.setStatusMessage(restApiResponseUtil.getJobStatusMessage(jobData));
            }
            executor.shutdown();
            return jobResponse;
        } catch (InterruptedException e) {
            logger.error("Nypl job response interrupted for job id -> {} " , jobId);
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            Thread.currentThread().interrupt();
            executor.shutdownNow();
            jobResponse.setStatusMessage("Nypl job response interrupted : " + e.getMessage());
            return jobResponse;
        } catch (ExecutionException e) {
            logger.error("Nypl job response execution failed for job id -> {} " , jobId);
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            executor.shutdownNow();
            jobResponse.setStatusMessage("Nypl job response execution failed : " + e.getMessage());
            return jobResponse;
        } catch (TimeoutException e) {
            logger.error("Nypl job response polling timed out for job id -> {} " , jobId);
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            executor.shutdownNow();
            jobResponse.setStatusMessage("Nypl job response polling timed out : " + e.getMessage());
            return jobResponse;
        } catch (Exception e) {
            logger.error("Nypl job response polling failed for job id -> {} " , jobId);
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            executor.shutdownNow();
            jobResponse.setStatusMessage("Nypl job response polling failed : " + e.getMessage());
            return jobResponse;
        }
    }
}
