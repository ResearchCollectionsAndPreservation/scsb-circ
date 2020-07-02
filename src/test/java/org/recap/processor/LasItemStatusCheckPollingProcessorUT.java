package org.recap.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.gfa.model.GFAItemStatusCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class LasItemStatusCheckPollingProcessorUT extends BaseTestCase {

    @Autowired
    LasItemStatusCheckPollingProcessor lasItemStatusCheckPollingProcessor;

   /* @Test
    public void testpollLasItemStatusJobResponse() {
        CamelContext context = new DefaultCamelContext();
        GFAItemStatusCheckResponse response = lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse("32101068878931", context);
        assertNotNull(response);
    }*/

}
