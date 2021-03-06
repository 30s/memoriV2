package com.xtremeprog.memoriv2.api;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.xtremeprog.memoriv2.models.Memori;
import com.xtremeprog.memoriv2.models.Photo;
import com.xtremeprog.memoriv2.net.HttpEntityWithProgress.ProgressListener;

public interface IMemoriAPI {
	JSONObject account_login(String username, String password,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException;

	JSONObject photo_upload(File file, Photo photo, File voiceFile,
			String memori, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;

	JSONObject memori(String next,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException;

	JSONObject memori_create(Memori memori, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;

	JSONObject memori_join(String url, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;
}
