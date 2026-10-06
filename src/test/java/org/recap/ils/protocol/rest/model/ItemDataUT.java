package org.recap.ils.protocol.rest.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemDataUT extends BaseTestCaseUT {

    @Test
    public void getItemData() {

        ItemData itemData = new ItemData();
        itemData.setId("1");
        itemData.setCreatedDate(new Date().toString());
        assertNotNull(itemData.getId());
        assertNotNull(itemData.getCreatedDate());
    }
}
