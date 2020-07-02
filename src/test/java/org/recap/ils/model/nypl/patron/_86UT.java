package org.recap.ils.model.nypl.patron;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class _86UT extends BaseTestCase {

    _86 inst;

    @Test
    public void test_86() {
        inst = new _86();
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

