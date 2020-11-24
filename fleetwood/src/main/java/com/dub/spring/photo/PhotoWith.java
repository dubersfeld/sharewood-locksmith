package com.dub.spring.photo;

public class PhotoWith extends Photo {
	
	private byte[] image;
	
	public PhotoWith() {
		super();
	}
	
	public PhotoWith(Photo photo, byte[] image) {
		super(photo);
		this.image = image;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	

}
