package org.recap.ils.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class RecordTypeUT extends BaseTestCaseUT {

    @Test
    public void getRecordType() {
        RecordType recordType = new RecordType();
        recordType.setType(RecordTypeType.AUTHORITY);
        recordType.setDatafield(null);
        recordType.setId("1");

        assertNotNull(recordType.getType());
        assertNotNull(recordType.getDatafield());
        assertNotNull(recordType.getId());
    }
}
