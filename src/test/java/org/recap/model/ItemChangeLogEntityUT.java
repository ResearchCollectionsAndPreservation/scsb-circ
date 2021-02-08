package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.ItemChangeLogEntity;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 17/2/17.
 */
public class ItemChangeLogEntityUT extends BaseTestCaseUT {

    @Test
    public void testItemChangeLogEntity() {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setNotes("test");
        itemChangeLogEntity.setOperationType("test");
        itemChangeLogEntity.setUpdatedBy("test");
        itemChangeLogEntity.setRecordId(1);
        itemChangeLogEntity.setId(12);
        itemChangeLogEntity.setUpdatedDate(new Date());
        assertNotNull(itemChangeLogEntity);
        assertNotNull(itemChangeLogEntity.getId());
        assertNotNull(itemChangeLogEntity.getNotes());
        assertNotNull(itemChangeLogEntity.getOperationType());
        assertNotNull(itemChangeLogEntity.getUpdatedBy());
        assertNotNull(itemChangeLogEntity.getUpdatedDate());
    }


}