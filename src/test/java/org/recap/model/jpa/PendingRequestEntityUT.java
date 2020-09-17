package org.recap.model.jpa;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class PendingRequestEntityUT {

    @Test
    public  void getPendingRequest(){
        PendingRequestEntity pendingRequestEntity = new PendingRequestEntity();
        pendingRequestEntity.setId(1);
        pendingRequestEntity.setItemEntity(new ItemEntity());
        pendingRequestEntity.setItemId(1);
        pendingRequestEntity.setRequestCreatedDate(new Date());
        pendingRequestEntity.setRequestId(1);
        pendingRequestEntity.setRequestItemEntity(new RequestItemEntity());
        assertNotNull(pendingRequestEntity.getItemEntity());
        assertNotNull(pendingRequestEntity.getItemId());
        assertNotNull(pendingRequestEntity.getRequestCreatedDate());
        assertNotNull(pendingRequestEntity.getRequestId());
        assertNotNull(pendingRequestEntity.getRequestItemEntity());
    }
}
