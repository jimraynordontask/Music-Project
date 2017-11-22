package com.tma.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.*;

import com.tma.db.user.UserDatabaseService;
import com.tma.model.WebUser;

@Component
public class WebUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserDatabaseService userDetailService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("username: "+username);
		WebUser userDetails = userDetailService.getUserDetails(username);
		if(userDetails != null) {
			System.out.println("Found one!");
			return new User(userDetails.getName(),userDetails.getPassword(),userDetails.getRoles());
		}
		else {
			throw new UsernameNotFoundException("");
		}
	}
}