package com.tma.db.song;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.tma.model.Song;
import com.tma.model.SongWithUrl;

@Repository
@Service
public interface SongDatabaseService{
	/*
	 * This provide a service for Controller Access to Any Kind of DataBase;
	 */
	public List<SongWithUrl> getAllSongs();

	public String addSong(Song newSong);

	public Song getSong(String id);

	public boolean updateSong(String id, String newName, String newGerne);

	public boolean deleteSong(String id);

	public long countSongs();
	
	public void deleteSongById(String id);
}
