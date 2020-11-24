package com.dub.spring.api;

import org.springframework.core.io.FileSystemResource;

public class CreatePhotoComposite {
	
	byte[] image;
	String title;
	boolean shared;
	
	public CreatePhotoComposite(
				byte[] image,
				String title,
				boolean shared) {
		this.shared = shared;
		this.title = title;
		this.image = image;
	}
	
	public CreatePhotoComposite() {
		
	}
	
	
	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	

	

}
