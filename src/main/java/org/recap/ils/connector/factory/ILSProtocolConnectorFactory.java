package org.recap.ils.connector.factory;

import lombok.extern.slf4j.Slf4j;
import org.recap.ScsbConstants;
import org.recap.ils.connector.AbstractProtocolConnector;
import org.recap.ils.connector.NCIPProtocolConnector;
import org.recap.ils.connector.RestProtocolConnector;
import org.recap.ils.connector.SIPProtocolConnector;
import org.recap.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ILSProtocolConnectorFactory extends BaseILSProtocolConnectorFactory {

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private ApplicationContext applicationContext;

    private static final Map<String, AbstractProtocolConnector> protocolConnectorsMap = new HashMap<>();

    public AbstractProtocolConnector getIlsProtocolConnector(String institution) {
        AbstractProtocolConnector connector = null;
        if (protocolConnectorsMap.containsKey(institution)) {
            ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(institution);
            connector = protocolConnectorsMap.get(institution);
            if (connector != null){
                connector.setInstitution(institution);
                connector.setIlsConfigProperties(ilsConfigProperties);
            }
        } else {
            ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(institution);
            String protocol = ilsConfigProperties.getProtocol();
            if (ScsbConstants.SIP2_PROTOCOL.equalsIgnoreCase(protocol)) {
                connector = applicationContext.getBean(SIPProtocolConnector.class);
            } else if (ScsbConstants.REST_PROTOCOL.equalsIgnoreCase(protocol)) {
                connector = applicationContext.getBean(RestProtocolConnector.class);
            } else if (ScsbConstants.NCIP_PROTOCOL.equalsIgnoreCase(protocol)) {
                connector = applicationContext.getBean(NCIPProtocolConnector.class);
            }
            if (connector != null){
                connector.setInstitution(institution);
                connector.setIlsConfigProperties(ilsConfigProperties);
                protocolConnectorsMap.put(institution, connector);
            }
        }
        return connector;
    }

}
