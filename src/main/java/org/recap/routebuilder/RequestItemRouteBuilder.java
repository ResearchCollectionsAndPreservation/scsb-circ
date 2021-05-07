package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.route.StartRouteProcessor;
import org.recap.las.GFALasService;
import org.recap.mqconsumer.RequestItemQueueConsumer;
import org.recap.processor.LasHeartBeatCheckPollingProcessor;
import org.recap.request.BulkItemRequestProcessService;
import org.recap.request.BulkItemRequestService;
import org.recap.request.ItemEDDRequestService;
import org.recap.request.ItemRequestService;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by sudhishk on 2/12/16.
 */
@Component
public class RequestItemRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RequestItemRouteBuilder.class);

    /**
     * Instantiates a new Request item route builder.
     *
     * @param camelContext          the camel context
     * @param itemRequestService    the item request service
     * @param itemEDDRequestService the item edd request service
     */
    @Autowired
    public RequestItemRouteBuilder(@Value("${bulk.request.concurrent.consumer.count}") Integer bulkRequestConsumerCount, CamelContext camelContext, ApplicationContext applicationContext, ItemRequestService itemRequestService, ItemEDDRequestService itemEDDRequestService, BulkItemRequestService bulkItemRequestService, BulkItemRequestProcessService bulkItemRequestProcessService, PropertyUtil propertyUtil, CommonUtil commonUtil) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.REQUEST_ITEM_QUEUE)
                        .routeId(ScsbConstants.REQUEST_ITEM_QUEUE_ROUTEID)
                        .threads(30,50)
                        .choice()
                            .when(header(ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(ScsbCommonConstants.REQUEST_TYPE_RETRIEVAL))
                                .bean(new RequestItemQueueConsumer(itemRequestService), "requestItemOnMessage")
                            .when(header(ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(ScsbCommonConstants.REQUEST_TYPE_EDD))
                                .bean(new RequestItemQueueConsumer(itemEDDRequestService), "requestItemEDDOnMessage")
                            .when(header(ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(ScsbCommonConstants.REQUEST_TYPE_RECALL))
                                .bean(new RequestItemQueueConsumer(itemRequestService), "requestItemRecallOnMessage");
                }
            });

            for (String imsLocationCode : commonUtil.findAllImsLocationCodeExceptUN()) {
                camelContext.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.SCSB_LAS_OUTGOING_QUEUE_PREFIX + imsLocationCode + ScsbConstants.OUTGOING_QUEUE_SUFFIX)
                                .routeId(imsLocationCode + ScsbConstants.SCSB_OUTGOING_ROUTE_ID)
                                .log("Message Received in SCSB OUTGOING QUEUE for " + imsLocationCode)
                                .bean(applicationContext.getBean(LasHeartBeatCheckPollingProcessor.class), "pollLasHeartBeatResponse");
                    }
                });

                camelContext.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.LAS_OUTGOING_QUEUE_PREFIX + imsLocationCode + ScsbConstants.OUTGOING_QUEUE_SUFFIX)
                                .routeId(imsLocationCode + ScsbConstants.LAS_OUTGOING_ROUTE_ID)
                                .log("Message Received in LAS OUTGOING QUEUE for " + imsLocationCode)
                                .bean(applicationContext.getBean(GFALasService.class), "gfaItemRequestProcessor");

                    }
                });
            }

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.LAS_INCOMING_QUEUE)
                        .routeId(ScsbConstants.LAS_INCOMING_ROUTE_ID)
                        .choice()
                        .when(header(ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(ScsbCommonConstants.REQUEST_TYPE_RETRIEVAL))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponseRetrievalOnMessage")
                        .when(header(ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(ScsbCommonConstants.REQUEST_TYPE_EDD))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponseEDDOnMessage")
                        .when(header(ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(ScsbConstants.REQUEST_TYPE_PW_INDIRECT))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponsePWIOnMessage")
                        .when(header(ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(ScsbConstants.REQUEST_TYPE_PW_DIRECT))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponsePWDOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbCommonConstants.BULK_REQUEST_ITEM_QUEUE)
                            .routeId(ScsbConstants.BULK_REQUEST_ITEM_QUEUE_ROUTEID)
                            .bean(new RequestItemQueueConsumer(bulkItemRequestService), "bulkRequestItemOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.BULK_REQUEST_ITEM_PROCESSING_QUEUE + ScsbConstants.ASYNC_CONCURRENT_CONSUMERS + bulkRequestConsumerCount)
                            .routeId(ScsbConstants.BULK_REQUEST_ITEM_PROCESSING_QUEUE_ROUTEID)
                            .bean(new RequestItemQueueConsumer(bulkItemRequestProcessService), "bulkRequestProcessItemOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE)
                            .routeId(ScsbConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID)
                            .noAutoStartup()
                            .choice()
                                .when(body().isNull())
                                    .log("No Requests To Process")
                                .otherwise()
                                    .log("Start Route 1")
                                    .process(new StartRouteProcessor(ScsbConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID))
                                    .bean(new RequestItemQueueConsumer(itemRequestService), "requestItemLasStatusCheckOnMessage")
                            .endChoice();
                }
            });

            for (String institutionCode : commonUtil.findAllInstitutionCodesExceptHTC()) {
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
            }

        } catch (Exception e) {
            logger.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }

    }

}
