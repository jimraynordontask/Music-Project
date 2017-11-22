package com.tma.model;

public class SongWithoutId {
	String name;
	String gerne;
	
	public SongWithoutId() {}

	public SongWithoutId(String name, String gerne) {
		this.name = name;
		this.gerne = gerne;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGerne() {
		return gerne;
	}

	public void setGerne(String gerne) {
		this.gerne = gerne;
	}

	@Override
	public String toString() {
		return "SongWithoutId [name=" + name + ", gerne=" + gerne + "]";
	};
}
