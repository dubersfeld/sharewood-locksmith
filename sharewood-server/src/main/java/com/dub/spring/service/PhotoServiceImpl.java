package com.dub.spring.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dub.spring.exceptions.NotFoundException;
import com.dub.spring.exceptions.UnauthorizedException;
import com.dub.spring.persistence.PhotoRepository;
import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class PhotoServiceImpl implements PhotoService {
	
	@Value("${baseDirPath}")
	private String baseDirPath;
	
	private Scheduler scheduler;
	
	@Autowired
	private PhotoRepository photoRepository;
	
	 
	@Autowired    
	public PhotoServiceImpl(Scheduler scheduler) {   
		this.scheduler = scheduler;  
	}
	
	
	@Override
	public Flux<PhotoWith> getPhotosMyWith(String username) {
		
		List<Photo> photos = this.photoRepository.findPhotosByUsername(username);
				
		Flux<PhotoWith> photosW = 
				asyncFlux(photos)
					.map(photoToPhotoWith); 
		
		return photosW;
		
	}
	
	
	@Override
	public Flux<PhotoWith> getSharedPhotos() {
		
		List<Photo> photos = this.photoRepository.findPhotosByShared(true);
				
		Flux<PhotoWith> photosW = 
				asyncFlux(photos)
					.map(photoToPhotoWith); 
		
		return photosW;
			
	}
	
	@Override
	public Mono<Long> createPhoto(byte[] image, String username, String title, boolean shared) {

		Photo photo = new Photo();
		photo.setUsername(username);
		photo.setShared(shared);
		photo.setTitle(title);
		
		long newId = this.photoRepository.save(photo).getId();
			
		String fileName = "photo" + "Tmp.jpg";
	
		String path = baseDirPath + fileName;
		
		try {
			FileOutputStream fos = new FileOutputStream(path);      
			fos.write(image);
			fos.close();
		
			// now change file name
			Path source = FileSystems.getDefault().getPath(baseDirPath, fileName);
			Path target = FileSystems.getDefault().getPath(baseDirPath, "photo" + newId + ".jpg");

			Files.move(source, target);
			
			return Mono.just(newId);
		} catch (IOException e) {
			return Mono.error(new RuntimeException());
		}
	}
	

	
	private Function<Photo, PhotoWith> photoToPhotoWith =
			
			photo -> {
				String path = this.baseDirPath + "photo" + photo.getId() + ".jpg";
				
				try {
					FileInputStream stream = new FileInputStream(path);
			
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = stream.read(buffer);
					while (len >= 0) {
					
						out.write(buffer, 0, len);
						len = stream.read(buffer);
					}
					stream.close();
				
					PhotoWith photoWith = new PhotoWith(photo, out.toByteArray());
			
					return photoWith;
			
				} catch (Exception e) {
				
					return null;
				}		
			};

	@Override
	public Mono<PhotoWith> getPhotoWith(long id, String username) {
	
		String path = baseDirPath + "photo" + id + ".jpg";
	
		Optional<Photo> photo = this.photoRepository.findById(id);
		
		// first check if photo is present
		if (photo.isEmpty()) {
			
			return Mono.error(NotFoundException::new);
		}
		
		try {
			FileInputStream stream = new FileInputStream(path);
		
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = stream.read(buffer);
			while (len >= 0) {
			
				out.write(buffer, 0, len);
				len = stream.read(buffer);
			}
			stream.close();
			
			PhotoWith photoWith = new PhotoWith(photo.get(), out.toByteArray());
		
			if (!username.equals(photoWith.getUsername())) {
				return Mono.error(UnauthorizedException::new);
			}
	
			return Mono.just(photoWith);
		} catch (Exception e) {
			return Mono.error(new RuntimeException());
		}
		
	}

	
	@Override
	public Mono<Long> deletePhoto(long photoId, String username) {
		
		// first retrieve Photo by Id
		Optional<Photo> photo = this.photoRepository.findById(photoId);
		
		// check if Photo exists
		if (photo.isEmpty()) {
			return Mono.error(NotFoundException::new);
		}
		Photo ph = photo.get();
		
		if (username.equals(ph.getUsername())) {
			this.photoRepository.delete(ph);
			
			String filename = "photo" + photoId + ".jpg";
			// delete actual image file			
			Path path = FileSystems.getDefault().getPath(baseDirPath, filename);
			
			try {
				Files.deleteIfExists(path);
				return Mono.just(photoId);
			} catch (IOException e) {
				e.printStackTrace();
				return Mono.error(new RuntimeException());
			}
			
		} else {
			return Mono.error(new UnauthorizedException());
		}
		
	}

	@Override
	public Mono<Photo> updatePhoto(Photo photo, String username) {
		Photo uPhoto = this.photoRepository.save(photo);
		return Mono.just(uPhoto);
	}

	private <T> Flux<T> asyncFlux(Iterable<T> iterable) {
        return Flux.fromIterable(iterable).publishOn(scheduler);
    }
	
}
