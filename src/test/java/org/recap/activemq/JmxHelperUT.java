package org.recap.activemq;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class JmxHelperUT{
    private static final Logger logger = LoggerFactory.getLogger(JmxHelper.class);

    @InjectMocks
    JmxHelper jmxHelper;

    @Before
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(jmxHelper, "serviceUrl", "service:jmx:rmi:///jndi/rmi://127.0.0.1:1099/jmxrmi");
    }
    @Test
    public void testGetBeanForQueueName() {
        DestinationViewMBean DestinationViewMBean = null;
        DestinationViewMBean = jmxHelper.getBeanForQueueName("test");
        assertNotNull(DestinationViewMBean);
    }

}
