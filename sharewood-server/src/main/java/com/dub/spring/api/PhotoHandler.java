package com.dub.spring.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.dub.spring.exceptions.NotFoundException;
import com.dub.spring.photo.CreatePhotoComposite;
import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;
import com.dub.spring.service.PhotoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
public class PhotoHandler {
	
	@Value("${basePhotosURL}")
	String basePhotosURL;
	
	@Autowired 
	private PhotoService photoService;
	
	public Mono<ServerResponse> updatePhoto(ServerRequest request) {
		System.out.println("/updatePhoto begin");
		
		final Mono<Photo> photo = request.bodyToMono(Photo.class);
		
		return photo.flatMap(transformUpdate)
				.onErrorResume(photoFallback);	
	}
	
	public Mono<ServerResponse> deletePhoto(ServerRequest request) {
		System.out.println("/deletePhoto begin");
		
		String photoId = request.pathVariable("photoId");
		Mono<Long> photoIdLong = Mono.just(Long.parseLong(photoId));
			
		String username = request.headers().firstHeader("username");
			
		Mono<Tuple2<Long, String>> tuple2 = Mono.zip(photoIdLong, Mono.just(username));  
		return tuple2.flatMap(transformDelete2);
				
	}
	
	
	public Mono<ServerResponse> createPhoto(ServerRequest request) {
		System.out.println("/createPhoto begin");
		
		String username = request.headers().firstHeader("username");
		
		final Mono<CreatePhotoComposite> compo = request.bodyToMono(CreatePhotoComposite.class);
		
		Mono<Tuple2<CreatePhotoComposite, String>> tuple2 = Mono.zip(compo, Mono.just(username));  
		return tuple2
				.flatMap(transformCreate)
				.flatMap(finishCreate)
				.onErrorResume(photoFallback);	
	}
	
	public Mono<ServerResponse> sharedPhotos(ServerRequest request) {
		System.err.println("/sharedPhotos begin");
		
		Mono<Flux<PhotoWith>> photos = Mono.just(this.photoService.getSharedPhotos());

		return photos
				.flatMap(sharedPhotosSuccess)
				.onErrorResume(photoFallback);
	}
	
	
	public Mono<ServerResponse> photosMy(ServerRequest request) {
				
		String username = request.headers().firstHeader("username");
		
		System.out.println("From /allPhotos auth " + username);
						
		Mono<Flux<PhotoWith>> photo = Mono.just(this.photoService.getPhotosMyWith(username)); 
		
		return photo
		.flatMap(photosMyWithSuccess)
		.onErrorResume(photoFallback);
	}
	
	public Mono<ServerResponse> getPhotoWithById(ServerRequest request) {
		
		String username = request.headers().firstHeader("username");
		String photoId = request.pathVariable("photoId");
			
		Mono<Long> photoIdLong = Mono.just(Long.parseLong(photoId));
	 
		Mono<Tuple2<Long, String>> tuple2 = Mono.zip(photoIdLong, Mono.just(username));
		
		return tuple2
				.flatMap(transformGetPhotoById3);
	}
	
	
	private Function<Tuple2<Long,String>, Mono<ServerResponse>> transformGetPhotoById3 = 
			tuple -> {
					Mono<PhotoWith> photo = photoService.getPhotoWith(tuple.getT1(), tuple.getT2());
				
					System.out.println("Returning OK");
									
					return photo.flatMap(ph -> 
					ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(Mono.just(ph), PhotoWith.class))
					.onErrorResume(er -> {
							System.out.println("transformPhotoById Exception " + er); 
							// Exception hidden from client
							if (NotFoundException.class.equals(er.getClass())) {
								return ServerResponse.notFound().build();
							} else {
								return ServerResponse.badRequest().build();
							}			
					});
	};

	private Function<Flux<PhotoWith>, Mono<ServerResponse>> photosMyWithSuccess = 
			photos -> {
					return ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(photos, PhotoWith.class);
	};
	
	private Function<Throwable, Mono<ServerResponse>> photoFallback = 
			error -> {
				System.out.println("Returning error "
						+ error);
					return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.build();
	};
	
	private Function<Flux<PhotoWith>, Mono<ServerResponse>> sharedPhotosSuccess = 
			photos -> {
					return ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(photos, PhotoWith.class);
	};
	
	private Function<Tuple2<CreatePhotoComposite, String>, Mono<URI>> transformCreate = s -> {
		
		System.err.println("transformCreate begin " + s.getT1().getTitle());
		
		String title = s.getT1().getTitle();
		boolean shared = s.getT1().isShared();
		byte[] image = s.getT1().getImage();
		String username = s.getT2(); 
		
		// actual creation here
		try {
			Mono<Long> newId = this.photoService.createPhoto(image, username, title, shared);
			
			return newId
					.flatMap(id -> {
						try {
							return Mono.just(new URI(basePhotosURL + "/photoById/" + id));				
						} catch (URISyntaxException e) {
							e.printStackTrace();
							return Mono.error(new RuntimeException("SATOR"));
						}
					});
		} catch (Exception ex) {
			return Mono.error(new RuntimeException("SATOR"));
			
		}
    
	};
			
			    
	private Function<URI, Mono<ServerResponse>> finishCreate =
			location -> {	
				return ServerResponse.created(location).build();
	
	};
	
	private Function<Tuple2<Long, String>, Mono<ServerResponse>> transformDelete2 = s -> {
		
		System.out.println("transformDelete " + s.getT1() + " " + s.getT2());
		
		Mono<Long> deleteId = this.photoService.deletePhoto(s.getT1(), s.getT2());
		
		return deleteId.flatMap(id -> 
			ServerResponse.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(Mono.just(id), Long.class)
		)
		.onErrorResume(er -> {
			System.out.println("transformDelete2 Exception " + er); 
			// Exception hidden from client
			if (NotFoundException.class.equals(er.getClass())) {
				return ServerResponse.notFound().build();
			} else {
				return ServerResponse.badRequest().build();
			}
			
		});
	};

	Function<Photo, Mono<ServerResponse>> transformUpdate = s -> {
		
		System.out.println("transformUpdate " + s.getUsername());
		Mono<Photo> name = this.photoService.updatePhoto(s, s.getUsername()); 
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(name, Photo.class);
	};
	
}


