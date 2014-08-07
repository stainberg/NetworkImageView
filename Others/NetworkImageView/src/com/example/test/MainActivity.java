package com.example.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String TAG = MainActivity.class.getName();
	private NetworkImageView mTestImage = null;
	private Button mStart = null, mStop = null, mStopAll = null;
	private TextView mCount = null;
	private int count = 0;
	
	private static final String[] urls = {"http://c.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3cea90da22c9045d688d53f20db.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311FZ8.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F911.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F913.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F916.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F917.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F920.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F923.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F925.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311FU9.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311FZ6.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311FZ8.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F911.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F913.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F916.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F917.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F920.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F923.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311F925.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311FU9.jpg",
		"http://www.7160.com//uploads/allimg/131231/9-1312311FZ6.jpg"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTestImage = (NetworkImageView) findViewById(R.id.networkimageview);
		mTestImage.setLoaderListener(ll);
		mTestImage.setCancelCategoryTag("test");
		mStart = (Button) findViewById(R.id.button1);
		mStart.setOnClickListener(l);
		mStop = (Button) findViewById(R.id.button2);
		mStop.setOnClickListener(l);
		mStopAll = (Button) findViewById(R.id.button3);
		mStopAll.setOnClickListener(l);
		mCount = (TextView) findViewById(R.id.textView1);
	}
	
	private OnImageLoaderListener ll = new OnImageLoaderListener() {
		
		@Override
		public void onLoaderError(String mseeage) {
			
		}
		
		@Override
		public void onLoaderComplition(Bitmap bitmap, String tag) {
			count++;
			mCount.setText(String.valueOf(count));
		}
		
		@Override
		public void onLoaderCanncel() {
			
		}
	};
	
	private OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == mStart.getId()) {
				for(int i=0; i<urls.length; i++) {
					mTestImage.setImageUrl(urls[i]);
				}
			}
			if(v.getId() == mStop.getId()) {
				mTestImage.canncelRequestByCategoryTag("test");
			}
			if(v.getId() == mStopAll.getId()) {
				ImageLoader.getInstance().canncelAll();
			}
		}
	};
	
	protected void onDestroy() {
		super.onDestroy();
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
