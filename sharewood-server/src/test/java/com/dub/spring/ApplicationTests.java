package com.dub.spring;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.dub.spring.photo.CreatePhotoComposite;
import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment=RANDOM_PORT,
classes = {Application.class, TestSecurityConfig.class},
properties = {"spring.main.allow-bean-definition-overriding=true","eureka.client.enabled=false"})
@WithMockUser(username="alice", roles={"SHAREWOOD_USER"})
@TestMethodOrder(OrderAnnotation.class)
class ApplicationTests {

	static String baseTestPath = "/home/dominique/Documents/sharewood-locksmith";
	
	@Value("${basePhotosURL}")
	String basePhotosURL;
	
	@Autowired
	private WebTestClient client;// provided
	
	
	@BeforeAll
    public static void restore() {
	
    	// restore folder photos before tests
    	//String photos = "/home/dominique/Documents/sharewood-locksmith/photos";
    	//String photosSave = "/home/dominique/Documents/sharewood-locksmith/photosSave";
    	String photos = baseTestPath + "/photos";
    	String photosSave = baseTestPath + "/photosSave";
    	File source = new File(photosSave);
    	File dest = new File(photos);
    	
    	try {
			FileUtils.cleanDirectory(dest);
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	@Order(2)
	@Test
	void sharedPhotosTest() {
		
		client.get()
		.uri("/photos/sharedPhotos")
		.accept(MediaType.APPLICATION_JSON)
		.header("username", "alice")
		.attribute("username", "LOREM IPSUM")
		.exchange()
		.expectStatus().isEqualTo(OK)
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.returnResult(PhotoWith.class)
		.getResponseBody()
		.as(StepVerifier::create)
		.expectNextCount(1)
		.expectComplete()
		.verify();
	}
	
	
	@Order(1)
	@Test
	@Sql({"/photosDB.sql"})
	void photoByIdTest() {
		long id = 1L;
		client.get()
		.uri("/photos/photoById/" + id)
		.accept(MediaType.APPLICATION_JSON)
		.header("username", "alice")
		.exchange()
		.expectStatus().isEqualTo(OK)
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.returnResult(Photo.class)
		.getResponseBody()
		.as(StepVerifier::create)
		.expectNextMatches(entity -> entity.getTitle().equals("photo1"))
		.expectComplete()
		.verify();
	}
	

	@Order(3)
	@Test
	void createPhotoTest() {
		
		String path = baseTestPath + "/pictures/siamese.jpg";
		//String path = "/home/dominique/Pictures/siamese.jpg";
    	byte[] image = TestUtils.importImage(path);
    	   
    	CreatePhotoComposite comp = new CreatePhotoComposite(image, "Siamese", false);
    	
    	client.post()
		.uri("/photos/createPhoto")
		.header("username", "alice")
		.body(Mono.just(comp), CreatePhotoComposite.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().valueEquals("location", basePhotosURL + "/photoById/7");
	
	}
	
	@Order(4)
	@Test
	void afterCreateTest() {
		
		client.get()
		.uri("/photos/photoById/7")
		.accept(MediaType.APPLICATION_JSON)
		.header("username", "alice")
		.exchange()
		.expectStatus().isEqualTo(OK)
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.returnResult(PhotoWith.class)
		.getResponseBody()
		.as(StepVerifier::create)
		.expectNextCount(1)
		.expectComplete()
		.verify();
		
	}
	
	@Order(6)
	@Test
	void afterDeleteTest() {
		
		Long id = 1L;
		client.get()
		.uri("/photos/photoById/" + id)
		.accept(MediaType.APPLICATION_JSON)
		.header("username", "alice")
		.exchange()
		.expectStatus().isNotFound();
	}
	
	
	@Order(5)
	@Test
	void deletePhotoTest() {
		
		Long id = 1L;
		client.delete()
		.uri("/photos/deletePhoto/" + id)
		.accept(APPLICATION_JSON)
		.header("username", "alice")
		.exchange()
		.expectStatus().is2xxSuccessful()
		.returnResult(Long.class)
		.getResponseBody()
		.as(StepVerifier::create)
		.expectNext(1L)
		
		.expectComplete()
		.verify();
			
	}
	
	
	@Order(7)
	@Test
	void illegalDeleteTest() {
		
		Long id = 2L;
		client.delete()
		.uri("/photos/deletePhoto/" + id)
		.accept(APPLICATION_JSON)
		.header("username", "alice")
		.exchange()
		.expectStatus().is4xxClientError();
	}
	
	@AfterAll
    public static void restoreAfter() {
	
    	// restore folder photos before tests
    	String photos = "/home/dominique/Documents/sharewood-locksmith/photos";
    	String photosSave = "/home/dominique/Documents/sharewood-locksmith/photosSave";
    	File source = new File(photosSave);
    	File dest = new File(photos);
    	
    	try {
			FileUtils.cleanDirectory(dest);
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
