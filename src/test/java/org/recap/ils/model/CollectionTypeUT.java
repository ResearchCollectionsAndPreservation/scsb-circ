package org.recap.ils.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class CollectionTypeUT extends BaseTestCaseUT {

    @Test
    public void getCollectionTypeUT() {
        CollectionType collectionType = new CollectionType();
        collectionType.setId("1");
        collectionType.setRecord(null);
        assertNotNull(collectionType.getId());
        assertNotNull(collectionType.getRecord());
    }
}
