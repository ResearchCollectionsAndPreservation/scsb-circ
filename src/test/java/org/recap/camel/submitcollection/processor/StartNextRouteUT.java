package org.recap.camel.submitcollection.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.recap.BaseTestCase;

public class StartNextRouteUT extends BaseTestCase {
    StartNextRoute startNextRoute;

    @Test
    public void testStartNextRoute() {
        startNextRoute = new StartNextRoute("pulSubmitCollectionFTPCgdProtectedRoute");

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");

        try {
            startNextRoute.process(ex);} catch (Exception e) {}
        try{
            startNextRoute.sendEmailForEmptyDirectory(ex);} catch (Exception e) {}
    }
}
