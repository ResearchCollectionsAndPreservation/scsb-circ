package org.recap.ils.model.request;

import org.junit.Test;
import org.recap.ils.model.nypl.request.CheckinRequest;

import static org.junit.Assert.assertNotNull;

public class CheckinRequestUT {

    @Test
    public void getCheckinRequest(){
        CheckinRequest checkinRequest = new CheckinRequest();
        checkinRequest.setItemBarcode("123456");

        assertNotNull(checkinRequest.getItemBarcode());
    }
}
