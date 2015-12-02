package com.zhongli.happycity.model.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class UserAccount {

	private Long user_id;
	private String email;
	private String password;
	private boolean enabled;
	private Date created_on;
	private ArrayList<Role> roles;
	private String login_token;
	private long token_expire_date;

	public UserAccount() {
		this.enabled = false;
	}

	public UserAccount(String email, String password) {
		if (((email == null) || "".equals(email)) || (password == null)) {
			throw new IllegalArgumentException(
					"Cannot pass null or empty values to constructor");
		}
		this.created_on = new Date();
		this.email = email;
		this.password = password;
		this.enabled = false;
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

	public void setRoles(final ArrayList<Role> roles) {
		this.roles = roles;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

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

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getLogin_token() {
		return login_token;
	}

	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}

	public long getToken_expire_date() {
		return token_expire_date;
	}

	public void setToken_expire_date(long token_expire_date) {
		this.token_expire_date = token_expire_date;
	}

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
		final UserAccount user = (UserAccount) obj;
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
		return "UserAccount [user_id=" + user_id + ", email=" + email
				+ ", password=" + password + ", enabled=" + enabled
				+ ", created_on=" + created_on + ", roles=" + roles
				+ ", login_token=" + login_token + ", token_expire_date="
				+ token_expire_date + "]";
	}

}
