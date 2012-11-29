package com.xtremeprog.memoriv2.models;

public class Photo {
	
	private String path;
	private long date_taken;
	
	public Photo(String path, long date_taken) {
		this.setPath(path);
		this.setDate_taken(date_taken);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getDate_taken() {
		return date_taken;
	}

	public void setDate_taken(long date_taken) {
		this.date_taken = date_taken;
	}
	
	
}
