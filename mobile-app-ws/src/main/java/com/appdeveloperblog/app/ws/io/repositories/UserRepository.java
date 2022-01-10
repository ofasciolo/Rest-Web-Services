package com.appdeveloperblog.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appdeveloperblog.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId); 
	UserEntity findByEmailVerificationToken(String token);
	
	//Just for learning
	
	//Native SQL 
	@Query(value = "select * from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'",
		   countQuery = "select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'", 
		   nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);

	@Query(value = "select * from Users u where u.FIRST_NAME = ?1", nativeQuery = true)
	List<UserEntity> findByFirstName(String firstName); 
	
	@Query(value = "select * from Users u where u.LAST_NAME = :lastName", nativeQuery = true)
	List<UserEntity> findByLastName(@Param("lastName") String lastName); 
	
	@Query(value = "select * from Users u where u.FIRST_NAME LIKE %:keyword% or u.LAST_NAME LIKE %:keyword%", nativeQuery = true)
	List<UserEntity> findByKeword(@Param("keyword") String keyword); 
	
	@Query(value = "select u.FIRST_NAME, u.LAST_NAME from Users u where u.FIRST_NAME LIKE %:keyword% or u.LAST_NAME LIKE %:keyword%", nativeQuery = true)
	List<Object[]> findNameByKeword(@Param("keyword") String keyword); 
	
	@Modifying
	@Transactional
	@Query(value = "update Users u set u.EMAIL_VERIFICATION_STATUS = :emailVerificationStatus where u.USER_ID = :userId", nativeQuery = true)
	void updateEmailVerificationStatus(@Param("emailVerificationStatus") Boolean emailVerificationStatus, @Param("userId") String userId); 

	//JPQL
	@Query("select user from UserEntity user where user.userId = :userId")
	UserEntity findByUserIdJPQL(@Param("userId") String userId);
	
	@Query("select user.firstName, user.lastName from UserEntity user where user.userId = :userId")
	List<Object[]> findNameByUserIdJPQL(@Param("userId") String userId);

	@Modifying
	@Transactional
	@Query(value = "update UserEntity user set user.emailVerificationStatus = :emailVerificationStatus where user.userId = :userId")
	void updateEmailVerificationStatusJPQL(@Param("emailVerificationStatus") Boolean emailVerificationStatus, @Param("userId") String userId); 

}
