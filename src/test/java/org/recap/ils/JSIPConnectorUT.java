package org.recap.ils;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.messages.SIP2MessageResponse;
import com.pkrete.jsip2.messages.requests.SIP2LoginRequest;
import com.pkrete.jsip2.messages.requests.SIP2PatronInformationRequest;
import com.pkrete.jsip2.messages.responses.SIP2LoginResponse;
import com.pkrete.jsip2.messages.responses.SIP2PatronInformationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.Socket;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 11/11/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class JSIPConnectorUT{

    @Mock
    JSIPConnector mockedJsipConnector;

    @Mock
    SIP2SocketConnection mockedSip2SocketConnection;

    @Mock
    SIP2MessageResponse mockedSIP2MessageResponse;
    @Mock
    SIP2PatronInformationRequest mockedSIP2PatronInformationRequest;
    @Mock
    SIP2LoginRequest mockedSIP2LoginRequest;
    @Mock
    SIP2LoginResponse mockedSIP2LoginResponse;

    @Mock
    SIP2PatronInformationResponse mockedSip2PatronInformationResponse;
   /* @Test
    public void testValidPatron(){
        boolean isValid = princetonJSIPConnector.patronValidation("PUL", "45678915");
        assertTrue(isValid);
    }*/

    @Test
    public  void jSIPLogin() throws Exception{
        String patronIdentifier = "123456";
        mockedSIP2LoginRequest.setUserName("recap");
        mockedSIP2LoginRequest.setPassword("recap");
        mockedSIP2LoginRequest.setCirculationLocation("location");
        Mockito.when(mockedSip2SocketConnection.connect()).thenReturn(true);
//        Mockito.when((SIP2LoginResponse)mockedSip2SocketConnection.send(mockedSIP2LoginRequest)).thenReturn((mockedSIP2LoginResponse));
//        Mockito.when((SIP2PatronInformationResponse)mockedSip2SocketConnection.send(mockedSIP2PatronInformationRequest)).thenReturn(mockedSip2PatronInformationResponse);
        Mockito.when(mockedJsipConnector.jSIPLogin(mockedSip2SocketConnection,patronIdentifier)).thenCallRealMethod();
        boolean result = mockedJsipConnector.jSIPLogin(mockedSip2SocketConnection,patronIdentifier);
        assertNotNull(result);
    }
    @Test
    public void patronValidation() throws Exception{

        Mockito.when(mockedJsipConnector.patronValidation("3","123456")).thenCallRealMethod();
        boolean result = mockedJsipConnector.patronValidation("3","123456");
//        Mockito.when((SIP2LoginResponse)mockedSip2SocketConnection.send(mockedSIP2LoginRequest)).thenReturn((mockedSIP2LoginResponse));
//        Mockito.when((SIP2LoginResponse)mockedSip2SocketConnection.send(mockedSIP2LoginRequest)).thenReturn((mockedSIP2LoginResponse));
//        Mockito.when((SIP2PatronInformationResponse)mockedSip2SocketConnection.send(mockedSIP2PatronInformationRequest)).thenReturn(mockedSip2PatronInformationResponse);
        assertNotNull(result);
    }

}