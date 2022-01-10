package com.appdeveloperblog.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appdeveloperblog.app.ws.service.AddressService;
import com.appdeveloperblog.app.ws.service.UserService;
import com.appdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appdeveloperblog.app.ws.shared.dto.UserDTO;
import com.appdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appdeveloperblog.app.ws.ui.model.response.AddressResponseModel;
import com.appdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appdeveloperblog.app.ws.ui.model.request.RequestPasswordResetRequestModel;
import com.appdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appdeveloperblog.app.ws.ui.model.response.RequestOperationName;
import com.appdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appdeveloperblog.app.ws.ui.model.response.UserResponseModel;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService; 
	
	@Autowired
	AddressService addressService; 
	
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "${userController.authorization-header.description}", paramType = "header")})
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public List<UserResponseModel> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "25") int limit){
		
		ModelMapper modelMapper = new ModelMapper();
		List<UserResponseModel> returnValue = new ArrayList<>(); 
		
		List<UserDTO> users = userService.getUsers(page, limit); 
		
		for(UserDTO user: users) {
			UserResponseModel userModel = modelMapper.map(user, UserResponseModel.class);
			returnValue.add(userModel);
		}
		
		return returnValue; 
	}
	
	@ApiOperation(value = "Get User Details", notes = "${userController.getUser.apiOperations-notes}")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "${userController.authorization-header.description}", paramType = "header")})
	@GetMapping(path="/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public UserResponseModel getUser(@PathVariable String userId) {
		
		ModelMapper modelMapper = new ModelMapper();
		
		UserDTO userDto = userService.getUserByUserId(userId); 
		UserResponseModel returnValue = modelMapper.map(userDto, UserResponseModel.class);
		
		return returnValue;
	}
	
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "${userController.authorization-header.description}", paramType = "header")})
	@GetMapping(path="/{userId}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public CollectionModel<AddressResponseModel> getAddresses(@PathVariable String userId) {
		
		List<AddressDTO> addressDto = addressService.getAddresses(userId); 
		
		if(Objects.nonNull(addressDto) && !addressDto.isEmpty()) {
			
			ModelMapper modelMapper = new ModelMapper();
			Type listType = new TypeToken<List<AddressResponseModel>>() {}.getType();
			List<AddressResponseModel> returnValue = modelMapper.map(addressDto, listType);
			
			for(AddressResponseModel address: returnValue) {
				Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddress(userId, address.getAddressId())).withSelfRel();
				address.add(selfLink);
			}
			
			Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId)).withRel("user");
			Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddresses(userId)).withSelfRel();
			
			return CollectionModel.of(returnValue, userLink, selfLink); 
		}
		
		return CollectionModel.of(new ArrayList<>());
	}
	
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "${userController.authorization-header.description}", paramType = "header")})
	@GetMapping(path="/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public EntityModel<AddressResponseModel> getAddress(@PathVariable String userId, @PathVariable String addressId) {
		
		AddressDTO addressDto = addressService.getAddress(addressId); 
		
		ModelMapper modelMapper = new ModelMapper();
		AddressResponseModel returnValue = modelMapper.map(addressDto, AddressResponseModel.class); 
		
		Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId)).withRel("user");
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddresses(userId)).withRel("addresses");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddress(userId, addressId)).withSelfRel();
		
		return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
	}
	
	@GetMapping(path="/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
		
		OperationStatusModel returnValue = new OperationStatusModel(); 
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		Boolean isVerified = userService.verifyEmailToken(token); 
		
		if(Boolean.TRUE.equals(isVerified)) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		return returnValue;
	}
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public UserResponseModel createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		
		if(userDetails.getFirstName().isEmpty()) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}
		
		ModelMapper modelMapper = new ModelMapper();

		UserDTO userDto = modelMapper.map(userDetails, UserDTO.class);
		
		UserDTO createUser = userService.createUser(userDto);
		UserResponseModel returnValue = modelMapper.map(createUser, UserResponseModel.class);

		return returnValue;
	}
	
	@PostMapping(path = "/password-reset-request", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel requestPasswordReset(@RequestBody RequestPasswordResetRequestModel requestPasswordResetRequestModel) {
		
		OperationStatusModel returnValue = new OperationStatusModel(); 
		
		Boolean operationResult = userService.requestPasswordReset(requestPasswordResetRequestModel.getEmail());
	
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if(Boolean.TRUE.equals(operationResult)) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	@PostMapping(path = "/password-reset", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel resetPassword(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		
		OperationStatusModel returnValue = new OperationStatusModel(); 
		
		Boolean operationResult = userService.resetPassword(passwordResetRequestModel.getToken(), passwordResetRequestModel.getPassword());
	
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if(Boolean.TRUE.equals(operationResult)) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "${userController.authorization-header.description}", paramType = "header")})
	@PutMapping(path="/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public UserResponseModel updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String userId) {
		
		ModelMapper modelMapper = new ModelMapper();
		
		UserDTO userDto = modelMapper.map(userDetails, UserDTO.class);

		UserDTO updatedUser = userService.updateUser(userDto, userId);
		UserResponseModel returnValue = modelMapper.map(updatedUser, UserResponseModel.class);
		
		return returnValue;
	}
	
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "${userController.authorization-header.description}", paramType = "header")})
	@DeleteMapping(path="/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String userId) {
		
		OperationStatusModel returnValue = new OperationStatusModel(); 
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(userId);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name()); 
		
		return returnValue;
	}
}
