package org.recap.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 17/11/16.
 */
@Slf4j
public class ItemStatusDetailsRepositoryUT extends BaseTestCase {


    @Autowired
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Test
    public void testItemStatus() {
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findById(1).orElse(null);
        log.info(itemStatusEntity.getStatusCode());
        assertNotNull(itemStatusEntity);
        assertEquals("Available", itemStatusEntity.getStatusCode());
    }
}
