package org.recap.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.camel.route.StartRouteProcessor;
import org.recap.mqconsumer.RequestItemQueueConsumer;
import org.recap.request.BulkItemRequestProcessService;
import org.recap.request.BulkItemRequestService;
import org.recap.request.ItemEDDRequestService;
import org.recap.request.ItemRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public RequestItemRouteBuilder(@Value("${bulk.request.concurrent.consumer.count}") Integer bulkRequestConsumerCount, CamelContext camelContext, ItemRequestService itemRequestService, ItemEDDRequestService itemEDDRequestService, BulkItemRequestService bulkItemRequestService, BulkItemRequestProcessService bulkItemRequestProcessService) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.REQUEST_ITEM_QUEUE)
                        .routeId(RecapConstants.REQUEST_ITEM_QUEUE_ROUTEID)
                        .threads(30,50)
                        .choice()
                            .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL))
                                .bean(new RequestItemQueueConsumer(itemRequestService), "requestItemOnMessage")
                            .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapCommonConstants.REQUEST_TYPE_EDD))
                                .bean(new RequestItemQueueConsumer(itemEDDRequestService), "requestItemEDDOnMessage")
                            .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapCommonConstants.REQUEST_TYPE_BORROW_DIRECT))
                                .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "requestItemBorrowDirectOnMessage")
                            .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapCommonConstants.REQUEST_TYPE_RECALL))
                                .bean(new RequestItemQueueConsumer(itemRequestService), "requestItemRecallOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.SCSB_OUTGOING_QUEUE)
                        .routeId(RecapConstants.SCSB_OUTGOING_ROUTE_ID)
                        .to(RecapConstants.LAS_OUTGOING_QUEUE)
                        .onCompletion().bean(new RequestItemQueueConsumer(itemRequestService),"lasOutgoingQOnCompletion");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.LAS_INCOMING_QUEUE)
                        .routeId(RecapConstants.LAS_INCOMING_ROUTE_ID)
                        .choice()
                        .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponseRetrivalOnMessage")
                        .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapCommonConstants.REQUEST_TYPE_EDD))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponseEDDOnMessage")
                        .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapConstants.REQUEST_TYPE_PW_INDIRECT))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponsePWIOnMessage")
                        .when(header(RecapCommonConstants.REQUEST_TYPE_QUEUE_HEADER).isEqualTo(RecapConstants.REQUEST_TYPE_PW_DIRECT))
                        .bean(new RequestItemQueueConsumer(itemRequestService), "lasResponsePWDOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapCommonConstants.BULK_REQUEST_ITEM_QUEUE)
                            .routeId(RecapConstants.BULK_REQUEST_ITEM_QUEUE_ROUTEID)
                            .bean(new RequestItemQueueConsumer(bulkItemRequestService), "bulkRequestItemOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.BULK_REQUEST_ITEM_PROCESSING_QUEUE + RecapConstants.ASYNC_CONCURRENT_CONSUMERS + bulkRequestConsumerCount)
                            .routeId(RecapConstants.BULK_REQUEST_ITEM_PROCESSING_QUEUE_ROUTEID)
                            .bean(new RequestItemQueueConsumer(bulkItemRequestProcessService), "bulkRequestProcessItemOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE)
                            .routeId(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID)
                            .noAutoStartup()
                            .choice()
                                .when(body().isNull())
                                    .log("No Requests To Process")
                                .otherwise()
                                    .log("Start Route 1")
                                    .process(new StartRouteProcessor(RecapConstants.REQUEST_ITEM_LAS_STATUS_CHECK_QUEUE_ROUTEID))
                                    .bean(new RequestItemQueueConsumer(itemRequestService), "requestItemLasStatusCheckOnMessage")
                            .endChoice();
                }
            });
            /* PUL Topics*/
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.PUL_REQUEST_TOPIC)
                            .routeId(RecapConstants.PUL_REQUEST_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "pulRequestTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.PUL_EDD_TOPIC)
                            .routeId(RecapConstants.PUL_EDD_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "pulEDDTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.PUL_RECALL_TOPIC)
                            .routeId(RecapConstants.PUL_RECALL_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "pulRecalTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.PUL_BORROW_DIRECT_TOPIC)
                            .routeId(RecapConstants.PUL_BORROW_DIRECT_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "pulBorrowDirectTopicOnMessage");
                }
            });
            /* PUL Topics*/

            /* CUL Topics*/
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.CUL_REQUEST_TOPIC)
                            .routeId(RecapConstants.CUL_REQUEST_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "culRequestTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.CUL_EDD_TOPIC)
                            .routeId(RecapConstants.CUL_EDD_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "culEDDTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.CUL_RECALL_TOPIC)
                            .routeId(RecapConstants.CUL_RECALL_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "culRecalTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.CUL_BORROW_DIRECT_TOPIC)
                            .routeId(RecapConstants.CUL_BORROW_DIRECT_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "culBorrowDirectTopicOnMessage");
                }
            });
            /* CUL Topics*/

            /* NYPL Topics */
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.NYPL_REQUEST_TOPIC)
                            .routeId(RecapConstants.NYPL_REQUEST_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "nyplRequestTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.NYPL_EDD_TOPIC)
                            .routeId(RecapConstants.NYPL_EDD_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "nyplEDDTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.NYPL_RECALL_TOPIC)
                            .routeId(RecapConstants.NYPL_RECALL_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "nyplRecalTopicOnMessage");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.NYPL_BORROW_DIRECT_TOPIC)
                            .routeId(RecapConstants.NYPL_BORROW_DIRECT_TOPIC_ROUTEID)
                            .bean(new RequestItemQueueConsumer(itemRequestService, itemEDDRequestService), "nyplBorrowDirectTopicOnMessage");
                }
            });
            /* NYPL Topics */


        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }

    }

}
