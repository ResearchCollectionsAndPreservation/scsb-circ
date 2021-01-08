package org.recap.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.BooleanUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.callable.LasHeartBeatCheckPollingCallable;
import org.recap.gfa.model.GFALasStatusCheckResponse;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.request.GFAService;
import org.recap.util.ItemRequestServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by rajeshbabuk on 25/Nov/2020
 */
@Service
@Scope("prototype")
@Slf4j
public class LasHeartBeatCheckPollingProcessor {

    @Value("${las.polling.time.interval}")
    private Integer pollingTimeInterval;

    @Autowired
    private GFAService gfaService;

    @Autowired
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Autowired
    ProducerTemplate producerTemplate;

    public void pollLasHeartBeatResponse(Exchange exchange) {
        ItemRequestInformation itemRequestInformation = (ItemRequestInformation) exchange.getIn().getBody();
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<GFALasStatusCheckResponse> future = executor.submit(new LasHeartBeatCheckPollingCallable(pollingTimeInterval, gfaService, itemRequestInformation.getImsLocationCode()));
            gfaLasStatusCheckResponse = future.get();
            log.info("GFA Las Status Poll Response: {}", gfaLasStatusCheckResponse);
            if (null != gfaLasStatusCheckResponse
                    && null != gfaLasStatusCheckResponse.getDsitem()
                    && null != gfaLasStatusCheckResponse.getDsitem().getTtitem()
                    && !gfaLasStatusCheckResponse.getDsitem().getTtitem().isEmpty()
                    && BooleanUtils.toBoolean(gfaLasStatusCheckResponse.getDsitem().getTtitem().get(0).getSuccess())) {
                log.info("Sending to Outgoing Queue");
                producerTemplate.sendBodyAndHeader(RecapConstants.LAS_OUTGOING_QUEUE, itemRequestInformation, RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
            }
            executor.shutdown();
        } catch (InterruptedException e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            Thread.currentThread().interrupt();
            executor.shutdown();
        } catch (ExecutionException e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
    }
}
