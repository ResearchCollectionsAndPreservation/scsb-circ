package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAPwdDsItemResponse;
import org.recap.las.model.GFAPwdResponse;

public class GFAPwdResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwdResponse(){
        GFAPwdResponse gfaPwdResponse = new GFAPwdResponse();
        GFAPwdResponse gfaPwdResponse1 = new GFAPwdResponse();
        gfaPwdResponse.setDsitem(new GFAPwdDsItemResponse());
        gfaPwdResponse.equals(gfaPwdResponse);
        gfaPwdResponse.equals(gfaPwdResponse1);
        gfaPwdResponse1.equals(gfaPwdResponse);
        gfaPwdResponse.hashCode();
        gfaPwdResponse1.hashCode();
        gfaPwdResponse.toString();
    }
}
