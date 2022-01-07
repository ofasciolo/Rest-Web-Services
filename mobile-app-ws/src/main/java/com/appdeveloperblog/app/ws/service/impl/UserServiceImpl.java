package com.appdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appdeveloperblog.app.ws.service.UserService;
import com.appdeveloperblog.app.ws.shared.AmazonSES;
import com.appdeveloperblog.app.ws.shared.Utils;
import com.appdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appdeveloperblog.app.ws.shared.dto.UserDTO;
import com.appdeveloperblog.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository; 
	
	@Autowired
	Utils utils; 
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
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
	public boolean verifyEmailToken(String token) {
		
		boolean returnValue = false; 
		
		UserEntity userEntity = userRepository.findByEmailVerificationToken(token); 
		
		if(Objects.nonNull(userEntity)) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if(!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity); 
				returnValue = true; 
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
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicId));
		userEntity.setEmailVerificationStatus(Boolean.FALSE);
		
		UserEntity savedUserDetails = userRepository.save(userEntity);
		
		UserDTO returnValue = modelMapper.map(savedUserDetails, UserDTO.class);
		
		new AmazonSES().verifyEmail(returnValue);
		
		return returnValue;
	}
	
	//PUT
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
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, new ArrayList<>()); 
	}
}