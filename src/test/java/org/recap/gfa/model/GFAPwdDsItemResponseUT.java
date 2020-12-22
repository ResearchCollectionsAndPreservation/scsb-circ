package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

public class GFAPwdDsItemResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwdDsItemResponse(){
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse1 = new GFAPwdDsItemResponse();
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(new GFAPwdTtItemResponse()));
        gfaPwdDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwdDsItemResponse.setProdsHasChanges(true);
        gfaPwdDsItemResponse.equals(gfaPwdDsItemResponse);
        gfaPwdDsItemResponse.equals(gfaPwdDsItemResponse1);
        gfaPwdDsItemResponse1.equals(gfaPwdDsItemResponse);
        gfaPwdDsItemResponse.hashCode();
        gfaPwdDsItemResponse1.hashCode();
        gfaPwdDsItemResponse.toString();
    }
}
