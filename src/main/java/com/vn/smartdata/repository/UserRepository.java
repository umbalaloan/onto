package com.vn.smartdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vn.smartdata.model.User;


/**
 * The Interface UserRepository.
 */
public interface UserRepository extends GenericRepository<User> {
	
	/**
	 * Gets the all users.
	 *
	 * @return the all users
	 */
	@Query("select u from User u left join fetch u.roles")
	public List<User> getAllUsers();
	
	/**
	 * Gets the user by user id.
	 *
	 * @param userId the user id
	 * @return the user by user id
	 */
	@Query("select u from User u left join fetch u.roles where u.id = :id")
	public User getUserByUserId(@Param("id") Long userId);
	
	/**
	 * Gets the user by user name.
	 *
	 * @param username the username
	 * @return the user by user name
	 */
	@Query("select u from User u left join fetch u.roles where u.username = :username")
	public User getUserByUserName(@Param("username") String username);
	
	/**
	 * Gets the user by id.
	 *
	 * @param id the id
	 * @return the user by id
	 */
	@Query("select u from User u left join fetch u.roles where u.id = :id")
	public User getUserById(@Param("id") Long id);
	
	/**
	 * Gets the user by role.
	 *
	 * @param roleId the role id
	 * @return the user by role
	 */
	@Query("select u from User u  join u.roles uroles where uroles.id = :roleId")
	public List<User> getUserByRole(@Param("roleId") Long roleId);
}
