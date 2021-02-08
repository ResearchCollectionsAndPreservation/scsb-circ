package org.recap.ils.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RecordTypeTypeUT extends BaseTestCaseUT {

    @Test
    public void getRecordTypeType() {
        RecordTypeType recordTypeType = RecordTypeType.HOLDINGS;
        String value = recordTypeType.value();
        try {
            recordTypeType.fromValue("value");
        } catch (Exception e) {
            String message = e.getMessage();
            assertEquals("value", e.getMessage());
        }
        assertNotNull(value);
    }
}
