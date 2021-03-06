package com.appdeveloperblog.app.ws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.appdeveloperblog.app.ws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class Utils {

	private final Random RANDOM = new SecureRandom(); 
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public static boolean hasTokenExpired(String token) {
		Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token).getBody();
		
		Date tokenExpirationDate = claims.getExpiration();
		
		return tokenExpirationDate.before(new Date());
	}
	
	public String generateToken(String userId, long expirationTime) {
		String token = Jwts.builder().setSubject(userId)
					   .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
					   .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();
		return token; 
	}
	
	public String generateId(int length) {
		return generateRandomString(length);
	}
	
	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length); 
		
		for(int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return new String(returnValue); 
	}
	
	
}
