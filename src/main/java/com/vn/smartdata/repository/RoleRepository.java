package com.vn.smartdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vn.smartdata.model.Role;


/**
 * The Interface RoleRepository.
 */
public interface RoleRepository extends GenericRepository<Role> {
	
	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	@Query("select r from Role r")
	public List<Role> getRoles();

	/**
	 * Gets the role by name.
	 *
	 * @param roleName the role name
	 * @return the role by name
	 */
	@Query("select r from Role r where r.name = :roleName")
	public Role getRoleByName(@Param("roleName") String roleName);

	/*@Modifying
	@Transactional
	@Query(nativeQuery=true, value= "DELETE FROM [ithelpdesk].[dbo].[user_role] WHERE user_id=:userId AND role_id=:roleId")
	public void deleteRoleUser(@Param("userId") Long UserId, @Param("roleId") Long RoleId);*/
	
	/*@Query("select count(*) from Role r where r.roleName =:roleName")
	public int countSimilarRoleName(@Param("roleName") String roleName);*/
}
