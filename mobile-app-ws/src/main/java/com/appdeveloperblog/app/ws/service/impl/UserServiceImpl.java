package com.appdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appdeveloperblog.app.ws.io.entity.PasswordResetTokenEntity;
import com.appdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repositories.PasswordResetTokenRepository;
import com.appdeveloperblog.app.ws.io.repositories.RoleRepository;
import com.appdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appdeveloperblog.app.ws.security.SecurityConstants;
import com.appdeveloperblog.app.ws.security.UserPrincipal;
import com.appdeveloperblog.app.ws.service.UserService;
import com.appdeveloperblog.app.ws.shared.AmazonSES;
import com.appdeveloperblog.app.ws.shared.Utils;
import com.appdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appdeveloperblog.app.ws.shared.dto.UserDTO;
import com.appdeveloperblog.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	Utils utils; 
	
	@Autowired
	UserRepository userRepository; 
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	AmazonSES amazonSES; 
	
	@Autowired
	RoleRepository roleRepository;
	
	//GET
	@Override
	public List<UserDTO> getUsers(int page, int limit) {
		List<UserDTO> returnValue = new ArrayList<>();
		
		if(page > 0) {
			page -= 1; 
		}
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		ModelMapper modelMapper = new ModelMapper();
		
		for(UserEntity userEntity: usersPage.getContent()) {
			UserDTO user = modelMapper.map(userEntity, UserDTO.class);
			returnValue.add(user);
		}
		
		return returnValue;
	}
	
	@Override
	@Transactional
	public UserDTO getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(Objects.isNull(userEntity)) {
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
		}
		
		ModelMapper modelMapper = new ModelMapper();
		
		UserDTO returnValue = modelMapper.map(userEntity, UserDTO.class);
		
		return returnValue;
	}

	@Override
	public UserDTO getUserByUserId(String userId) {
		
		ModelMapper modelMapper = new ModelMapper();

		UserEntity userEntity = userRepository.findByUserId(userId); 
		
		if(Objects.isNull(userEntity)) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()); 
		}
		
		UserDTO returnValue = modelMapper.map(userEntity, UserDTO.class);
		
		return returnValue; 
	}
	
	@Override 
	public Boolean verifyEmailToken(String token) {
		
		Boolean returnValue = Boolean.FALSE; 
		
		UserEntity userEntity = userRepository.findByEmailVerificationToken(token); 
		
		if(Objects.nonNull(userEntity)) {
			Boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if(Boolean.FALSE.equals(hasTokenExpired)) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity); 
				returnValue = Boolean.TRUE; 
			}
		}
		
		return returnValue;
	}
	
	//POST
	@Override
	public UserDTO createUser(UserDTO user) {
		
		UserEntity storedUserDetails = userRepository.findByEmail(user.getEmail());
		
		if(Objects.nonNull(storedUserDetails)) {
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
		}
		
		for(int i = 0; i < user.getAddresses().size(); i++) { 
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateId(30));
			user.getAddresses().set(i, address);
		}
		
		ModelMapper modelMapper = new ModelMapper();
		
		String publicId = utils.generateId(30);
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		userEntity.setUserId(publicId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateToken(publicId, SecurityConstants.EMAIL_EXPIRATION_TIME));
		userEntity.setEmailVerificationStatus(Boolean.FALSE);
		
		Collection<RoleEntity> roles = new HashSet<>();
		for(String role: user.getRoles()) {
			RoleEntity roleEntity = roleRepository.findByName(role);
			
			if(Objects.nonNull(roleEntity)) {
				roles.add(roleEntity);
			}
		}
		
		userEntity.setRoles(roles);
		
		UserEntity savedUserDetails = userRepository.save(userEntity);
		
		UserDTO returnValue = modelMapper.map(savedUserDetails, UserDTO.class);
		
		amazonSES.verifyEmail(returnValue);
		
		return returnValue;
	}
	
	@Override
	public Boolean requestPasswordReset(String email) {
		
		Boolean returnValue = Boolean.FALSE; 
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(Objects.isNull(userEntity)) {
			return returnValue; 
		}
		
		String token = utils.generateToken(userEntity.getUserId(), SecurityConstants.PASSWORD_EXPIRATION_TIME); 
		
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity); 
		
		returnValue = amazonSES.sendPasswordResetRequest(userEntity.getFirstName(), userEntity.getEmail(), token);
		
		return returnValue;
	}
	
	@Override
	public Boolean resetPassword(String token, String password) {
		Boolean returnValue = Boolean.FALSE; 
		
		if(Utils.hasTokenExpired(token)) {
			return returnValue; 
		}
		
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		
		if(Objects.isNull(passwordResetTokenEntity)) {
			return returnValue; 
		}
		
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPassword);
		UserEntity savedUserEntity = userRepository.save(userEntity); 
		
		if(Objects.nonNull(savedUserEntity) && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
			returnValue = Boolean.TRUE;
		}
		
		passwordResetTokenRepository.delete(passwordResetTokenEntity);
		
		return returnValue;
	}
	
	//PUT
	@Override
	public UserDTO updateUser(UserDTO user, String userId) {
		
		UserEntity userEntity = userRepository.findByUserId(userId); 
		
		if(Objects.isNull(userEntity)) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()); 
		}
		
		ModelMapper modelMapper = new ModelMapper();
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updatedUserDetails = userRepository.save(userEntity);
		UserDTO returnValue = modelMapper.map(updatedUserDetails, UserDTO.class);
		
		return returnValue;
	}
	
	//DELETE
	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId); 
		
		if(Objects.isNull(userEntity)) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()); 
		}
		
		userRepository.delete(userEntity);
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(Objects.isNull(userEntity)) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()); 
		}
		
		return new UserPrincipal(userEntity);
	}
}
