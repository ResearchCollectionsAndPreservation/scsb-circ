package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAPwdDsItemRequest;
import org.recap.las.model.GFAPwdRequest;

public class GFAPwdRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwdRequest(){
        GFAPwdRequest gfaPwdRequest = new GFAPwdRequest();
        GFAPwdRequest gfaPwdRequest1 = new GFAPwdRequest();
        gfaPwdRequest.setDsitem(new GFAPwdDsItemRequest());
        gfaPwdRequest.equals(gfaPwdRequest);
        gfaPwdRequest1.equals(gfaPwdRequest);
        gfaPwdRequest.equals(gfaPwdRequest1);
        gfaPwdRequest.hashCode();
        gfaPwdRequest1.hashCode();
        gfaPwdRequest.toString();
    }
}
