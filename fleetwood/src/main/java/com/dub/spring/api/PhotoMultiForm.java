package com.dub.spring.api;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

//import org.springframeworSynchronossFilePart;

/* Command object */
public class PhotoMultiForm {
	
	private FilePart uploadedFile;
	private String title;
	private boolean shared;
	
	public FilePart getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(FilePart uploadedFile) {
		this.uploadedFile = uploadedFile;
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