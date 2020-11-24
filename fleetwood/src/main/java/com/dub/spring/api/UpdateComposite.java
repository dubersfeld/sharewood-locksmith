package com.dub.spring.api;

import com.dub.spring.photo.PhotoWith;

public class UpdateComposite {

	PhotoWith photoWith;
	String name = "FOUTRE";
	
	PhotoUpdateForm form = new PhotoUpdateForm();
	
	
	public UpdateComposite(PhotoWith photoWith) {
		this.photoWith = photoWith;
		form.setId(photoWith.getId());
		form.setUsername(photoWith.getUsername());
		form.setTitle(photoWith.getTitle());
		form.setShared(photoWith.isShared());
	}

	public PhotoWith getPhotoWith() {
		return photoWith;
	}

	public void setPhotoWith(PhotoWith photoWith) {
		this.photoWith = photoWith;
	}

	public PhotoUpdateForm getForm() {
		return form;
	}

	public void setForm(PhotoUpdateForm form) {
		this.form = form;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
