package org.recap.ils.connector;


import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.messages.requests.SIP2SCStatusRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class SIPProtocolConnectorTest {

    @InjectMocks
    private SIPProtocolConnector sipProtocolConnector;

    @Mock
    private SIP2SocketConnection connection;


    @Test
    public void testGetScreenMessageWithEmptyList() {
        List<String> screenMessage = new ArrayList<>();
        ReflectionTestUtils.invokeMethod(sipProtocolConnector, "getScreenMessage", screenMessage);
    }

    @Test
    public void testGetScreenMessageWithNonEmptyList() {
        List<String> screenMessage = new ArrayList<>();
        screenMessage.add("Message 1");
        screenMessage.add("Message 2");
        ReflectionTestUtils.invokeMethod(sipProtocolConnector, "getScreenMessage", screenMessage);
    }

    @Test
    public void testGetScreenMessageWithSingleElementList() {
        List<String> screenMessage = new ArrayList<>();
        screenMessage.add("Only Message");
        ReflectionTestUtils.invokeMethod(sipProtocolConnector, "getScreenMessage", screenMessage);
    }

    @Test
    public void testSendAcsStatus() throws Exception {
        ReflectionTestUtils.invokeMethod(sipProtocolConnector, "sendAcsStatus", connection);
        verify(connection, times(1)).send(Mockito.any(SIP2SCStatusRequest.class));
    }


}
