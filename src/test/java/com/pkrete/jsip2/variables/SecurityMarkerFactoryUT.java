package com.pkrete.jsip2.variables;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertEquals;
@RunWith(MockitoJUnitRunner.class)
public class SecurityMarkerFactoryUT {

    @InjectMocks
    private SecurityMarkerFactory mockSecurityMarkerFactory;

    @Test
    public void testSecurityMarkerFactoryForOTHER() throws InvalidSIP2ResponseValueException {
        SecurityMarker returnValue = SecurityMarker.OTHER;
        SecurityMarker result = mockSecurityMarkerFactory.getSecurityMarker("00");
        assertEquals(returnValue, result);
    }
    @Test
    public void testSecurityMarkerFactoryForNONE() throws InvalidSIP2ResponseValueException {
        SecurityMarker returnValue = SecurityMarker.NONE;
        SecurityMarker result = mockSecurityMarkerFactory.getSecurityMarker("01");
        assertEquals(returnValue, result);
    }
    @Test
    public void testSecurityMarkerFactoryForTATTLE_TAPE_SECURITY_STRIP_3M() throws InvalidSIP2ResponseValueException {
        SecurityMarker returnValue = SecurityMarker.TATTLE_TAPE_SECURITY_STRIP_3M;
        SecurityMarker result = mockSecurityMarkerFactory.getSecurityMarker("02");
        assertEquals(returnValue, result);
    }
    @Test
    public void testSecurityMarkerFactoryForWHISPER_TAPE_3M() throws InvalidSIP2ResponseValueException {
        SecurityMarker returnValue = SecurityMarker.WHISPER_TAPE_3M;
        SecurityMarker result = mockSecurityMarkerFactory.getSecurityMarker("03");
        assertEquals(returnValue, result);
    }
    @Test
    public void testSecurityMarkerFactory() throws InvalidSIP2ResponseValueException {
        SecurityMarker returnValue = SecurityMarker.OTHER;
        SecurityMarker result = mockSecurityMarkerFactory.getSecurityMarker("04");
        assertEquals(returnValue, result);
    }
}
