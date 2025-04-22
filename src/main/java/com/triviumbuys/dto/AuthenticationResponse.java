package com.triviumbuys.dto;

import java.util.Set;

import com.triviumbuys.entity.AdminAccess;

public class AuthenticationResponse {
    private String token;
    private String role;
    private String username; // ✅ New field
    private Set<AdminAccess> accessRights;
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Set<AdminAccess> getAccessRights() {
		return accessRights;
	}
	public void setAccessRights(Set<AdminAccess> accessRights) {
		this.accessRights = accessRights;
	}
    
    
}
