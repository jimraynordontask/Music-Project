package com.tma.model;

import java.io.File;

public class SongWithUrl extends Song{
	String url;
	public SongWithUrl(Song song){
		super(song.getName(),song.getGerne());
		this.id = song.getId();
		String l = File.separator;
		this.url = "files"+l+id+l+"song.mp3";
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public void setId(String id) {
		this.id = id;
		String l = File.separator;
		this.url = "files"+l+id+l+"song.mp3";
	}
}
