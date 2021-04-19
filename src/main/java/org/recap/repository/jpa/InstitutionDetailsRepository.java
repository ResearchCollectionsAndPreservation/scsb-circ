package org.recap.repository.jpa;

import org.recap.model.jpa.InstitutionEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface InstitutionDetailsRepository extends BaseRepository<InstitutionEntity> {

    /**
     * Find by institution code institution entity.
     *
     * @param institutionCode the institution code
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionCode(String institutionCode);

    /**
     * Find by institution name institution entity.
     *
     * @param institutionName the institution name
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionName(String institutionName);

    /**
     * Check if exists by institution code institution entity.
     *
     * @param institutionCode the institution code
     * @return the institution entity
     */
    boolean existsByInstitutionCode(String institutionCode);

    @Query(value = "select INSTITUTION_CODE from institution_t where INSTITUTION_CODE != 'HTC';",nativeQuery = true)
    List<String> findAllInstitutionCodeExceptHTC();

    /**
     * To get the list of institution entities for home page.
     *
     * @return the institutions
     */
    @Query(value="select inst from InstitutionEntity inst  where inst.institutionCode not in ('HTC') ORDER BY inst.id")
    List<InstitutionEntity> getCodes();
}
