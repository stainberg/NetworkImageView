package com.example.test;

import android.graphics.Bitmap;

public interface OnImageLoaderListener {
	public void onLoaderComplition(Bitmap bitmap);
	public void onLoaderError(String mseeage);
	public void onLoaderCanncel();
}
