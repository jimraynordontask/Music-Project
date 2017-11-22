package com.tma.db.song;

import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.tma.model.Song;
import com.tma.model.SongWithUrl;
import com.tma.ref.Libraries;
import com.tma.repository.SongRepository;

public class MongoSongServiceImpl implements SongDatabaseService {
	@Autowired
	SongRepository songRepository;

	@Override
	public List<SongWithUrl> getAllSongs() {
		Iterable<Song>iterableSong =  songRepository.findAll();
		List<Song> listSong = Libraries.iterableToList(iterableSong);
		List<SongWithUrl> listSongWithUrl = new LinkedList<SongWithUrl>();
		for (Song song: listSong) listSongWithUrl.add(new SongWithUrl(song));
		return listSongWithUrl;
	}

	@Override
	public String addSong(Song newSong) {
		Song savedSong = songRepository.save(newSong);
		return savedSong.getId();
	}

	@Override
	public Song getSong(String id) {
		return songRepository.findById(id).get();
	}

	@Override
	public boolean updateSong(String id, String newName, String newGerne) {
		Song song = songRepository.findById(id).get();
		if (song != null) {
			song.setName(newName);
			song.setGerne(newGerne);
			songRepository.save(song);
			return true;
		} else
			return false;
	}

	@Override
	public boolean deleteSong(String id) {
		Song song = songRepository.findById(id).get();
		if (song != null) {
			songRepository.delete(song);
			return true;
		} else
			return false;
	}

	@Override
	public long countSongs() {
		return songRepository.count();
	}

	@Override
	public void deleteSongById(String id) {
		songRepository.deleteById(id);
	}

}
