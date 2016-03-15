package com.vn.smartdata.model;


import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;


/**
 * The Class Role.
 */
@Entity
@Table(name = "role")
@NamedQueries({ @javax.persistence.NamedQuery(name = "findRoleByName", query = "select r from Role r where r.name = :name ") })
public class Role extends BaseEntity implements Serializable, GrantedAuthority {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3690197650654049848L;
	
	/** The name. */
	private String name;
	
	/** The description. */
	private String description;
	
	/** The users. */
	@ManyToMany(fetch=FetchType.EAGER)
	@JsonIgnore
	@JoinTable(name="user_role",
	inverseJoinColumns={ @JoinColumn(name="user_id")} ,
	joinColumns={@JoinColumn(name="role_id")})
	private List<User> users;

	/**
	 * Instantiates a new role.
	 */
	public Role() {
	}

	/**
	 * Instantiates a new role.
	 *
	 * @param name the name
	 */
	public Role(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.GrantedAuthority#getAuthority()
	 */
	@Transient
	public String getAuthority() {
		return getName();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Column(length = 20)
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Column(length = 64)
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see com.vn.smartdata.model.BaseEntity#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Role)) {
			return false;
		}

		Role role = (Role) o;

		return this.name != null ? this.name.equals(role.name)
				: role.name == null;
	}

	/* (non-Javadoc)
	 * @see com.vn.smartdata.model.BaseEntity#hashCode()
	 */
	public int hashCode() {
		return this.name != null ? this.name.hashCode() : 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append(this.name).toString();
	}
}
