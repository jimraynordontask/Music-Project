package com.tma.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "playlist")
public class Playlist {
	@Id
	String id;
	String name;
	String author;
	List<String> listSongId;

	public Playlist() {};
	
	public Playlist(String id, String name, String authorName, List<String> listSongId) {
		super();
		this.id = id;
		this.name = name;
		this.author = authorName;
		this.listSongId = listSongId;
	}

	public Playlist(PlaylistWithoutId playlistNoId) {
		this.name= playlistNoId.getName();
		this.author = playlistNoId.getAuthor();
		this.listSongId = playlistNoId.getListSongId();
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return "Playlist [id=" + id + ", name=" + name + ", author=" + author + ", listSongId=" + listSongId
				+ "]";
	}

	
	;
};
