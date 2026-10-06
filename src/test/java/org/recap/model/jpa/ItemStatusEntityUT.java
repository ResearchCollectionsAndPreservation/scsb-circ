package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
