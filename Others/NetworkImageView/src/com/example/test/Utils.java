package com.example.test;


public class Utils {
	public static int DpToPx(int x) {
		int result = 0;
		final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
		result = (int) (x*scale + 0.5f);
		return result;
	}
}
