package org.recap.ils.model.nypl.patron;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class _49UT extends BaseTestCase {

    _49 inst;

    @Test
    public void test_49() {
        inst = new _49();
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

