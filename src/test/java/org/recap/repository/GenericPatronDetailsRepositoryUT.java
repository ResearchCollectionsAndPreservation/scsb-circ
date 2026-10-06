package org.recap.repository;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.GenericPatronDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class GenericPatronDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    GenericPatronDetailsRepository genericPatronDetailsRepository;

    @Test
    public void testFindGenericPatron() {
        String requestingInstitutionCode = "PUL";
        String owningInstitutionCode = "NYPL";
        /*GenericPatronEntity genericPatronEntity = genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(requestingInstitutionCode, owningInstitutionCode);
        assertNotNull(genericPatronEntity);
        assertNotNull(genericPatronEntity.getEddGenericPatron());
        assertNotNull(genericPatronEntity.getRetrievalGenericPatron());*/
    }
}
