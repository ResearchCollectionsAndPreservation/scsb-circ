package org.recap.las.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFALasStatusCheckResponse;
import org.recap.las.model.GFALasStatusDsItem;

public class GFALasStatusCheckResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFALasStatusCheckResponse(){
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = new GFALasStatusCheckResponse();
        GFALasStatusCheckResponse gfaLasStatusCheckResponse1 = new GFALasStatusCheckResponse();
        gfaLasStatusCheckResponse.setDsitem(new GFALasStatusDsItem());
        gfaLasStatusCheckResponse.equals(gfaLasStatusCheckResponse1);
        gfaLasStatusCheckResponse.equals(gfaLasStatusCheckResponse);
        gfaLasStatusCheckResponse1.equals(gfaLasStatusCheckResponse);
        gfaLasStatusCheckResponse.toString();
        gfaLasStatusCheckResponse.hashCode();
        gfaLasStatusCheckResponse1.hashCode();
    }
}
