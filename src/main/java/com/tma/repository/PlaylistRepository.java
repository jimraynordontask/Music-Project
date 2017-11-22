package com.tma.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tma.model.Playlist;

@Repository
public interface PlaylistRepository extends PagingAndSortingRepository<Playlist, String>{

	@Query("{'name':?0}")
	Playlist findByName(String name);
}
