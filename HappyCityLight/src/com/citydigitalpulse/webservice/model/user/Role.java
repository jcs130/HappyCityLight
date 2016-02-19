/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Zhongli Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.webservice.model.user;

import java.io.Serializable;
import java.util.ArrayList;

public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private ArrayList<Privilege> privileges;

	public Role(final int role_id) {
		super();
		this.id = role_id;
	}

	public Role(final String name) {
		super();
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public ArrayList<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(ArrayList<Privilege> privileges) {
		this.privileges = privileges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
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
		final Role role = (Role) obj;
		if (!(this.name.equals(role.name) || this.id == role.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", privileges="
				+ privileges + "]";
	}

}