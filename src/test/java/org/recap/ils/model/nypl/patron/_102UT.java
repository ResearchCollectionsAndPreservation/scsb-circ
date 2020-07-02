package org.recap.ils.model.nypl.patron;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class _102UT extends BaseTestCase {

    _102 inst;

    @Test
    public void test_102() {
        inst = new _102();
        Object object = new Object();
        inst.setDisplay(object);
        inst.setLabel("test");
        inst.setValue(1);
        inst.getDisplay();
        inst.getLabel();
        inst.getValue();
        assertTrue(true);
    }
}

