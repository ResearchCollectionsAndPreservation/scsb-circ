package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ImsLocationEntityUT extends BaseTestCaseUT {

    @Test
    public void getImsLocationEntity(){
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationName("test");
        imsLocationEntity.setId(1);
        imsLocationEntity.setUpdatedDate(new Date());
        imsLocationEntity.setImsLocationCode("1");
        imsLocationEntity.setDescription("imslocation");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setUpdatedBy("test");
        imsLocationEntity.setActive(true);
        imsLocationEntity.setCreatedBy("test");
        ImsLocationEntity imsLocationEntity1 = new ImsLocationEntity();
        imsLocationEntity.equals(imsLocationEntity1);
        imsLocationEntity.equals(imsLocationEntity);
        imsLocationEntity.canEqual(imsLocationEntity);
        imsLocationEntity.hashCode();
        imsLocationEntity.toString();

        assertTrue(imsLocationEntity.isActive());
        assertNotNull(imsLocationEntity.getId());
        assertNotNull(imsLocationEntity.getImsLocationCode());
        assertNotNull(imsLocationEntity.getImsLocationName());
        assertNotNull(imsLocationEntity.getCreatedBy());
        assertNotNull(imsLocationEntity.getDescription());
        assertNotNull(imsLocationEntity.getUpdatedBy());
        assertNotNull(imsLocationEntity.getCreatedDate());
        assertNotNull(imsLocationEntity.getUpdatedDate());

    }
}
