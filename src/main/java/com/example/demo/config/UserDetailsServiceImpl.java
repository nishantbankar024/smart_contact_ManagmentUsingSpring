package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.dao.UserRepository;
import com.example.demo.entity.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

User user=userRepository.getUserByUserName(username);		
		
		if(user==null) {
			throw new UsernameNotFoundException("could not found user!!");
		}
	CustomUserDetails customeUserDeails=new CustomUserDetails(user);
	return customeUserDeails;
	}

}
