package com.appdeveloperblog.app.ws;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.appdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repositories.AuthorityRepository;
import com.appdeveloperblog.app.ws.io.repositories.RoleRepository;
import com.appdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appdeveloperblog.app.ws.shared.Roles;
import com.appdeveloperblog.app.ws.shared.Utils;

@Component
public class InitialUserSetUp {

	@Autowired
	AuthorityRepository authorityRepository; 
	
	@Autowired
	RoleRepository roleRepository; 
	
	@Autowired
	Utils utils; 
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Transactional
	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		
		AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
		AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
		AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
		
		createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
		RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
	
		if(Objects.isNull(roleAdmin)) {
			return; 
		}
		
		UserEntity adminUser = new UserEntity();
		adminUser.setFirstName("Ornella");
		adminUser.setLastName("Fasciolo");
		adminUser.setEmail("pepito@gmail.com");
		adminUser.setEmailVerificationStatus(Boolean.TRUE);
		adminUser.setUserId(utils.generateId(30));
		adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("123"));
		adminUser.setRoles(Arrays.asList(roleAdmin));
		
		userRepository.save(adminUser);
	}
	
	@Transactional
	private AuthorityEntity createAuthority(String name) {
		AuthorityEntity authorityEntity = authorityRepository.findByName(name); 
		
		if(Objects.isNull(authorityEntity)) {
			authorityEntity = new AuthorityEntity(name); 
			authorityRepository.save(authorityEntity);
		}
		
		return authorityEntity;
	}
	
	@Transactional
	private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
		
		RoleEntity role = roleRepository.findByName(name); 
		
		if(Objects.isNull(role)) {
			role = new RoleEntity(name);
			role.setAuthorities(authorities);
			roleRepository.save(role);
		}
		
		return role;
	}
}
