package com.appdeveloperblog.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.appdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appdeveloperblog.app.ws.shared.dto.UserDTO;
import com.appdeveloperblog.app.ws.ui.model.response.UserResponseModel;

class UserControllerTest {

	@InjectMocks
	UserController userController; 
	
	@Mock
	UserServiceImpl userServiceImpl; 
	
	UserDTO userDto;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		userDto = new UserDTO();
		userDto.setUserId("soyunid");
		userDto.setFirstName("Ornella");
		userDto.setLastName("Fasciolo");
		userDto.setEmail("ornella_sofia@hotmail.com");
		userDto.setEmailVerificationToken("soyuntoken");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEncryptedPassword("estoyencriptado");
		userDto.setAddresses(listOfAddresses());
	}
	
	private List<AddressDTO> listOfAddresses(){
		
		AddressDTO shippingAddressDto = new AddressDTO();
		shippingAddressDto.setType("shipping");
		shippingAddressDto.setCity("Vancouver");
		shippingAddressDto.setCountry("Canada");
		shippingAddressDto.setPostalCode("ABCCBA");
		shippingAddressDto.setStreetName("123 Street name");
		
		AddressDTO billingAddressDto = new AddressDTO();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Vancouver");
		billingAddressDto.setCountry("Canada");
		billingAddressDto.setPostalCode("ABCCBA");
		billingAddressDto.setStreetName("123 Street name");
		
		List<AddressDTO> addressesList = new ArrayList<>(); 
		addressesList.add(billingAddressDto);
		addressesList.add(shippingAddressDto);
		
		return addressesList;
	}

	@Test
	@Disabled
	void testGetUsers() {
		fail("Not yet implemented");
	}

	@Test
	void testGetUser() {
		when(userServiceImpl.getUserByUserId(Mockito.anyString())).thenReturn(userDto);

		UserResponseModel user = userController.getUser(userDto.getUserId());
		
		assertNotNull(user);
		assertEquals(userDto.getUserId(), user.getUserId());
		assertEquals(userDto.getFirstName(), user.getFirstName());
		assertEquals(userDto.getLastName(), user.getLastName());
		assertTrue(userDto.getAddresses().size() == user.getAddresses().size());
	}

	@Test
	@Disabled
	void testGetAddresses() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testGetAddress() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testVerifyEmailToken() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testCreateUser() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testRequestPasswordReset() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testResetPassword() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testUpdateUser() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testDeleteUser() {
		fail("Not yet implemented");
	}

}
