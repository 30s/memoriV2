package com.xtremeprog.memoriv2.models;

import java.util.ArrayList;

public class Memori {
	
	private ArrayList<Photo> photos;
	
	public Memori() {
		photos = new ArrayList<Photo>();
	}
	
	public void add_photo(Photo photo) {
		photos.add(photo);
	}
	
	public String get_cover() {
		return photos.get(0).getPath();
	}
	
	public Photo get_photo(int index) {
		return photos.get(index);
	}
	
	public int get_photo_count() {
		return photos.size();
	}
}
