package com.dub.spring.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class PhotoRouter {

	@Bean
	public RouterFunction<ServerResponse> allPhotos(PhotoHandler photoHandler) {

	    return RouterFunctions
	    	
	     		.route(RequestPredicates.GET("/photos/sharedPhotos")
		  	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), photoHandler::sharedPhotos)
	    		.andRoute(RequestPredicates.GET("/photos/photosMy")
		  	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), photoHandler::photosMy)
	    		.andRoute(RequestPredicates.DELETE("/photos/deletePhoto/{photoId}")
		  	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), photoHandler::deletePhoto)
		  	  
	    		.andRoute(RequestPredicates.POST("/photos/createPhoto")
		  	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), photoHandler::createPhoto)
		  
	    		.andRoute(RequestPredicates.PUT("/photos/updatePhoto")
		  	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), photoHandler::updatePhoto)

	    		.andRoute(RequestPredicates.GET("/photos/photoById/{photoId}")
	  	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), photoHandler::getPhotoWithById);
	}
}
