package com.tma.ref;

public enum ResponseStatus {
	OK, FAILED;
	public String toTextStr() {
		return this.name().toLowerCase();
	}
}
