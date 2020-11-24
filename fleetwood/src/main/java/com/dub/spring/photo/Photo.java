package com.dub.spring.photo;

/** Photo entity is the main business entity in the application
 * */

public class Photo {
	private long id;
	private String title;
	private String username;
	private boolean shared;
	
	public Photo() {
		
	}
	
	public Photo(Photo that) {
		this.id = that.id;
		this.title = that.title;
		this.username = that.username;
		this.shared = that.shared;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}	
}