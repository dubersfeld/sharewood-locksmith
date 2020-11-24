package com.dub.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dub.spring.service.UserService;
import com.dub.spring.user.SharewoodUser;
import com.dub.spring.user.User;

import reactor.core.publisher.Mono;

@Service
public class SharewoodReactiveUserDetailsService implements ReactiveUserDetailsService {
  
	private static final Logger LOGGER =
      LoggerFactory.getLogger(SharewoodReactiveUserDetailsService.class);

	private final UserService userService;
  
	

	public SharewoodReactiveUserDetailsService(UserService userService) {
		this.userService = userService;
   
	}

	
	
	@Override
  	public Mono<UserDetails> findByUsername(String username) {
  		LOGGER.info("Finding user for user name {}", username);
   
  		Mono<User> checkUser = this.userService.findOneByUsername(username);
  		
  		Mono<UserDetails> checkSharewoodUser = checkUser.map(SharewoodUser::new);
  	 			
  		return checkSharewoodUser;
  		
  	}
	
	
}
