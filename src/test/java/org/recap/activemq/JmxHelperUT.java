package org.recap.activemq;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class JmxHelperUT extends BaseTestCaseUT {
    private static final Logger logger = LoggerFactory.getLogger(JmxHelper.class);

    @InjectMocks
    JmxHelper jmxHelper;


    private String serviceUrl = "service:jmx:rmi:///jndi/rmi://127.0.0.1:1099/jmxrmi";

    @Mock
    JMXConnector jmxConnector;

    private JMXConnectorFactory jmxConnectorFactory;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(jmxHelper, "serviceUrl",serviceUrl);
    }
    @Test
    public void testGetBeanForQueueName() {
        try {
            DestinationViewMBean DestinationViewMBean = null;
            DestinationViewMBean = jmxHelper.getBeanForQueueName("test");
            assertNotNull(DestinationViewMBean);
        }catch (Exception e){
        }
    }

}
