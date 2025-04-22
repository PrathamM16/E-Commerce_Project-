package com.triviumbuys.dto;

import com.triviumbuys.entity.AdminAccess;
import lombok.Data;
import java.util.Set;

@Data
public class AdminCreationRequest {
    private String name;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String address;
    private Set<AdminAccess> accessRights;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Set<AdminAccess> getAccessRights() {
		return accessRights;
	}
	public void setAccessRights(Set<AdminAccess> accessRights) {
		this.accessRights = accessRights;
	}
    
}
