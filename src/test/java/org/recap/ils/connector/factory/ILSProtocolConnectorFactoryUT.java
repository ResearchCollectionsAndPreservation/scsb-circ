package org.recap.ils.connector.factory;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ils.connector.AbstractProtocolConnector;
import org.recap.ils.connector.factory.ILSProtocolConnectorFactory;
import org.recap.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class ILSProtocolConnectorFactoryUT extends BaseTestCaseUT {

    @InjectMocks
    ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Mock
    AbstractProtocolConnector abstractProtocolConnector;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Test
    public void getIlsProtocolConnector(){
        String institution = "PUL";
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setProtocol("NCIP");
        ILSProtocolConnectorFactory ilsProtocolConnectorFactory = new ILSProtocolConnectorFactory();
        Mockito.when(propertyUtil.getILSConfigProperties(any())).thenReturn(ilsConfigProperties);
        Mockito.when(abstractProtocolConnector.supports("NCIP")).thenReturn(true);
        AbstractProtocolConnector abstractProtocolConnector = ilsProtocolConnectorFactory.getIlsProtocolConnector(institution);
        assertNotNull(abstractProtocolConnector);
    }


}
