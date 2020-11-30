package org.recap.callable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.recap.gfa.model.GFALasStatus;
import org.recap.gfa.model.GFALasStatusCheckRequest;
import org.recap.gfa.model.GFALasStatusCheckResponse;
import org.recap.request.GFAService;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Created by rajeshbabuk on 25/Nov/2020
 */
@Slf4j
public class LasHeartBeatCheckPollingCallable implements Callable {

    private String imsLocationCode;
    private GFAService gfaService;
    private Integer pollingTimeInterval;

    public LasHeartBeatCheckPollingCallable(Integer pollingTimeInterval, GFAService gfaService, String imsLocationCode) {
        this.imsLocationCode = imsLocationCode;
        this.gfaService = gfaService;
        this.pollingTimeInterval = pollingTimeInterval;
    }

    @Override
    public GFALasStatusCheckResponse call() throws Exception {
        return poll();
    }

    private GFALasStatusCheckResponse poll() throws Exception {
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = null;
        GFALasStatusCheckRequest gfaLasStatusCheckRequest = new GFALasStatusCheckRequest();
        GFALasStatus lasStatus = new GFALasStatus();
        lasStatus.setImsLocationCode(imsLocationCode);
        gfaLasStatusCheckRequest.setLasStatus(Collections.singletonList(lasStatus));
        //Pol HeartBeat Rest Service
        try {
            gfaLasStatusCheckResponse = gfaService.heartBeatCheck(gfaLasStatusCheckRequest);
            log.info("LAS Heart Beat Check Polling -> {}", gfaLasStatusCheckResponse);
            if (null == gfaLasStatusCheckResponse
                    || null == gfaLasStatusCheckResponse.getDsitem()
                    || null == gfaLasStatusCheckResponse.getDsitem().getTtitem()
                    || gfaLasStatusCheckResponse.getDsitem().getTtitem().isEmpty()
                    || !BooleanUtils.toBoolean(gfaLasStatusCheckResponse.getDsitem().getTtitem().get(0).getSuccess())) {
                Thread.sleep(pollingTimeInterval);
                log.debug("LAS Heart Beat Check Polling");
                gfaLasStatusCheckResponse = poll();
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return gfaLasStatusCheckResponse;
    }
}
