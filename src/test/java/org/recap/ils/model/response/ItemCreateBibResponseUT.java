package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class ItemCreateBibResponseUT extends BaseTestCaseUT {

    @Test
    public void getItemCreateBibResponse() {

        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setItemId("12356");
        itemCreateBibResponse.setItemBarcode("563434");
        itemCreateBibResponse.setBibId("664342");

        assertNotNull(itemCreateBibResponse.getItemId());
        assertNotNull(itemCreateBibResponse.getItemBarcode());
        assertNotNull(itemCreateBibResponse.getBibId());
    }
}
