package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.report.GfaDeaccessionInfo;
import org.recap.request.GFAService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class GFAServiceControllerUT extends BaseTestCaseUT {
    @InjectMocks
    GFAServiceController gfaServiceController;
    @Mock
    GFAService gfaService;

    @Test
    public void itemStatusCheck(){
        String itemBarcode = "3455632";
        Mockito.when(gfaService.callGfaItemStatus(itemBarcode)).thenReturn("IN");
        String gfaItemStatusValue = gfaServiceController.itemStatusCheck(itemBarcode);
        assertNotNull(gfaItemStatusValue);
    }
    @Test
    public void itemStatusCheckList(){
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("3455632");
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = new GFAItemStatusCheckResponse();
        gfaItemStatusCheckResponse.setDsitem(new Dsitem());
        Mockito.when(gfaService.getGFAItemStatusCheckResponseByBarcodes(itemBarcodes)).thenReturn(gfaItemStatusCheckResponse);
        GFAItemStatusCheckResponse response = gfaServiceController.itemStatusCheck(itemBarcodes);
        assertNotNull(response);
    }
    @Test
    public void deaccessionInGfa(){
        GfaDeaccessionInfo gfaDeaccessionInfo = new GfaDeaccessionInfo();
        gfaDeaccessionInfo.setUsername("test");
        gfaDeaccessionInfo.setDeAccessionDBResponseEntities(new ArrayList<>());
        Mockito.doNothing().when(gfaService).callGfaDeaccessionService(gfaDeaccessionInfo.getDeAccessionDBResponseEntities(), gfaDeaccessionInfo.getUsername());
        gfaServiceController.deaccessionInGfa(gfaDeaccessionInfo);

    }

}
