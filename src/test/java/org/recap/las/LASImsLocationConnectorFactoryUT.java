package org.recap.las;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.IMSConfigProperties;
import org.recap.util.PropertyUtil;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


public class LASImsLocationConnectorFactoryUT extends BaseTestCaseUT {

    @InjectMocks
    LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Mock
    private PropertyUtil propertyUtil;

    @Mock
    IMSConfigProperties imsConfigProperties;

    @Mock
    AbstractLASImsLocationConnector abstractLASImsLocationConnector;
    private final List<AbstractLASImsLocationConnector> imsLocationConnectors = new ArrayList<>();


    @Test
    public void getLasImsLocationConnector(){
        imsLocationConnectors.add(abstractLASImsLocationConnector);
        lasImsLocationConnectorFactory = new LASImsLocationConnectorFactory(imsLocationConnectors,propertyUtil);
        Mockito.when(propertyUtil.getIMSConfigProperties(any())).thenReturn(imsConfigProperties);
        Mockito.when(abstractLASImsLocationConnector.supports("HD")).thenReturn(true);
        lasImsLocationConnectorFactory.getLasImsLocationConnector("HD");
    }
}
