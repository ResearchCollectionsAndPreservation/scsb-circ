package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

public class GFALasStatusUT extends BaseTestCaseUT {
    @Test
    public void getGFALasStatus() {
        GFALasStatus gfaLasStatus = new GFALasStatus();
        GFALasStatus gfaLasStatus1 = new GFALasStatus();
        gfaLasStatus.setImsLocationCode("1");
        gfaLasStatus.toString();
        gfaLasStatus.equals(gfaLasStatus);
        gfaLasStatus.equals(gfaLasStatus1);
        gfaLasStatus1.equals(gfaLasStatus);
        gfaLasStatus.toString();
        gfaLasStatus.hashCode();
        gfaLasStatus1.hashCode();
    }
}
