package org.recap.repository.jpa;

import org.recap.model.jpa.UsersEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author dinakar on 16/04/21
 */
public interface UserDetailRepository extends BaseRepository<UsersEntity>{

    /**
     *
     * @param username
     * @return the Institution Name
     */
    @Query(value = "SELECT INSTITUTION_CODE " +
            "FROM USER_T    " +
            "INNER JOIN  INSTITUTION_T ON USER_T.USER_INSTITUTION = INSTITUTION_T.INSTITUTION_ID    " +
            "WHERE USER_T.LOGIN_ID = :username",nativeQuery = true)
    String findInstitutionCodeByUserName(@Param("username") String username);

    /**
     * Gets ROles based on the given userName
     *
     * @param userName the login id
     * @return the RoleEntity
     */
    @Query(value="SELECT ROLE_NAME  " +
            "FROM USER_T    " +
            "INNER JOIN  USER_ROLE_T ON USER_T.USER_ID = USER_ROLE_T.USER_ID    " +
            "INNER JOIN ROLES_T ON USER_ROLE_T.ROLE_ID = ROLES_T.ROLE_ID    " +
            "WHERE USER_T.LOGIN_ID = :userName",nativeQuery = true)
    List<String> getUserRoles(@Param("userName") String userName);
}
