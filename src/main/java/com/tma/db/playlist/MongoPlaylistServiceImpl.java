package com.tma.db.playlist;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.tma.db.playlist.PlaylistDatabaseService;
import com.tma.model.Playlist;
import com.tma.ref.Libraries;
import com.tma.repository.PlaylistRepository;

public class MongoPlaylistServiceImpl implements PlaylistDatabaseService {
	@Autowired
	PlaylistRepository playlistRepository;

	@Override
	public List<Playlist> getAllPlaylist() {
		Iterable<Playlist> iterablePlaylist = playlistRepository.findAll();
		System.out.println(iterablePlaylist);
		List<Playlist> listPlaylist = Libraries.iterableToList(iterablePlaylist);
		return listPlaylist;
	}

	@Override
	public String addPlaylist(Playlist newPlaylist) {
		Playlist savedPlaylist = playlistRepository.save(newPlaylist);
		return savedPlaylist.getId();
	}

	@Override
	public Playlist getPlaylist(String id) {
		return playlistRepository.findById(id).get();
	}

	@Override
	public boolean updatePlaylist(String id, String newName, String newAuthor, List<String> newListSongId) {
		Playlist playlist = playlistRepository.findById(id).get();
		if (playlist != null) {
			playlist.setName(newName);
			playlist.setAuthor(newAuthor);
			playlistRepository.save(playlist);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deletePlaylist(String id) {
		Playlist playlist = playlistRepository.findById(id).get();
		if (playlist != null) {
			playlistRepository.delete(playlist);
			return true;
		} else
			return false;
	}

	@Override
	public long countPlaylists() {
		return playlistRepository.count();
	}

	@Override
	public void deletePlaylistById(String id) {
		playlistRepository.deleteById(id);
	}

}
