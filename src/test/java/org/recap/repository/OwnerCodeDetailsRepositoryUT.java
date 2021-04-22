package org.recap.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.OwnerCodeEntity;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

@Ignore
public class OwnerCodeDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Test
    public void testFindGenericPatron() {
        String ownerCode = "PA";
        String institutionCode = "PUL";
        OwnerCodeEntity ownerCodeEntity = ownerCodeDetailsRepository.findByOwnerCodeAndOwningInstitutionCode(ownerCode, institutionCode);
        assertNotNull(ownerCodeEntity);
    }
}
