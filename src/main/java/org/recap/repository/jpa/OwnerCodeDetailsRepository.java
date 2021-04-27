package org.recap.repository.jpa;


import org.recap.model.jpa.OwnerCodeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * The interface Owner code details repository.
 */
public interface OwnerCodeDetailsRepository extends BaseRepository<OwnerCodeEntity> {

    /**
     * Find by owner code owner code entity.
     *
     * @param ownerCode the owner code
     * @return the owner code entity
     */
    OwnerCodeEntity findByOwnerCode(@Param("ownerCode") String ownerCode);

    /**
     * Find by owner code owner code entity.
     *
     * @param ownerCode the owner code
     * @return the owner code entity
     */
    @Query(value = "select ownerCodeEntity from OwnerCodeEntity ownerCodeEntity inner join ownerCodeEntity.institutionEntity ie where ie.institutionCode = :institutionCode and ownerCodeEntity.ownerCode = :ownerCode")
    OwnerCodeEntity findByOwnerCodeAndOwningInstitutionCode(@Param("ownerCode") String ownerCode, @Param("institutionCode") String institutionCode);

    /**
     * Find by owner code in list.
     *
     * @param ownerCodes the owner codes
     * @return the list
     */
    List<OwnerCodeEntity> findByOwnerCodeIn(List<String> ownerCodes);

/*
    @Query(value="select ownerCode from OwnerCodeEntity ownerCode where ownerCode.ownerCode =:ownerCode and ownerCode.recapDeliveryRestrictions LIKE ('%EDD%')")
   */

    @Query(value="select OC.* from OWNER_CODES_T OC join OWN_DELIVERY_MAPPING_T ODM on OC.OWNER_CODE_ID = ODM.OWNER_CODE_ID JOIN DELIVERY_CODES_T DC ON DC.DELIVERY_CODE_ID = ODM.DELIVERY_CODE_ID WHERE DC.DELIVERY_CODE = 'EDD' AND OC.OWNER_CODE=:ownerCode", nativeQuery = true)
    OwnerCodeEntity findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(@Param("ownerCode") String ownerCode);

    @Query(value="select DC.* from DELIVERY_CODES_T DC where DC.DELIVERY_CODE = :deliveryLocation and DC.DELIVERY_CODE_ID in (select ODM.DELIVERY_CODE_ID from OWN_DELIVERY_MAPPING_T ODM where ODM.OWNER_CODE_ID =:ownerCodeId and ODM.REQUESTING_INST_ID = :institutionId) ", nativeQuery = true)
            List<Object[]> findByOwnerCodeAndRequestingInstitution(@Param("ownerCodeId") Integer ownerCodeId, @Param("institutionId") Integer institutionId, @Param("deliveryLocation") String deliveryLocation);

}
