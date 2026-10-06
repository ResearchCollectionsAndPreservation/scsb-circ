package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.model.response.BulkRequestResponse;

public class BulkRequestResponseUT {

    @Test
    public void getBulkRequestResponse() {

        BulkRequestResponse bulkRequestResponse = new BulkRequestResponse();
        bulkRequestResponse.setScreenMessage("ga");
        bulkRequestResponse.setSuccess(true);
        bulkRequestResponse.getScreenMessage();
        bulkRequestResponse.isSuccess();
    }
}
