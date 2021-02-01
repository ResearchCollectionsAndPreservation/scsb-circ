package org.recap.callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.las.AbstractLASImsLocationConnector;
import org.recap.las.LASImsLocationConnectorFactory;
import org.recap.las.model.GFAItemStatus;
import org.recap.las.model.GFAItemStatusCheckRequest;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.recap.las.GFALasService;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class LasItemStatusCheckPollingCallableUT {

    @InjectMocks
    LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable;

    @Mock
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Mock
    AbstractLASImsLocationConnector abstractLASImsLocationConnector;

    @Test
    public void call() throws Exception{
        LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable = new LasItemStatusCheckPollingCallable(2, lasImsLocationConnectorFactory,"12345","HD");
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("12345");
        gfaItemStatusCheckRequest.setItemStatus(Arrays.asList(gfaItemStatus));
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        lasItemStatusCheckPollingCallable.setBarcode("12346");
        assertNotNull(lasItemStatusCheckPollingCallable.getBarcode());
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse1 = lasItemStatusCheckPollingCallable.call();
        assertNotNull(gfaItemStatusCheckResponse1);
    }
    @Test
    public void callException() throws Exception{
        LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable = new LasItemStatusCheckPollingCallable(2, lasImsLocationConnectorFactory,"12345","HD");
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("12345");
        gfaItemStatusCheckRequest.setItemStatus(Arrays.asList(gfaItemStatus));
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        lasItemStatusCheckPollingCallable.setBarcode("12346");
        assertNotNull(lasItemStatusCheckPollingCallable.getBarcode());
        Mockito.when(lasImsLocationConnectorFactory.getLasImsLocationConnector(any())).thenReturn(abstractLASImsLocationConnector);
        Mockito.when(abstractLASImsLocationConnector.itemStatusCheck(any())).thenThrow(new NullPointerException());
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse1 = lasItemStatusCheckPollingCallable.call();
        assertNull(gfaItemStatusCheckResponse1);
    }
    @Test
    public void getBarcode(){
        lasItemStatusCheckPollingCallable.setBarcode("366573");
        assertNotNull(lasItemStatusCheckPollingCallable.getBarcode());

    }
    private GFAItemStatusCheckResponse getGfaItemStatusCheckResponse(){
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = new GFAItemStatusCheckResponse();
        Dsitem dsitem = new Dsitem();
        Ttitem ttitem = new Ttitem();
        ttitem.setItemBarcode("12345");
        ttitem.setItemStatus("SUCCESS");
        dsitem.setTtitem(Arrays.asList(ttitem));
        gfaItemStatusCheckResponse.setDsitem(dsitem);
        assertNotNull(dsitem.getTtitem().get(0));
        assertNotNull(gfaItemStatusCheckResponse.getDsitem());
        return gfaItemStatusCheckResponse;
    }
}
