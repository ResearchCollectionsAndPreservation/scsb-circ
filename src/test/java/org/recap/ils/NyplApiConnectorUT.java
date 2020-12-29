package org.recap.ils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;

public class NyplApiConnectorUT extends BaseTestCaseUT {

    @InjectMocks
    NyplApiConnector nyplApiConnector;


    @Before
    public  void setup(){
        ReflectionTestUtils.setField(nyplApiConnector, "newyorkILSPort", 9090);
    }
    @Test
    public void getPort(){
        int port =  nyplApiConnector.getPort();
        assertNotNull(port);
    }
}
