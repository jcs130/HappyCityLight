package com.zhongli.happycity.model.user;

import java.io.Serializable;

public class Privilege implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;

	private String name;
	private String description;

	public Privilege() {
		super();
	}

	public Privilege(final String name) {
		super();
		this.name = name;
	}

	//

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
		final Privilege privilege = (Privilege) obj;
		if (!privilege.equals(privilege.name)) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Privilege [id=" + id + ", name=" + name + ", description="
				+ description + "]";
	}

}
