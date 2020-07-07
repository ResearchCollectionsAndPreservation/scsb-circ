package com.pkrete.jsip2.variables;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertEquals;

public class SecurityMarkerFactoryUT extends BaseTestCase {
    private SecurityMarkerFactory mockSecurityMarkerFactory;

    @Before
    public void Setup() {
        mockSecurityMarkerFactory = Mockito.mock(SecurityMarkerFactory.class);
    }

    @Test
    public void testSecurityMarkerFactory() throws InvalidSIP2ResponseValueException {
        SecurityMarker returnValue = SecurityMarker.OTHER;
        Mockito.when(mockSecurityMarkerFactory.getSecurityMarker("04")).thenReturn(returnValue);
        assertEquals(returnValue, mockSecurityMarkerFactory.getSecurityMarker("04"));
    }
}
