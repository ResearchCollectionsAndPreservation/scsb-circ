package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.ItemStatusEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 14/3/17.
 */
public class ItemStatusEntityUT extends BaseTestCaseUT {

    @Test
    public void testItemStatus() {
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("test");
        itemStatusEntity.setStatusDescription("test");
        assertNotNull(itemStatusEntity);
        assertEquals("test", itemStatusEntity.getStatusCode());
        assertEquals("test", itemStatusEntity.getStatusDescription());
    }

}
