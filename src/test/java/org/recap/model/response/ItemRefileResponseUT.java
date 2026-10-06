package org.recap.model.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRefileResponseUT {

    @Test
    public void getItemRefileResponse() {
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setRequestId(1);
        itemRefileResponse.setJobId("1");
        assertNotNull(itemRefileResponse.getRequestId());
        assertNotNull(itemRefileResponse.getJobId());
    }
}
