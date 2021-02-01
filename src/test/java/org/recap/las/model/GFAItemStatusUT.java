package org.recap.las.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAItemStatus;

public class GFAItemStatusUT extends BaseTestCaseUT {
    @Test
    public void getGFAItemStatus(){
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        GFAItemStatus gfaItemStatus1 = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("124356");
        gfaItemStatus.equals(gfaItemStatus);
        gfaItemStatus.equals(gfaItemStatus1);
        gfaItemStatus1.equals(gfaItemStatus);
        gfaItemStatus.hashCode();
        gfaItemStatus1.hashCode();
        gfaItemStatus.toString();
    }
}
