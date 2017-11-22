package com.tma.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tma.model.Song;

@Repository
public interface SongRepository extends PagingAndSortingRepository<Song, String>{

	@Query("{'name':?0}")
	Song findByName(String name);
}
