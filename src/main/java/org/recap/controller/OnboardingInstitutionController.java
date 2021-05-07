package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.las.GFALasService;
import org.recap.mqconsumer.RequestItemQueueConsumer;
import org.recap.processor.LasHeartBeatCheckPollingProcessor;
import org.recap.request.ItemEDDRequestService;
import org.recap.request.ItemRequestService;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 21/Jan/2021
 */

@RestController
@RequestMapping("/onboardInstitution")
@Slf4j
public class OnboardingInstitutionController {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemEDDRequestService itemEDDRequestService;

    @GetMapping(value = "/createTopicsForNewInstitution")
    public String createTopicsForNewInstitution(@RequestParam String institutionCode) {
        String responseStatus = ScsbCommonConstants.SUCCESS;
        try {
            String retrievalInstitutionTopic = propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "ils.topic.retrieval.request");
            String eddInstitutionTopic = propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "ils.topic.edd.request");
            String recallInstitutionTopic = propertyUtil.getPropertyByInstitutionAndKey(institutionCode, "ils.topic.recall.request");

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(retrievalInstitutionTopic)
                            .routeId(institutionCode + "RequestTopicRouteId")
                            .bean(new RequestItemQueueConsumer(institutionCode, itemRequestService, itemEDDRequestService), "requestTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(eddInstitutionTopic)
                            .routeId(institutionCode + "EDDTopicRouteId")
                            .bean(new RequestItemQueueConsumer(institutionCode, itemRequestService, itemEDDRequestService), "eddTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(recallInstitutionTopic)
                            .routeId(institutionCode + "RecallTopicRouteId")
                            .bean(new RequestItemQueueConsumer(institutionCode, itemRequestService, itemEDDRequestService), "recallTopicOnMessage");
                }
            });
        } catch (Exception e) {
            log.error("Failed to create Topics for institution - {} : {}", institutionCode, e);
            responseStatus = ScsbCommonConstants.FAILURE;
        }
        return responseStatus;
    }

    @GetMapping(value = "/createQueuesForNewInstitution")
    public String createQueuesForNewImsLocation(@RequestParam String imsLocationCode) {
        String responseStatus = ScsbCommonConstants.SUCCESS;
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.SCSB_LAS_OUTGOING_QUEUE_PREFIX + imsLocationCode + ScsbConstants.OUTGOING_QUEUE_SUFFIX)
                            .routeId(imsLocationCode + ScsbConstants.SCSB_OUTGOING_ROUTE_ID)
                            .log("Message Received in SCSB OUTGOING QUEUE")
                            .bean(applicationContext.getBean(LasHeartBeatCheckPollingProcessor.class), "pollLasHeartBeatResponse");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.LAS_OUTGOING_QUEUE_PREFIX + imsLocationCode + ScsbConstants.OUTGOING_QUEUE_SUFFIX)
                            .routeId(ScsbConstants.LAS_OUTGOING_ROUTE_ID)
                            .log("Message Received in LAS OUTGOING QUEUE for " + imsLocationCode)
                            .bean(applicationContext.getBean(GFALasService.class), "gfaItemRequestProcessor");

                }
            });
        } catch (Exception e) {
            log.error("Failed to create Queues for IMS Location - {} : {}", imsLocationCode, e);
            responseStatus = ScsbCommonConstants.FAILURE;
        }
        return responseStatus;
    }

}
