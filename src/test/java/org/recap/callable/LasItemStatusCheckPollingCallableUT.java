package org.recap.callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.las.model.GFAItemStatus;
import org.recap.las.model.GFAItemStatusCheckRequest;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.recap.las.GFALasService;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class LasItemStatusCheckPollingCallableUT {

    @Mock
    private GFALasService gfaLasService;

    @Test
    public void call() throws Exception{
        LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable = new LasItemStatusCheckPollingCallable(2, gfaLasService,"12345");
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("12345");
        gfaItemStatusCheckRequest.setItemStatus(Arrays.asList(gfaItemStatus));
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
        lasItemStatusCheckPollingCallable.setBarcode("12346");
        assertNotNull(lasItemStatusCheckPollingCallable.getBarcode());
        Mockito.when(gfaLasService.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse1 = lasItemStatusCheckPollingCallable.call();
        assertNotNull(gfaItemStatusCheckResponse1);
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
