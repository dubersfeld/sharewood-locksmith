package com.dub.spring;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.dub.spring.exceptions.NotFoundException;
import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;
import com.dub.spring.service.PhotoService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@WithMockUser(username="alice", roles={"SHAREWOOD_USER"})
public class ServiceTests {

	static String baseTestPath = "/home/dominique/Documents/sharewood-locksmith";
	
	
	@Autowired
	private PhotoService photoService;
	
	private Predicate<PhotoWith> getByIdPred = 
			ph -> 
				ph.getTitle().equals("photo1")
				&& 
				ph.getImage().length == 7487;
				
	private Predicate<PhotoWith> createPhotoPred = 
			ph -> ph.getImage().length == 5591;
			
	private Predicate<Photo> updatePhotoPred = 
			ph -> ph.isShared();					
	
	@BeforeAll
    public static void restore() {
    	// restore folder photos before tests
    	//String photosDir = "/home/dominique/Documents/sharewood-locksmith/photos";
    	//String photosSaveDir = "/home/dominique/Documents/sharewood-locksmith/photosSave";
    	
     	String photosDir = baseTestPath + "/photos";
    	String photosSaveDir = baseTestPath + "/photosSave";
    	File source = new File(photosSaveDir);
    	File dest = new File(photosDir);
    	
    	try {
			FileUtils.cleanDirectory(dest);
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	/** remove all block() later
	 * */
	@Order(1)
	@Test
	@Sql({"/photosDB.sql"})
	public void getPhotoWithByIdTest() {
		Mono<PhotoWith> photo = this.photoService.getPhotoWith(1L, "alice");
		StepVerifier.create(photo)
		.expectNextMatches(getByIdPred)
		.verifyComplete();
	}
	
	
	@Order(2)
	@Test
	public void createPhotoWithTest() {
		
		String path = baseTestPath + "/pictures/siamese.jpg";
		//String path = "/home/dominique/Pictures/siamese.jpg";
	    	
		byte[] image = TestUtils.importImage(path);
	    	     
		Mono<Long> newId = this.photoService.createPhoto(image, "alice", "Siamese", true);
		Mono<PhotoWith> photo = newId.flatMap(id -> this.photoService.getPhotoWith(id, "alice"));
			
		StepVerifier.create(photo)
			.expectNextMatches(createPhotoPred)
			.verifyComplete();
				
	}

	
	@Order(3)
    @Test
	//@Sql({"/photosDB.sql"})
    public void updatePhotoTest() {
		
    	Mono<PhotoWith> photoW = photoService.getPhotoWith(1L, "alice");
        
    	// create a new Photo entity
    	Mono<Photo> photo = photoW.flatMap(ph -> Mono.just(createPhotoEntity(ph.getId(), ph.getTitle(), ph.getUsername(), true)));
    	photo.hasElement().subscribe(System.out::println);
        
    	Mono<Photo> uPhoto = photo.flatMap(p -> this.photoService.updatePhoto(p, "alice"));
         	
    	Mono<PhotoWith> uPhotoW = uPhoto.flatMap(up -> this.photoService.getPhotoWith(up.getId(), "alice"));
    	
    	uPhotoW.hasElement().subscribe(System.out::println);
    	
    	StepVerifier.create(uPhotoW)
    	.expectNextMatches(updatePhotoPred)
    	.verifyComplete();
    	
    }
	

	@Order(4)
    @Test 
    public void deletePhotoTest() {
    	Mono<Long> delId = photoService.deletePhoto(7L, "alice");
    	
    	delId.subscribe();
    	
    	Mono<PhotoWith> check = this.photoService.getPhotoWith(7L, "alice");
    	 	
    	StepVerifier.create(check)
    	.expectError(NotFoundException.class)
    	//.expectError(UnauthorizedException.class)
    	.verify();
    	
    }
	
	
	@AfterAll
    public static void restoreAfter() {
    	// restore folder photos before tests
    	
     	String photosDir = baseTestPath + "/photos";
    	String photosSaveDir = baseTestPath + "/photosSave";
    	File source = new File(photosSaveDir);
    	File dest = new File(photosDir);
    	
    	try {
			FileUtils.cleanDirectory(dest);
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	

	private static Photo createPhotoEntity(long id, String title, String username, boolean shared) {
		Photo photo = new Photo();
		photo.setId(id);
		photo.setTitle(title);
		photo.setUsername(username);
		photo.setShared(shared);
		return photo;		
	}
}
