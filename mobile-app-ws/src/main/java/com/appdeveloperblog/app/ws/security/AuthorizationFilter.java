package com.appdeveloperblog.app.ws.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repositories.UserRepository;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter{
	
	
	private final UserRepository userRepository;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String header = request.getHeader(SecurityConstants.HEADER_STRING); 
		
		if(Objects.isNull(header) || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return; 
		}
		
		UsernamePasswordAuthenticationToken authentication = getAuthentication(request); 
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
	}
	
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(SecurityConstants.HEADER_STRING); 
		if(Objects.nonNull(token)) {
			token = token.replace(SecurityConstants.TOKEN_PREFIX , "");
			String user = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret())
					.parseClaimsJws(token).getBody().getSubject(); 
			if(Objects.nonNull(user)) {
				UserEntity userEntity = userRepository.findByEmail(user);
				
				if(Objects.isNull(userEntity)) {
					return null;
				}
				
				UserPrincipal userPrincipal = new UserPrincipal(userEntity);
				return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()); 
			}
			return null; 
		}
		return null; 
	}
	
	
}
