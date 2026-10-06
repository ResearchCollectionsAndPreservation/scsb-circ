package org.recap.ils.connector;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.messages.requests.SIP2CheckoutRequest;
import com.pkrete.jsip2.messages.requests.SIP2LoginRequest;
import com.pkrete.jsip2.messages.requests.SIP2SCStatusRequest;
import com.pkrete.jsip2.messages.responses.SIP2ACSStatusResponse;
import com.pkrete.jsip2.messages.responses.SIP2CheckoutResponse;
import com.pkrete.jsip2.messages.responses.SIP2LoginResponse;
import com.pkrete.jsip2.variables.SupportedMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.recap.model.ILSConfigProperties;
import org.recap.model.response.ItemCheckoutResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class CheckoutItemTest {

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
        sipProtocolConnector.setIlsConfigProperties(mockIlsConfig);
        sipProtocolConnector = spy(sipProtocolConnector);
        doReturn("localhost").when(sipProtocolConnector).getHost();
        doReturn(1234).when(sipProtocolConnector).getPort();
    }

    @Test
    public void testCheckOutItem_Success() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    when(mockConn.connect()).thenReturn(true);
                    doReturn(true).when(sipProtocolConnector).jSIPLogin(mockConn, "patron123");

                    SIP2ACSStatusResponse acsStatusResponse = mock(SIP2ACSStatusResponse.class);
                    SupportedMessages supportedMessages = mock(SupportedMessages.class);
                    when(supportedMessages.isCheckout()).thenReturn(true);
                    when(acsStatusResponse.getSupportedMessages()).thenReturn(supportedMessages);

                    SIP2CheckoutResponse checkoutResponse = mock(SIP2CheckoutResponse.class);
                    when(checkoutResponse.isOk()).thenReturn(true);
                    when(checkoutResponse.getItemIdentifier()).thenReturn("barcode-123");
                    when(checkoutResponse.getPatronIdentifier()).thenReturn("patron123");
                    when(checkoutResponse.getTitleIdentifier()).thenReturn("title-1");
                    when(checkoutResponse.isDesensitizeSupported()).thenReturn(false);
                    when(checkoutResponse.isRenewalOk()).thenReturn(true);
                    when(checkoutResponse.isMagneticMedia()).thenReturn(false);
                    when(checkoutResponse.getDueDate()).thenReturn("20250101    120000");
                    when(checkoutResponse.getTransactionDate()).thenReturn("20250101    120000");
                    when(checkoutResponse.getInstitutionId()).thenReturn("INST_1");
                    when(checkoutResponse.getBibId()).thenReturn("bib-1");
                    when(checkoutResponse.getScreenMessage()).thenReturn(Collections.singletonList("Checked out"));

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2SCStatusRequest) {
                            return acsStatusResponse;
                        } else if (req instanceof SIP2CheckoutRequest) {
                            return checkoutResponse;
                        } else if (req instanceof SIP2LoginRequest) {
                            return loginResponse;
                        }
                        return null;
                    });

                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckoutResponse resp = (ItemCheckoutResponse) sipProtocolConnector.checkOutItem("barcode-123", 1, "patron123");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            assertEquals("barcode-123", resp.getItemBarcode());
            assertEquals("patron123", resp.getPatronIdentifier());
            assertEquals("bib-1", resp.getBibId());
            assertEquals("Checked out", resp.getScreenMessage());

            assertEquals(1, mocked.constructed().size());
            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, atLeast(2)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckOutItem_LoginFails() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    doReturn(false).when(sipProtocolConnector).jSIPLogin(mockConn, "patron123");
                    SIP2LoginResponse lr = mock(SIP2LoginResponse.class);
                    when(lr.isOk()).thenReturn(false);
                    when(lr.getScreenMessage()).thenReturn(Collections.singletonList("Login Failed"));
                    when(mockConn.send(any())).thenReturn(lr);

                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckoutResponse resp = (ItemCheckoutResponse) sipProtocolConnector.checkOutItem("barcode-123", 1, "patron123");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());
            assertEquals(org.recap.common.ScsbConstants.ILS_LOGIN_FAILED, resp.getScreenMessage());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckOutItem_CheckoutNotSupported() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    doReturn(true).when(sipProtocolConnector).jSIPLogin(mockConn, "patron123");
                    SIP2ACSStatusResponse acsStatusResponse = mock(SIP2ACSStatusResponse.class);
                    SupportedMessages supportedMessages = mock(SupportedMessages.class);
                    when(supportedMessages.isCheckout()).thenReturn(false);
                    when(acsStatusResponse.getSupportedMessages()).thenReturn(supportedMessages);
                    SIP2LoginResponse lr = mock(SIP2LoginResponse.class);
                    when(lr.isOk()).thenReturn(true);

                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2SCStatusRequest) return acsStatusResponse;
                        return lr;
                    });

                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckoutResponse resp = (ItemCheckoutResponse) sipProtocolConnector.checkOutItem("barcode-123", 1, "patron123");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, atLeast(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckOutItem_InvalidSIP2ResponseException() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    doReturn(true).when(sipProtocolConnector).jSIPLogin(mockConn, "patron123");
                    when(mockConn.send(any())).thenThrow(new InvalidSIP2ResponseException("Invalid SIP2"));

                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckoutResponse resp = (ItemCheckoutResponse) sipProtocolConnector.checkOutItem("barcode-123", 1, "patron123");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckOutItem_ConnectionFails() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    SIP2LoginResponse lr = mock(SIP2LoginResponse.class);
                    when(lr.isOk()).thenReturn(false);
                    when(lr.getScreenMessage()).thenReturn(Collections.singletonList(org.recap.common.ScsbConstants.ILS_LOGIN_FAILED));
                    when(mockConn.send(any())).thenReturn(lr);

                    when(mockConn.connect()).thenReturn(false);
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckoutResponse resp = (ItemCheckoutResponse) sipProtocolConnector.checkOutItem("barcode-123", 1, "patron123");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());
            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).close();
        }
    }
}
