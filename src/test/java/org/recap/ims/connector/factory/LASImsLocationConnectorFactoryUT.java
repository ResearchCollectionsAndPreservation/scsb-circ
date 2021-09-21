package org.recap.ims.connector.factory;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ims.connector.AbstractLASImsLocationConnector;
import org.recap.model.IMSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
        lasImsLocationConnectorFactory = new LASImsLocationConnectorFactory();
        ReflectionTestUtils.setField(lasImsLocationConnectorFactory,"propertyUtil",propertyUtil);
        Mockito.when(propertyUtil.getIMSConfigProperties(any())).thenReturn(imsConfigProperties);
        //Mockito.when(abstractLASImsLocationConnector.supports("HD")).thenReturn(true);
        AbstractLASImsLocationConnector abstractLASImsLocationConnector = lasImsLocationConnectorFactory.getLasImsLocationConnector("HD");
        assertNotNull(abstractLASImsLocationConnector);
    }
}
