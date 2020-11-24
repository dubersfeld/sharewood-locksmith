package com.dub.spring.api;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ClientResponse.Headers;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.dub.spring.resource.BookResource;
import com.dub.spring.resource.CreateBookResource;

import reactor.core.publisher.Mono;

@Controller
public class DefaultController {
	  
	@Value("${sharewood.server}")
	private String sharewoodServer;

	
	@GetMapping({"/", "/backHome"})  
	Mono<String> index(@AuthenticationPrincipal OAuth2User user, Model model) {

		Collection<? extends GrantedAuthority> auths = user.getAuthorities();
	    for (GrantedAuthority auth : auths) {
	    	System.err.println(auth);
	    }
		
		
		model.addAttribute("fullname", user.getName());
	   // model.addAttribute(
	   //     "isCurator",
	    //    user.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("sharewood_curator")));
	    return Mono.just("index");  
	}
	
}
