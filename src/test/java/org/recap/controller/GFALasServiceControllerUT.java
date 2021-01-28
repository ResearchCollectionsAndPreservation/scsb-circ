package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.gfa.Dsitem;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.report.GfaDeaccessionInfo;
import org.recap.las.GFALasService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class GFALasServiceControllerUT extends BaseTestCaseUT {
    @InjectMocks
    GFAServiceController gfaServiceController;
    @Mock
    GFALasService gfaLasService;

    @Test
    public void itemStatusCheck(){
        String itemBarcode = "3455632";
        Mockito.when(gfaLasService.callGfaItemStatus(itemBarcode)).thenReturn("IN");
        String gfaItemStatusValue = gfaServiceController.itemStatusCheck(itemBarcode);
        assertNotNull(gfaItemStatusValue);
    }
    @Test
    public void itemStatusCheckList(){
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("3455632");
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = new GFAItemStatusCheckResponse();
        gfaItemStatusCheckResponse.setDsitem(new Dsitem());
/*//        Mockito.when(gfaLasService.getGFAItemStatusCheckResponseByBarcodes(itemBarcodes)).thenReturn(gfaItemStatusCheckResponse);
        GFAItemStatusCheckResponse response = gfaServiceController.itemStatusCheck(itemBarcodes);
        assertNotNull(response);*/
    }
    @Test
    public void deaccessionInGfa(){
        GfaDeaccessionInfo gfaDeaccessionInfo = new GfaDeaccessionInfo();
        gfaDeaccessionInfo.setUsername("test");
        gfaDeaccessionInfo.setDeAccessionDBResponseEntities(new ArrayList<>());
        /*Mockito.doNothing().when(gfaLasService).callGfaDeaccessionService(gfaDeaccessionInfo.getDeAccessionDBResponseEntities(), gfaDeaccessionInfo.getUsername());
        gfaServiceController.deaccessionInGfa(gfaDeaccessionInfo);*/

    }

}
