package com.xtremeprog.memoriv2.models;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Memori implements Parcelable {
	
	private ArrayList<Photo> photos;
	
	public Memori() {
		photos = new ArrayList<Photo>();
	}

	public static final Parcelable.Creator<Memori> CREATOR = new Parcelable.Creator<Memori>() {

		@Override
		public Memori createFromParcel(Parcel source) {
			Memori memori = new Memori();
			memori.photos = source.readArrayList(Photo.class.getClassLoader());
			return memori;
		}

		@Override
		public Memori[] newArray(int size) {
			return null;
		}
		
	};
	
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(photos.toArray());
	}
}
