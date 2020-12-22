package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

public class GFAPwiDsItemResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwiDsItemResponse(){
        GFAPwiDsItemResponse gfaPwiDsItemResponse = new GFAPwiDsItemResponse();
        GFAPwiDsItemResponse gfaPwiDsItemResponse1 = new GFAPwiDsItemResponse();
        gfaPwiDsItemResponse.setTtitem(Arrays.asList(new GFAPwiTtItemResponse()));
        gfaPwiDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwiDsItemResponse.setProdsHasChanges(Boolean.TRUE);
        gfaPwiDsItemResponse.equals(gfaPwiDsItemResponse);
        gfaPwiDsItemResponse.equals(gfaPwiDsItemResponse1);
        gfaPwiDsItemResponse1.equals(gfaPwiDsItemResponse);
        gfaPwiDsItemResponse.hashCode();
        gfaPwiDsItemResponse1.hashCode();
        gfaPwiDsItemResponse.toString();
        gfaPwiDsItemResponse.canEqual(gfaPwiDsItemResponse);
        gfaPwiDsItemResponse.canEqual(gfaPwiDsItemResponse1);
    }
}
