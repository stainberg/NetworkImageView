package com.example.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public abstract class AbsImageLoaderRunnable implements Runnable {

	private String mUrl = null;
	private Bitmap mBitmap = null;
	private OnImageLoaderListener mListener = null;
	private String mMessage = "";
	private boolean mCacheable = false;
	
	public void setUrl(String url) {
		mUrl = url;
	}
	
	public void setListener(OnImageLoaderListener l) {
		mListener = l;
	}
	
	public void setCacheable(boolean cacheable) {
		mCacheable = cacheable;
	}
	
	@Override
	public void run() {
		if(mCacheable) {
			mBitmap = ImageCache.getInstance().getBitmap(MD5.ToMD5(mUrl));
			if(mBitmap != null) {//in memory or disk cache hit
				mListener.onLoaderComplition(mBitmap);
			} else {
				if(httpLoadImage()) {//load from network
					ImageCache.getInstance().setBitmap(MD5.ToMD5(mUrl), mBitmap);
					mListener.onLoaderComplition(mBitmap);
				} else {
					mListener.onLoaderError(mMessage);
				}
			}
		} else {
			if(httpLoadImage()) {//load from network
				mListener.onLoaderComplition(mBitmap);
			} else {
				mListener.onLoaderError(mMessage);
			}
		}
	}
	
	private boolean httpLoadImage() {
		try {
			HttpGet httpGet = new HttpGet();
			httpGet.setURI(new URI(mUrl));
			HttpResponse response = BaseHttpClient.getHttpObject().getHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();  
				InputStream inStream = entity.getContent();
				Options options = new Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				options.inSampleSize = 1;
				options.inPurgeable = true;
				options.inInputShareable = true;
				mBitmap = BitmapFactory.decodeStream(inStream, null, options);
				inStream.close();
				httpGet.abort();
				if(mBitmap == null) {
					return false;
				}
//				BaseHttpClient.getHttpObject().getHttpClient().getConnectionManager().shutdown();
				return true;
			}
			httpGet.abort();
//			BaseHttpClient.getHttpObject().getHttpClient().getConnectionManager().shutdown();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
