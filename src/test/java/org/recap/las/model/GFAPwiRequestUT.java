package org.recap.las.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAPwiDsItemRequest;
import org.recap.las.model.GFAPwiRequest;

public class GFAPwiRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwiRequest(){
        GFAPwiRequest gfaPwiRequest = new GFAPwiRequest();
        GFAPwiRequest gfaPwiRequest1 = new GFAPwiRequest();
        gfaPwiRequest.setDsitem(new GFAPwiDsItemRequest());
        gfaPwiRequest.equals(gfaPwiRequest);
        gfaPwiRequest.equals(gfaPwiRequest1);
        gfaPwiRequest1.equals(gfaPwiRequest);
        gfaPwiRequest.hashCode();
        gfaPwiRequest1.hashCode();
        gfaPwiRequest.toString();
    }
}
