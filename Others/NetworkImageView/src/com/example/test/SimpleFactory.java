package com.example.test;

public class SimpleFactory {
	
	private static SimpleFactory instance = null;
	private final static Object mMutex = new Object();
	
	private SimpleFactory() {
		
	}
	
	public static SimpleFactory getFactory() {
		if(instance == null) {
			synchronized (mMutex) {
				if(instance == null) {
					instance = new SimpleFactory();
				}
			}
		}
		return instance;
	}
	
	public <T> T getInstance(Class<T> cls, String url, OnImageLoaderListener l, String stag, String tag, boolean cacheable) {
		T result = null;
		try {
			result = cls.newInstance();
			if(result instanceof AbsImageLoaderRunnable) {
				((AbsImageLoaderRunnable) result).setUrl(url);
				((AbsImageLoaderRunnable) result).setListener(l);
				((AbsImageLoaderRunnable) result).setCacheable(cacheable);
				((AbsImageLoaderRunnable) result).setCategoryTag(tag);
				((AbsImageLoaderRunnable) result).setSingleTag(stag);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInstance(String clsName) {
		T result = null;
		try {
			result = (T) Class.forName(clsName).newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}

