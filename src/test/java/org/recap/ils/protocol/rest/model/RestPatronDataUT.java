package org.recap.ils.protocol.rest.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 3/4/17.
 */
public class RestPatronDataUT extends BaseTestCaseUT {

    @Test
    public void testRestPatronData() {
        RestPatronData restPatronData = new RestPatronData();
        restPatronData.setId("1");
        restPatronData.setUpdatedDate(new Date().toString());
        restPatronData.setCreatedDate(new Date().toString());
        restPatronData.setDeletedDate(new Date().toString());
        restPatronData.setExpirationDate(new Date().toString());
        restPatronData.setBirthDate(new Date().toString());
        restPatronData.setDeleted(false);
        restPatronData.setSuppressed(false);
        restPatronData.setNames(Arrays.asList("test"));
        restPatronData.setBarCodes(Arrays.asList("3545874547253814556"));
        restPatronData.setHomeLibraryCode("test");
        restPatronData.setEmails(Arrays.asList("test@gmail.com"));

        assertNotNull(restPatronData.getId());
        assertNotNull(restPatronData.getUpdatedDate());
        assertNotNull(restPatronData.getCreatedDate());
        assertNotNull(restPatronData.getDeletedDate());
        assertNotNull(restPatronData.getDeleted());
        assertNotNull(restPatronData.getSuppressed());
        assertNotNull(restPatronData.getNames());
        assertNotNull(restPatronData.getBarCodes());
        assertNotNull(restPatronData.getExpirationDate());
        assertNotNull(restPatronData.getHomeLibraryCode());
        assertNotNull(restPatronData.getBirthDate());
        assertNotNull(restPatronData.getEmails());
    }

}