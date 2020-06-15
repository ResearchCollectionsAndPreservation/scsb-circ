package org.recap.repository.jpa;

import org.recap.model.jpa.RequestTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 28/10/16.
 */
public interface RequestTypeDetailsRepository extends BaseRepository<RequestTypeEntity> {
    /**
     * Find byrequest type code request type entity.
     *
     * @param requestTypeCode the request type code
     * @return the request type entity
     */
    RequestTypeEntity findByrequestTypeCode(String requestTypeCode);

}
