package org.recap.repository;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.repository.jpa.ImsLocationDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 25/Nov/2020
 */
public class ImsLocationDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Test
    public void testFindImsLocation() {
        List<ImsLocationEntity> imsLocationEntities = imsLocationDetailsRepository.findAll();
        assertNotNull(imsLocationEntities);
        assertNotNull(imsLocationEntities.get(0).getImsLocationCode());
    }
}
