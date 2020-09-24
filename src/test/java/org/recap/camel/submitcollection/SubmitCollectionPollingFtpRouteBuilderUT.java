package org.recap.camel.submitcollection;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCollectionPollingFtpRouteBuilderUT {

    @InjectMocks
    SubmitCollectionPollingFtpRouteBuilder submitCollectionPollingFtpRouteBuilder;
    @Test
    public void getSubmitCollectionPollingFtpRouteBuilder(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("CamelFileName", "SubmitCollectionFile.gz");
        boolean result = submitCollectionPollingFtpRouteBuilder.gzipFile.matches(exchange);
        assertTrue(result);
    }
}
