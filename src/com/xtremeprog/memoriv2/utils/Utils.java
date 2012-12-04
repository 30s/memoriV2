package com.xtremeprog.memoriv2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
