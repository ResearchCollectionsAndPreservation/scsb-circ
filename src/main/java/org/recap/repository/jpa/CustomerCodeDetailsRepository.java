package org.recap.repository.jpa;


import org.recap.model.jpa.CustomerCodeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * The interface Customer code details repository.
 */
public interface CustomerCodeDetailsRepository extends BaseRepository<CustomerCodeEntity> {

    /**
     * Find by customer code customer code entity.
     *
     * @param customerCode the customer code
     * @return the customer code entity
     */
    CustomerCodeEntity findByCustomerCode(@Param("customerCode") String customerCode);

    /**
     * Find by customer code customer code entity.
     *
     * @param customerCode the customer code
     * @return the customer code entity
     */
    @Query(value = "select customerCodeEntity from CustomerCodeEntity customerCodeEntity inner join customerCodeEntity.institutionEntity ie where ie.institutionCode = :institutionCode and customerCodeEntity.customerCode = :customerCode ")
    CustomerCodeEntity findByCustomerCodeAndOwningInstitutionCode(@Param("customerCode") String customerCode, @Param("institutionCode") String institutionCode);

    /**
     * Find by customer code in list.
     *
     * @param customerCodes the customer codes
     * @return the list
     */
    List<CustomerCodeEntity> findByCustomerCodeIn(List<String> customerCodes);

    @Query(value="select customerCode from CustomerCodeEntity customerCode where customerCode.customerCode =:customerCode and customerCode.recapDeliveryRestrictions LIKE ('%EDD%')")
    CustomerCodeEntity findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(@Param("customerCode") String customerCode);
}
