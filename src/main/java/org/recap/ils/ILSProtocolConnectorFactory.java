package org.recap.ils;

import org.recap.ils.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ILSProtocolConnectorFactory extends BaseILSProtocolConnectorFactory {

    private final List<AbstractProtocolConnector> protocolConnectors;
    private final PropertyUtil propertyUtil;

    @Autowired
    public ILSProtocolConnectorFactory(List<AbstractProtocolConnector> protocolConnectors, PropertyUtil propertyUtil) {
        this.protocolConnectors = protocolConnectors;
        this.propertyUtil = propertyUtil;
    }

    public AbstractProtocolConnector getIlsProtocolConnector(String institution) {
        ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(institution);
        String protocol = ilsConfigProperties.getProtocol();
        AbstractProtocolConnector connector = protocolConnectors
                .stream()
                .filter(service -> service.supports(protocol))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        connector.setInstitution(institution);
        connector.setIlsConfigProperties(ilsConfigProperties);
        return connector;
    }

}