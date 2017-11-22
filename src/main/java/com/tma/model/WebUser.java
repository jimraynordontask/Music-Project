package com.tma.model;

import java.util.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.tma.ref.UserRole;

@Document(collection = "user")
public class WebUser {
	@Id
	String id;
	@Indexed(unique = true)
	String name;
	String password;
	UserRole role;
	List<SimpleGrantedAuthority> roles = new LinkedList<SimpleGrantedAuthority>();

	public WebUser(String name, String password, UserRole role) {
		this.name = name;
		this.password = password;
		this.role = role;
		this.roles.add(new SimpleGrantedAuthority(role.toTextStr()));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public List<SimpleGrantedAuthority> getRoles() {
		return roles;
	}

	public void setRoles(List<SimpleGrantedAuthority> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "UserDetail [name=" + name + ", password=" + password + ", roles=" + roles + "]";
	}
}
