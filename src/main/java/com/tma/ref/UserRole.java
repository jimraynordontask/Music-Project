package com.tma.ref;

public enum UserRole {
	ADMIN,USER;
	public String toTextStr() {
		return "ROLE_"+this.name();
	}
}
