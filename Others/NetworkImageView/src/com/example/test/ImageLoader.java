package com.example.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageLoader {

	private static ImageLoader instance = null;
	private static final Object mMutex = new Object();
	private ExecutorService mPool = null;
	
	private ImageLoader() {
		mPool = Executors.newFixedThreadPool(2);
	}
	
	public static final ImageLoader getInstance() {
		if(instance == null) {
			synchronized (mMutex) {
				if(instance == null) {
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}
	
	public void excute(String url, OnImageLoaderListener l, boolean cacheable) {
		ImageRunnable r = SimpleFactory.getFactory().getInstance(ImageRunnable.class, url, l, cacheable);
		mPool.execute(r);
	}
	
	public void canncel(String url) {
		
	}
	
}
