package org.recap.ils.model.nypl;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 3/4/17.
 */
public class NoticeUT extends BaseTestCaseUT {

    @Test
    public void testNotice() {
        Notice notice = new Notice();
        notice.setCreatedDate(new Date().toString());
        notice.setData("test");
        notice.setText("test");

        assertNotNull(notice.getCreatedDate());
        assertNotNull(notice.getData());
        assertNotNull(notice.getText());
    }

}