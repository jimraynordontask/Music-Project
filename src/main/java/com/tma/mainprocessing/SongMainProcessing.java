package com.tma.mainprocessing;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.tma.db.song.SongDatabaseService;
import com.tma.model.Song;
import com.tma.model.SongWithUrl;
import com.tma.model.SongWithoutId;
import com.tma.ref.Libraries;
import com.tma.ref.RequestType;
import com.tma.ref.ResponseStatus;
import com.tma.ref.WebResponse;

public class SongMainProcessing {

	@Autowired
	SongDatabaseService songDatabaseService;

	public void setDatabaseService(SongDatabaseService songDatabaseService) {
		this.songDatabaseService = songDatabaseService;
	};

	public WebResponse addSong(SongWithoutId songToBeAddedNoId, MultipartFile userFile) {
		Song songToBeAdded = new Song(songToBeAddedNoId);
		String id = "";
		try {
			if (songToBeAdded.getName() == null || songToBeAdded.getGerne() == null || userFile==null) {
				return new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Malformed Request!", null);
			}
		} catch (Exception jsonConversionException) {
			return new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Malformed Request!", null);
		}
		try {
			// get the file extension and check if it is mp3.
			String userFileOriginalName = userFile.getOriginalFilename();
			int posOfDot = userFileOriginalName.lastIndexOf(".") + 1;
			if (posOfDot < 0)
				return new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Invalid File Type!", null);
			String userFileOriginalExtension = userFileOriginalName.substring(posOfDot);
			if (!userFileOriginalExtension.equals("mp3"))
				return new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Invalid File Type!", null);

			// add song entry to database
			id = songDatabaseService.addSong(songToBeAdded);

			// Add the folder and file.
			String l = File.separator;
			File rootFilesFolder = new File("files");
			rootFilesFolder.mkdir();
			File folderToSaveUploadedFile = new File("files" + l + id);
			folderToSaveUploadedFile.mkdir();
			String pathToUploadedFile = "files" + l + id + l + "song.mp3";
			File uploadedFileOnServer = new File(pathToUploadedFile);
			userFile.transferTo(uploadedFileOnServer);
		} catch (Exception exception) {
			// clean up if something failed!
			if (id != "")
				removeSongById(id);
			exception.printStackTrace();
			return new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Song already exists!", null);
		}

		return (new WebResponse(RequestType.ADD, ResponseStatus.OK, null, null));
	}

	public WebResponse listAllSongs() {
		try {
			List<SongWithUrl> listSong = songDatabaseService.getAllSongs();
			return new WebResponse(RequestType.LIST, ResponseStatus.OK, null, listSong);
		} catch (RuntimeException DatabaseError) {
			return new WebResponse(RequestType.LIST, ResponseStatus.FAILED, "Database Error!", null);
		}
	}

	public WebResponse getSong(String requestedGetSongID) {
		if (requestedGetSongID != null && requestedGetSongID != "") {
			try {
				Song songMatchedInDatabase = songDatabaseService.getSong(requestedGetSongID);
				SongWithUrl songMatchedInDatabseWithUrl = new SongWithUrl(songMatchedInDatabase);
				return (new WebResponse(RequestType.GET, ResponseStatus.OK, null, songMatchedInDatabseWithUrl));
			} catch (Exception databaseException) {
				return (new WebResponse(RequestType.GET, ResponseStatus.FAILED, "No song found!", null));
			}
		} else
			return (new WebResponse(RequestType.GET, ResponseStatus.FAILED, "Malformed Request!", null));
	}

	public WebResponse editSong(String requestedEditSongID, SongWithoutId requestedEditSongNoId) {
		Song requestedEditSong = new Song(requestedEditSongNoId);
		System.out.println("Song Received:" + requestedEditSong.toString());
		/*
		 * This method response a edit request. Not accept request as JSON.
		 */
		// edit database entry
		try {
			if (requestedEditSongID == null || requestedEditSongID == "" || requestedEditSong == null
					|| requestedEditSong.getName() == null || requestedEditSong.getGerne() == null)
				return new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Malformed Request!", null);
			if (songDatabaseService.updateSong(requestedEditSongID, requestedEditSong.getName(),
					requestedEditSong.getGerne())) {
				return new WebResponse(RequestType.EDIT, ResponseStatus.OK, null, null);
			} else {
				return new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Song not found!", null);
			}
		} catch (Exception databaseException) {
			databaseException.printStackTrace();
			return new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Database Error!", null);
		}
	}

	public WebResponse deleteSong(String requestedSongId) {
		/*
		 * Remove song from database and file system. Not accept JSON request.
		 */
		try {
			Song requestedSong = Libraries.jsonToSong(requestedSongId);
			if (requestedSong == null)
				return new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "Song not found!", null);
			Song songNeedDeletation = songDatabaseService.getSong(requestedSong.getId());
			try {
				// delete the file and folder
				File file = Libraries.pathCreator(songNeedDeletation.getId());
				if (!file.delete())
					throw new RuntimeException("File not deleted!");
			} catch (RuntimeException FileException) {
				return new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "File not deleted!", null);
			}
			try {
				// Remove database entry.
				songDatabaseService.deleteSong(requestedSong.getId());
				return new WebResponse(RequestType.DELETE, ResponseStatus.OK, null, null);
			} catch (RuntimeException databaseException) {
				return new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "Database Entry not deleted!", null);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return new WebResponse(RequestType.DELETE, ResponseStatus.OK, null, null);
		}
	}

	public void removeSongById(String id) {
		try {
			songDatabaseService.deleteSongById(id);
			File file = Libraries.pathCreator(id);
			file.delete();
		} catch (Exception exception) {
		}
	}
}
