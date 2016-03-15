package com.vn.smartdata.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.appfuse.model.LabelValue;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * The Class User.
 */
@XmlRootElement
@Entity
@Table(name = "app_user")
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User extends BaseEntity implements Serializable, UserDetails {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3832626162173359411L;

	/** The username. */
	private String username;

	/** The password. */
	private String password;

	/** The confirm password. */
	@Transient
	@XmlTransient
	@JsonIgnore
	private String confirmPassword;

	/** The password hint. */
	@Column(name = "password_hint")
	@XmlTransient
	private String passwordHint;

	/** The first name. */
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	/** The last name. */
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	/** The email. */
	private String email;

	/** The phone number. */
	@Column(name = "phone_number")
	private String phoneNumber;

	/** The website. */
	private String website;

	/** The version. */
	@Column(name = "version", nullable = true, length = 11)
	private Integer version;

	/** The enabled. */
	@Column(name = "account_enabled")
	private boolean enabled;

	/** The account expired. */
	@Column(name = "account_expired", nullable = false)
	private boolean accountExpired;

	/** The account locked. */
	@Column(name = "account_locked", nullable = false)
	private boolean accountLocked;

	/** The credentials expired. */
	@Column(name = "credentials_expired")
	private boolean credentialsExpired;

	/** The address. */
	private String address;

	/** The city. */
	private String city;

	/** The province. */
	private String province;

	/** The country. */
	private String country;

	/** The postal code. */
	@Column(name = "postal_code", length = 15)
	private String postalCode;

	/** The roles. */
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	private Set<Role> roles = new HashSet<Role>();

	@OneToMany(mappedBy="creator")
	private List<Ontology> createdOntologies;

	/** The full name. */
	@Transient
	private String fullName;

	/**
	 * Instantiates a new user.
	 */
	public User() {
	}

	/**
	 * Instantiates a new user.
	 *
	 * @param username the username
	 */
	public User(final String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	@Override
	@Column(nullable = false, length = 50, unique = true)
	public String getUsername() {
		return this.username;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	@Override
	@Column(nullable = false)
	@XmlTransient
	@JsonIgnore
	public String getPassword() {
		return this.password;
	}

	/**
	 * Gets the confirm password.
	 *
	 * @return the confirm password
	 */
	public String getConfirmPassword() {
		return this.confirmPassword;
	}

	/**
	 * Gets the password hint.
	 *
	 * @return the password hint
	 */
	public String getPasswordHint() {
		return this.passwordHint;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	@Column(name = "email", nullable = false, unique = true)
	public String getEmail() {
		return this.email;
	}

	/**
	 * Gets the phone number.
	 *
	 * @return the phone number
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * Gets the website.
	 *
	 * @return the website
	 */
	public String getWebsite() {
		return this.website;
	}

	/**
	 * Gets the full name.
	 *
	 * @return the full name
	 */
	@Transient
	public String getFullName() {
		return this.firstName + ' ' + this.lastName;
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public Set<Role> getRoles() {
		return this.roles;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	@Column(length = 150)
	public String getAddress() {
		return this.address;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	@Column(length = 50)
	public String getCity() {
		return this.city;
	}

	/**
	 * Gets the province.
	 *
	 * @return the province
	 */
	@Column(length = 100)
	public String getProvince() {
		return this.province;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	@Column(length = 100)
	public String getCountry() {
		return this.country;
	}

	/**
	 * Gets the postal code.
	 *
	 * @return the postal code
	 */
	public String getPostalCode() {
		return this.postalCode;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(final String address) {
		this.address = address;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * Sets the postal code.
	 *
	 * @param postalCode the new postal code
	 */
	public void setPostalCode(final String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Sets the province.
	 *
	 * @param province the new province
	 */
	public void setProvince(final String province) {
		this.province = province;
	}

	/**
	 * Gets the role list.
	 *
	 * @return the role list
	 */
	@Transient
	public List<LabelValue> getRoleList() {
		final List<LabelValue> userRoles = new ArrayList();

		if (this.roles != null) {
			for (final Role role : this.roles) {
				userRoles.add(new LabelValue(role.getName(), role.getName()));
			}
		}

		return userRoles;
	}

	/**
	 * Adds the role.
	 *
	 * @param role the role
	 */
	public void addRole(final Role role) {
		this.getRoles().add(role);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
	 */
	@Override
	@Transient
	@JsonIgnore
	public Set<GrantedAuthority> getAuthorities() {
		final Set<GrantedAuthority> authorities = new LinkedHashSet();
		authorities.addAll(this.roles);
		return authorities;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	@Version
	public Integer getVersion() {
		return this.version;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Gets the enabled.
	 *
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return this.enabled;
	}

	/**
	 * Gets the account expired.
	 *
	 * @return the account expired
	 */
	public boolean getAccountExpired() {
		return this.accountExpired;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
	 */
	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return !this.getAccountExpired();
	}

	/**
	 * Gets the account locked.
	 *
	 * @return the account locked
	 */
	public boolean getAccountLocked() {
		return this.accountLocked;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
	 */
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return !this.getAccountLocked();
	}

	/**
	 * Gets the credentials expired.
	 *
	 * @return the credentials expired
	 */
	public boolean getCredentialsExpired() {
		return this.credentialsExpired;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return !this.credentialsExpired;
	}

	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Sets the confirm password.
	 *
	 * @param confirmPassword the new confirm password
	 */
	public void setConfirmPassword(final String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * Sets the password hint.
	 *
	 * @param passwordHint the new password hint
	 */
	public void setPasswordHint(final String passwordHint) {
		this.passwordHint = passwordHint;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Sets the phone number.
	 *
	 * @param phoneNumber the new phone number
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Sets the website.
	 *
	 * @param website the new website
	 */
	public void setWebsite(final String website) {
		this.website = website;
	}

	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(final Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(final Integer version) {
		this.version = version;
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enabled the new enabled
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Sets the account expired.
	 *
	 * @param accountExpired the new account expired
	 */
	public void setAccountExpired(final boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	/**
	 * Sets the account locked.
	 *
	 * @param accountLocked the new account locked
	 */
	public void setAccountLocked(final boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	/**
	 * Sets the credentials expired.
	 *
	 * @param credentialsExpired the new credentials expired
	 */
	public void setCredentialsExpired(final boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	/* (non-Javadoc)
	 * @see com.vn.smartdata.model.BaseEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User)) {
			return false;
		}

		final User user = (User) o;

		return this.username != null ? this.username.equals(user.getUsername())
				: user.getUsername() == null;
	}

	/* (non-Javadoc)
	 * @see com.vn.smartdata.model.BaseEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.username != null ? this.username.hashCode() : 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final ToStringBuilder sb = new ToStringBuilder(this,
				ToStringStyle.DEFAULT_STYLE).append("username", this.username)
				.append("enabled", this.enabled)
				.append("accountExpired", this.accountExpired)
				.append("credentialsExpired", this.credentialsExpired)
				.append("accountLocked", this.accountLocked);

		int i;

		if (this.roles != null) {
			sb.append("Granted Authorities: ");

			i = 0;
			for (final Role role : this.roles) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(role.toString());
				i++;
			}
		} else {
			sb.append("No Granted Authorities");
		}
		return sb.toString();
	}

	/**
	 * Sets the full name.
	 *
	 * @param fullName the new full name
	 */
	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}
}
