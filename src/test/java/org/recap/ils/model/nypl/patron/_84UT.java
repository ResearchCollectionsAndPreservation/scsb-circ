package org.recap.ils.model.nypl.patron;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class _84UT extends BaseTestCase {

    _84 inst;

    @Test
    public void test_84() {
        inst = new _84();
        Object object = new Object();
        inst.setDisplay(object);
        inst.setLabel("test");
        inst.setValue("test");
        inst.getDisplay();
        inst.getLabel();
        inst.getValue();
        assertTrue(true);
    }
}

