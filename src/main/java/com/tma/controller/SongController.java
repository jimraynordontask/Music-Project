package com.tma.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tma.db.song.SongDatabaseService;
import com.tma.mainprocessing.SongMainProcessing;
import com.tma.model.SongWithoutId;
import com.tma.ref.WebConfigs;
import com.tma.ref.WebResponse;

@Controller
public class SongController {
	
	ApplicationContext AppContext = new AnnotationConfigApplicationContext(WebConfigs.class);
	SongMainProcessing songModelProcessing = (SongMainProcessing) AppContext.getBean("songMainProcessing");
//	@Autowired
//	SongDatabaseService databaseService;
//	@Autowired
//	ModelMainProcessing modelProcessing;
	
	public void setDatabaseService(SongDatabaseService databaseService) {
		this.songModelProcessing.setDatabaseService(databaseService);
	};
	
	@PostMapping(value = "/api/songs", headers = "Accept=application/json", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse addSong(@RequestBody(required = false) SongWithoutId songToBeAddedNoId,
			@RequestParam(value = "file", required = false) MultipartFile userFile) {
		/*
		 * the post method handle the add song & upload. Use one JSON requestbody and 1
		 * multipart file Use JSON for requestbody
		 */
		return songModelProcessing.addSong(songToBeAddedNoId, userFile);
	}

	@GetMapping(value = "/api/songs", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse listAllSongs() {
		/*
		 * the get handle the list all songs. Only use Url, JSON request not allowed.
		 */
		// list all song, no exception
		return songModelProcessing.listAllSongs();
	}

	@GetMapping(value = "/api/songs/{id}", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse getSong(@PathVariable(value = "id", required = false) String requestedGetSongID) {
		/*
		 * the get method handler for get one song request. Only use requestbody, JSON
		 * request not allowed.
		 */
		// show one song &link file
		return songModelProcessing.getSong(requestedGetSongID);
	}

	@PutMapping(value = "api/songs/{id}", headers = "Accept=application/json", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse editSong(@PathVariable(value = "id") String requestedEditSongID,
			@RequestBody(required = false) SongWithoutId requestedEditSongNoId) {
		/*
		 * This method response a edit request. Not accept request as JSON.
		 */
		// edit database entry
		return songModelProcessing.editSong(requestedEditSongID, requestedEditSongNoId);

	}

	@DeleteMapping(value = "/api/songs/{id}", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse deleteSong(@PathVariable(value = "id", required = false) String requestedSongId) {
		/*
		 * Remove song from database and file system. Not accept JSON request.
		 */
		return songModelProcessing.deleteSong(requestedSongId);
	}
	
}