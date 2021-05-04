package org.recap.model;

import org.junit.Test;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.OwnerCodeEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 14/3/17.
 */
public class OwnerCodeEntityUT{

    @Test
    public void testCustomerCode(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        assertNotNull(institutionEntity);

        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setId(1);
        ownerCodeEntity.setOwnerCode("AB");
        ownerCodeEntity.setDescription("test");
        ownerCodeEntity.setInstitutionId(institutionEntity.getId());
        ownerCodeEntity.setInstitutionEntity(institutionEntity);
        ownerCodeEntity.equals(ownerCodeEntity);
        ownerCodeEntity.equals(null);

        assertNotNull(ownerCodeEntity.getId());
        assertEquals("AB", ownerCodeEntity.getOwnerCode());
        assertEquals("test", ownerCodeEntity.getDescription());
        assertNotNull(ownerCodeEntity.getInstitutionId());
        assertNotNull(ownerCodeEntity.getInstitutionEntity());

        OwnerCodeEntity ownerCodeEntity1 = new OwnerCodeEntity();
        ownerCodeEntity1.setId(2);
        ownerCodeEntity1.equals(ownerCodeEntity);
        OwnerCodeEntity ownerCodeEntity2 = new OwnerCodeEntity();
        ownerCodeEntity2.setId(1);
        ownerCodeEntity2.setDescription("RE");
        ownerCodeEntity2.equals(ownerCodeEntity);
        OwnerCodeEntity ownerCodeEntity3 = new OwnerCodeEntity();
        ownerCodeEntity3.setId(1);
        ownerCodeEntity3.setDescription("test");
        ownerCodeEntity3.setOwnerCode("OP");
        ownerCodeEntity3.equals(ownerCodeEntity);
        OwnerCodeEntity ownerCodeEntity4 = new OwnerCodeEntity();
        ownerCodeEntity4.setId(1);
        ownerCodeEntity4.setDescription("test");
        ownerCodeEntity4.setOwnerCode("AB");
        ownerCodeEntity4.setInstitutionId(5);
        ownerCodeEntity4.equals(ownerCodeEntity);

        ownerCodeEntity.hashCode();
        ownerCodeEntity1.compareTo(ownerCodeEntity2);
        //ownerCodeEntity1.equals(ownerCodeEntity2);
    }

}
