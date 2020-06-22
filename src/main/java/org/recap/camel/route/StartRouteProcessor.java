package org.recap.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by akulak on 19/7/17.
 */
@Service
@Scope("prototype")
public class StartRouteProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(StartRouteProcessor.class);
    private String routeId;

    public StartRouteProcessor(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getContext().getRouteController().startRoute(routeId);
    }
}
