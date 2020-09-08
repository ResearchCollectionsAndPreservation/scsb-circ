package org.recap.model.jpa;

import org.junit.Test;

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
