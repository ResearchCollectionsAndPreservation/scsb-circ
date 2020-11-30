package org.recap.repository.jpa;

import org.recap.model.jpa.GenericPatronEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenericPatronDetailsRepository extends JpaRepository<GenericPatronEntity, Integer> {

    /**
     * Find edd/retrieval generic patron RequestingInstitutionId And ItemOwningInstitutionId.
     *
     * @param requestingInstitutionCode the requesting Institution Code
     * @param owningInstitutionCode the item Owning InstitutionId Code
     * @return the GenericPatronEntity
     */
    @Query(value = "select genericPatronEntity from GenericPatronEntity genericPatronEntity inner join genericPatronEntity.requestingInstitutionEntity rie inner join genericPatronEntity.owningInstitutionEntity oie where rie.institutionCode = :requestingInstitutionCode and oie.institutionCode= :owningInstitutionCode ")
    GenericPatronEntity findByRequestingInstitutionCodeAndItemOwningInstitutionCode(@Param("requestingInstitutionCode") String requestingInstitutionCode, @Param("owningInstitutionCode") String owningInstitutionCode);


    @Query(value = "select genericPatronEntity from GenericPatronEntity genericPatronEntity inner join genericPatronEntity.owningInstitutionEntity oie where oie.institutionCode= :owningInstitutionCode ")
    GenericPatronEntity findByItemOwningInstitutionCode(@Param("owningInstitutionCode") String owningInstitutionCode);
}
