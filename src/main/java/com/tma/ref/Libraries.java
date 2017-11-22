package com.tma.ref;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tma.model.Playlist;
import com.tma.model.Song;

public class Libraries {
	/*
	 * This class's method shorten the length of response and remove unnecessary null fields;
	 * Also convert Json String to Object and vice-versa;
	 */
	static ObjectMapper jsonObj = new ObjectMapper();
	//static Gson gson = new Gson();
	public static String songToJson(Song song) {
		try {
			return jsonObj.writeValueAsString(song);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public static Song jsonToSong(String json) {
		Song result;
		try {
			result = jsonObj.readValue(json, Song.class);
			return result;
		} catch (Exception e) {
			return new Song(null,null);
		}
	}

	public static String responseToJson(WebResponse response) {
		try {
			return jsonObj.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public static WebResponse jsonToResponse(String json) {
		WebResponse result;
		try {
			result = jsonObj.readValue(json, WebResponse.class);
			return result;
		} catch (Exception e) {
			return new WebResponse(null,ResponseStatus.FAILED,"JSON parse failed!",null);
		}
	}
	
	public static File pathCreator(String id) {
		Path resultPath = Paths.get("files");
		resultPath = resultPath.resolve(id);
		resultPath = resultPath.resolve("song.mp3");
		return resultPath.toFile();
	}
	
	public static <T> List<T> iterableToList(Iterable<T> iterableInput){
		List<T> listResult = new LinkedList<T>();
		for(T item:iterableInput)listResult.add(item);
		return listResult;
	}

	public static Playlist jsonToPlaylist(String requestedPlaylistId) {
		try {
			return jsonObj.readValue(requestedPlaylistId, Playlist.class);
		} catch (Exception e) {
			return new Playlist(null,null,null,null);
		}
	};
	
//	public String tokenCreator() {
//		Random rand = new Random();
//		int a = rand.nextInt(256);
//		String result = "";
//		return result;
//	};

}
