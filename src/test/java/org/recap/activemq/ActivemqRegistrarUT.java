package org.recap.activemq;

import jakarta.jms.JMSException;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ActivemqRegistrarUT extends BaseTestCaseUT {

    @Test
    public void testActivemqRegistrar() throws JMSException {
        String brokerUrl = "tcp://localhost:61616";
        CamelContext ctx = new DefaultCamelContext();
        ActivemqRegistrar activemqRegistrar = new ActivemqRegistrar(ctx, brokerUrl);
        assertNotNull(activemqRegistrar);
    }
}
