package com.appdeveloperblog.app.ws.shared.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class UserDTO implements Serializable{

	private static final long serialVersionUID = 1647320396292434683L;
	private long id; 
	private String userId; 
	private String firstName; 
	private String lastName; 
	private String password;  
	private String encryptedPassword;
	private String email;
	private String emailVerificationToken; 
	private Boolean emailVerificationStatus = Boolean.FALSE;
	private List<AddressDTO> addresses; 
	private Collection<String> roles; 
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}
	
	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}
	
	public Boolean getEmailVerificationStatus() {
		return emailVerificationStatus;
	}
	
	public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
		this.emailVerificationStatus = emailVerificationStatus;
	}

	public List<AddressDTO> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressDTO> addresses) {
		this.addresses = addresses;
	}

	public Collection<String> getRoles() {
		return roles;
	}

	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}	
}
