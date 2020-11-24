package org.recap.ils;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.ILSConfigProperties;

import static org.junit.Assert.assertTrue;

public class SIPProtocolConnectorUT extends BaseTestCase {

    @Test
    public void testGetSocketConnection() {
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setHost("libserv86.princeton.edu");
        ilsConfigProperties.setPort(7031);
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        boolean connected = sipProtocolConnector.checkSocketConnection();
        assertTrue(connected);
    }
}
