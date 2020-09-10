package org.recap.ils;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.SIP2MessageResponse;
import com.pkrete.jsip2.messages.requests.SIP2LoginRequest;
import com.pkrete.jsip2.messages.requests.SIP2PatronInformationRequest;
import com.pkrete.jsip2.messages.responses.SIP2LoginResponse;
import com.pkrete.jsip2.messages.responses.SIP2PatronInformationResponse;
import com.pkrete.jsip2.variables.CurrencyType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.BaseTestCase;
import org.recap.model.AbstractResponseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.Socket;
import java.util.Date;

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
    SIP2SocketConnection mockedSip2SocketConnection ;

    @Mock
    SIP2MessageResponse mockedSIP2MessageResponse;
    @Mock
    SIP2PatronInformationRequest mockedSIP2PatronInformationRequest;
    @Mock
    SIP2LoginRequest mockedSIP2LoginRequest;
    @Mock
    SIP2LoginResponse mockedSIP2LoginResponse;

    @Mock
    NyplApiConnector mockedNyplApiConnector;

    @Mock
    PrincetonJSIPConnector mockedPrincetonJSIPConnector;

    @Mock
    ColumbiaJSIPConnector mockedColumbiaJSIPConnector;

    @Mock
    SIP2PatronInformationResponse mockedSip2PatronInformationResponse;

    @Before
    public  void setup(){
        // ReflectionTestUtils.invokeMethod(mockedJsipConnector,"getSocketConnection");
        mockedSip2SocketConnection = new SIP2SocketConnection("localhost",9090,10000);
        ReflectionTestUtils.setField(mockedColumbiaJSIPConnector,"columbiaILS","localhost");
        ReflectionTestUtils.setField(mockedPrincetonJSIPConnector,"princetonILS","localhost");
        ReflectionTestUtils.setField(mockedNyplApiConnector,"newyorkILS","localhost");
        ReflectionTestUtils.setField(mockedColumbiaJSIPConnector,"columbiaILSPort",8012);
        ReflectionTestUtils.setField(mockedPrincetonJSIPConnector,"princetonILSPort",8012);
        ReflectionTestUtils.setField(mockedNyplApiConnector,"newyorkILSPort",8090);
        Socket socket = new Socket();
        mockedSip2SocketConnection.setSocket(socket);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public  void jSIPLogin() throws Exception{
        String patronIdentifier = "123456";
        mockedSIP2LoginRequest.setUserName("recap");
        mockedSIP2LoginRequest.setPassword("recap");
        mockedSIP2LoginRequest.setCirculationLocation("location");
        Mockito.when(mockedSip2SocketConnection.connect()).thenReturn(true);
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

    @Test
    public void lookupItem() throws Exception {
        SIP2LoginResponse sip2LoginResponse = getSip2LoginResponse();
        mockedSIP2LoginRequest = new SIP2LoginRequest("recap","recap");
        mockedSIP2LoginRequest.setUserName(null);
        mockedSIP2LoginRequest.setPassword(null);
        mockedSIP2LoginRequest.setCirculationLocation(null);
        // PowerMockito.whenNew(SIP2LoginRequest.class).withArguments(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),ArgumentMatchers.anyString()).thenReturn(mockedSIP2LoginRequest);
//        Mockito.when(mockedSip2SocketConnection.send(mockedSIP2LoginRequest)).thenReturn(sip2LoginResponse);
        Mockito.when(mockedJsipConnector.lookupItem("23456")).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem =  mockedJsipConnector.lookupItem("23456");
    }

    @Test
    public void lookupItemException(){
        Mockito.when(mockedJsipConnector.lookupItem("23456")).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem =  mockedJsipConnector.lookupItem("23456");
    }
    @Test
    public void lookupUser(){
//        Mockito.when(mockedColumbiaJSIPConnector.getHost()).thenReturn("");
//        Mockito.when(mockedSip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(mockedJsipConnector.lookupUser("2","123456")).thenCallRealMethod();
        mockedJsipConnector.lookupUser("2","123456");
    }
    @Test
    public void lookupUserRequestFailed(){
        Mockito.when(mockedJsipConnector.lookupUser("2","123456")).thenCallRealMethod();
        mockedJsipConnector.lookupUser("2","123456");
    }
    private SIP2LoginResponse getSip2LoginResponse() {
        SIP2LoginResponse sip2LoginResponse = new SIP2LoginResponse("test");
        sip2LoginResponse.setOk(true);
        sip2LoginResponse.setBibId("1");
        sip2LoginResponse.setCheckSum("test");
        sip2LoginResponse.setCode("GU003215");
        sip2LoginResponse.setCurrentLocation("PA");
        sip2LoginResponse.setData("test");
        sip2LoginResponse.setDueDate(new Date().toString());
        sip2LoginResponse.setExpirationDate(new Date().toString());
        return sip2LoginResponse;
    }

}