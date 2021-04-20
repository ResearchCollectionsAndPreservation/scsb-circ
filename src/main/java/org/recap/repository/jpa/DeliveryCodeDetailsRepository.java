package org.recap.repository.jpa;


import org.recap.model.jpa.DeliveryCodeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * The interface Delivery code details repository.
 */
public interface DeliveryCodeDetailsRepository extends BaseRepository<DeliveryCodeEntity> {

    /**
     * Find by delivery code delivery code entity.
     *
     * @param deliveryCode the delivery code
     * @return the delivery code entity
     */
    DeliveryCodeEntity findByDeliveryCode(@Param("deliveryCode") String deliveryCode);

}
