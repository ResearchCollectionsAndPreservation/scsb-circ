package org.recap.ils.model.nypl;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ItemDataUT extends BaseTestCaseUT {

    @Test
    public void getItemData(){

        ItemData itemData = new ItemData();
        itemData.setId("1");
        itemData.setNyplType("34");
        itemData.setCreatedDate(new Date().toString());
        itemData.setVarFields(Arrays.asList(new VarField()));
        itemData.setFixedFields(new FixedFields());
        assertNotNull(itemData.getId());
        assertNotNull(itemData.getNyplType());
        assertNotNull(itemData.getCreatedDate());
        assertNotNull(itemData.getVarFields());
        assertNotNull(itemData.getFixedFields());
    }
}
