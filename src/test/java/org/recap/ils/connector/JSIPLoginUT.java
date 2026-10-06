package org.recap.ils.connector;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.requests.SIP2LoginRequest;
import com.pkrete.jsip2.messages.requests.SIP2PatronInformationRequest;
import com.pkrete.jsip2.messages.responses.SIP2LoginResponse;
import com.pkrete.jsip2.messages.responses.SIP2PatronInformationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.recap.model.ILSConfigProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class JSIPLoginUT {

    @InjectMocks
    private SIPProtocolConnector sipProtocolConnector;

    @Mock
    private SIP2SocketConnection mockConnection;

    @Mock
    private ILSConfigProperties mockILSConfigProperties; // Mock the ILSConfigProperties dependency

    @BeforeEach
    public void setup() {

        // Setup operator credentials required for SIPProtocolConnector instance
        sipProtocolConnector.setInstitution("testInstitution");

        // Mock the ILSConfigProperties methods
        when(mockILSConfigProperties.getOperatorUserId()).thenReturn("testUser");
        when(mockILSConfigProperties.getOperatorPassword()).thenReturn("testPass");
        when(mockILSConfigProperties.getOperatorLocation()).thenReturn("testLocation");

        // Set the mocked ILSConfigProperties in the SIPProtocolConnector
        sipProtocolConnector.setIlsConfigProperties(mockILSConfigProperties);

        // Spy on sipProtocolConnector to override getHost() and getPort() methods
        sipProtocolConnector = spy(sipProtocolConnector);
        doReturn("localhost").when(sipProtocolConnector).getHost();
        doReturn(1234).when(sipProtocolConnector).getPort();
    }

    @Test
    public void testJSIPLogin_Success() throws Exception {
        when(mockConnection.connect()).thenReturn(true);

        SIP2LoginResponse mockLoginResponse = mock(SIP2LoginResponse.class);
        when(mockLoginResponse.isOk()).thenReturn(true);

        SIP2PatronInformationResponse mockPatronInfoResponse = mock(SIP2PatronInformationResponse.class);
        when(mockPatronInfoResponse.isValidPatron()).thenReturn(true);
        when(mockPatronInfoResponse.isValidPatronPassword()).thenReturn(true);

        when(mockConnection.send(ArgumentMatchers.any())).thenAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            if (arg instanceof SIP2LoginRequest) {
                return mockLoginResponse;
            } else if (arg instanceof SIP2PatronInformationRequest) {
                return mockPatronInfoResponse;
            }
            return null;
        });

        boolean result = sipProtocolConnector.jSIPLogin(mockConnection, "patron123");

        assertTrue(result, "Expected login to succeed when all responses are OK");
        verify(mockConnection, times(1)).connect();
        verify(mockConnection, times(2)).send(ArgumentMatchers.any());
    }

    @Test
    public void testJSIPLogin_Failure_LoginResponseNotOK() throws Exception {
        when(mockConnection.connect()).thenReturn(true);
        SIP2LoginResponse mockLoginResponse = mock(SIP2LoginResponse.class);
        when(mockLoginResponse.isOk()).thenReturn(false);

        SIP2PatronInformationResponse mockPatronInfoResponse = mock(SIP2PatronInformationResponse.class);
        when(mockConnection.send(ArgumentMatchers.any())).thenAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            if (arg instanceof SIP2LoginRequest) {
                return mockLoginResponse;
            } else if (arg instanceof SIP2PatronInformationRequest) {
                return mockPatronInfoResponse;
            }
            return null;
        });

        boolean result = sipProtocolConnector.jSIPLogin(mockConnection, "patron123");
        assertFalse(result, "Expected login to fail when login response is not OK");
    }

    @Test
    public void testJSIPLogin_Failure_InvalidPatron() throws Exception {
        when(mockConnection.connect()).thenReturn(true);
        SIP2LoginResponse mockLoginResponse = mock(SIP2LoginResponse.class);
        when(mockLoginResponse.isOk()).thenReturn(true);

        SIP2PatronInformationResponse mockPatronInfoResponse = mock(SIP2PatronInformationResponse.class);
        when(mockPatronInfoResponse.isValidPatron()).thenReturn(false);
        when(mockPatronInfoResponse.isValidPatronPassword()).thenReturn(true);

        when(mockConnection.send(ArgumentMatchers.any())).thenAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            if (arg instanceof SIP2LoginRequest) {
                return mockLoginResponse;
            } else if (arg instanceof SIP2PatronInformationRequest) {
                return mockPatronInfoResponse;
            }
            return null;
        });

        boolean result = sipProtocolConnector.jSIPLogin(mockConnection, "patron123");
        assertFalse(result, "Expected login to fail when patron is invalid");
    }

    @Test
    public void testJSIPLogin_Failure_ConnectionFails() {
        when(mockConnection.connect()).thenReturn(false);
        boolean result = sipProtocolConnector.jSIPLogin(mockConnection, "patron123");
        assertFalse(result, "Expected login to fail when connection fails");
    }


    @Test
    public void testJSIPLogin_ThrowsInvalidSIP2ResponseException() throws Exception {
        when(mockConnection.connect()).thenReturn(true);
        // Throw InvalidSIP2ResponseException when sending SIP2LoginRequest
        when(mockConnection.send(ArgumentMatchers.any())).thenThrow(new InvalidSIP2ResponseException("Invalid SIP2 Response"));
        boolean result = sipProtocolConnector.jSIPLogin(mockConnection, "patron123");
        assertFalse(result, "Expected login to fail when InvalidSIP2ResponseException is thrown");
        verify(mockConnection, times(1)).connect();
        verify(mockConnection, times(1)).send(ArgumentMatchers.any());
    }

    @Test
    public void testJSIPLogin_ThrowsInvalidSIP2ResponseValueException() throws Exception {
        when(mockConnection.connect()).thenReturn(true);
        // Throw InvalidSIP2ResponseValueException when sending SIP2LoginRequest
        when(mockConnection.send(ArgumentMatchers.any())).thenThrow(new InvalidSIP2ResponseValueException("Invalid SIP2 Response Value"));
        boolean result = sipProtocolConnector.jSIPLogin(mockConnection, "patron123");
        assertFalse(result, "Expected login to fail when InvalidSIP2ResponseValueException is thrown");
        verify(mockConnection, times(1)).connect();
        verify(mockConnection, times(1)).send(ArgumentMatchers.any());
    }

}
