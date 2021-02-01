package org.recap.las.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFALasStatusDsItem;
import org.recap.las.model.GFALasStatusTtItem;

import java.util.Arrays;

public class GFALasStatusDsItemUT extends BaseTestCaseUT {

    @Test
    public void getGFALasStatusDsItem(){
        GFALasStatusDsItem gfaLasStatusDsItem = new GFALasStatusDsItem();
        GFALasStatusDsItem gfaLasStatusDsItem1 = new GFALasStatusDsItem();
        gfaLasStatusDsItem.setTtitem(Arrays.asList(new GFALasStatusTtItem()));
        gfaLasStatusDsItem.toString();
        gfaLasStatusDsItem.equals(gfaLasStatusDsItem1);
        gfaLasStatusDsItem.equals(gfaLasStatusDsItem);
        gfaLasStatusDsItem1.equals(gfaLasStatusDsItem);
        gfaLasStatusDsItem.hashCode();
        gfaLasStatusDsItem1.hashCode();
    }
}
