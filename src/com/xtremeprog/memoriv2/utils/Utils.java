package com.xtremeprog.memoriv2.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xtremeprog.memoriv2.R;
import com.xtremeprog.memoriv2.models.Memori;

public class Utils {

	// private static String TAG = Utils.class.getSimpleName();

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static String read(InputStream in) throws IOException,
			UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(
				new InputStreamReader(in, "UTF-8"), 1024);
		char[] buf = new char[1024];
		for (int read = r.read(buf); read != -1; read = r.read(buf)) {
			sb.append(buf, 0, read);
		}
		in.close();
		return sb.toString();
	}

	public static JSONObject login(Context context, String username,
			String password) {
		InputStream is = null;
		OutputStream os = null;
		HttpURLConnection conn = null;

		HashMap<String, String> ret = new HashMap<String, String>();
		JSONObject json = null;
		ret.put("result", "failed");

		try {
			String query = String.format(
					"username=%s&password=%s&client_id=%s", URLEncoder.encode(
							username, "utf-8"), URLEncoder.encode(password,
							"utf-8"), URLEncoder.encode(
							context.getString(R.string.apikey), "utf-8"));
			URL url = new URL(Preferences.getServer(context)
					+ "/v1/account/login/");
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			os = conn.getOutputStream();
			os.write(query.getBytes("utf-8"));

			conn.connect();
			is = conn.getInputStream();
			json = new JSONObject(Utils.read(is));
			if (json.has("token")) {
				Preferences
						.setLoginInfo(context, json.getString("token"),
								json.getString("refresh_token"),
								json.getLong("expire"));
			} else {
				Preferences.expireToken(context);
			}
		} catch (UnsupportedEncodingException e) {
			ret.put("message", "Encoding error!");
		} catch (MalformedURLException e) {
			ret.put("message", "URL error!");
		} catch (IOException e) {
			ret.put("message", "Open connection error!");
		} catch (JSONException e) {
			ret.put("message", "Decode response error!");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		if (json == null) {
			json = new JSONObject(ret);
		}
		return json;
	}

	public static JSONObject create_memori(Context context, Memori memori) {
		InputStream is = null;
		OutputStream os = null;
		HttpURLConnection conn = null;

		HashMap<String, String> ret = new HashMap<String, String>();
		JSONObject json = null;
		try {
			String query = String.format(
					"start_timestamp=%s",
					URLEncoder.encode(memori.get_photo(0).getDate_taken()
							/ 1000 + "", "utf-8"));

			URL url = new URL(Preferences.getServer(context)
					+ "/v1/memori/create/");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("AUTHORIZATION",
					"Bearer " + Preferences.getToken(context));
			conn.connect();

			os = conn.getOutputStream();
			os.write(query.getBytes("utf-8"));

			conn.connect();
			is = conn.getInputStream();
			json = new JSONObject(Utils.read(is));
		} catch (UnsupportedEncodingException e) {
			ret.put("message", "Encoding error!");
		} catch (MalformedURLException e) {
			ret.put("message", "URL error!");
		} catch (IOException e) {
			ret.put("message", "Open connection error!");
		} catch (JSONException e) {
			ret.put("message", "Decode response error!");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		if (json == null) {
			json = new JSONObject(ret);
		}
		return json;
	}

	public static JSONObject load_memori_list(Context context,
			ArrayList<JSONObject> memoris) {
		HashMap<String, String> ret = new HashMap<String, String>();

		InputStream is = null;
		HttpURLConnection conn = null;
		String path = "/v1/memori/?format=json";

		while (!path.equals("null")) {
			try {
				URL url = new URL(Preferences.getServer(context) + path);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.setRequestProperty("AUTHORIZATION", "Bearer "
						+ Preferences.getToken(context));
				conn.connect();

				try {
					int response = conn.getResponseCode();
					if (response != 200) {
						ret.put("message", response + "");
						return new JSONObject(ret);
					}
				} catch (IOException e) {
					// assume it's a 401
					ret.put("message", "401");
					return new JSONObject(ret);
				}

				is = conn.getInputStream();
				JSONObject json = new JSONObject(Utils.read(is));
				JSONObject meta = json.getJSONObject("meta");
				path = meta.getString("next");
				if (!json.has("objects")) {
					continue;
				}

				JSONArray json_array = json.getJSONArray("objects");
				for (int i = 0; i < json_array.length(); i++) {
					memoris.add(json_array.getJSONObject(i));
				} // for
			} catch (MalformedURLException e) {
				ret.put("message", "URL error!");
				path = "null";
			} catch (IOException e) {
				ret.put("message", "Open connection error!");
				path = "null";
			} catch (JSONException e) {
				ret.put("message", "JSON error!");
				path = "null";
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		} // while

		return new JSONObject(ret);
	}
	
	//decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f, int size, boolean stopIfNoScaleNeeded){
	    try {
	        //decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	        
	        //Find the correct scale value. It should be the power of 2.
	        final int REQUIRED_SIZE=size;
	        int width_tmp=o.outWidth, height_tmp=o.outHeight;
	        int scale=1;
	        while(true){
	            if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                break;
	            width_tmp/=2;
	            height_tmp/=2;
	            scale*=2;
	        }
	        
	        if (stopIfNoScaleNeeded && scale == 1) {
	        	return null;
	        }
	        //decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=BitmapUtils.computeSampleSizeLarger(o.outWidth, o.outHeight, size);
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
    // Returns the next power of two.
    // Returns the input if it is already power of 2.
    // Throws IllegalArgumentException if the input is <= 0 or
    // the answer overflows.
    public static int nextPowerOf2(int n) {
        if (n <= 0 || n > (1 << 30)) throw new IllegalArgumentException();
        n -= 1;
        n |= n >> 16;
        n |= n >> 8;
        n |= n >> 4;
        n |= n >> 2;
        n |= n >> 1;
        return n + 1;
    }	
    
    // Returns the previous power of two.
    // Returns the input if it is already power of 2.
    // Throws IllegalArgumentException if the input is <= 0
    public static int prevPowerOf2(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        return Integer.highestOneBit(n);
    }
    
    // Throws AssertionError if the input is false.
    public static void assertTrue(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }    
}
