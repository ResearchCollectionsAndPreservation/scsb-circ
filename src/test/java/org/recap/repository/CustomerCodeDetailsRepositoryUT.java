package org.recap.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

@Ignore
public class CustomerCodeDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Test
    public void testFindGenericPatron() {
        String customerCode = "PA";
        String institutionCode = "PUL";
        CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionCode(customerCode, institutionCode);
        assertNotNull(customerCodeEntity);
    }
}
