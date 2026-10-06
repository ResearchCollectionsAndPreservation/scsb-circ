package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 13/3/17.
 */
public class CollectionGroupEntityUT extends BaseTestCaseUT {

    @Test
    public void testCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCreatedDate(new Date());
        collectionGroupEntity.setCollectionGroupCode("others");
        collectionGroupEntity.setCollectionGroupDescription("others");
        collectionGroupEntity.setLastUpdatedDate(new Date());
        assertNotNull(collectionGroupEntity);
        assertNotNull(collectionGroupEntity.getId());
        assertNotNull(collectionGroupEntity.getCollectionGroupCode());
        assertNotNull(collectionGroupEntity.getCollectionGroupDescription());
        assertNotNull(collectionGroupEntity.getCreatedDate());
        assertNotNull(collectionGroupEntity.getLastUpdatedDate());
    }

}