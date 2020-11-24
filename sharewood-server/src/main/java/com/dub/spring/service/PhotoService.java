package com.dub.spring.service;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;

import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface PhotoService {
	
	@PreAuthorize("authentication.principal.username.equals(#username) and " +
			"hasAuthority('ROLE_SHAREWOOD_USER')")
	Flux<PhotoWith> getPhotosMyWith(String username);
	
	@PreAuthorize("hasAuthority('ROLE_SHAREWOOD_USER')")
	Flux<PhotoWith> getSharedPhotos();
	
	@PreAuthorize("authentication.principal.username.equals(#username) and " +
			"hasAuthority('ROLE_SHAREWOOD_USER')")
	Mono<PhotoWith> getPhotoWith(long id, String username);
	
	@PreAuthorize("authentication.principal.username.equals(#username) and " +
			"hasAuthority('ROLE_SHAREWOOD_USER')")
	Mono<Long> createPhoto(
			byte[] image, 
			String username,
			String title,
			boolean shared);
	
	@PreAuthorize("authentication.principal.username.equals(#username) and " +
			"hasAuthority('ROLE_SHAREWOOD_USER')")
	Mono<Long> deletePhoto(long photoId, String username);
	
	@PreAuthorize("authentication.principal.username.equals(#username) and " +
			"hasAuthority('ROLE_SHAREWOOD_USER')")
	Mono<Photo> updatePhoto(Photo photo, String username);
	
}
