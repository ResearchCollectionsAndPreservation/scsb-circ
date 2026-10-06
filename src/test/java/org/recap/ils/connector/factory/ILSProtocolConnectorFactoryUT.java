package org.recap.ils.connector.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.BaseTestCaseUT;
import org.recap.ils.connector.AbstractProtocolConnector;
import org.recap.model.ILSConfigProperties;
import org.recap.util.PropertyUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ILSProtocolConnectorFactoryUT extends BaseTestCaseUT {

    @InjectMocks
    ILSProtocolConnectorFactory ilsProtocolConnectorFactory;

    @Mock
    AbstractProtocolConnector abstractProtocolConnector;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Test
    public void getIlsProtocolConnector() {
        String institution = "PUL";
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setProtocol("NCIP");
        ILSProtocolConnectorFactory ilsProtocolConnectorFactory = new ILSProtocolConnectorFactory();
        ReflectionTestUtils.setField(ilsProtocolConnectorFactory, "propertyUtil", propertyUtil);
        ReflectionTestUtils.setField(ilsProtocolConnectorFactory, "applicationContext", applicationContext);
        Mockito.when(propertyUtil.getILSConfigProperties(any())).thenReturn(ilsConfigProperties);
        Mockito.when(applicationContext.getBean(ArgumentMatchers.<Class>any())).thenReturn(abstractProtocolConnector);
        Mockito.when(abstractProtocolConnector.supports("NCIP")).thenReturn(true);
        AbstractProtocolConnector connector = ilsProtocolConnectorFactory.getIlsProtocolConnector(institution);
        assertNotNull(connector);
    }

    @Test
    public void getIlsProtocolConnectorForSIP() {
        String institution = "PUL";
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setProtocol("SIP2");
        Map<String, AbstractProtocolConnector> protocolConnectorsMap = new HashMap<>();
        protocolConnectorsMap.put("PUL", abstractProtocolConnector);
        ILSProtocolConnectorFactory ilsProtocolConnectorFactory = new ILSProtocolConnectorFactory();
        ReflectionTestUtils.setField(ilsProtocolConnectorFactory, "propertyUtil", propertyUtil);
        ReflectionTestUtils.setField(ilsProtocolConnectorFactory, "applicationContext", applicationContext);
        Mockito.when(propertyUtil.getILSConfigProperties(any())).thenReturn(ilsConfigProperties);
        Mockito.when(applicationContext.getBean(ArgumentMatchers.<Class>any())).thenReturn(abstractProtocolConnector);
        Mockito.when(abstractProtocolConnector.supports(any())).thenReturn(true);
        AbstractProtocolConnector connector = ilsProtocolConnectorFactory.getIlsProtocolConnector(institution);
        assertNotNull(connector);
    }


    @Test
    public void getIlsProtocolConnectorForREST() {
        String institution = "PUL";
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setProtocol("REST");
        Map<String, AbstractProtocolConnector> protocolConnectorsMap = new HashMap<>();
        protocolConnectorsMap.put("PUL", abstractProtocolConnector);
        ILSProtocolConnectorFactory ilsProtocolConnectorFactory = new ILSProtocolConnectorFactory();
        ReflectionTestUtils.setField(ilsProtocolConnectorFactory, "propertyUtil", propertyUtil);
        ReflectionTestUtils.setField(ilsProtocolConnectorFactory, "applicationContext", applicationContext);
        Mockito.when(propertyUtil.getILSConfigProperties(any())).thenReturn(ilsConfigProperties);
        Mockito.when(applicationContext.getBean(ArgumentMatchers.<Class>any())).thenReturn(abstractProtocolConnector);
        Mockito.when(abstractProtocolConnector.supports(any())).thenReturn(true);
        AbstractProtocolConnector connector = ilsProtocolConnectorFactory.getIlsProtocolConnector(institution);
        assertNotNull(connector);
    }
}