package com.appdeveloperblog.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appdeveloperblog.app.ws.shared.AmazonSES;
import com.appdeveloperblog.app.ws.shared.Utils;
import com.appdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appdeveloperblog.app.ws.shared.dto.UserDTO;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userServiceImpl; 
	
	@Mock
	UserRepository userRepository; 
	
	@Mock
	Utils utils; 
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Mock
	AmazonSES amazonSES;
	
	UserEntity userEntity; 
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		userEntity = new UserEntity(); 
		userEntity.setId(1L);
		userEntity.setUserId("soyunid");
		userEntity.setFirstName("Ornella");
		userEntity.setLastName("Fasciolo");
		userEntity.setEmail("ornella_sofia@hotmail.com");
		userEntity.setEmailVerificationToken("soyuntoken");
		userEntity.setEncryptedPassword("estoyencriptado");
		userEntity.setAddresses(listOfAddressesEntity());
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
	
	private List<AddressEntity> listOfAddressesEntity(){
		
		List<AddressDTO> listOfAddresses = listOfAddresses();
		
		Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		
		ModelMapper modelMapper = new ModelMapper(); 
		
		return modelMapper.map(listOfAddresses, listType);
	}
	
	@Test
	@Disabled
	void testGetUsers() {
		fail("Not yet implemented");
	}

	@Test
	void testGetUser() {
		
		when(userRepository.findByEmail(Mockito.anyString())).thenReturn(userEntity);
	
		UserDTO userDto = userServiceImpl.getUser("soyunemail@gmail.com");
		
		assertNotNull(userDto);
		assertEquals(userEntity.getFirstName(), userDto.getFirstName());
	}
	
	@Test
	void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(Mockito.anyString())).thenReturn(null);

		assertThrows(UserServiceException.class, () -> {userServiceImpl.getUser("soyunemail@gmail.com");});
	}
	

	@Test
	@Disabled
	void testGetUserByUserId() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testVerifyEmailToken() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateUser() {
		
		when(userRepository.findByEmail(Mockito.anyString())).thenReturn(userEntity);
		when(utils.generateId(Mockito.anyInt())).thenReturn("soyunid");
		when(bCryptPasswordEncoder.encode(Mockito.anyString())).thenReturn("estoyencriptado");
		when(utils.generateToken(Mockito.anyString(), Mockito.anyLong())).thenReturn("soyuntoken");
		when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(userEntity);
		
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDTO.class));

		UserDTO userDto = new UserDTO(); 
		userDto.setPassword("soyunacontrasenia");
		userDto.setAddresses(listOfAddresses());
		
		UserDTO storedUser= userServiceImpl.createUser(userDto);		
		
		assertNotNull(userDto);
		assertEquals(userEntity.getFirstName(), storedUser.getFirstName());
		assertEquals(userEntity.getLastName(), storedUser.getLastName());
		assertNotNull(storedUser.getUserId());
		assertEquals(userEntity.getAddresses().size(), storedUser.getAddresses().size());
		verify(utils, times(3)).generateId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("soyunacontrasenia");
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}
	
	@Test
	void testCreateUser_UserServiceException() {
		
		UserDTO userDto = new UserDTO();
		userDto.setEmail("soyunemail@gmail.com");
		
		when(userRepository.findByEmail(Mockito.anyString())).thenReturn(userEntity);
		
		assertThrows(UserServiceException.class, () -> {userServiceImpl.createUser(userDto);});
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

	@Test
	@Disabled
	void testLoadUserByUsername() {
		fail("Not yet implemented");
	}

}
