package org.recap.activemq;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.recap.BaseTestCaseUT;

import javax.jms.JMSException;

import static org.junit.Assert.assertNotNull;

public class ActivemqRegistrarUT extends BaseTestCaseUT {

    @Test
    public void testActivemqRegistrar() throws JMSException {
        String brokerUrl = "tcp://localhost:61616";
        CamelContext ctx = new DefaultCamelContext();
        ActivemqRegistrar activemqRegistrar = new ActivemqRegistrar(ctx, brokerUrl);
        assertNotNull(activemqRegistrar);
    }
}
