package com.tma.db.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.tma.model.WebUser;

public class MongoUserDatabaseServiceImpl implements UserDatabaseService {
	@Autowired
	MongoOperations mongoOperation;

	@Override
	public WebUser getUserDetails(String username) {
		Query searchQuery = new Query(Criteria.where("name").is(username));
		WebUser result = mongoOperation.findOne(searchQuery, WebUser.class);
		return result;
	}

}
