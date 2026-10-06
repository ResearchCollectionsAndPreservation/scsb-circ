package org.recap.ils.protocol.rest.model.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RefileRequestUT {

    @Test
    public void getRefileRequest() {
        RefileRequest refileRequest = new RefileRequest();
        refileRequest.setItemBarcode("123456");

        assertNotNull(refileRequest.getItemBarcode());
    }
}
