package com.dub.spring;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import com.dub.spring.photo.PhotoWith;

import reactor.core.publisher.Mono;

// This class should export static methods only

public class TestUtils {
	
	// import an image file given by its full path
	public static byte[] importImage(String path) {
		
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
			
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Import Image Error");
		}
	
	}

}
