package com.vn.smartdata.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.Role;
import com.vn.smartdata.repository.RoleRepository;


/**
 * Implementation of RoleManager interface.
 *
 * @author <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 */
@Service("roleManager")
public class RoleManager extends GenericManager<Role> {
	
	/** The role repository. */
	private RoleRepository roleRepository ;

	/**
	 * Sets the role repository.
	 *
	 * @param roleRepository the new role repository
	 */
	@Autowired
	public void setRoleRepository(RoleRepository roleRepository) {
		this.repository = roleRepository;
		this.roleRepository = roleRepository;
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public List<Role> getRoles(){
		return this.roleRepository.getRoles();
	}

	/**
	 * Gets the role by name.
	 *
	 * @param roleName the role name
	 * @return the role by name
	 */
	public Role getRoleByName(String roleName){
		return this.roleRepository.getRoleByName(roleName);
	}
}
