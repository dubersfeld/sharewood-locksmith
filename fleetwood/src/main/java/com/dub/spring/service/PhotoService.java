package com.dub.spring.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.dub.spring.api.CreatePhotoComposite;
import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface PhotoService {

	public Mono<String> getHealth();
	
	public Flux<Photo> allPhotos();
	
	public Mono<Long> deletePhoto(Long photoId); 
	
	public Mono<Photo> updatePhoto(Photo photo);
	
	public Mono<PhotoWith> getPhotoById(String photoId);
	
	
	public Flux<PhotoWith> getMyPhotos();
	
	public Flux<PhotoWith> sharedPhotos();

	//public Mono<Photo> forge();
	
	
	//public Flux<Photo> photosMy();
	
	public Mono<String> createPhoto(CreatePhotoComposite compo);
	
	//public Mono<Long> createPhoto(FilePart uploadedFileRef, String title, boolean shared); 

	public Mono<byte[]> getImage(String id);
	
}
