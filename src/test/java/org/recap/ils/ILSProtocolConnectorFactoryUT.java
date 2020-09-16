package org.recap.ils;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class ILSProtocolConnectorFactoryUT extends BaseTestCase {

    @Autowired
    ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Test
    public void testCreateSIPConnector() {
        String institution = "PUL";
        AbstractProtocolConnector abstractProtocolConnector = ilsProtocolConnectorFactory.getIlsProtocolConnector(institution);
        abstractProtocolConnector.lookupItem("testItem");
        assertNotNull(abstractProtocolConnector);
        assertNotNull(abstractProtocolConnector.getHost());
        assertNotNull(abstractProtocolConnector.getOperatorUserId());
        assertNotNull(abstractProtocolConnector.getOperatorPassword());
    }
}
