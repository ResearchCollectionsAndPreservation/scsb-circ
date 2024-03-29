package org.recap.ims.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.recap.PropertyKeyConstants;
import org.recap.common.ScsbConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ims.callable.LasItemStatusCheckPollingCallable;
import org.recap.ims.connector.factory.LASImsLocationConnectorFactory;
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
        Integer pollingTimeInterval = Integer.parseInt(propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, PropertyKeyConstants.IMS.IMS_POLLING_TIME_INTERVAL));
        try {
            Future<GFAItemStatusCheckResponse> future = executor.submit(new LasItemStatusCheckPollingCallable(pollingTimeInterval, lasImsLocationConnectorFactory, barcode, imsLocationCode));
            gfaItemStatusCheckResponse = future.get();
            log.info("Process -1 -> {}" , gfaItemStatusCheckResponse);
            if (gfaItemStatusCheckResponse != null
                    && gfaItemStatusCheckResponse.getDsitem() != null
                    && gfaItemStatusCheckResponse.getDsitem().getTtitem() != null && !gfaItemStatusCheckResponse.getDsitem().getTtitem().isEmpty()) {
                log.info("Start Route");
                camelContext.getRouteController().startRoute(ScsbConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID);
            }
            executor.shutdown();
        } catch (InterruptedException e) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            Thread.currentThread().interrupt();
            executor.shutdown();
        }  catch(ExecutionException e) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        } catch (Exception e) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }
        return gfaItemStatusCheckResponse;
    }
}
