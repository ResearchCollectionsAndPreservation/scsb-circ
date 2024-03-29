package org.recap.ims.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.common.ScsbConstants;
import org.recap.ims.callable.LasHeartBeatCheckPollingCallable;
import org.recap.ims.connector.factory.LASImsLocationConnectorFactory;
import org.recap.ims.model.GFALasStatusCheckResponse;
import org.recap.model.request.ItemRequestInformation;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    PropertyUtil propertyUtil;

    public void pollLasHeartBeatResponse(Exchange exchange) {
        ItemRequestInformation itemRequestInformation = (ItemRequestInformation) exchange.getIn().getBody();
        if (StringUtils.isNotBlank(itemRequestInformation.getImsLocationCode())) {
            GFALasStatusCheckResponse gfaLasStatusCheckResponse = null;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Integer pollingTimeInterval = Integer.parseInt(propertyUtil.getPropertyByImsLocationAndKey(itemRequestInformation.getImsLocationCode(), PropertyKeyConstants.IMS.IMS_POLLING_TIME_INTERVAL));
            try {
                log.info("Polling Started on LAS Heart Beat Check for IMS Location : {}", itemRequestInformation.getImsLocationCode());
                Future<GFALasStatusCheckResponse> future = executor.submit(new LasHeartBeatCheckPollingCallable(pollingTimeInterval, lasImsLocationConnectorFactory, itemRequestInformation.getImsLocationCode()));
                gfaLasStatusCheckResponse = future.get();
                log.info("Polling Ended on LAS Heart Beat Check for IMS Location : {}", itemRequestInformation.getImsLocationCode());
                if (null != gfaLasStatusCheckResponse
                        && null != gfaLasStatusCheckResponse.getDsitem()
                        && null != gfaLasStatusCheckResponse.getDsitem().getTtitem()
                        && !gfaLasStatusCheckResponse.getDsitem().getTtitem().isEmpty()
                        && BooleanUtils.toBoolean(gfaLasStatusCheckResponse.getDsitem().getTtitem().get(0).getSuccess())) {
                    log.info("Sending to Outgoing Queue at {}", itemRequestInformation.getImsLocationCode());
                    producerTemplate.sendBodyAndHeader(ScsbConstants.LAS_OUTGOING_QUEUE_PREFIX + itemRequestInformation.getImsLocationCode() + ScsbConstants.OUTGOING_QUEUE_SUFFIX, itemRequestInformation, ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
                }
                executor.shutdown();
            } catch (InterruptedException e) {
                log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
                Thread.currentThread().interrupt();
                executor.shutdown();
            } catch (ExecutionException e) {
                log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            } catch (Exception e) {
                log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
            }
        } else {
            log.info("");
            producerTemplate.sendBodyAndHeader(ScsbConstants.LAS_OUTGOING_QUEUE_PREFIX + itemRequestInformation.getImsLocationCode() + ScsbConstants.OUTGOING_QUEUE_SUFFIX, itemRequestInformation, ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
        }
    }
}
