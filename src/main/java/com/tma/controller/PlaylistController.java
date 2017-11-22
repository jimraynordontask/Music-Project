package com.tma.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.tma.db.playlist.PlaylistDatabaseService;
import com.tma.mainprocessing.PlaylistMainProcessing;
import com.tma.model.PlaylistWithoutId;
import com.tma.ref.WebConfigs;
import com.tma.ref.WebResponse;

@Controller
public class PlaylistController {
	
	ApplicationContext AppContext = new AnnotationConfigApplicationContext(WebConfigs.class);
	PlaylistMainProcessing playlistModelProcessing = (PlaylistMainProcessing) AppContext.getBean("playlistMainProcessing");
//	@Autowired
//	playlistDatabaseService databaseService;
//	@Autowired
//	ModelMainProcessing modelProcessing;
	
	public void setDatabaseService(PlaylistDatabaseService databaseService) {
		this.playlistModelProcessing.setDatabaseService(databaseService);
	};
	
	@PostMapping(value = "/api/playlists", headers = "Accept=application/json", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse addplaylist(@RequestBody(required = false) PlaylistWithoutId playlistToBeAddedNoId) {
		/*
		 * the post method handle the add playlist & upload. Use one JSON requestbody and 1
		 * multipart file Use JSON for requestbody
		 */
		return playlistModelProcessing.addPlaylist(playlistToBeAddedNoId);
	}

	@GetMapping(value = "/api/playlists", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse listAllplaylists() {
		/*
		 * the get handle the list all playlists. Only use Url, JSON request not allowed.
		 */
		// list all playlist, no exception
		return playlistModelProcessing.listAllPlaylists();
	}

	@GetMapping(value = "/api/playlists/{id}", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse getplaylist(@PathVariable(value = "id", required = false) String requestedGetplaylistID) {
		/*
		 * the get method handler for get one playlist request. Only use requestbody, JSON
		 * request not allowed.
		 */
		// show one playlist &link file
		return playlistModelProcessing.getPlaylist(requestedGetplaylistID);
	}

	@PutMapping(value = "api/playlists/{id}", headers = "Accept=application/json", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse editplaylist(@PathVariable(value = "id") String requestedEditplaylistID,
			@RequestBody(required = false) PlaylistWithoutId requestedEditplaylistNoId) {
		/*
		 * This method response a edit request. Not accept request as JSON.
		 */
		// edit database entry
		return playlistModelProcessing.editPlaylist(requestedEditplaylistID, requestedEditplaylistNoId);

	}

	@DeleteMapping(value = "/api/playlists/{id}", produces = "application/json;charset=utf-8")
	@ResponseBody
	public WebResponse deleteplaylist(@PathVariable(value = "id", required = false) String requestedplaylistId) {
		/*
		 * Remove playlist from database and file system. Not accept JSON request.
		 */
		return playlistModelProcessing.deletePlaylist(requestedplaylistId);
	}
	
}