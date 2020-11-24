package com.dub.spring.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.dub.spring.photo.Photo;
import com.dub.spring.photo.PhotoWith;
import com.dub.spring.service.PhotoService;
import com.dub.spring.utils.ImageHolder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class PhotoController {
	  
	@Value("${tempDir}")
	String tempDir;
	
	@Value("${sharewoodPhotosBaseURL}")
	String sharewoodPhotosBaseURL;
			
	@Autowired
	private ImageHolder imageHolder;
	
	@Autowired
	private PhotoService photoService;
	
	
	@GetMapping("/deletePhoto")
	public String deletePhoto(Model model) {
			
		model.addAttribute("getPhoto", new PhotoIdForm());
		return "deletePhoto";
	}
	
	@PostMapping("/deletePhoto")
	public Mono<String> deletePhoto(
					@ModelAttribute("getPhoto") PhotoIdForm form, 
					Model model) {	
		
		// first check server health
		Mono<String> health = this.photoService.getHealth();
		
		return health.flatMap(h -> {
					
			Mono<String> page = Mono.just(form.getId())
					.flatMap(id -> this.photoService.deletePhoto(id))
					.flatMap(deli -> {
						IReactiveDataDriverContextVariable reactiveDataDrivenMode =
					            new ReactiveDataDriverContextVariable(Flux.just(deli), 1);
						
					    model.addAttribute("photos", reactiveDataDrivenMode);
					    return Mono.just("deletePhotoSuccess");
					});
			
			return page;
		})
		.onErrorResume(er -> {
			model.addAttribute("error", er.getMessage());		
			return Mono.just("error");
		});
		
	}
	
	
	@GetMapping("/createPhotoMulti")
	public String createPhotoMulti(Model model) {
		
		model.addAttribute("photoMulti", new PhotoMultiForm());
	
		return "createPhotoMultipart";
	}
	
	
	@PostMapping(
			value = "/createPhotoMulti",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)      	 
	public Mono<String> uploadPhoto(
            @ModelAttribute("photoMulti") PhotoMultiForm form, 
            Model model) {	 
				
		// clean this code by moving it to a new private method
		System.out.println(form.getTitle());
	
		// Get name of uploaded file.
		FilePart uploadedFileRef = null;
		boolean shared = form.isShared();
		String title = form.getTitle();
		uploadedFileRef = form.getUploadedFile();
					
		String photoFilePath = 
				uploadedFileRef.filename();
			
		String path = tempDir + photoFilePath; 
    	File tempFile = new File(path);
		uploadedFileRef.transferTo(tempFile);
		
		// then convert uploaded file into byte[]			
		FileInputStream stream;
		try {
			stream = new FileInputStream(path);
		
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = stream.read(buffer);
			while (len >= 0) {
				out.write(buffer, 0, len);
				len = stream.read(buffer);
			}
			stream.close();
			System.out.println("out " + out.toByteArray().length);
			
			CreatePhotoComposite compo = new CreatePhotoComposite();
			compo.setShared(shared);
			compo.setTitle(title);
			compo.setImage(out.toByteArray());
				
			return Mono.just(compo)
					.flatMap(c -> this.photoService.createPhoto(c))
					.flatMap(loc -> {
				IReactiveDataDriverContextVariable reactiveDataDrivenMode =
						            new ReactiveDataDriverContextVariable(Flux.just(loc), 1);
  
				model.addAttribute("photos", reactiveDataDrivenMode);
		    	return Mono.just("createPhotoSuccess");			
				
			}).onErrorReturn("createPhotoFailure");
		
		} catch (Exception e) {
			e.printStackTrace();
			return Mono.just("createPhotoFailure");
		}
		 /** clean this stuff later by removing the try-catch construct */
	}
 	
	
	@GetMapping("/sharewood/photosList/{id}")
	public  ResponseEntity<byte[]> photo(@PathVariable long id) throws Exception {
	
		byte[] image = this.imageHolder.getImage(id);
			
        HttpHeaders headers = new HttpHeaders();
       
        headers.set("Content-Type", "image/jpeg");
        return new ResponseEntity<byte[]>(image, headers, HttpStatus.OK);
	}
	
	
	@GetMapping("/photosMyWith")  
	Mono<String> photosMyWith(final Model model) {
		
		// first check server health
		Mono<String> health = this.photoService.getHealth();
		
		return health.map(h-> {
			
			Flux<PhotoWith> photos = this.photoService.getMyPhotos();
			Flux<PhotoWith> photos2 = photos.doOnNext(t -> {
			
				this.imageHolder.putImage(t.getImage(), t.getId());
			
			});
			IReactiveDataDriverContextVariable reactiveDataDrivenMode =
		            new ReactiveDataDriverContextVariable(photos2, 1);
			
		    model.addAttribute("photos", reactiveDataDrivenMode);
	
			return "sharewoodList";
		})
		.onErrorResume(er -> {
			model.addAttribute("error", er.getMessage());
			return Mono.just("error");
		});
		
	}
	
	
	@GetMapping("/updatePhoto") 
	public String updatePhoto(final Model model) {
		
		model.addAttribute("getPhoto", new PhotoIdForm());
		
		return "updatePhoto1";
	}
	
	
	@PostMapping("/updatePhoto1")
	public Mono<String> updatePhoto1(
			@Valid @ModelAttribute("getPhoto") PhotoIdForm form, final Model model) {
			
		// first check server health
		Mono<String> health = this.photoService.getHealth();
		
		return health.flatMap(h -> {
			
			Mono<PhotoWith> photo = photoService.getPhotoById(form.getId().toString());
			
			Mono<String> page = photo.map(ph -> {
				imageHolder.putImage(ph.getImage(), ph.getId());	
				IReactiveDataDriverContextVariable reactiveDataDrivenMode =
				         new ReactiveDataDriverContextVariable(Flux.just(ph), 1);
				model.addAttribute("photos", reactiveDataDrivenMode);
				
				return "updatePhoto2";
			});
			
			return page;
		})
		.onErrorResume(er -> {
			model.addAttribute("error", er.getMessage());
			return Mono.just("error");
		});
		
	}
	
	
	@PostMapping(value = "/updatePhoto2")	
	public Mono<String> updatePhoto2(@Valid @ModelAttribute PhotoUpdateForm form,
			Model model) {
		
		Photo photo = new Photo();
		System.out.println("form " + form.getId() + " " 
					+ form.getTitle() + " " + form.getUsername() + " "
					+ form.isShared());
		photo.setId(form.getId());
		photo.setUsername(form.getUsername());
		photo.setTitle(form.getTitle());
		photo.setShared(form.isShared());
				
		return Mono.just(photo)
				.flatMap(ph -> this.photoService.updatePhoto(photo))
				.flatMap(p -> {
					IReactiveDataDriverContextVariable reactiveDataDrivenMode =
				            new ReactiveDataDriverContextVariable(Flux.just(p), 1);

					model.addAttribute("photos", reactiveDataDrivenMode);
					
					return Mono.just("updatePhotoSuccess");
				})
				.onErrorReturn("updatePhotoFailure");
		
	}
	
	@GetMapping("/photosSharedWith")  
	Mono<String> photoSharedWith(final Model model) {
		   
		// first check server health
		Mono<String> health = photoService.getHealth();
		
		return health.map(h -> {
			Flux<PhotoWith> photos = this.photoService.sharedPhotos();
			
			Flux<PhotoWith> photos2 = photos.doOnNext(t -> {
				System.out.println(t.getImage().length);
				this.imageHolder.putImage(t.getImage(), t.getId());
			});
			
			IReactiveDataDriverContextVariable reactiveDataDrivenMode =
		            new ReactiveDataDriverContextVariable(photos2, 1);
		     		
		    model.addAttribute("photos", reactiveDataDrivenMode);

			return "sharedPhotos";
		})
		.onErrorResume(er -> {
			model.addAttribute("error", er.getMessage());
			
			return Mono.just("error");
		});
		
	}
	
}
