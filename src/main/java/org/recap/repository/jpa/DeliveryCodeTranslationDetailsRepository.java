package org.recap.repository.jpa;


import org.recap.model.jpa.DeliveryCodeEntity;
import org.recap.model.jpa.DeliveryCodeTranslationEntity;
import org.recap.model.jpa.OwnerCodeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * The interface Owner code details repository.
 */
public interface DeliveryCodeTranslationDetailsRepository extends BaseRepository<DeliveryCodeTranslationEntity> {

    /**
     * Find by  DeliveryCodeTranslationEntity
     *
     * @param requestingInstitutionId the owner code
     * @return the DeliveryCodeTranslationEntity
     */
    @Query(value = "select * from DELIVERY_CODE_TRANSLATION_T where REQUESTING_INST_ID = :requestingInstitutionId and REQUESTING_INST_DELIVERY_CODE_ID = :deliveryCodeId and IMS_LOCATION_ID = :imsLocationCodeId" , nativeQuery = true)
    DeliveryCodeTranslationEntity findByRequestingInstitutionandImsLocation(@Param("requestingInstitutionId") Integer requestingInstitutionId, @Param("deliveryCodeId") Integer deliveryCodeId, @Param("imsLocationCodeId") Integer imsLocationCodeId);

}
