package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

public class GFAPwiDsItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwiDsItemRequest(){

        GFAPwiDsItemRequest gfaPwiDsItemRequest = new GFAPwiDsItemRequest();
        GFAPwiDsItemRequest gfaPwiDsItemRequest1 = new GFAPwiDsItemRequest();
        gfaPwiDsItemRequest.setTtitem(Arrays.asList(new GFAPwiTtItemRequest()));
        gfaPwiDsItemRequest.equals(gfaPwiDsItemRequest);
        gfaPwiDsItemRequest.equals(gfaPwiDsItemRequest1);
        gfaPwiDsItemRequest1.equals(gfaPwiDsItemRequest);
        gfaPwiDsItemRequest.hashCode();
        gfaPwiDsItemRequest1.hashCode();
        gfaPwiDsItemRequest.toString();
        gfaPwiDsItemRequest.canEqual(gfaPwiDsItemRequest);
        gfaPwiDsItemRequest.canEqual(gfaPwiDsItemRequest1);
    }
}
