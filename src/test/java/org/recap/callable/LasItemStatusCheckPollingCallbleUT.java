package org.recap.callable;

import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.gfa.model.*;
import org.recap.request.GFAService;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class LasItemStatusCheckPollingCallbleUT {

    @Mock
    private GFAService gfaService;

    @Test
    public void call() throws Exception{
        LasItemStatusCheckPollingCallable lasItemStatusCheckPollingCallable = new LasItemStatusCheckPollingCallable(2,gfaService,"12345");
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("12345");
        gfaItemStatusCheckRequest.setItemStatus(Arrays.asList(gfaItemStatus));
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGfaItemStatusCheckResponse();
//        Mockito.when(gfaService.itemStatusCheck(gfaItemStatusCheckRequest)).thenReturn(gfaItemStatusCheckResponse);
        //GFAItemStatusCheckResponse gfaItemStatusCheckResponse1 = lasItemStatusCheckPollingCallable.call();
       // assertNotNull(gfaItemStatusCheckResponse1);
    }
    private GFAItemStatusCheckResponse getGfaItemStatusCheckResponse(){
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = new GFAItemStatusCheckResponse();
        Dsitem dsitem = new Dsitem();
        Ttitem ttitem = new Ttitem();
        ttitem.setItemBarcode("12345");
        ttitem.setItemStatus("SUCCESS");
        dsitem.setTtitem(Arrays.asList(ttitem));
        gfaItemStatusCheckResponse.setDsitem(dsitem);
        return gfaItemStatusCheckResponse;
    }
}
