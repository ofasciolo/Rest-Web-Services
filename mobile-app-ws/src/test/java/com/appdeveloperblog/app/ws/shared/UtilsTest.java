package com.appdeveloperblog.app.ws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appdeveloperblog.app.ws.security.SecurityConstants;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

	@Autowired
	Utils utils; 
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testHasTokenExpired_False() {
		String token = utils.generateToken("1", SecurityConstants.EMAIL_EXPIRATION_TIME);
		Boolean hasTokenExpired = Utils.hasTokenExpired(token);
		
		assertFalse(hasTokenExpired);
	}
	
	@Test
	@Disabled
	void testHasTokenExpired_True() {
		Boolean hasTokenExpired = Utils.hasTokenExpired("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJHeGhRV2hTY0EzOVRSZnkwSkJlaGJBZFA4RnRlamMiLCJleHAiOjE2NDI0NzYwMjR9.MVRIYn-xsanaZa590NbIyZjlb4Rd86WfR60ILuG9Qt5OMxzLmFCau0gNXK4kaq9SmfcdUYrQvg6tktCY1tUCFA");
		
		assertTrue(hasTokenExpired);
	}

	@Test
	@Disabled
	void testGenerateToken() {
		fail("Not yet implemented");
	}

	@Test
	void testGenerateId() {
		String userId = utils.generateId(30);
		String userId2 = utils.generateId(30);
		
		assertNotNull(userId);
		assertTrue(userId.length() == 30);
		assertNotEquals(userId, userId2);
	}

}
