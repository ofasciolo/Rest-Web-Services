package com.appdeveloperblog.app.ws.restAssuredTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersWebServiceEndpointTest {

	private final String CONTEXT_PATH = "/mobile-app-ws";
	private final String APPLICATION_JSON = "application/json";
	private final String EMAIL_ADDRESS = "ornella_sofia@hotmail.com";
	
	private static String authorization;
	private static String userId;
	private static List<Map<String, String>> storedAddresses;
	
	@BeforeEach
	void setUp() throws Exception {
		
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

	@Test
	@Order(1)
	void testUserLogin() {
		
		Map<String, String> user = new HashMap<>(); 
		user.put("email", EMAIL_ADDRESS);
		user.put("password", "123");
		
		Response response = RestAssured.given().contentType(APPLICATION_JSON).accept(APPLICATION_JSON).body(user)
							.when().post(CONTEXT_PATH + "/users/login")
							.then().statusCode(200).extract().response();
		
		authorization = response.header("Authorization");
		userId = response.header("UserID");
		
		assertNotNull(authorization); 
		assertNotNull(userId); 
		
	}
	
	@Test
	@Order(2)
	void testGetUser() {
		Response response = RestAssured.given().accept(APPLICATION_JSON).header("Authorization", authorization).pathParam("userId", userId)
				.when().get(CONTEXT_PATH + "/users/{userId}")
				.then().statusCode(200).extract().response();

		String userPublicId = response.jsonPath().getString("userId");
		String email = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		storedAddresses = response.jsonPath().getList("addresses");
		String address = storedAddresses.get(0).get("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(email);
		assertNotNull(firstName);
		assertNotNull(lastName);
		
		assertEquals(EMAIL_ADDRESS, email);
		
		assertTrue(storedAddresses.size() == 2);
		assertTrue(address.length() == 30);
	}
	
	@Test
	@Order(3)
	void testUpdateUser() {
		
		Map<String, String> user = new HashMap<>(); 
		user.put("firstName", "Pepito");
		user.put("lastName", "Gomez");
		
		Response response = RestAssured.given().contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
							.header("Authorization", authorization).pathParam("userId", userId).body(user)
							.when().put(CONTEXT_PATH + "/users/{userId}")
							.then().statusCode(200).contentType(APPLICATION_JSON).extract().response();
	
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		List<Map<String, String>> addresses = response.jsonPath().getList("addresses");
		String address = addresses.get(0).get("addressId");
		
		assertEquals("Pepito",firstName);
		assertEquals("Gomez",lastName);
		assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
		
		assertNotNull(address);
		
		assertTrue(addresses.size() == storedAddresses.size());
		
	}
	
	@Test
	@Order(4)
	void testDeleteUser() {
		
		Response response = RestAssured.given().accept(APPLICATION_JSON).header("Authorization", authorization).pathParam("userId", userId)
				.when().delete(CONTEXT_PATH + "/users/{userId}")
				.then().statusCode(200).contentType(APPLICATION_JSON).extract().response();

		String operationResult = response.jsonPath().getString("operationResult");
		
		assertEquals("SUCCESS", operationResult);
	}
}
