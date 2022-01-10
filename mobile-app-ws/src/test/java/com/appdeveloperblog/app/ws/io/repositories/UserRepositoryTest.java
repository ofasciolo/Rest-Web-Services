package com.appdeveloperblog.app.ws.io.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository; 
	
	@BeforeEach
	void setUp() throws Exception {
		
		createRecords();	
	}
	
	@AfterEach
	void cleanUp() throws Exception {
		userRepository.deleteAll();
	}
	
	private void createRecords() {
		AddressEntity shippingAddress = new AddressEntity();
		shippingAddress.setType("shipping");
		shippingAddress.setCity("Vancouver");
		shippingAddress.setCountry("Canada");
		shippingAddress.setPostalCode("ABCCBA");
		shippingAddress.setStreetName("123 Street name");
		shippingAddress.setAddressId("12341");

		List<AddressEntity> addresses = new ArrayList<>(); 
		addresses.add(shippingAddress);
		
		UserEntity userEntity = new UserEntity(); 
		userEntity.setId(1L);
		userEntity.setUserId("soyunid");
		userEntity.setFirstName("Ornella");
		userEntity.setLastName("Fasciolo");
		userEntity.setEmail("ornella_sofia@hotmail.com");
		userEntity.setEmailVerificationStatus(true);
		userEntity.setEncryptedPassword("estoyencriptado");
		userEntity.setAddresses(addresses);
	
		userRepository.save(userEntity);
		
		AddressEntity shippingAddress2 = new AddressEntity();
		shippingAddress2.setType("shipping");
		shippingAddress2.setCity("Vancouver");
		shippingAddress2.setCountry("Canada");
		shippingAddress2.setPostalCode("ABCCBA");
		shippingAddress2.setStreetName("123 Street name");
		shippingAddress2.setAddressId("123412");

		List<AddressEntity> addresses2 = new ArrayList<>(); 
		addresses2.add(shippingAddress2);
		
		UserEntity userEntity2 = new UserEntity(); 
		userEntity2.setId(2L);
		userEntity2.setUserId("soyunid2");
		userEntity2.setFirstName("Ornella");
		userEntity2.setLastName("Fasciolo");
		userEntity2.setEmail("pepito@hotmail.com");
		userEntity2.setEmailVerificationStatus(true);
		userEntity2.setEncryptedPassword("estoyencriptado");
		userEntity2.setAddresses(addresses2);
		
		userRepository.save(userEntity2);
	}
	
	@Test
	void testGetVerifiedUsers() {
		
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		
		assertNotNull(pages);
		
		List<UserEntity> userEntities = pages.getContent(); 
		assertNotNull(userEntities);
	}
	
	@Test
	void testFindByFirstName() {
		
		List<UserEntity> users = userRepository.findByFirstName("Ornella");
	
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertEquals("Ornella", user.getFirstName());
	}
	
	@Test
	void testFindByLastName() {
		
		List<UserEntity> users = userRepository.findByLastName("Fasciolo");
	
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertEquals("Fasciolo", user.getLastName());
	}

	@Test
	void testFindByKeyword() {
		
		List<UserEntity> users = userRepository.findByKeword("ne");
	
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getFirstName().contains("ne") || user.getLastName().contains("ne"));
	}
	
	@Test
	void testFindNameByKeyword() {
		
		List<Object[]> users = userRepository.findNameByKeword("ne");
	
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		Object[] user = users.get(0);
		
		String firstName = String.valueOf(user[0]);
		String lastName = String.valueOf(user[1]);
		
		assertNotNull(firstName);
		assertNotNull(lastName);
		
		assertTrue(firstName.contains("ne") || lastName.contains("ne"));
	}

	@Test
	void testUpdateEmailVerificationStatus() {
		userRepository.updateEmailVerificationStatus(Boolean.FALSE, "soyunid");
		
		UserEntity storedUser = userRepository.findByUserId("soyunid");
		
		assertEquals(Boolean.FALSE, storedUser.getEmailVerificationStatus());
	}
	
	@Test
	void testFindByUserIdJPQL() {
		UserEntity userEntity = userRepository.findByUserIdJPQL("soyunid");
		
		assertNotNull(userEntity);
		assertEquals("soyunid", userEntity.getUserId());
	}

	@Test
	void testFindNameByUserIdJPQL() {
		List<Object[]> users = userRepository.findNameByUserIdJPQL("soyunid");
		
		assertNotNull(users);
		assertTrue(users.size() == 1);
		
		Object[] user = users.get(0);
		
		String firstName = String.valueOf(user[0]);
		String lastName = String.valueOf(user[1]);
		
		assertNotNull(firstName);
		assertNotNull(lastName);
	}
	
	@Test
	void testUpdateEmailVerificationStatusJPQL() {
		userRepository.updateEmailVerificationStatusJPQL(Boolean.FALSE, "soyunid");
		
		UserEntity storedUser = userRepository.findByUserId("soyunid");
		
		assertEquals(Boolean.FALSE, storedUser.getEmailVerificationStatus());
	}
}
