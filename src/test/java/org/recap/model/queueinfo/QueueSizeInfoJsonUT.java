package org.recap.model.queueinfo;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QueueSizeInfoJsonUT extends BaseTestCaseUT {
    @Test
    public void testQueueSizeInfoJson() {
        QueueSizeInfoJson queueSizeInfoJson = new QueueSizeInfoJson();
        queueSizeInfoJson.setValue("test");
        assertNotNull(queueSizeInfoJson.getValue());
    }
}
