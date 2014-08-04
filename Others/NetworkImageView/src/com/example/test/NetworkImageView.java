package com.example.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

public class NetworkImageView extends ImageView {

	private String mUrl = null;
	private Drawable mDefaultImageId = null;
	private Drawable mErrorImageId = null;
	private Bitmap mBitmap = null;
	private static final int MSG_SUCCUSS_INVALIDATE = 0x00;
	private static final int MSG_ERROR_INVALIDATE = 0x01;
	private static final int ST_IDLE = 0;
	private static final int ST_PROGRESS = 1;
	private static final int ST_LOADED = 2;
	private static final int ST_ERROR = 3;
	private int mStatus = -1;
	
	public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.NetworkImageView);
		Drawable errorIcon = a.getDrawable(R.styleable.NetworkImageView_imageError);
		Drawable defaultIcon = a.getDrawable(R.styleable.NetworkImageView_imageDefault);
		mErrorImageId = errorIcon;
		mDefaultImageId = defaultIcon;
		a.recycle();
	}

	public NetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public NetworkImageView(Context context) {
		this(context, null);
	}
	
	public void setImageUrl(String url) {
		mUrl = url;
		mStatus = ST_IDLE;
		loadImage();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(mDefaultImageId != null)
			setImageDrawable(mDefaultImageId);
		loadImage();
	}
	
	private synchronized void loadImage() {
		if(mStatus != ST_PROGRESS) {
			mStatus = ST_PROGRESS;
			ImageLoader.getInstance().excute(mUrl, l, true);
		}
	}
	
	private OnImageLoaderListener l = new OnImageLoaderListener() {
		
		@Override
		public void onLoaderError(String mseeage) {
			mStatus = ST_ERROR;
			mHandler.sendEmptyMessage(MSG_ERROR_INVALIDATE);
		}
		
		@Override
		public void onLoaderComplition(Bitmap bitmap) {
			mBitmap = bitmap;
			mStatus = ST_LOADED;
			mHandler.sendEmptyMessage(MSG_SUCCUSS_INVALIDATE);
		}

		@Override
		public void onLoaderCanncel() {
			mStatus = ST_IDLE;
		}
	};
	
	public void canncelRequest() {
		ImageLoader.getInstance().canncel(mUrl);
	}
	
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_SUCCUSS_INVALIDATE:
				if(mBitmap != null) {
					NetworkImageView.this.setImageBitmap(mBitmap);
				}
				Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
				break;
			case MSG_ERROR_INVALIDATE:
				if(mErrorImageId != null) {
					setImageDrawable(mErrorImageId);
				} else {
					if(mDefaultImageId != null) {
						setImageDrawable(mDefaultImageId);
					}
				}
				Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
}
