package org.recap.ils;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;

import java.util.Date;

import static org.junit.Assert.*;

public class NCIPProtocolConnectorUT extends BaseTestCaseUT {

    @InjectMocks
    NCIPProtocolConnector ncipProtocolConnector;

    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Test
    public void supports(){
     boolean result = ncipProtocolConnector.supports("NCIP");
     assertNotNull(result);
    }

    @Test
    public void setInstitution(){
        ncipProtocolConnector.setInstitution("NYPL");
    }

    @Test
    public void setIlsConfigProperties(){
        ncipProtocolConnector.setIlsConfigProperties(ilsConfigProperties);
    }

    @Test
    public void lookupItem(){
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupItem("13245676");
        assertNull(abstractResponseItem);
    }

    @Test
    public void checkOutItem(){
        Object result = ncipProtocolConnector.checkOutItem("23456","43567");
        assertNull(result);
    }

    @Test
    public void checkInItem(){
        Object result = ncipProtocolConnector.checkInItem("24456","255677");
        assertNull(result);
    }

    @Test
    public void placeHold(){
        Object result = ncipProtocolConnector.placeHold("234466","345562","1","1",new Date().toString(),"135456","PA","2345676","NYPL","test","245677");
        assertNull(result);
    }

    @Test
    public void cancelHold(){
        Object result = ncipProtocolConnector.cancelHold("2345546","435422","1",new Date().toString(),"234554","PA","233454");
        assertNull(result);
    }

    @Test
    public void createBib(){
        Object result = ncipProtocolConnector.createBib("322345","235435","1","23455");
        assertNull(result);
    }

    @Test
    public void patronValidation(){
        boolean result = ncipProtocolConnector.patronValidation("23434","234563");
        assertFalse(result);
    }

    @Test
    public void lookupPatron(){
        AbstractResponseItem abstractResponseItem = ncipProtocolConnector.lookupPatron("234566");
        assertNull(abstractResponseItem);
    }

    @Test
    public void recallItem(){
        Object result = ncipProtocolConnector.recallItem("225563","345677","1",new Date().toString(),"345622","PA");
        assertNull(result);
    }

    @Test
    public void refileItem(){
        Object result = ncipProtocolConnector.refileItem("23556");
        assertNull(result);
    }
}
