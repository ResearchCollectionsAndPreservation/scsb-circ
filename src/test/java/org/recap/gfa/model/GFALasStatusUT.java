package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFALasStatus;

public class GFALasStatusUT extends BaseTestCaseUT {
    @Test
    public void getGFALasStatus(){
        GFALasStatus gfaLasStatus = new GFALasStatus();
        GFALasStatus gfaLasStatus1 = new GFALasStatus();
        gfaLasStatus.setImsLocationCode("1");
        gfaLasStatus.toString();
        gfaLasStatus.equals(gfaLasStatus);
        gfaLasStatus.equals(gfaLasStatus1);
        gfaLasStatus1.equals(gfaLasStatus);
        gfaLasStatus.canEqual(gfaLasStatus);
        gfaLasStatus.canEqual(gfaLasStatus1);
        gfaLasStatus.toString();
        gfaLasStatus.hashCode();
        gfaLasStatus1.hashCode();
    }
}
