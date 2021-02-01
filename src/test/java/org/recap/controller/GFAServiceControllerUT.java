package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.las.GFALasService;
import org.recap.model.gfa.ScsbLasItemStatusCheckModel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class GFAServiceControllerUT extends BaseTestCaseUT {

    @InjectMocks
    GFAServiceController gfaServiceController;

    @Mock
    private GFALasService gfaLasService;

    @Test
    public void itemStatusCheck(){
        String itemBarcode = "3455632";
        Mockito.when(gfaLasService.callGfaItemStatus(itemBarcode)).thenReturn("IN");
        String gfaItemStatusValue = gfaServiceController.itemStatusCheck(itemBarcode);
        assertNotNull(gfaItemStatusValue);
    }

    @Test
    public void multipleItemsStatusCheck(){
        List<ScsbLasItemStatusCheckModel> itemsStatusCheckModel = new ArrayList<>();
        ScsbLasItemStatusCheckModel scsbLasItemStatusCheckModel = new ScsbLasItemStatusCheckModel();
        scsbLasItemStatusCheckModel.setItemStatus("Complete");
        scsbLasItemStatusCheckModel.setItemBarcode("346732");
        scsbLasItemStatusCheckModel.setImsLocation("HD");
        itemsStatusCheckModel.add(scsbLasItemStatusCheckModel);
        Mockito.when(gfaLasService.getGFAItemStatusCheckResponseByBarcodesAndImsLocationList(itemsStatusCheckModel)).thenReturn(itemsStatusCheckModel);
        List<ScsbLasItemStatusCheckModel> response = gfaServiceController.multipleItemsStatusCheck(itemsStatusCheckModel);
        assertNotNull(response);
    }
}
