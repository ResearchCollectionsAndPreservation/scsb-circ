package org.recap.ils.connector;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.requests.SIP2ItemInformationRequest;
import com.pkrete.jsip2.messages.requests.SIP2LoginRequest;
import com.pkrete.jsip2.messages.responses.SIP2ItemInformationResponse;
import com.pkrete.jsip2.messages.responses.SIP2LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class LookupItemTest {

    private SIPProtocolConnector sipProtocolConnector;
    private ILSConfigProperties mockILSConfigProperties;

    @BeforeEach
    public void setup() {
        sipProtocolConnector = new SIPProtocolConnector();
        sipProtocolConnector.setInstitution("testInstitution");

        mockILSConfigProperties = mock(ILSConfigProperties.class);
        when(mockILSConfigProperties.getOperatorUserId()).thenReturn("testUser");
        when(mockILSConfigProperties.getOperatorPassword()).thenReturn("testPass");
        when(mockILSConfigProperties.getOperatorLocation()).thenReturn("testLocation");
        sipProtocolConnector.setIlsConfigProperties(mockILSConfigProperties);
        sipProtocolConnector = spy(sipProtocolConnector);
        doReturn("localhost").when(sipProtocolConnector).getHost();
        doReturn(1234).when(sipProtocolConnector).getPort();
    }

    @Test
    public void testLookupItem_Success() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, context) -> {
                    when(mockConn.connect()).thenReturn(true);
                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);
                    SIP2ItemInformationResponse itemResponse = mock(SIP2ItemInformationResponse.class);
                    when(itemResponse.isOk()).thenReturn(true);
                    when(itemResponse.getData()).thenReturn("ITEM-REQ-DATA");
                    when(itemResponse.getItemIdentifier()).thenReturn("barcode-123");
                    when(itemResponse.getScreenMessage()).thenReturn(Collections.singletonList("All good"));
                    when(itemResponse.getTitleIdentifier()).thenReturn("title-987");
                    when(itemResponse.getDueDate()).thenReturn("20250101    120000");
                    when(itemResponse.getTransactionDate()).thenReturn("20250101    120000");
                    when(itemResponse.getCurrentLocation()).thenReturn("CURR-LOC");
                    when(itemResponse.getPermanentLocation()).thenReturn("PERM-LOC");
                    when(itemResponse.getOwner()).thenReturn("OWNER");
                    when(itemResponse.getBibId()).thenReturn("bib-111");
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2ItemInformationRequest) return itemResponse;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            AbstractResponseItem result = sipProtocolConnector.lookupItem("barcode-123");

            assertNotNull(result);

            assertEquals("barcode-123", result.getItemBarcode());
            assertEquals(1, mocked.constructed().size());
            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).connect();
            verify(constructed, atLeast(2)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testLookupItem_LoginFails() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, context) -> {
                    when(mockConn.connect()).thenReturn(true);

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(false);
                    when(loginResponse.getScreenMessage()).thenReturn(Collections.singletonList("Login Failed"));
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        return null;
                    });

                    when(mockConn.close()).thenReturn(true);
                })) {

            AbstractResponseItem result = sipProtocolConnector.lookupItem("barcode-xyz");

            assertNotNull(result);
            assertFalse(result.isSuccess(), "Expected success false when login fails");
            assertEquals("Login Failed", result.getScreenMessage());

            assertEquals(1, mocked.constructed().size());
            SIP2SocketConnection constructed = mocked.constructed().get(0);

            verify(constructed, times(1)).connect();
            verify(constructed, times(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testLookupItem_InvalidSIP2ResponseException() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, context) -> {
                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenThrow(new InvalidSIP2ResponseException("Invalid SIP2"));
                    when(mockConn.close()).thenReturn(true);
                })) {

            AbstractResponseItem result = sipProtocolConnector.lookupItem("barcode-exc");

            assertNotNull(result);
            assertFalse(result.isSuccess(), "Expected success false on InvalidSIP2ResponseException");
            assertEquals(org.recap.common.ScsbConstants.INVALID_NO_RESPONSE_FROM_ILS, result.getScreenMessage());

            assertEquals(1, mocked.constructed().size());
            verify(mocked.constructed().get(0), times(1)).connect();
            verify(mocked.constructed().get(0), times(1)).send(any());
            verify(mocked.constructed().get(0), times(1)).close();
        }
    }

    @Test
    public void testLookupItem_InvalidSIP2ResponseValueException() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, context) -> {
                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenThrow(new InvalidSIP2ResponseValueException("Invalid value"));
                    when(mockConn.close()).thenReturn(true);
                })) {

            AbstractResponseItem result = sipProtocolConnector.lookupItem("barcode-exc2");

            assertNotNull(result);
            assertFalse(result.isSuccess(), "Expected success false on InvalidSIP2ResponseValueException");
            assertEquals(org.recap.common.ScsbConstants.SCREEN_MESSAGE_ITEM_BARCODE_NOT_FOUND, result.getScreenMessage());

            assertEquals(1, mocked.constructed().size());
            verify(mocked.constructed().get(0), times(1)).connect();
            verify(mocked.constructed().get(0), times(1)).send(any());
            verify(mocked.constructed().get(0), times(1)).close();
        }
    }

    @Test
    public void testLookupItem_ConnectionFails() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, context) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(false);
                    when(loginResponse.getScreenMessage()).thenReturn(Collections.singletonList(org.recap.common.ScsbConstants.ILS_LOGIN_FAILED));
                    when(mockConn.send(any())).thenReturn(loginResponse);

                    when(mockConn.connect()).thenReturn(false);
                    when(mockConn.close()).thenReturn(true);
                })) {

            AbstractResponseItem result = sipProtocolConnector.lookupItem("any-barcode");

            assertNotNull(result);
            assertFalse(result.isSuccess(), "Expected success false when connection fails");
            assertEquals(org.recap.common.ScsbConstants.ILS_LOGIN_FAILED, result.getScreenMessage());

            assertEquals(1, mocked.constructed().size());
            verify(mocked.constructed().get(0), times(1)).connect();
            verify(mocked.constructed().get(0), times(1)).send(any());
            verify(mocked.constructed().get(0), times(1)).close();
        }
    }
}
