package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.GenericPatronEntity;
import org.recap.repository.jpa.GenericPatronDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class GenericPatronDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    GenericPatronDetailsRepository genericPatronDetailsRepository;

    @Test
    public void testFindGenericPatron() {
        String requestingInstitutionCode = "PUL";
        String owningInstitutionCode = "NYPL";
        GenericPatronEntity genericPatronEntity = genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(requestingInstitutionCode, owningInstitutionCode);
        assertNotNull(genericPatronEntity);
        assertNotNull(genericPatronEntity.getEddGenericPatron());
        assertNotNull(genericPatronEntity.getRetrievalGenericPatron());
    }
}
