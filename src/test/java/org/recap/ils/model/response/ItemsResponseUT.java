package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.ItemData;
import org.recap.ils.model.nypl.response.ItemsResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ItemsResponseUT extends BaseTestCaseUT {

    @Test
    public void testItemsResponse() {
        ItemsResponse itemsResponse = new ItemsResponse();
        List<DebugInfo> debuginfolist = new ArrayList<>();
        DebugInfo debugInfo = new DebugInfo();
        debuginfolist.add(debugInfo);
        List<ItemData> itemsDatalist = new ArrayList<>();
        ItemData itemData = new ItemData();
        itemsDatalist.add(itemData);
        itemsResponse.setCount(1);
        itemsResponse.setStatusCode(2);
        itemsResponse.setDebugInfo(debuginfolist);
        itemsResponse.setItemsData(itemsDatalist);
        assertNotNull(itemsResponse.getCount());
        assertNotNull(itemsResponse.getItemsData());
        assertNotNull(itemsResponse.getStatusCode());
        assertNotNull(itemsResponse.getDebugInfo());
    }
}
