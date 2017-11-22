package com.tma.db.playlist;

import java.util.List;

import com.tma.model.Playlist;

public interface PlaylistDatabaseService {
	public List<Playlist> getAllPlaylist();

	public String addPlaylist(Playlist newPlaylist);

	public Playlist getPlaylist(String id);

	public boolean updatePlaylist(String id, String newName, String newGerne, List<String> listPlaylist);

	public boolean deletePlaylist(String id);

	public void deletePlaylistById(String id);

	long countPlaylists();

}
