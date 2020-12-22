package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

public class GFALasStatusCheckResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFALasStatusCheckResponse(){
        GFALasStatusCheckResponse gfaLasStatusCheckResponse = new GFALasStatusCheckResponse();
        GFALasStatusCheckResponse gfaLasStatusCheckResponse1 = new GFALasStatusCheckResponse();
        gfaLasStatusCheckResponse.setDsitem(new GFALasStatusDsItem());
        gfaLasStatusCheckResponse.equals(gfaLasStatusCheckResponse1);
        gfaLasStatusCheckResponse.equals(gfaLasStatusCheckResponse);
        gfaLasStatusCheckResponse1.equals(gfaLasStatusCheckResponse);
        gfaLasStatusCheckResponse.canEqual(gfaLasStatusCheckResponse1);
        gfaLasStatusCheckResponse.canEqual(gfaLasStatusCheckResponse);
        gfaLasStatusCheckResponse.toString();
        gfaLasStatusCheckResponse.hashCode();
        gfaLasStatusCheckResponse1.hashCode();
    }
}
