package org.recap.activemq;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.junit.Test;
import org.recap.BaseTestCase;

import javax.management.MBeanServerConnection;

import static org.junit.Assert.assertNull;

public class JmxHelperUT extends BaseTestCase {
  /*  @Test
    public void testGetBeanForQueueName() {
        JmxHelper JmxHelper = new JmxHelper();
        DestinationViewMBean DestinationViewMBean = null;
        DestinationViewMBean = JmxHelper.getBeanForQueueName("test");
        assertNull(DestinationViewMBean);
    }*/

  /*  @Test
    public void testGetConnection() {
        JmxHelper JmxHelper = new JmxHelper();
        MBeanServerConnection MBeanServerConnection = null;
        MBeanServerConnection = JmxHelper.getConnection();
        assertNull(MBeanServerConnection);
    }*/
}
