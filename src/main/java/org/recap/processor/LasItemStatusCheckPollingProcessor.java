package org.recap.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.callable.LasItemStatusCheckPollingCallable;
import org.recap.las.LASImsLocationConnectorFactory;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Slf4j
public class LasItemStatusCheckPollingProcessor {
    
    @Autowired
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Autowired
    PropertyUtil propertyUtil;

    public GFAItemStatusCheckResponse pollLasItemStatusJobResponse(String barcode, String imsLocationCode, CamelContext camelContext) {
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Integer pollingTimeInterval = Integer.parseInt(propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.polling.time.interval"));
        try {
            Future<GFAItemStatusCheckResponse> future = executor.submit(new LasItemStatusCheckPollingCallable(pollingTimeInterval, lasImsLocationConnectorFactory, barcode, imsLocationCode));
            gfaItemStatusCheckResponse = future.get();
            log.info("Process -1 -> {}" , gfaItemStatusCheckResponse);
            if (gfaItemStatusCheckResponse != null
                    && gfaItemStatusCheckResponse.getDsitem() != null
                    && gfaItemStatusCheckResponse.getDsitem().getTtitem() != null && !gfaItemStatusCheckResponse.getDsitem().getTtitem().isEmpty()) {
                log.info("Start Route");
                camelContext.getRouteController().startRoute(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID);
            }
            executor.shutdown();
        } catch (InterruptedException e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
            Thread.currentThread().interrupt();
            executor.shutdown();
        }  catch(ExecutionException e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        } catch (Exception e) {
            log.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaItemStatusCheckResponse;
    }
}
