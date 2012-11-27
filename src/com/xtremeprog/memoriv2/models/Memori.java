package com.xtremeprog.memoriv2.models;

import java.util.ArrayList;

public class Memori {
	
	private ArrayList<String> photos;
	
	public Memori() {
		photos = new ArrayList<String>();
	}
	
	public void add_photo(String photo) {
		photos.add(photo);
	}
	
	public String get_cover() {
		return photos.get(0);
	}
	
	public String get_photo(int index) {
		return photos.get(index);
	}
	
	public int get_photo_count() {
		return photos.size();
	}
}
