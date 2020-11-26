package org.recap.ils;

import com.pkrete.jsip2.connection.SIP2SocketConnection;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.ILSConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class SIPProtocolConnectorUT extends BaseTestCaseUT {


    @Before
    public void setup() {
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        ilsConfigProperties.setHost("localhost");
        ilsConfigProperties.setPort(8080);
        SIP2SocketConnection connection = new SIP2SocketConnection(ilsConfigProperties.getHost(), ilsConfigProperties.getPort());
    }

    @Test
    public void supports() {
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        String protocol = RecapConstants.SIP2_PROTOCOL;
        boolean result = sipProtocolConnector.supports(protocol);
        assertTrue(result);
    }


    @Test
    public void testGetSocketConnection() {
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        boolean connected = sipProtocolConnector.checkSocketConnection();
        assertTrue(connected);
    }

    @Test
    public void jSIPLogin() throws IOException {
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        SIP2SocketConnection connection = new SIP2SocketConnection(ilsConfigProperties.getHost(), ilsConfigProperties.getPort());

        sipProtocolConnector.jSIPLogin(connection, "123456");
    }

    @Test
    public void patronValidation() {
        String institutionId = "1";
        String patronIdentifier = "123456";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.patronValidation(institutionId, patronIdentifier);
    }

    @Test
    public void lookupItem() {
        String itemIdentifier = "231456";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.lookupItem(itemIdentifier);
    }

    @Test
    public void lookupUser() {
        String institutionId = "231456";
        String patronIdentifier = "23456676";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.lookupUser(institutionId, patronIdentifier);
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

    @Test
    public void checkOutItem() {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.checkOutItem(itemIdentifier, patronIdentifier);
    }

    @Test
    public void checkInItem() {
        String itemIdentifier = "1456883";
        String patronIdentifier = "123456";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.checkInItem(itemIdentifier, patronIdentifier);
    }

    @Test
    public void placeHold() {
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
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.placeHold(itemIdentifier, patronIdentifier, callInstitutionId, itemInstitutionId, expirationDate, bibId, pickupLocation, trackingId, title, author, callNumber);

    }

    @Test
    public void cancelHold() {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";
        String trackingId = "67878890";

        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.cancelHold(itemIdentifier, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation, trackingId);

    }

    @Test
    public void createBib() {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String titleIdentifier = "245";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.createBib(itemIdentifier, patronIdentifier, institutionId, titleIdentifier);
    }

    @Test
    public void lookupPatron() {
        String patronIdentifier = "132456";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.lookupPatron(patronIdentifier);
    }

    @Test
    public void recallItem() {
        String itemIdentifier = "223467";
        String patronIdentifier = "2234567";
        String institutionId = "2";
        String expirationDate = new Date().toString();
        String bibId = "357221";
        String pickupLocation = "PA";

        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        ILSConfigProperties ilsConfigProperties = getIlsConfigProperties();
        sipProtocolConnector.setInstitution("PUL");
        sipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
        sipProtocolConnector.recallItem(itemIdentifier, patronIdentifier, institutionId, expirationDate, bibId, pickupLocation);
    }

    @Test
    public void refileItem() {
        String itemIdentifier = "223467";
        SIPProtocolConnector sipProtocolConnector = new SIPProtocolConnector();
        sipProtocolConnector.refileItem(itemIdentifier);
    }
}


