package com.appdeveloperblog.app.ws.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.appdeveloperblog.app.ws.service.UserService;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{

	private final UserService userService; 
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		super();
		this.userService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	} 
	
	@Override 
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
		.antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL).permitAll()
		.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL).permitAll()
		.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL).permitAll()
		.antMatchers(SecurityConstants.H2_CONSOLE).permitAll()
		.antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**").permitAll()
		.anyRequest().authenticated().and()
		.addFilter(getAuthenticationFilter())
		.addFilter(new AuthorizationFilter(authenticationManager()))
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
		
		http.headers().frameOptions().disable();
	}
	
	@Override 
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	public AuthenticationFilter getAuthenticationFilter() throws Exception {
		final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/users/login");
		return filter; 
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration(); 
		
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("*"));
	
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); 
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}
}
