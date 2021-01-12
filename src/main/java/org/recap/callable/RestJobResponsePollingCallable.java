package org.recap.callable;

import org.recap.ils.RestProtocolConnector;
import org.recap.ils.model.nypl.JobData;
import org.recap.ils.model.nypl.response.JobResponse;

import java.util.concurrent.Callable;

/**
 * Created by rajeshbabuk on 08/Jan/2021
 */
public class RestJobResponsePollingCallable implements Callable {

    private String jobId;
    private RestProtocolConnector restProtocolConnector;
    private Integer pollingTimeInterval;

    /**
     *
     * @param jobId
     * @param pollingTimeInterval
     * @param restProtocolConnector
     */
    public RestJobResponsePollingCallable(String jobId, Integer pollingTimeInterval, RestProtocolConnector restProtocolConnector) {
        this.jobId = jobId;
        this.restProtocolConnector = restProtocolConnector;
        this.pollingTimeInterval = pollingTimeInterval;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public JobResponse call() throws Exception {
        return poll();
    }

    /**
     *
     * @return
     * @throws Exception
     */
    private JobResponse poll() throws Exception {
        Boolean statusFlag;
        JobResponse jobResponse = restProtocolConnector.queryForJob(jobId);
        JobData jobData = jobResponse.getData();
        statusFlag = jobData.getFinished();
        if (!statusFlag) {
            Thread.sleep(pollingTimeInterval);
            jobResponse = poll();
        }
        return jobResponse;
    }
}
