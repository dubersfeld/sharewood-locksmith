package com.dub.spring.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dub.spring.persistence.UserRepository;
import com.dub.spring.user.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
	
	
	@PreAuthorize("isAnonymous() or isAuthenticated()")
	public Mono<User> findOneByUsername(String username);
	

  /*
  public Mono<Void> create(Mono<User> user) {
    return null;
  }

  public Mono<User> update(User user) {
    return null;
  }

  public Mono<User> findById(UUID uuid) {
    return null;
  }

  public Flux<User> findAll() {
    return null;
  }

  public Mono<Void> deleteById(UUID uuid) {
    return null;
  }
  */
}
