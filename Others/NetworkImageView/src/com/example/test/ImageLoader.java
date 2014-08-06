package com.example.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class ImageLoader {

	private static ImageLoader instance = null;
	private static final Object mMutex = new Object();
	private ThreadPoolExecutor mPool = null;
	
	private ImageLoader() {
		mPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
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
	
	public void excute(String url, OnImageLoaderListener l, String stag, String tag, boolean cacheable) {
		ImageRunnable r = SimpleImageFactory.getFactory().getInstance(ImageRunnable.class, url, l, stag, tag, cacheable);
		mPool.execute(r);
	}
	
	public void canncelByCategoryTag(String tag) {
		BlockingQueue<Runnable> queue = mPool.getQueue();
		for(Runnable r : queue) {
			if(r instanceof ImageRunnable) {
				if(((ImageRunnable) r).getCategoryTag().equals(tag)) {
					mPool.remove(r);
				}
			}
		}
	}
	
	public void canncelAll() {
		BlockingQueue<Runnable> queue = mPool.getQueue();
		for(Runnable r : queue) {
			mPool.remove(r);
		}
	}
	
	public void canncelByTag(String tag) {
		BlockingQueue<Runnable> queue = mPool.getQueue();
		for(Runnable r : queue) {
			if(r instanceof ImageRunnable) {
				if(((ImageRunnable) r).getSingleTag().equals(tag)) {
					mPool.remove(r);
					return;
				}
			}
		}
	}
}
