package com.tma.ref;

public enum RequestType {
	ADD, GET, LIST, EDIT, DELETE;
	public String toTextStr() {
		return this.name().toLowerCase();
	}
}
