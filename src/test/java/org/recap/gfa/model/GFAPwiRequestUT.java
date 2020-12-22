package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

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
        gfaPwiRequest.canEqual(gfaPwiRequest);
        gfaPwiRequest.canEqual(gfaPwiRequest1);
        gfaPwiRequest.toString();
    }
}
