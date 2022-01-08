package com.appdeveloperblog.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appdeveloperblog.app.ws.shared.dto.UserDTO;

public interface UserService extends UserDetailsService{
	
	//GET
	List<UserDTO> getUsers(int page, int limit);
	UserDTO getUser(String email);
	UserDTO getUserByUserId(String userId); 
	Boolean verifyEmailToken(String token);
	
	//POST
	UserDTO createUser(UserDTO user);
	Boolean requestPasswordReset(String email);
	Boolean resetPassword(String token, String password);

	//PUT
	UserDTO updateUser(UserDTO user, String userId); 
	
	//DELETE
	void deleteUser(String userId); 
}
