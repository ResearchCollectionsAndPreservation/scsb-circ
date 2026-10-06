package org.recap.ils.connector;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.requests.SIP2LoginRequest;
import com.pkrete.jsip2.messages.requests.SIP2PatronInformationRequest;
import com.pkrete.jsip2.messages.responses.SIP2LoginResponse;
import com.pkrete.jsip2.messages.responses.SIP2PatronInformationResponse;
import com.pkrete.jsip2.variables.PatronStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.recap.common.ScsbConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.model.response.PatronInformationResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class SIPProtocolConnectorLookupPatronTest {

    private SIPProtocolConnector sipProtocolConnector;
    private ILSConfigProperties mockIlsConfig;

    @BeforeEach
    public void setup() {
        sipProtocolConnector = new SIPProtocolConnector();
        sipProtocolConnector.setInstitution("testInstitution");

        mockIlsConfig = mock(ILSConfigProperties.class);
        when(mockIlsConfig.getOperatorUserId()).thenReturn("testUser");
        when(mockIlsConfig.getOperatorPassword()).thenReturn("testPass");
        when(mockIlsConfig.getOperatorLocation()).thenReturn("testLocation");
        when(mockIlsConfig.getHost()).thenReturn("localhost");
        when(mockIlsConfig.getPort()).thenReturn(6001);
        sipProtocolConnector.setIlsConfigProperties(mockIlsConfig);

        sipProtocolConnector = spy(sipProtocolConnector);
        doReturn("localhost").when(sipProtocolConnector).getHost();
        doReturn(6001).when(sipProtocolConnector).getPort();
    }

    private SIP2PatronInformationResponse buildFullPatronResponse() {
        SIP2PatronInformationResponse patronResp = mock(SIP2PatronInformationResponse.class);
        PatronStatus patronStatus = mock(PatronStatus.class);

        when(patronStatus.isChargePrivilegesDenied()).thenReturn(false);
        when(patronStatus.isRenewalPrivilegesDenied()).thenReturn(false);
        when(patronStatus.isRecallPrivilegesDenied()).thenReturn(false);
        when(patronStatus.isHoldPrivilegesDenied()).thenReturn(false);
        when(patronStatus.toString()).thenReturn("STATUS-OK");

        when(patronResp.getStatus()).thenReturn(patronStatus);
        when(patronResp.getScreenMessage()).thenReturn(Arrays.asList("Welcome"));
        when(patronResp.getPersonalName()).thenReturn("John Doe");
        when(patronResp.getPatronIdentifier()).thenReturn("PATRON-001");
        when(patronResp.getEmail()).thenReturn("john@example.com");
        when(patronResp.getBirthDate()).thenReturn("19900101");
        when(patronResp.getPhone()).thenReturn("555-1234");
        when(patronResp.getPermanentLocation()).thenReturn("MAIN");
        when(patronResp.getPickupLocation()).thenReturn("PICKUP-1");
        when(patronResp.getChargedItemsCount()).thenReturn(2);
        when(patronResp.getChargedItemsLimit()).thenReturn(10);
        when(patronResp.getFeeLimit()).thenReturn("50.00");
        when(patronResp.getHoldItemsCount()).thenReturn(1);
        when(patronResp.getHoldItemsLimit()).thenReturn(5);
        when(patronResp.getUnavailableHoldsCount()).thenReturn(0);
        when(patronResp.getFineItemsCount()).thenReturn(0);
        when(patronResp.getFeeAmount()).thenReturn("0.00");
        when(patronResp.getHomeAddress()).thenReturn("123 Main St");
        when(patronResp.getItems()).thenReturn(Collections.emptyList());
        when(patronResp.getOverdueItemsCount()).thenReturn(0);
        when(patronResp.getOverdueItemsLimit()).thenReturn(3);
        when(patronResp.getPacAccessType()).thenReturn("PAC");
        when(patronResp.getPatronGroup()).thenReturn("GROUP-A");
        when(patronResp.getPatronType()).thenReturn("FACULTY");
        when(patronResp.getDueDate()).thenReturn("20250101    120000");
        when(patronResp.getExpirationDate()).thenReturn("20260101    120000");
        when(patronResp.getData()).thenReturn("ESIP-PATRON-OUT");
        return patronResp;
    }

    @Test
    public void testLookupPatron_Success_AllFieldsPopulated() throws Exception {
        SIP2PatronInformationResponse patronResp = buildFullPatronResponse();

        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2PatronInformationRequest) return patronResp;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-001");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            assertEquals("John Doe", resp.getPatronName());
            assertEquals("PATRON-001", resp.getPatronIdentifier());
            assertEquals("john@example.com", resp.getEmail());
            assertEquals("19900101", resp.getBirthDate());
            assertEquals("555-1234", resp.getPhone());
            assertEquals("MAIN", resp.getPermanentLocation());
            assertEquals("PICKUP-1", resp.getPickupLocation());
            assertEquals("50.00", resp.getFeeLimit());
            assertEquals("0.00", resp.getFeeAmount());
            assertEquals("123 Main St", resp.getHomeAddress());
            assertNotNull(resp.getItems());
            assertEquals("PAC", resp.getPacAccessType());
            assertEquals("GROUP-A", resp.getPatronGroup());
            assertEquals("FACULTY", resp.getPatronType());
            assertEquals("STATUS-OK", resp.getStatus());
            assertNotNull(resp.getEsipDataIn());
            assertEquals("ESIP-PATRON-OUT", resp.getEsipDataOut());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, atLeast(2)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testLookupPatron_Success_NullFeeTypeAndItemType() throws Exception {
        SIP2PatronInformationResponse patronResp = buildFullPatronResponse();
        when(patronResp.getFeeType()).thenReturn(null);   // NULL ? ""
        when(patronResp.getItemType()).thenReturn(null);  // NULL ? ""

        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2PatronInformationRequest) return patronResp;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-002");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            assertEquals("", resp.getFeeType());  // null feeType ? ""
            assertEquals("", resp.getItemType()); // null itemType ? ""
        }
    }

    @Test
    public void testLookupPatron_Success_NullScreenMessage() throws Exception {
        SIP2PatronInformationResponse patronResp = buildFullPatronResponse();
        when(patronResp.getScreenMessage()).thenReturn(null); // null ? ternary uses ""

        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2PatronInformationRequest) return patronResp;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-003");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            // screenMessage null ? ternary picks "" ? response still success
            assertNotNull(resp.getScreenMessage());
        }
    }


    @Test
    public void testLookupPatron_LoginFails_SetsScreenMessageFromLoginResponse()
            throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(false);
                    when(loginResponse.getScreenMessage())
                            .thenReturn(Arrays.asList("Login failed message"));

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenReturn(loginResponse);
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-004");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            assertEquals("Login failed message", resp.getScreenMessage());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).close();
        }
    }


    @Test
    public void testLookupPatron_ConnectionFails_SetsConnectionFailedMessage()
            throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(false);
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-005");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            assertEquals(ScsbConstants.ILS_CONNECTION_FAILED, resp.getScreenMessage());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, never()).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testLookupPatron_RuntimeException_IsCaught() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any()))
                            .thenThrow(new RuntimeException("Unexpected runtime error"));
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-006");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testLookupPatron_InvalidSIP2ResponseException_IsCaught() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any()))
                            .thenThrow(new InvalidSIP2ResponseException("Bad SIP2 response"));
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-007");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testLookupPatron_InvalidSIP2ResponseValueException_IsCaught()
            throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any()))
                            .thenThrow(new InvalidSIP2ResponseValueException("Bad value"));
                    when(mockConn.close()).thenReturn(true);
                })) {

            PatronInformationResponse resp = (PatronInformationResponse)
                    sipProtocolConnector.lookupPatron("PATRON-008");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testLookupPatron_FinallyBlock_AlwaysClosesConnection() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(false); // short-circuit
                    when(mockConn.close()).thenReturn(true);
                })) {

            sipProtocolConnector.lookupPatron("PATRON-009");

            assertEquals(1, mocked.constructed().size());
            verify(mocked.constructed().get(0), times(1)).close();
        }
    }
}