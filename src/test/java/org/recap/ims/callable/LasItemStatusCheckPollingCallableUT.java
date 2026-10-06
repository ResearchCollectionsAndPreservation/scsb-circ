package org.recap.ims.callable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.ims.connector.AbstractLASImsLocationConnector;
import org.recap.ims.connector.factory.LASImsLocationConnectorFactory;
import org.recap.ims.model.GFAItemStatus;
import org.recap.ims.model.GFAItemStatusCheckRequest;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class LasItemStatusCheckPollingCallableUT {

    @InjectMocks
    LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable;

    @Mock
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;

    @Mock
    AbstractLASImsLocationConnector abstractLASImsLocationConnector;

    @Test
    public void call() throws Exception {
        LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable = new LasItemStatusCheckPollingCallable(2, lasImsLocationConnectorFactory, "12345", "HD");
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
    public void callException() throws Exception {
        LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable = new LasItemStatusCheckPollingCallable(2, lasImsLocationConnectorFactory, "12345", "HD");
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
    public void getBarcode() {
        lasItemStatusCheckPollingCallable.setBarcode("366573");
        assertNotNull(lasItemStatusCheckPollingCallable.getBarcode());

    }

    private GFAItemStatusCheckResponse getGfaItemStatusCheckResponse() {
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
