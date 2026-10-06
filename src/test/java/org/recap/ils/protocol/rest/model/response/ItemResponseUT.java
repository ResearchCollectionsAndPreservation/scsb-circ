package org.recap.ils.protocol.rest.model.response;

import org.junit.jupiter.api.Test;
import org.recap.ils.protocol.rest.model.DebugInfo;
import org.recap.ils.protocol.rest.model.ItemData;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemResponseUT {

    @Test
    public void getItemResponse() {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setCount(1);
        itemResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        itemResponse.setItemData(new ItemData());
        itemResponse.setStatusCode(1);

        assertNotNull(itemResponse.getCount());
        assertNotNull(itemResponse.getDebugInfo());
        assertNotNull(itemResponse.getItemData());
        assertNotNull(itemResponse.getStatusCode());
    }
}
