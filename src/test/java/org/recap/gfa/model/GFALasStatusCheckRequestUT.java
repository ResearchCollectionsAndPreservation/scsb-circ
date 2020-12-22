package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

public class GFALasStatusCheckRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFALasStatusCheckRequest(){
        GFALasStatusCheckRequest gfaLasStatusCheckRequest = new GFALasStatusCheckRequest();
        GFALasStatusCheckRequest gfaLasStatusCheckRequest1 = new GFALasStatusCheckRequest();
        gfaLasStatusCheckRequest.setLasStatus(Arrays.asList(new GFALasStatus()));
        gfaLasStatusCheckRequest.equals(gfaLasStatusCheckRequest1);
        gfaLasStatusCheckRequest.equals(gfaLasStatusCheckRequest);
        gfaLasStatusCheckRequest1.equals(gfaLasStatusCheckRequest);
        gfaLasStatusCheckRequest.canEqual(gfaLasStatusCheckRequest);
        gfaLasStatusCheckRequest.canEqual(gfaLasStatusCheckRequest1);
        gfaLasStatusCheckRequest.toString();
        gfaLasStatusCheckRequest.hashCode();
        gfaLasStatusCheckRequest1.hashCode();
    }
}
