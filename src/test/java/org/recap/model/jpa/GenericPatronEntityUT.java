package org.recap.model.jpa;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class GenericPatronEntityUT {

    @Test
    public void getGenericPatronEntity(){
        GenericPatronEntity genericPatronEntity = new GenericPatronEntity();
        genericPatronEntity.setGenericPatronId(1);
        genericPatronEntity.setRetrievalGenericPatron("173623e34");
        genericPatronEntity.setEddGenericPatron("233455");
        genericPatronEntity.setGenericPatronId(123456);
        genericPatronEntity.setCreatedBy("test");
        genericPatronEntity.setCreatedDate(new Date());
        genericPatronEntity.setItemOwningInstitutionId(1);
        genericPatronEntity.setOwningInstitutionEntity(new InstitutionEntity());
        genericPatronEntity.setRequestingInstitutionEntity(new InstitutionEntity());
        genericPatronEntity.setRequestingInstitutionId(1);
        genericPatronEntity.setUpdatedBy("test");
        genericPatronEntity.setUpdatedDate(new Date());
        GenericPatronEntity genericPatronEntity1 = new GenericPatronEntity();
        genericPatronEntity.equals(genericPatronEntity1);
        genericPatronEntity.equals(genericPatronEntity);
        genericPatronEntity1.equals(genericPatronEntity);
        genericPatronEntity.canEqual(genericPatronEntity);
        genericPatronEntity1.canEqual(genericPatronEntity);
        genericPatronEntity.hashCode();
        assertNotEquals(genericPatronEntity, null);
        assertNotNull(genericPatronEntity.getCreatedBy());
        assertNotNull(genericPatronEntity.getCreatedDate());
        assertNotNull(genericPatronEntity.getEddGenericPatron());
        assertNotNull(genericPatronEntity.getGenericPatronId());
        assertNotNull(genericPatronEntity.getItemOwningInstitutionId());
        assertNotNull(genericPatronEntity.getOwningInstitutionEntity());
        assertNotNull(genericPatronEntity.getRequestingInstitutionEntity());
        assertNotNull(genericPatronEntity.getRequestingInstitutionId());
        assertNotNull(genericPatronEntity.getRetrievalGenericPatron());
        assertNotNull(genericPatronEntity.getUpdatedBy());
        assertNotNull(genericPatronEntity.getUpdatedDate());
        assertEquals(genericPatronEntity1, genericPatronEntity1);
        assertNotEquals(genericPatronEntity, genericPatronEntity1);
    }
}
