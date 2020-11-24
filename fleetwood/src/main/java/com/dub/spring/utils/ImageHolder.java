package com.dub.spring.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ImageHolder {
	
	Map<Long, ImageWrap> images = new HashMap<>();

	public Map<Long, ImageWrap> getImages() {
		return images;
	}

	public void setImages(Map<Long, ImageWrap> images) {
		this.images = images;
	}
	
	public void putImage(byte[] image, long id) {
		System.out.println("Adding image " + image.length);
		ImageWrap wrap = new ImageWrap(image);
		
		this.images.put(id, wrap);
	}
	
	public byte[] getImage(long id) {
		
		byte[] image = this.images.get(id).getBytes();
		return image;
		
	}
	
	static class ImageWrap {
		
		byte[] bytes;
		
		public ImageWrap(byte[] bytes) {
			this.bytes = bytes; 
		}

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}
		
		
	}

}
