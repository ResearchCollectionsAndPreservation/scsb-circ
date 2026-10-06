package org.recap.ils.connector;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.requests.SIP2CheckinRequest;
import com.pkrete.jsip2.messages.requests.SIP2LoginRequest;
import com.pkrete.jsip2.messages.requests.SIP2SCStatusRequest;
import com.pkrete.jsip2.messages.responses.SIP2ACSStatusResponse;
import com.pkrete.jsip2.messages.responses.SIP2CheckinResponse;
import com.pkrete.jsip2.messages.responses.SIP2LoginResponse;
import com.pkrete.jsip2.variables.MediaType;
import com.pkrete.jsip2.variables.SupportedMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.recap.model.ILSConfigProperties;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.response.ItemCheckinResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class SIPProtocolConnectorCheckInItemTest {

    private SIPProtocolConnector sipProtocolConnector;
    private ILSConfigProperties mockIlsConfig;
    private ItemRequestInformation itemRequestInformation;

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

        itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("BARCODE-001"));
    }

    @Test
    public void testCheckInItem_Success_AllFieldsPopulated() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    SIP2ACSStatusResponse acsStatusResponse = mock(SIP2ACSStatusResponse.class);
                    SupportedMessages supportedMessages = mock(SupportedMessages.class);
                    when(supportedMessages.isCheckin()).thenReturn(true);
                    when(acsStatusResponse.getSupportedMessages()).thenReturn(supportedMessages);

                    SIP2CheckinResponse checkinResponse = mock(SIP2CheckinResponse.class);
                    when(checkinResponse.isOk()).thenReturn(true);
                    when(checkinResponse.getItemIdentifier()).thenReturn("BARCODE-001");
                    when(checkinResponse.getTitleIdentifier()).thenReturn("TITLE-001");
                    when(checkinResponse.getDueDate()).thenReturn("20250101    120000");
                    when(checkinResponse.isResensitize()).thenReturn(true);
                    when(checkinResponse.isAlert()).thenReturn(true);
                    when(checkinResponse.isMagneticMedia()).thenReturn(false);
                    when(checkinResponse.getTransactionDate()).thenReturn("20250101    120000");
                    when(checkinResponse.getInstitutionId()).thenReturn("INST-001");
                    when(checkinResponse.getPatronIdentifier()).thenReturn("PATRON-001");
                    when(checkinResponse.getMediaType()).thenReturn(MediaType.OTHER);
                    when(checkinResponse.getBibId()).thenReturn("BIB-001");
                    when(checkinResponse.getPermanentLocation()).thenReturn("PERM-LOC");
                    when(checkinResponse.getCollectionCode()).thenReturn("COL-CODE");
                    when(checkinResponse.getSortBin()).thenReturn("BIN-1");
                    when(checkinResponse.getCallNumber()).thenReturn("QA99");
                    when(checkinResponse.getDestinationLocation()).thenReturn("DEST-LOC");
                    com.pkrete.jsip2.variables.AlertType alertType =
                            mock(com.pkrete.jsip2.variables.AlertType.class);
                    when(alertType.name()).thenReturn("HOLD");
                    when(checkinResponse.getAlertType()).thenReturn(alertType);
                    when(checkinResponse.getHoldPatronId()).thenReturn("HOLD-PAT-001");
                    when(checkinResponse.getHoldPatronName()).thenReturn("Hold Patron");
                    when(checkinResponse.getScreenMessage()).thenReturn(Arrays.asList("Check-in successful"));
                    when(checkinResponse.getData()).thenReturn("ESIP-OUT-DATA");

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2SCStatusRequest) return acsStatusResponse;
                        if (req instanceof SIP2CheckinRequest) return checkinResponse;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-001");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            assertEquals("BARCODE-001", resp.getItemBarcode());
            assertEquals("TITLE-001", resp.getTitleIdentifier());
            assertTrue(resp.isResensitize());
            assertTrue(resp.isAlert());
            assertFalse(resp.isMagneticMedia());
            assertEquals("INST-001", resp.getInstitutionID());
            assertEquals("INST-001", resp.getItemOwningInstitution());
            assertEquals("PATRON-001", resp.getPatronIdentifier());
            assertEquals("OTHER", resp.getMediaType());
            assertEquals("BIB-001", resp.getBibId());
            assertEquals("PERM-LOC", resp.getPermanentLocation());
            assertEquals("COL-CODE", resp.getCollectionCode());
            assertEquals("BIN-1", resp.getSortBin());
            assertEquals("QA99", resp.getCallNumber());
            assertEquals("DEST-LOC", resp.getDestinationLocation());
            assertEquals("HOLD", resp.getAlertType());
            assertEquals("HOLD-PAT-001", resp.getHoldPatronId());
            assertEquals("Hold Patron", resp.getHoldPatronName());
            assertEquals("Check-in successful", resp.getScreenMessage());
            assertNotNull(resp.getEsipDataIn());
            assertEquals("ESIP-OUT-DATA", resp.getEsipDataOut());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, atLeast(2)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckInItem_Success_NullMediaTypeAndAlertType() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    SIP2ACSStatusResponse acsStatusResponse = mock(SIP2ACSStatusResponse.class);
                    SupportedMessages supportedMessages = mock(SupportedMessages.class);
                    when(supportedMessages.isCheckin()).thenReturn(true);
                    when(acsStatusResponse.getSupportedMessages()).thenReturn(supportedMessages);

                    SIP2CheckinResponse checkinResponse = mock(SIP2CheckinResponse.class);
                    when(checkinResponse.isOk()).thenReturn(true);
                    when(checkinResponse.getItemIdentifier()).thenReturn("BARCODE-002");
                    when(checkinResponse.getTitleIdentifier()).thenReturn("TITLE-002");
                    when(checkinResponse.getDueDate()).thenReturn(null);
                    when(checkinResponse.isResensitize()).thenReturn(false);
                    when(checkinResponse.isAlert()).thenReturn(false);
                    when(checkinResponse.isMagneticMedia()).thenReturn(true);
                    when(checkinResponse.getTransactionDate()).thenReturn(null);
                    when(checkinResponse.getInstitutionId()).thenReturn("INST-002");
                    when(checkinResponse.getPatronIdentifier()).thenReturn("PATRON-002");
                    when(checkinResponse.getMediaType()).thenReturn(null);    // NULL ? ""
                    when(checkinResponse.getBibId()).thenReturn("BIB-002");
                    when(checkinResponse.getPermanentLocation()).thenReturn("");
                    when(checkinResponse.getCollectionCode()).thenReturn("");
                    when(checkinResponse.getSortBin()).thenReturn("");
                    when(checkinResponse.getCallNumber()).thenReturn("");
                    when(checkinResponse.getDestinationLocation()).thenReturn("");
                    when(checkinResponse.getAlertType()).thenReturn(null);
                    when(checkinResponse.getHoldPatronId()).thenReturn("");
                    when(checkinResponse.getHoldPatronName()).thenReturn("");
                    when(checkinResponse.getScreenMessage()).thenReturn(Collections.emptyList());
                    when(checkinResponse.getData()).thenReturn("ESIP-OUT-2");

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2SCStatusRequest) return acsStatusResponse;
                        if (req instanceof SIP2CheckinRequest) return checkinResponse;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-002");

            assertNotNull(resp);
            assertTrue(resp.isSuccess());
            assertEquals("", resp.getMediaType());
            assertEquals("", resp.getAlertType());
            assertEquals("", resp.getScreenMessage());
        }
    }

    @Test
    public void testCheckInItem_CheckinResponseNotOk_LogsFailed() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    SIP2ACSStatusResponse acsStatusResponse = mock(SIP2ACSStatusResponse.class);
                    SupportedMessages supportedMessages = mock(SupportedMessages.class);
                    when(supportedMessages.isCheckin()).thenReturn(true);
                    when(acsStatusResponse.getSupportedMessages()).thenReturn(supportedMessages);

                    SIP2CheckinResponse checkinResponse = mock(SIP2CheckinResponse.class);
                    when(checkinResponse.isOk()).thenReturn(false);
                    when(checkinResponse.getData()).thenReturn("RAW-FAIL");
                    when(checkinResponse.getScreenMessage()).thenReturn(Arrays.asList("Check-in failed"));

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2SCStatusRequest) return acsStatusResponse;
                        if (req instanceof SIP2CheckinRequest) return checkinResponse;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-003");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());
            assertEquals("Check-in failed", resp.getScreenMessage());
            assertNull(resp.getItemBarcode());
        }
    }

    @Test
    public void testCheckInItem_CheckinNotSupported() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(true);

                    SIP2ACSStatusResponse acsStatusResponse = mock(SIP2ACSStatusResponse.class);
                    SupportedMessages supportedMessages = mock(SupportedMessages.class);
                    when(supportedMessages.isCheckin()).thenReturn(false);
                    when(acsStatusResponse.getSupportedMessages()).thenReturn(supportedMessages);

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenAnswer(invocation -> {
                        Object req = invocation.getArgument(0);
                        if (req instanceof SIP2LoginRequest) return loginResponse;
                        if (req instanceof SIP2SCStatusRequest) return acsStatusResponse;
                        return null;
                    });
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-004");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());
            assertNull(resp.getItemBarcode());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, atLeast(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }


    @Test
    public void testCheckInItem_LoginFails() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {

                    SIP2LoginResponse loginResponse = mock(SIP2LoginResponse.class);
                    when(loginResponse.isOk()).thenReturn(false);

                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any())).thenReturn(loginResponse);
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-005");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());
            assertNull(resp.getItemBarcode());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckInItem_ConnectionFails() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(false);
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-006");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());
            assertNull(resp.getItemBarcode());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, never()).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckInItem_InvalidSIP2ResponseException() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any()))
                            .thenThrow(new InvalidSIP2ResponseException("Bad SIP2 response"));
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-007");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }

    @Test
    public void testCheckInItem_InvalidSIP2ResponseValueException() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(true);
                    when(mockConn.send(any()))
                            .thenThrow(new InvalidSIP2ResponseValueException("Bad SIP2 value"));
                    when(mockConn.close()).thenReturn(true);
                })) {

            ItemCheckinResponse resp = (ItemCheckinResponse)
                    sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-008");

            assertNotNull(resp);
            assertFalse(resp.isSuccess());

            SIP2SocketConnection constructed = mocked.constructed().get(0);
            verify(constructed, times(1)).send(any());
            verify(constructed, times(1)).close();
        }
    }


    @Test
    public void testCheckInItem_FinallyBlock_AlwaysClosesConnection() throws Exception {
        try (MockedConstruction<SIP2SocketConnection> mocked = mockConstruction(SIP2SocketConnection.class,
                (mockConn, ctx) -> {
                    when(mockConn.connect()).thenReturn(false); // short-circuit
                    when(mockConn.close()).thenReturn(true);
                })) {

            sipProtocolConnector.checkInItem(itemRequestInformation, "PATRON-009");

            assertEquals(1, mocked.constructed().size());
            verify(mocked.constructed().get(0), times(1)).close();
        }
    }
}