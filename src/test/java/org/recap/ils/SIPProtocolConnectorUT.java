package org.recap.ils;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import com.pkrete.jsip2.messages.requests.*;
import com.pkrete.jsip2.messages.responses.*;
import com.pkrete.jsip2.variables.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.ScsbConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.model.jpa.ItemRequestInformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SIPProtocolConnector.class)
@PowerMockIgnore("com.sun.org.apache.xerces.*")
public class SIPProtocolConnectorUT {

    SIPProtocolConnector sipProtocolConnector;

    @Mock
    SIP2SocketConnection sip2SocketConnection;

    @Before
    public void setUp() throws Exception {
        sipProtocolConnector = PowerMockito.spy(new SIPProtocolConnector());
        getIlsConfigProperties();
    }

    @Test
    public void supports() {
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        String protocol = ScsbConstants.SIP2_PROTOCOL;
        boolean result = sipProtocolConnector.supports(protocol);
        assertTrue(result);
    }

    @Test
    public void lookupItem() throws Exception {
        String itemIdentifier = "2133566";
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2ItemInformationResponse sip2ItemInformationResponse = getSIP2ItemInformationResponse();
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        //PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        PowerMockito.when(sipProtocolConnector, "getSocketConnection").thenReturn(sip2SocketConnection);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2ItemInformationRequest.class))).thenReturn(sip2ItemInformationResponse);
        sipProtocolConnector.lookupItem(itemIdentifier);
    }

