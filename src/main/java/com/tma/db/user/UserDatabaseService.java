package com.tma.db.user;

import org.springframework.stereotype.Service;

import com.tma.model.WebUser;

@Service
public interface UserDatabaseService {

	/*
	 * This provide a User service for Controller Access to Any Kind of DataBase;
	 */
	public WebUser getUserDetails(String username) ;


}
