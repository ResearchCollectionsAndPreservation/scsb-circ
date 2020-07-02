package org.recap.ils.model.nypl.patron;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class _51UT extends BaseTestCase {

    _51 inst;

    @Test
    public void test_51() {
        inst = new _51();
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

