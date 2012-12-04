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

import com.xtremeprog.memoriv2.models.Photo;
import com.xtremeprog.memoriv2.net.ApiBase;
import com.xtremeprog.memoriv2.net.ApiRequest;
import com.xtremeprog.memoriv2.net.ApiResponse;
import com.xtremeprog.memoriv2.net.HttpEntityWithProgress.ProgressListener;
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
		if ( INSTANCE == null ) {
			INSTANCE = new MemoriAPI(context);
		}
		return INSTANCE;
	}
	
    private boolean writeBitmapToFile(Bitmap bitmap, File file)
            throws IOException, FileNotFoundException {

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), MemoriAPI.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }	

	@Override
	public JSONObject photo_upload(File file, Photo photo, File voiceFile,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException {
		
        ApiRequest request = new ApiRequest(ApiRequest.POST,
                "/v1/photo/upload/", ApiRequest.UPLOAD);
        request.setMime(true);
        
        File tempFile = null;
        if (compressPhoto) {
        	 Bitmap bitmap = Utils.decodeFile(file, maxSize, true);
        	 if (bitmap != null) {
	        	 try {
		        	 tempFile = File.createTempFile("photoVoice", ".jpg");
		        	 if (!writeBitmapToFile(bitmap, tempFile)) tempFile = null;
	        	 } catch (IOException e) {
	        		 tempFile = null;
	        	 }
        	 }
        }
        
        request.addParameter("shotted_at", android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", photo.getDate_taken()/1000).toString());

        if (tempFile != null) {
        	request.addFileParameter("photo", tempFile);
        } else {
        	request.addFileParameter("photo", file);
        }
        if (voiceFile.exists()) {
        	request.addFileParameter("voice", voiceFile);
        }
        ApiResponse response = execute(request, progressListener);
        if (tempFile != null) {
        	tempFile.delete();
        }

        return new JSONObject(response.getContentAsString());        
	}

}
