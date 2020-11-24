package com.dub.spring.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;

import reactor.core.publisher.Mono;

/** JWT converter that takes the roles from 'groups' claim of JWT token. */
public class SharewoodUserJwtAuthenticationConverter
    implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
  private static final String GROUPS_CLAIM = "groups";
  private static final String ROLE_PREFIX = "ROLE_";

  private final SharewoodReactiveUserDetailsService libraryReactiveUserDetailsService;

  public SharewoodUserJwtAuthenticationConverter(
      SharewoodReactiveUserDetailsService libraryReactiveUserDetailsService) {
    this.libraryReactiveUserDetailsService = libraryReactiveUserDetailsService;
  }

  @Override
  public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
	  	 
	  Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
         
	  return libraryReactiveUserDetailsService
        .findByUsername(jwt.getClaimAsString("preferred_username"))
        .map(u -> {
        	UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(u, "n/a", authorities);
          	return token;
        });
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    return this.getScopes(jwt).stream()
        .map(authority -> ROLE_PREFIX + authority.toUpperCase())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Collection<String> getScopes(Jwt jwt) {
    Object scopes = jwt.getClaims().get(GROUPS_CLAIM);
    if (scopes instanceof Collection) {
      return (Collection<String>) scopes;
    }

    return Collections.emptyList();
  }
}
