package org.recap.ils.protocol.rest.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.ItemData;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ItemDataUT extends BaseTestCaseUT {

    @Test
    public void getItemData(){

        ItemData itemData = new ItemData();
        itemData.setId("1");
        itemData.setNyplType("34");
        itemData.setCreatedDate(new Date().toString());
        assertNotNull(itemData.getId());
        assertNotNull(itemData.getNyplType());
        assertNotNull(itemData.getCreatedDate());
    }
}
