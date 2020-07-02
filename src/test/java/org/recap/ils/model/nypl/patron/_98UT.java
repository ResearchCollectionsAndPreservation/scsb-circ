package org.recap.ils.model.nypl.patron;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class _98UT extends BaseTestCase {

    _98 inst;

    @Test
    public void test_98() {
        inst = new _98();
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

