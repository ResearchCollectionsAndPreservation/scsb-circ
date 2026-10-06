package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.DebugInfo;
import org.recap.ils.protocol.rest.model.ItemData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
