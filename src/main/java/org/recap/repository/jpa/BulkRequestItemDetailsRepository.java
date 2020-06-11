package org.recap.repository.jpa;

import org.recap.model.jpa.BulkRequestItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 10/10/17.
 */
public interface BulkRequestItemDetailsRepository extends JpaRepository<BulkRequestItemEntity, Integer> {
}
