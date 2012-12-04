package com.xtremeprog.memoriv2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
	
	private String path;
	private long date_taken;
	
	public Photo(String path, long date_taken) {
		this.setPath(path);
		this.setDate_taken(date_taken);
	}
	
	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {

		@Override
		public Photo createFromParcel(Parcel source) {
			return new Photo(source.readString(), source.readLong());
		}

		@Override
		public Photo[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}};

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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(path);
		dest.writeLong(date_taken);
	}
	
	
}
