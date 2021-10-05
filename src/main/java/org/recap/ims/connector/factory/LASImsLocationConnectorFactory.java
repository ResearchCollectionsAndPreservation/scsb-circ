package org.recap.ims.connector.factory;

import org.recap.ims.connector.AbstractLASImsLocationConnector;
import org.recap.ims.connector.GFALasImsLocationConnector;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rajeshbabuk on 20/Jan/2021
 */
@Component
public class LASImsLocationConnectorFactory extends BaseLASImsLocationConnectorFactory {

    @Autowired
    private PropertyUtil propertyUtil;

    @Override
    public AbstractLASImsLocationConnector getLasImsLocationConnector(String imsLocationCode) {
        GFALasImsLocationConnector connector = new GFALasImsLocationConnector();
        connector.setImsLocationCode(imsLocationCode);
        connector.setImsConfigProperties(propertyUtil.getIMSConfigProperties(imsLocationCode));
        return connector;
    }
}
