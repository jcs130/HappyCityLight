package com.zhongli.happycity.model.user;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

	// ~ Instance fields
	// ================================================================================================
	private Long user_id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	// private Set<GrantedAuthority> authorities;
	private boolean enabled;
	private Date created_on;
	// private String nickName;
	private Collection<Role> roles;

	private boolean tokenExpired;

	// ~ Constructors
	// ===================================================================================================
	public User() {
		this.enabled = false;
		this.tokenExpired = false;
	}

	public User(String email, String password) {
		if (((email == null) || "".equals(email)) || (password == null)) {
			throw new IllegalArgumentException(
					"Cannot pass null or empty values to constructor");
		}
		this.created_on = new Date();
		this.email = email;
		this.password = password;
		this.enabled = false;
		this.tokenExpired = false;
		// this.authorities = getAuthorities(roles);
		// this.authorities = Collections
		// .unmodifiableSet(sortAuthorities(getGrantedAuthorities_Roles_and_Privileges(roles)));
	}

	// ~ Methods
	// ========================================================================================================
	public Long getUserId() {
		return user_id;
	}

	public void setUserId(final Long user_id) {
		this.user_id = user_id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String username) {
		this.email = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(final Collection<Role> roles) {
		this.roles = roles;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isTokenExpired() {
		return tokenExpired;
	}

	public void setTokenExpired(final boolean expired) {
		this.tokenExpired = expired;
	}

	// public Collection<GrantedAuthority> getAuthorities() {
	// return authorities;
	// }

	public String getUsername() {
		return email;
	}

	public void eraseCredentials() {
		password = null;
	}

	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	// private static SortedSet<GrantedAuthority> sortAuthorities(Collection<?
	// extends GrantedAuthority> authorities) {
	// Assert.notNull(authorities, "Cannot pass a null GrantedAuthority
	// collection");
	// // Ensure array iteration order is predictable (as per
	// // UserDetails.getAuthorities() contract and SEC-717)
	// SortedSet<GrantedAuthority> sortedAuthorities = new
	// TreeSet<GrantedAuthority>(new AuthorityComparator());
	//
	// for (GrantedAuthority grantedAuthority : authorities) {
	// Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain
	// any null elements");
	// sortedAuthorities.add(grantedAuthority);
	// }
	//
	// return sortedAuthorities;
	// }

	// private static class AuthorityComparator implements
	// Comparator<GrantedAuthority>, Serializable {
	// private static final long serialVersionUID =
	// SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	//
	// public int compare(GrantedAuthority g1, GrantedAuthority g2) {
	// // Neither should ever be null as each entry is checked before
	// // adding it to the set.
	// // If the authority is null, it is a custom authority and should
	// // precede others.
	// if (g2.getAuthority() == null) {
	// return -1;
	// }
	//
	// if (g1.getAuthority() == null) {
	// return 1;
	// }
	//
	// return g1.getAuthority().compareTo(g2.getAuthority());
	// }
	// }

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final User user = (User) obj;
		if (!email.toLowerCase().equals(user.email.toLowerCase())) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the hashcode of the {@code username}.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", email=" + email
				+ ", password= [Protected]" + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", enabled=" + enabled
				+ ", created_on=" + created_on + ", roles=" + roles
				+ ", tokenExpired=" + tokenExpired + "]";
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
