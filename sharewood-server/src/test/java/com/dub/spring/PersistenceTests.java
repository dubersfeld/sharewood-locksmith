package com.dub.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.dub.spring.persistence.PhotoRepository;
import com.dub.spring.photo.Photo;


@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class PersistenceTests {

    @Autowired
    private PhotoRepository repository;

    
    @Order(4)
    @Test
   	public void createTest() {

        Photo newPhoto = new Photo();
        newPhoto.setUsername("alice");
        newPhoto.setTitle("Siamese");
        newPhoto.setShared(false);
        
        Photo savedPhoto = repository.save(newPhoto);

        Photo foundPhoto = repository.findById(savedPhoto.getId()).get();
        assertEqualsPhoto(newPhoto, foundPhoto);

        assertEquals(4, repository.findPhotosByUsername("alice").size());
    }
    
    @Order(6)
    @Test
   	public void notFoundTest() {
    	
    	Optional<Photo> rPhoto = repository.findById(42L);
    	assertTrue(rPhoto.isEmpty());
    
    }
    
   
    @Order(3)
    @Test
    public void updateTest() {
    	Photo rPhoto = repository.findById(1L).get();
    	rPhoto.setShared(true);
    	repository.save(rPhoto);
    	
    	// retrieve shared Photo
    	List<Photo> sPhotos = repository.findPhotosByShared(true);
    	
    	assertEquals(2, sPhotos.size());
    }
   
    
    @Order(1)
    @Test
    @Sql({"/photosDB.sql"})
    public void findByUsernameTest() {
    	List<Photo> uPhotos = repository.findPhotosByUsername("alice");
    	
    	assertEquals(3, uPhotos.size());
    }
    
    
    @Order(2)
    @Test
    @Sql({"/photosDB.sql"})
    public void findSharedTest() {
    	List<Photo> uPhotos = repository.findPhotosByShared(true);
    		
    	assertEquals(1, uPhotos.size());
    }
    
    
    @Order(5)
    @Test
    public void deleteTest() {
    	
    	Photo dPhoto = repository.findPhotosByUsername("carol").get(0);
    	
    	repository.deleteById(dPhoto.getId());
    	
    	assertEquals(2, repository.findPhotosByUsername("carol").size());
    }
    
    

	private void assertEqualsPhoto(Photo expectedPhoto, Photo actualPhoto) {
        assertEquals(expectedPhoto.getId(),        actualPhoto.getId());
        assertEquals(expectedPhoto.getUsername(),  actualPhoto.getUsername());
        assertEquals(expectedPhoto.isShared(), actualPhoto.isShared());
        assertEquals(expectedPhoto.getTitle(),  actualPhoto.getTitle());
    }
	
}
