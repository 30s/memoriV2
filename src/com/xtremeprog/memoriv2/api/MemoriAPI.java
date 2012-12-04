package com.xtremeprog.memoriv2.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.xtremeprog.memoriv2.R;
import com.xtremeprog.memoriv2.models.Memori;
import com.xtremeprog.memoriv2.models.Photo;
import com.xtremeprog.memoriv2.net.ApiBase;
import com.xtremeprog.memoriv2.net.ApiRequest;
import com.xtremeprog.memoriv2.net.ApiResponse;
import com.xtremeprog.memoriv2.net.HttpEntityWithProgress.ProgressListener;
import com.xtremeprog.memoriv2.utils.Preferences;
import com.xtremeprog.memoriv2.utils.Utils;

public class MemoriAPI extends ApiBase implements IMemoriAPI {

	private static IMemoriAPI INSTANCE;
	private Context context;

	private static boolean compressPhoto = true;
	private static CompressFormat mCompressFormat = CompressFormat.JPEG;
	private static int mCompressQuality = 70;
	private static int maxSize = 480;
	public static final int IO_BUFFER_SIZE = 8 * 1024;

	public MemoriAPI(Context context) {
		super(context);
		this.context = context;
	}

	public static IMemoriAPI getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new MemoriAPI(context);
		}
		return INSTANCE;
	}

	@Override
	public ApiResponse execute(ApiRequest request, ProgressListener listener)
			throws ClientProtocolException, IOException {
		if ( !request.getPath().equals("/v1/account/login/") ) {
			request.addHeader("AUTHORIZATION",
					"Bearer " + Preferences.getToken(context));	
		}
		return super.execute(request, listener);
	}

	public JSONObject account_login(String username, String password,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.POST,
				"/v1/account/login/", ApiRequest.UPLOAD);
		request.addParameter("username", username);
		request.addParameter("password", password);
		request.addParameter("client_id", context.getString(R.string.apikey));

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());		
	}
	
	private boolean writeBitmapToFile(Bitmap bitmap, File file)
			throws IOException, FileNotFoundException {

		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file),
					MemoriAPI.IO_BUFFER_SIZE);
			return bitmap.compress(mCompressFormat, mCompressQuality, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	public JSONObject photo_upload(File file, Photo photo, File voiceFile,
			String memori, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException {

		ApiRequest request = new ApiRequest(ApiRequest.POST,
				"/v1/photo/upload/", ApiRequest.UPLOAD);
		request.setMime(true);

		File tempFile = null;
		if (compressPhoto) {
			Bitmap bitmap = Utils.decodeFile(file, maxSize, true);
			if (bitmap != null) {
				try {
					tempFile = File.createTempFile("photoVoice", ".jpg");
					if (!writeBitmapToFile(bitmap, tempFile))
						tempFile = null;
				} catch (IOException e) {
					tempFile = null;
				}
			}
		}

		request.addParameter("shotted_at", android.text.format.DateFormat
				.format("yyyy-MM-dd kk:mm:ss", photo.getDate_taken() / 1000)
				.toString());
		request.addParameter("memori", memori);

		if (tempFile != null) {
			request.addFileParameter("photo", tempFile);
		} else {
			request.addFileParameter("photo", file);
		}
		if ((voiceFile != null) && voiceFile.exists()) {
			request.addFileParameter("voice", voiceFile);
		}
		ApiResponse response = execute(request, progressListener);
		if (tempFile != null) {
			tempFile.delete();
		}

		return new JSONObject(response.getContentAsString());
	}

	public JSONObject memori(String next, ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.GET,
				next == null ? "/v1/memori/" : next, ApiRequest.UPLOAD);

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());		
	}
	
	public JSONObject memori_create(Memori memori,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.POST,
				"/v1/memori/create/", ApiRequest.UPLOAD);
		request.addParameter("start_timestamp", memori.get_photo(0)
				.getDate_taken() / 1000 + "");

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}

	public JSONObject memori_join(String url, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.GET, url,
				ApiRequest.UPLOAD);
		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}

}
