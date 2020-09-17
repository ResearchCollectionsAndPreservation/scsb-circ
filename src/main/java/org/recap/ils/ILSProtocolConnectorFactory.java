package org.recap.ils;

import org.recap.RecapConstants;
import org.recap.ils.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ILSProtocolConnectorFactory extends BaseILSProtocolConnectorFactory {

    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * Get Protocol connector class for the Institution
     * @param instituteCode
     * @return AbstractProtocolConnector
     */
    @Override
    public AbstractProtocolConnector getIlsProtocolConnector(String instituteCode) {
        ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(instituteCode);
        String protocol = ilsConfigProperties.getProtocol();
        AbstractProtocolConnector abstractProtocolConnector = null;
        if (RecapConstants.SIP2_PROTOCOL.equalsIgnoreCase(protocol)) {
            abstractProtocolConnector = new SIPProtocolConnector(ilsConfigProperties);
        } else if (RecapConstants.NCIP_PROTOCOL.equalsIgnoreCase(protocol)) {
            abstractProtocolConnector = new NCIPProtocolConnector(ilsConfigProperties);
        } else if (RecapConstants.REST_PROTOCOL.equalsIgnoreCase(protocol)) {
            abstractProtocolConnector = new RestProtocolConnector(ilsConfigProperties);
        }
        return abstractProtocolConnector;
    }
}