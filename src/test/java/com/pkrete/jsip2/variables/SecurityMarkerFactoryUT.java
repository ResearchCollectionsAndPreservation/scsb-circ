package com.pkrete.jsip2.variables;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
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
