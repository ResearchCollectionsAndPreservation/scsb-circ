package org.recap.callable;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.gfa.model.GFALasStatusCheckResponse;
import org.recap.gfa.model.GFALasStatusDsItem;
import org.recap.gfa.model.GFALasStatusTtItem;
import org.recap.request.GFAService;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class LasHeartBeatCheckPollingCallableUT extends BaseTestCaseUT {

    @InjectMocks
    LasHeartBeatCheckPollingCallable lasHeartBeatCheckPollingCallable;
    @Mock
    GFAService gfaService;
    @Test
    public void call() throws Exception {
        Integer pollingTimeInterval = 10000;
        String imsLocationCode = "1";
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = getGFALasStatusCheckResponse();
        LasHeartBeatCheckPollingCallable lasHeartBeatCheckPollingCallable = new LasHeartBeatCheckPollingCallable(pollingTimeInterval,gfaService,imsLocationCode);
        Mockito.when(gfaService.heartBeatCheck(any())).thenReturn(gfaLasStatusCheckResponse);
        GFALasStatusCheckResponse response = lasHeartBeatCheckPollingCallable.call();
        assertNotNull(response);
    }

    @Test
    public void callException() throws Exception {
        lasHeartBeatCheckPollingCallable.call();
    }

    private GFALasStatusCheckResponse getGFALasStatusCheckResponse() {
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = new GFALasStatusCheckResponse();
        GFALasStatusDsItem gfaLasStatusDsItem = new GFALasStatusDsItem();
        GFALasStatusTtItem gfaLasStatusTtItem = new GFALasStatusTtItem();
        gfaLasStatusTtItem.setImsLocationCode("1");
        gfaLasStatusTtItem.setSuccess(Boolean.TRUE.toString());
        gfaLasStatusTtItem.setScreenMessage("SUCCESS");
        gfaLasStatusDsItem.setTtitem(Arrays.asList(gfaLasStatusTtItem));
        gfaLasStatusCheckResponse.setDsitem(gfaLasStatusDsItem);
        return  gfaLasStatusCheckResponse;
    }
}
