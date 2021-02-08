package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.RequestStatusEntity;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 17/2/17.
 */
public class RequestStatusEntityUT extends BaseTestCaseUT {

    @Test
    public void testRequestStatus() {
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setId(1);
        requestStatusEntity.setRequestStatusCode("Refile");
        requestStatusEntity.setRequestStatusDescription("Refile");
        assertNotNull(requestStatusEntity);
        assertNotNull(requestStatusEntity.getId());
        assertNotNull(requestStatusEntity.getRequestStatusCode());
        assertNotNull(requestStatusEntity.getRequestStatusDescription());
    }

}