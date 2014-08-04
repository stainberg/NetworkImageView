package com.example.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;

public class ImageCache {

	private static final Object mMutex = new Object();
	private static ImageCache instance = null;
	private static LruCache<String, Bitmap> mMemoryCache;
	private String mImagePathDir = null;
	private static final String PATH_SD_CARD = Environment.getExternalStorageDirectory().toString();
	
	private ImageCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap){
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
        if(mImagePathDir == null) {
        	mImagePathDir = PATH_SD_CARD + File.separator + "Stainberg" + File.separator + "image";
        	File file = new File(mImagePathDir);
        	if(file.exists()) {
        		if(file.isDirectory()) {
    				return;
    			}
    		}
        	file.mkdirs();
        }
	}
	
	public static final ImageCache getInstance() {
		if(instance == null) {
			synchronized (mMutex) {
				if(instance == null) {
					instance = new ImageCache();
				}
			}
		}
		return instance;
	}
	
	public Bitmap getBitmap(String key) {
		Bitmap b = null;
		b = mMemoryCache.get(key);
		if(b == null) {
			b = diskLoadBitmap(key);
		}
		return b;
	}
	
	public synchronized void setBitmap(String key, Bitmap value) {
		mMemoryCache.put(key, value);
		diskSaveBitmap(key, value);
	}
	
	public void setImagePathDir(String dir) {
		mImagePathDir = dir;
	}
	
	private Bitmap diskLoadBitmap(String key) {
		if(mImagePathDir != null) {
			File file = new File(mImagePathDir + File.separator + key);
			if(file.exists()) {
				return BitmapFactory.decodeFile(file.getPath());
			}
		}
		return null;
	}
	
	private void diskSaveBitmap(String key, Bitmap b) {
		if(key == null || b == null) {
			return;
		}
		try {
			File destFile = null;
			if(mImagePathDir != null) {
				destFile = new File(mImagePathDir + File.separator + key);
			} else {
				
			}
			if(destFile.exists()) {
				destFile.delete();
			}
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
			b.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
