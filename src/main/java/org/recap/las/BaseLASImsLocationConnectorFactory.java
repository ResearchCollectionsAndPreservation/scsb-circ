package org.recap.las;

/**
 * Created by rajeshbabuk on 20/Jan/2021
 */
public abstract class BaseLASImsLocationConnectorFactory {
    public abstract AbstractLASImsLocationConnector getLasImsLocationConnector(String imsLocationCode);
}
