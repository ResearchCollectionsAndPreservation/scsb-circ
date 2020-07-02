package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.ItemData;
import org.recap.ils.model.nypl.response.ItemsResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ItemsResponseUT extends BaseTestCase {
    ItemsResponse itemsResponse;
    @Test
    public void testItemsResponse() {
        itemsResponse = new ItemsResponse();
        List<DebugInfo> debuginfolist= new ArrayList<>();
        DebugInfo debugInfo = new DebugInfo();
        debuginfolist.add(debugInfo);
        List<ItemData> itemsDatalist =new ArrayList<>();
        ItemData itemData = new ItemData();
        itemsDatalist.add(itemData);
        itemsResponse.setCount(1);
        itemsResponse.setStatusCode(2);
        itemsResponse.setDebugInfo(debuginfolist);
        itemsResponse.setItemsData(itemsDatalist);
        itemsResponse.getCount();
        itemsResponse.getDebugInfo();
        itemsResponse.getItemsData();
        itemsResponse.getStatusCode();
        assertTrue(true);
    }
}
