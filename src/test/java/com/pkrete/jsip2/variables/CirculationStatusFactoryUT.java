package com.pkrete.jsip2.variables;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.responses.SIP2CreateBibResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class CirculationStatusFactoryUT {
    @Mock
    CirculationStatusFactory circulationStatusFactory;
    @Before
    public void Setup() {
        circulationStatusFactory = Mockito.mock(CirculationStatusFactory.class);
    }
    @Test
    public void testgetCirculationStatus() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.MISSING;
        Mockito.when(circulationStatusFactory.getCirculationStatus("13")).thenReturn(returnValue);
        assertEquals(returnValue, circulationStatusFactory.getCirculationStatus("13"));
    }
}
