package com.tma.mainprocessing;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.tma.db.playlist.PlaylistDatabaseService;
import com.tma.model.Playlist;
import com.tma.model.PlaylistWithoutId;
import com.tma.ref.Libraries;
import com.tma.ref.RequestType;
import com.tma.ref.ResponseStatus;
import com.tma.ref.WebResponse;

public class PlaylistMainProcessing {

	@Autowired
	PlaylistDatabaseService playlistDatabaseService;

	public void setDatabaseService(PlaylistDatabaseService playlistDatabaseService) {
		this.playlistDatabaseService = playlistDatabaseService;
	};

	public WebResponse addPlaylist(PlaylistWithoutId playlistToBeAddedNoId) {
		Playlist playlistToBeAdded = new Playlist(playlistToBeAddedNoId);
		String id = "";
		System.out.println(playlistToBeAddedNoId);
		if (playlistToBeAdded.getName() == null || playlistToBeAdded.getAuthor() == null
				|| playlistToBeAdded.getListSongId() == null) {
			return new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Malformed Request!", null);
		}
		try {
			// add Playlist entry to database
			id = playlistDatabaseService.addPlaylist(playlistToBeAdded);
		} catch (Exception exception) {
			// clean up if something failed!
			if (id != "")
				removePlaylistById(id);
			exception.printStackTrace();
			return new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Playlist already exists!", null);
		}

		return (new WebResponse(RequestType.ADD, ResponseStatus.OK, null, null));
	}

	public WebResponse listAllPlaylists() {
		try {
			List<Playlist> listPlaylist = playlistDatabaseService.getAllPlaylist();
			return new WebResponse(RequestType.LIST, ResponseStatus.OK, null, listPlaylist);
		} catch (RuntimeException DatabaseError) {
			DatabaseError.printStackTrace();
			return new WebResponse(RequestType.LIST, ResponseStatus.FAILED, "Database Error!", null);
		}
	}

	public WebResponse getPlaylist(String requestedGetPlaylistID) {
		if (requestedGetPlaylistID != null && requestedGetPlaylistID != "") {
			try {
				Playlist PlaylistMatchedInDatabase = playlistDatabaseService.getPlaylist(requestedGetPlaylistID);
				return (new WebResponse(RequestType.GET, ResponseStatus.OK, null, PlaylistMatchedInDatabase));
			} catch (Exception databaseException) {
				return (new WebResponse(RequestType.GET, ResponseStatus.FAILED, "No playlist found!", null));
			}
		} else
			return (new WebResponse(RequestType.GET, ResponseStatus.FAILED, "Malformed Request!", null));
	}

	public WebResponse editPlaylist(String requestedEditPlaylistID, PlaylistWithoutId requestedEditPlaylistNoId) {
		Playlist requestedEditPlaylist = new Playlist(requestedEditPlaylistNoId);
		System.out.println("Playlist Received:" + requestedEditPlaylist.toString());
		/*
		 * This method response a edit request. Not accept request as JSON.
		 */
		// edit database entry
		try {
			if (requestedEditPlaylistID == null || requestedEditPlaylistID == "" || requestedEditPlaylist == null
					|| requestedEditPlaylist.getName() == null || requestedEditPlaylist.getAuthor() == null)
				return new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Malformed Request!", null);
			if (playlistDatabaseService.updatePlaylist(requestedEditPlaylistID, requestedEditPlaylist.getName(),
					requestedEditPlaylist.getAuthor(),requestedEditPlaylist.getListSongId())) {
				return new WebResponse(RequestType.EDIT, ResponseStatus.OK, null, null);
			} else {
				return new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Playlist not found!", null);
			}
		} catch (Exception databaseException) {
			databaseException.printStackTrace();
			return new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Database Error!", null);
		}
	}

	public WebResponse deletePlaylist(String requestedPlaylistId) {
		/*
		 * Remove Playlist from database and file system. Not accept JSON request.
		 */
		try {
			Playlist requestedPlaylist = Libraries.jsonToPlaylist(requestedPlaylistId);
			if (requestedPlaylist == null)
				return new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "Playlist not found!", null);
			Playlist PlaylistNeedDeletation = playlistDatabaseService.getPlaylist(requestedPlaylist.getId());
			try {
				// delete the file and folder
				File file = Libraries.pathCreator(PlaylistNeedDeletation.getId());
				if (!file.delete())
					throw new RuntimeException("File not deleted!");
			} catch (RuntimeException FileException) {
				return new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "File not deleted!", null);
			}
			try {
				// Remove database entry.
				playlistDatabaseService.deletePlaylist(requestedPlaylist.getId());
				return new WebResponse(RequestType.DELETE, ResponseStatus.OK, null, null);
			} catch (RuntimeException databaseException) {
				return new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "Database Entry not deleted!", null);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return new WebResponse(RequestType.DELETE, ResponseStatus.OK, null, null);
		}
	}

	public void removePlaylistById(String id) {
		try {
			playlistDatabaseService.deletePlaylistById(id);
		} catch (Exception exception) {
		}
	}
}
