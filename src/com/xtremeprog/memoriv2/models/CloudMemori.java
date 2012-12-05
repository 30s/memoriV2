package com.xtremeprog.memoriv2.models;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class CloudMemori implements Parcelable {

	private String guid;
	private String invite_code;
	private long start_timestamp;
	private ArrayList<String> owners;
	private ArrayList<String> photos;

	public CloudMemori(String guid, String invite_code, long start_timestamp,
			ArrayList<String> owners, ArrayList<String> photos) {
		this.set_guid(guid);
		this.set_invite_code(invite_code);
		this.start_timestamp = start_timestamp;
		this.owners = owners;
		this.photos = photos;
	}
	
	public static final Parcelable.Creator<CloudMemori> CREATOR = new Parcelable.Creator<CloudMemori>() {

		@Override
		public CloudMemori createFromParcel(Parcel source) {
			return new CloudMemori(source.readString(), 
					source.readString(), 
					source.readLong(), 
					source.readArrayList(String.class.getClassLoader()), 
					source.readArrayList(String.class.getClassLoader()));
		}

		@Override
		public CloudMemori[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	public String get_cover() {
		if ( photos.size() > 0 ) {
			return photos.get(0);
		}
		return "";
	}

	public long get_start_timestamp() {
		return start_timestamp;
	}

	public void set_start_timestamp(long start_timestamp) {
		this.start_timestamp = start_timestamp;
	}

	public ArrayList<String> get_owners() {
		return owners;
	}

	public void set_owners(ArrayList<String> owners) {
		this.owners = owners;
	}

	public String get_guid() {
		return guid;
	}

	public void set_guid(String guid) {
		this.guid = guid;
	}

	public String get_invite_code() {
		return invite_code;
	}

	public void set_invite_code(String invite_code) {
		this.invite_code = invite_code;
	}

	public ArrayList<String> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<String> photos) {
		this.photos = photos;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(guid);
		dest.writeString(invite_code);
		dest.writeLong(start_timestamp);
		dest.writeArray(owners.toArray());
		dest.writeArray(photos.toArray());
	}
}
