package org.recap.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.gfa.Ttitem;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.request.GFAService;
import org.recap.util.ItemRequestServiceUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

public class LasItemStatusCheckPollingProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    LasItemStatusCheckPollingProcessor lasItemStatusCheckPollingProcessor;

    @Mock
    private GFAService gfaService;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    CamelContext camelContext;

    @Mock
    RouteController routeController;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(lasItemStatusCheckPollingProcessor,"pollingTimeInterval",10);
    }

    @Test
    public void pollLasItemStatusJobResponse(){
        String barcode ="123456";
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGFAItemStatusCheckResponse();
        Mockito.when(gfaService.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse(barcode,ctx);
    }
    @Test
    public void pollLasItemStatusJobResponseException() throws Exception {
        String barcode ="123456";
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGFAItemStatusCheckResponse();
        Mockito.when(gfaService.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        Mockito.doThrow(new NullPointerException()).when(routeController).startRoute(any());
        lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse(barcode,ctx);
    }
    @Test
    public void pollLasItemStatusJobResponseInterruptedException() throws Exception {
        String barcode ="123456";
        CamelContext ctx = new DefaultCamelContext();
        ctx.setRouteController(routeController);
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = getGFAItemStatusCheckResponse();
        Mockito.when(gfaService.itemStatusCheck(any())).thenReturn(gfaItemStatusCheckResponse);
        Mockito.doThrow(new InterruptedException()).when(routeController).startRoute(any());
        lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse(barcode,ctx);
    }
    @Test
    public void pollLasItemStatusJobResponseExecutionException(){
        String barcode ="123456";
        lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse(barcode,camelContext);
    }
    private GFAItemStatusCheckResponse getGFAItemStatusCheckResponse() {
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = new GFAItemStatusCheckResponse();
        Dsitem dsitem = new Dsitem();
        Ttitem ttitem = new Ttitem();
        ttitem.setItemBarcode("7020");
        ttitem.setItemStatus("NOT ON FILE");
        dsitem.setTtitem(Arrays.asList(ttitem));
        gfaItemStatusCheckResponse.setDsitem(dsitem);
        return gfaItemStatusCheckResponse;
    }
}
