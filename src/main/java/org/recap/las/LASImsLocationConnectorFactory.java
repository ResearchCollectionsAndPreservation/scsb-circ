package org.recap.las;

import org.recap.model.IMSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by rajeshbabuk on 20/Jan/2021
 */
@Component
public class LASImsLocationConnectorFactory extends BaseLASImsLocationConnectorFactory {

    private final List<AbstractLASImsLocationConnector> imsLocationConnectors;
    private final PropertyUtil propertyUtil;

    @Autowired
    public LASImsLocationConnectorFactory(List<AbstractLASImsLocationConnector> imsLocationConnectors, PropertyUtil propertyUtil) {
        this.imsLocationConnectors = imsLocationConnectors;
        this.propertyUtil = propertyUtil;
    }

    @Override
    public AbstractLASImsLocationConnector getLasImsLocationConnector(String imsLocationCode) {
        IMSConfigProperties imsConfigProperties = propertyUtil.getIMSConfigProperties(imsLocationCode);
        AbstractLASImsLocationConnector connector = imsLocationConnectors
                .stream()
                .filter(service -> service.supports(imsLocationCode))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        connector.setImsLocationCode(imsLocationCode);
        connector.setImsConfigProperties(imsConfigProperties);
        return connector;
    }
}
