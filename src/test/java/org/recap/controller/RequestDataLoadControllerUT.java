package org.recap.controller;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RequestDataLoadControllerUT extends BaseTestCase {
    @Autowired
    RequestDataLoadController requestDataLoadController;
    @Test
    public void startAccessionReconcilation() throws Exception{
        String result = requestDataLoadController.startAccessionReconcilation();
        assertNotNull(result);
        assertEquals("Success",result);
    }
}
