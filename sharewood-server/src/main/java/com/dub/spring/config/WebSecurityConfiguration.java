package com.dub.spring.config;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.DELETE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.dub.spring.common.Role;
//import com.dub.spring.filter.UserWebFilter;
import com.dub.spring.filter.UserWebFilter;


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfiguration {

	private final String jwkSetUri;

	private final SharewoodReactiveUserDetailsService sharewoodReactiveUserDetailsService;

	// constructor
	@Autowired
	public WebSecurityConfiguration(
			SharewoodReactiveUserDetailsService sharewoodReactiveUserDetailsService,
			@Value("${jwkSetUri}") String jwkSetUri) {
		this.sharewoodReactiveUserDetailsService = sharewoodReactiveUserDetailsService;
		this.jwkSetUri = jwkSetUri;
	}
  

	
	@Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		
		OAuth2ResourceServerSpec oauth2Spec = 
				http
			.csrf().disable()
			
			.addFilterAfter(new UserWebFilter(this.sharewoodReactiveUserDetailsService, jwkSetUri), SecurityWebFiltersOrder.AUTHENTICATION)
				
			.authorizeExchange()
				.pathMatchers(DELETE, "/photos/deletePhoto").hasAnyRole(Role.SHAREWOOD_USER.name(), Role.SHAREWOOD_CURATOR.name())
				.pathMatchers(POST, "/photos/createPhoto").hasAnyRole(Role.SHAREWOOD_USER.name(), Role.SHAREWOOD_CURATOR.name())
				.pathMatchers(PUT, "/photos/updatePhoto").hasAnyRole(Role.SHAREWOOD_USER.name(), Role.SHAREWOOD_CURATOR.name())
				
				.pathMatchers(GET, "/photos/**").hasAnyRole(Role.SHAREWOOD_USER.name(), Role.SHAREWOOD_CURATOR.name())	
				.pathMatchers(POST, "/photos/**").hasAnyRole(Role.SHAREWOOD_USER.name(), Role.SHAREWOOD_CURATOR.name())
				.pathMatchers("/actuator/**").permitAll()

				.anyExchange().authenticated()
				.and()// ServerHttpSecurity
				.oauth2ResourceServer();// OAuth2ResourceServerSpec
				
		System.err.println("Inside configure OAuth2ResourceServerSpec");
		
				oauth2Spec
				.jwt()
				.jwtAuthenticationConverter(sharewoodUserJwtAuthenticationConverter());
				
		
		return http.build();
	}	
	

	/** custom implementation */
	
	@Bean
	public SharewoodUserJwtAuthenticationConverter sharewoodUserJwtAuthenticationConverter() {
		return new SharewoodUserJwtAuthenticationConverter(sharewoodReactiveUserDetailsService);
	}
		
	
	@DependsOn("reactiveJwtDecoder")
	@Bean 
	MyJwtReactiveAuthenticationManager myReactiveAuthenticationManager() {
		MyJwtReactiveAuthenticationManager manager = new MyJwtReactiveAuthenticationManager(reactiveJwtDecoder());
		manager.setJwtAuthenticationConverter(this.myAuthenticationConverter());
		
		
		return new MyJwtReactiveAuthenticationManager(reactiveJwtDecoder());
	}
	
	

	@Bean 
	public ReactiveJwtDecoder reactiveJwtDecoder() {
		return NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
	}

	
	@Bean
	public MyReactiveJwtAuthenticationConverter myAuthenticationConverter() {
		return new MyReactiveJwtAuthenticationConverter();
	}
	
	
}