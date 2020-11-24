package com.dub.spring.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.dub.spring.config.SharewoodReactiveUserDetailsService;

import reactor.core.publisher.Mono;

/** no default ReactiveAuthenticationManager is provided */
public class UserWebFilter implements WebFilter {
   
	private static final String GROUPS_CLAIM = "groups";	  
	private static final String ROLE_PREFIX = "ROLE_";
	
	private JwtDecoder jwtDecoder;
			
	private final SharewoodReactiveUserDetailsService libraryReactiveUserDetailsService;

	public UserWebFilter(			
			SharewoodReactiveUserDetailsService libraryReactiveUserDetailsService,
			String jwkSetUri) {
		this.libraryReactiveUserDetailsService = libraryReactiveUserDetailsService;
		jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
		
	}
	

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		   	
		Mono<Authentication> auth = this.convert(exchange);
		
		auth.subscribe(a -> System.out.println("Opera Principal " + a.getPrincipal()));
	
		auth.subscribe(a -> exchange.getAttributes().put("auth", a));
		
		auth.subscribe(a -> exchange.mutate().request(
                exchange.getRequest().mutate()
                .header("username", a.getName())
                .build())
        .build());
		
		return chain.filter(exchange);
	}
	

	
	public Mono<Void> onAuthenticationSuccess2(WebFilterExchange webFilterExchange, Authentication authentication) {
		ServerWebExchange exchange = webFilterExchange.getExchange();
		return webFilterExchange.getChain().filter(exchange);
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
	  
	    
	private Mono<Authentication> convert(ServerWebExchange exchange) {
		  	  
		try {
			HttpHeaders headers = exchange.getRequest().getHeaders();		
			String tokenStr = headers.getFirst("Authorization").substring(7);	
		
			Jwt jwt = this.jwtDecoder.decode(tokenStr);
		
			Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
					
			Mono<Authentication> grunge = libraryReactiveUserDetailsService
				        .findByUsername(jwt.getClaimAsString("preferred_username"))
				        .map(u -> new UsernamePasswordAuthenticationToken(u, "n/a", authorities));
		
			return grunge;
		} catch (Exception e) {
			return Mono.empty();
		} 
		  
	}
	  
}