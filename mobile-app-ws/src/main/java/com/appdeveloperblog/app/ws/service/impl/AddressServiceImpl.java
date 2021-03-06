package com.appdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repositories.AddressRepository;
import com.appdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appdeveloperblog.app.ws.service.AddressService;
import com.appdeveloperblog.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository; 
	
	@Autowired
	AddressRepository addressRepository; 
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		
		List<AddressDTO> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(Objects.isNull(userEntity)) {
			return returnValue;
		}
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity); 
		for(AddressEntity address: addresses) {
			returnValue.add(modelMapper.map(address, AddressDTO.class));
		}
		
		return returnValue;
	}
	
	@Override
	public AddressDTO getAddress(String addressId) {
		
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		
		if(Objects.nonNull(addressEntity)) {
			ModelMapper modelMapper = new ModelMapper();
			return modelMapper.map(addressEntity, AddressDTO.class);
		}
		
		return null; 
	}

}
