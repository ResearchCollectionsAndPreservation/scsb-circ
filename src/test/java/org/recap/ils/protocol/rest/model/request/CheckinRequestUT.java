package org.recap.ils.protocol.rest.model.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CheckinRequestUT {

    @Test
    public void getCheckinRequest() {
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode("123456");

        assertNotNull(checkinRequest.getItemBarcode());
    }
}
