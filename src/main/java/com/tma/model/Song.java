package com.tma.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "song")
public class Song {
	@Id
	String id;
	private String name;
	private String gerne;
	
	public Song() {}
	
	public Song(String name, String gerne) {
		this.name = name;
		this.gerne = gerne;
	}
	
	public Song(SongWithoutId songWithoutId) {
		this.name =songWithoutId.getName();
		this.gerne =songWithoutId.getGerne();
	}
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.id = name;
	}	
	
	public String getGerne() {
		return this.gerne;
	}
	public void setGerne(String gerne) {
		this.id = gerne;
	}	
	
	@Override
	public String toString() {
		return "Song [id=" + id + ", name=" + name + ", gerne=" + gerne + "]";
	}
	
}
