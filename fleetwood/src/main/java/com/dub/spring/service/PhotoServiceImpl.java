package com.dub.spring.service;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.dub.spring.api.CreatePhotoComposite;
import com.dub.spring.exceptions.NotFoundException;
import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PhotoServiceImpl implements PhotoService {

	private WebClient webClient;
	
	private final WebClient.Builder webClientBuilder;
	
	@Value("${tempDir}")
	String tempDir;
	
	@Value("${sharewood.server}")
	private String sharewoodServer;
	
	String sharewoodPhotosBaseURL;
	
	private String regex;// = sharewoodPhotosBaseURL + "/(\\d+)";	   
	private Pattern pattern;// = Pattern.compile(pattern);

	@Autowired
	public PhotoServiceImpl(WebClient.Builder webClientBuilder,
			@Value("${sharewoodPhotosBaseURL}") String sharewoodPhotosBaseURL) {
		
		this.sharewoodPhotosBaseURL = sharewoodPhotosBaseURL;
	    this.webClientBuilder = webClientBuilder;
	    regex = sharewoodPhotosBaseURL + "/photoById/(\\d+)";
	    
		pattern = Pattern.compile(regex);
	}

	
	@Override
	public Mono<Long> deletePhoto(Long photoId) {
		
		String deletePhotoUri = sharewoodPhotosBaseURL + "/deletePhoto/" + photoId;
				 
		Mono<ClientResponse> response = getWebClient()
				.method(HttpMethod.DELETE)
				.uri(deletePhotoUri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.exchange();
		
		Mono<Long> delId = response.flatMap(r -> {
	
			if (HttpStatus.OK.equals(r.statusCode())) {
				return r.bodyToMono(Long.class);
			} else if (HttpStatus.NOT_FOUND.equals(r.statusCode())) {
				return Mono.error(new NotFoundException("Photo Not Found"));
			} else {
				return Mono.error(new RuntimeException("Internal Server Error"));
			}
			
		});
		
		return delId;
	}
	
	
	
	@Override
	public Flux<Photo> allPhotos() {
		
		String allPhotosUri = "http://localhost:9091/photos/allPhotos";
		 
		Flux<Photo> photoFlux = getWebClient()
			        .get()
			        .uri(allPhotosUri)
			        .retrieve()
			        .onStatus(
			            HttpStatus::is4xxClientError,
			            cr -> Mono.just(new IllegalArgumentException(cr.statusCode().getReasonPhrase())))
			        .onStatus(
			            HttpStatus::is5xxServerError,
			            cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())))
			        .bodyToFlux(Photo.class);
		
		return photoFlux;
	}
	
	
	@Override
	public Mono<PhotoWith> getPhotoById(String photoId) {
		
		String photoByIdUri = "http://localhost:9091/photos/photoById/" + photoId;
			
		Mono<PhotoWith> photo = getWebClient()
			        .get()
			        .uri(photoByIdUri)
			        
			        .retrieve()
			        
			        .onStatus(
			            HttpStatus::is4xxClientError,
			            cr -> Mono.just(new IllegalArgumentException(cr.statusCode().getReasonPhrase())))
			        .onStatus(
			            HttpStatus::is5xxServerError,
			            cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())))
			        .bodyToMono(PhotoWith.class);
		
		return photo;
	}
	
	@Override
	public Flux<PhotoWith> getMyPhotos() {
		
		String allPhotosUri = "http://localhost:9091/photos/photosMy";
			
		/** Here a Flux is expected */
		Flux<PhotoWith> photos = getWebClient()
			        .get()
			        .uri(allPhotosUri)
			       
			        .retrieve()
			             
			        .onStatus(
				            HttpStatus::is4xxClientError,
				            cr -> {				        
				            	return Mono.just(new IllegalArgumentException(cr.statusCode().getReasonPhrase()));})
			         
			        .onStatus(
			            HttpStatus::is5xxServerError,
			            cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())))
			        
			        .bodyToFlux(PhotoWith.class);
			        
			        
			        //.onErrorResume(er -> Mono.error(new RuntimeException("GRUNGE")));
			       
			       
		return photos;
	}
	
	
	@Override
	public Mono<byte[]> getImage(String id) {
		
		String uri = "http://localhost:9091/photos/doGetPhoto/" + id;
				
		Mono<byte[]> photo 
				= this.getWebClient().get().uri(uri)
				.retrieve()
				.bodyToMono(byte[].class);
						
		photo.subscribe(p -> System.out.println("SATOR " + p.length));
				
		return Mono.empty();
	}
	
	
	private WebClient getWebClient() {
		if (webClient == null) {
			webClient = webClientBuilder.build();
		}
		return webClient;
	}

	@Override
	public Mono<String> createPhoto(CreatePhotoComposite compo) {
	
		String createUri = sharewoodPhotosBaseURL + "/createPhoto";
		
		List<MediaType> amt = new ArrayList<>();
        amt.add(MediaType.APPLICATION_JSON);
        HttpHeaders headers = new HttpHeaders();
 		headers.setContentType(MediaType.APPLICATION_STREAM_JSON);
 		headers.setAccept(amt);// JSON expected from resource server
 		
 		// actual request to server
 		Mono<String> location = this.getWebClient()
				.method(HttpMethod.POST)
				.uri(createUri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(compo), CreatePhotoComposite.class)
				.exchange()
				
				.flatMap(catchErrorsAndTransformCreate);
	
 		return location;
	}

	@Override
	public Mono<Photo> updatePhoto(Photo photo) {
		
		String updateUri = sharewoodPhotosBaseURL + "/updatePhoto";
			 
		Mono<Photo> upPhoto = this.getWebClient()
					.method(HttpMethod.PUT)
					.uri(updateUri)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.body(Mono.just(photo), Photo.class)
					.exchange()
					.flatMap(t -> t.bodyToMono(Photo.class));

		return upPhoto;
	}
	
	@Override
	public Flux<PhotoWith> sharedPhotos() {
		
		//String sharedPhotosUri = "http://localhost:9091/photos/sharedPhotos";
		String sharedPhotosUri = sharewoodPhotosBaseURL + "/sharedPhotos";
			
		/** Here a Flux is expected */
		Flux<PhotoWith> photos = getWebClient()
			        .get()
			        .uri(sharedPhotosUri)			       
			        .retrieve()			        
			        .onStatus(
			            HttpStatus::is4xxClientError,
			            cr -> Mono.just(new IllegalArgumentException(cr.statusCode().getReasonPhrase())))
			        .onStatus(
			            HttpStatus::is5xxServerError,
			            cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())))
			        .bodyToFlux(PhotoWith.class);
		
		return photos;
	}
	
	// helper function returns Mono<String> if OK
	Function<ClientResponse, Mono<String>> catchErrorsAndTransformCreate = 
						(ClientResponse clientResponse) -> {
							if (clientResponse.statusCode().is5xxServerError()) {
								throw new RuntimeException();
							} else {
								Mono<ResponseEntity<Void>> respEnt = clientResponse.toBodilessEntity();				
								return respEnt.flatMap(s -> {
									String location = s.getHeaders().get("location").get(0);
									
									Matcher matcher = pattern.matcher(location); 
									
									if (matcher.matches()) {
										return Mono.just(s.getHeaders().get("location").get(0));
									} else {
										return Mono.error(new RuntimeException("CREATE ERROR"));
									}
									
								});
							}
			};

	@Override
	public Mono<String> getHealth() {
	
		String healthUri = "http://localhost:9091/actuator/health";
					
				Mono<String> health = getWebClient()
					        .get()
					        .uri(healthUri)			       
					        .retrieve()			        
					            
					        .bodyToMono(String.class);
				
				return health
						.onErrorResume(er -> Mono.error(new RuntimeException("Sharewood not available")));
				
	}
	
}
