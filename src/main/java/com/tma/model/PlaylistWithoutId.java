package com.tma.model;

import java.util.List;

public class PlaylistWithoutId {
	String name;
	String author;
	List<String> listSongId;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<String> getListSongId() {
		return listSongId;
	}

	public void setListSongId(List<String> listSongId) {
		this.listSongId = listSongId;
	}

	@Override
	public String toString() {
		return "Playlist ["+"name=" + name + ", author=" + author + ", listSongId=" + listSongId
				+ "]";
	}
}