    @Test
    public void checkOutItem() throws Exception {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        Integer requestId = 2;
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2ACSStatusResponse sip2ACSStatusResponse = getSIP2ACSStatusResponse();
        SIP2CheckoutResponse sip2CheckoutResponse = getSIP2CheckoutResponse();
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        Mockito.doReturn(Boolean.TRUE).when(sipProtocolConnector).jSIPLogin(any(), any());
        Mockito.when(sip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2SCStatusRequest.class))).thenReturn(sip2ACSStatusResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2CheckoutRequest.class))).thenReturn(sip2CheckoutResponse);
        sipProtocolConnector.checkOutItem(itemIdentifier, requestId, patronIdentifier);
    }

    @Test
    public void checkInItem() throws Exception {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2ACSStatusResponse sip2ACSStatusResponse = getSIP2ACSStatusResponse();
        SIP2CheckinResponse sip2CheckinResponse = getSIP2CheckinResponse();
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        Mockito.when(sip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2SCStatusRequest.class))).thenReturn(sip2ACSStatusResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2CheckinRequest.class))).thenReturn(sip2CheckinResponse);
        sipProtocolConnector.checkInItem(getItemRequestInformation(), patronIdentifier);
    }

    @Test
    public void placeHold() throws Exception {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String callInstitutionId = "1";
        String itemInstitutionId = "24";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";
        String trackingId = "67878890";
        String title = "Y90223";
        String author = "john";
        String callNumber = "54956";
        Integer requestId = 2;
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2PatronInformationResponse sip2PatronInformationResponse = new SIP2PatronInformationResponse("940");
        sip2PatronInformationResponse.setValidPatron(true);
        sip2PatronInformationResponse.setValidPatronPassword(true);
        SIP2HoldResponse sip2HoldResponse = getSIP2HoldResponse();
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        Mockito.when(sip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2PatronInformationRequest.class))).thenReturn(sip2PatronInformationResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2HoldRequest.class))).thenReturn(sip2HoldResponse);
        sipProtocolConnector.placeHold(itemIdentifier, requestId, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);
    }

    @Test
    public void cancelHold() throws Exception {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        Integer requestId = 2;
        String pickupLocation = "PA";
        String trackingId = "67878890";
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2PatronInformationResponse sip2PatronInformationResponse = new SIP2PatronInformationResponse("940");
        sip2PatronInformationResponse.setValidPatron(true);
        sip2PatronInformationResponse.setValidPatronPassword(true);
        SIP2HoldResponse sip2HoldResponse = getSIP2HoldResponse();
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        Mockito.when(sip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2PatronInformationRequest.class))).thenReturn(sip2PatronInformationResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2HoldRequest.class))).thenReturn(sip2HoldResponse);
        sipProtocolConnector.cancelHold(itemIdentifier, requestId, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);
    }

    @Test
    public void createBib() throws Exception {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String titleIdentifier = "245";
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2CreateBibResponse sip2CreateBibResponse = getSIP2CreateBibResponse();
        SIP2PatronInformationResponse sip2PatronInformationResponse = new SIP2PatronInformationResponse("940");
        sip2PatronInformationResponse.setValidPatron(true);
        sip2PatronInformationResponse.setValidPatronPassword(true);
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        Mockito.when(sip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2PatronInformationRequest.class))).thenReturn(sip2PatronInformationResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2CreateBibRequest.class))).thenReturn(sip2CreateBibResponse);
        sipProtocolConnector.createBib(itemIdentifier, patronIdentifier, institutionId, titleIdentifier);
    }

    @Test
    public void lookupPatron() throws Exception {
        String patronIdentifier = "132456";
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2PatronInformationResponse sip2PatronInformationResponse = getSIP2PatronInformationResponse();
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        Mockito.when(sip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2PatronInformationRequest.class))).thenReturn(sip2PatronInformationResponse);
        sipProtocolConnector.lookupPatron(patronIdentifier);
    }

    @Test
    public void recallItem() throws Exception {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";

        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2RecallResponse sip2RecallResponse = getSIP2RecallResponse();
        SIP2PatronInformationResponse sip2PatronInformationResponse = getSIP2PatronInformationResponse();
        SIP2LoginResponse loginResponse = new SIP2LoginResponse("940");
        loginResponse.setOk(true);
        PowerMockito.doReturn(sip2SocketConnection).when(sipProtocolConnector, "getSocketConnection");
        Mockito.when(sip2SocketConnection.connect()).thenReturn(true);
        Mockito.when(sip2SocketConnection.send(any(SIP2LoginRequest.class))).thenReturn(loginResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2PatronInformationRequest.class))).thenReturn(sip2PatronInformationResponse);
        Mockito.when(sip2SocketConnection.send(any(SIP2RecallRequest.class))).thenReturn(sip2RecallResponse);
        sipProtocolConnector.recallItem(itemIdentifier, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation);
    }

    @Test
    public void refileItem() {
        String itemIdentifier = "223467";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        sipProtocolConnector.refileItem(itemIdentifier);
    }

    private SIP2RecallResponse getSIP2RecallResponse() {
        SIP2RecallResponse sip2RecallResponse = new SIP2RecallResponse("940");
        sip2RecallResponse.setBibId("325465");
        sip2RecallResponse.setCode("code");
        sip2RecallResponse.setScreenMessage(Arrays.asList("Success"));
        sip2RecallResponse.setFeeType(FeeType.PROCESSING);
        sip2RecallResponse.setCurrencyType(CurrencyType.EURO);
        sip2RecallResponse.setMediaType(MediaType.OTHER);
        sip2RecallResponse.setPatronIdentifier("2345656");
        return sip2RecallResponse;
    }

    private SIP2PatronInformationResponse getSIP2PatronInformationResponse() {
        SIP2PatronInformationResponse sip2PatronInformationResponse = new SIP2PatronInformationResponse("940");
        sip2PatronInformationResponse.setValidPatron(true);
        sip2PatronInformationResponse.setValidPatronPassword(true);
        sip2PatronInformationResponse.setCode("code");
        PatronStatus patronStatus = new PatronStatus();
        patronStatus.setHoldPrivilegesDenied(true);
        sip2PatronInformationResponse.setStatus(patronStatus);
        sip2PatronInformationResponse.setMediaType(MediaType.OTHER);
        sip2PatronInformationResponse.setFeeType(FeeType.PROCESSING);
        sip2PatronInformationResponse.setCurrencyType(CurrencyType.EURO);
        sip2PatronInformationResponse.setScreenMessage(Arrays.asList("Success"));
        sip2PatronInformationResponse.setBibId("1");
        return sip2PatronInformationResponse;
    }

    private SIP2CreateBibResponse getSIP2CreateBibResponse() {
        SIP2CreateBibResponse sip2CreateBibResponse = new SIP2CreateBibResponse("940");
        sip2CreateBibResponse.setBibId("1");
        sip2CreateBibResponse.setCheckSum("checksum");
        sip2CreateBibResponse.setData("940");
        sip2CreateBibResponse.setCurrencyType(CurrencyType.EURO);
        sip2CreateBibResponse.setFeeType(FeeType.PROCESSING);
        sip2CreateBibResponse.setMediaType(MediaType.OTHER);
        sip2CreateBibResponse.setScreenMessage(Arrays.asList("Success"));
        return sip2CreateBibResponse;
    }

    private SIP2HoldResponse getSIP2HoldResponse() {
        SIP2HoldResponse sip2HoldResponse = new SIP2HoldResponse("940");
        sip2HoldResponse.setCode("code");
        sip2HoldResponse.setAvailable(true);
        sip2HoldResponse.setCurrencyType(CurrencyType.EURO);
        sip2HoldResponse.setScreenMessage(Arrays.asList("Success"));
        sip2HoldResponse.setFeeType(FeeType.PROCESSING);
        sip2HoldResponse.setData("940");
        sip2HoldResponse.setOk(true);
        sip2HoldResponse.setMediaType(MediaType.OTHER);
        return sip2HoldResponse;
    }

    private SIP2CheckinResponse getSIP2CheckinResponse() {
        SIP2CheckinResponse sip2CheckinResponse = new SIP2CheckinResponse("940");
        sip2CheckinResponse.setCheckSum("test");
        sip2CheckinResponse.setCode("code");
        sip2CheckinResponse.setData("940");
        sip2CheckinResponse.setAlertType(AlertType.OTHER);
        sip2CheckinResponse.setCurrencyType(CurrencyType.EURO);
        sip2CheckinResponse.setTitleIdentifier("test");
        sip2CheckinResponse.setScreenMessage(Arrays.asList("Success"));
        sip2CheckinResponse.setOk(true);
        sip2CheckinResponse.setMediaType(MediaType.OTHER);
        sip2CheckinResponse.setFeeType(FeeType.PROCESSING);
        sip2CheckinResponse.setAlert(true);
        return sip2CheckinResponse;
    }

    private SIP2CheckoutResponse getSIP2CheckoutResponse() {
        SIP2CheckoutResponse sip2CheckoutResponse = new SIP2CheckoutResponse("940");
        sip2CheckoutResponse.setCheckSum("check");
        sip2CheckoutResponse.setCode("code");
        sip2CheckoutResponse.setBibId("2435722");
        sip2CheckoutResponse.setData("940");
        sip2CheckoutResponse.setCurrentLocation("PA");
        sip2CheckoutResponse.setDesensitize(true);
        sip2CheckoutResponse.setDesensitizeSupported(true);
        sip2CheckoutResponse.setRenewalOk(true);
        sip2CheckoutResponse.setSecurityInhibit(true);
        sip2CheckoutResponse.setSecurityInhibitUsed(true);
        sip2CheckoutResponse.setCurrencyType(CurrencyType.EURO);
        sip2CheckoutResponse.setFeeType(FeeType.PROCESSING);
        sip2CheckoutResponse.setExpirationDate(new Date().toString());
        sip2CheckoutResponse.setMediaType(MediaType.OTHER);
        sip2CheckoutResponse.setScreenMessage(Arrays.asList("Success"));
        return sip2CheckoutResponse;
    }

    private SIP2ACSStatusResponse getSIP2ACSStatusResponse() {
        SIP2ACSStatusResponse sip2ACSStatusResponse = new SIP2ACSStatusResponse("940");
        sip2ACSStatusResponse.setCheckoutOk(true);
        sip2ACSStatusResponse.setCheckinOk(true);
        SupportedMessages supportedMessages = new SupportedMessages();
        supportedMessages.setCheckout(true);
        supportedMessages.setCheckin(true);
        sip2ACSStatusResponse.setSupportedMessages(supportedMessages);
        return sip2ACSStatusResponse;
    }

    private SIP2ItemInformationResponse getSIP2ItemInformationResponse() {
        SIP2ItemInformationResponse sip2ItemInformationResponse = new SIP2ItemInformationResponse("940");
        sip2ItemInformationResponse.setCirculationStatus(CirculationStatus.AVAILABLE);
        sip2ItemInformationResponse.setHoldPickupDate(new Date().toString());
        sip2ItemInformationResponse.setOwner("test");
        sip2ItemInformationResponse.setRecallDate(new Date().toString());
        sip2ItemInformationResponse.setSecurityMarker(SecurityMarker.OTHER);
        sip2ItemInformationResponse.setHoldQueueLength("1");
        sip2ItemInformationResponse.setBibId("244363");
        sip2ItemInformationResponse.setCheckSum("test");
        sip2ItemInformationResponse.setCode("code");
        sip2ItemInformationResponse.setCurrencyType(CurrencyType.EURO);
        sip2ItemInformationResponse.setCurrentLocation("PA");
        sip2ItemInformationResponse.setData("940");
        sip2ItemInformationResponse.setDueDate(new Date().toString());
        sip2ItemInformationResponse.setExpirationDate(new Date().toString());
        sip2ItemInformationResponse.setFeeAmount("240");
        sip2ItemInformationResponse.setInstitutionId("1");
        sip2ItemInformationResponse.setItemProperties("test");
        sip2ItemInformationResponse.setScreenMessage(Arrays.asList("Success"));
        sip2ItemInformationResponse.setOk(true);
        sip2ItemInformationResponse.setMediaType(MediaType.OTHER);
        sip2ItemInformationResponse.setFeeType(FeeType.PROCESSING);
        sip2ItemInformationResponse.setTitleIdentifier("test");
        return sip2ItemInformationResponse;
    }

    private ILSConfigProperties getIlsConfigProperties() {
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setHost("libserv86.princeton.edu");
        ilsConfigProperties.setPort(7031);
        ilsConfigProperties.setOperatorUserId("recap");
        ilsConfigProperties.setOperatorPassword("recap");
        ilsConfigProperties.setOperatorLocation("location");
        return ilsConfigProperties;
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.singletonList("123456"));
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setRequestType("RETRIEVAL");
        return itemRequestInformation;
    }
}


