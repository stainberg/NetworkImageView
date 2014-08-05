package com.example.test;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.youku.laifeng.libcuteroom.MyLog;
import com.youku.laifeng.libcuteroom.base.AbsBaseActvity;
import com.youku.laifeng.libcuteroom.model.port.aidl.IChatManagerService;
import com.youku.laifeng.libcuteroom.model.port.aidl.IChatManagerServiceListener;
import com.youku.laifeng.libcuteroom.model.socketio.chatdata.UserListMessage;
import com.youku.laifeng.libcuteroom.model.socketio.send.EnterRoom;
import com.youku.laifeng.libcuteroom.utils.Utils;

public class MainActivity extends AbsBaseActvity {
	private static final int MSG_SOCKET_IO_RECV_USER_LIST_MSG = 0x10;
	private static final String TAG = MainActivity.class.getName();
	private IChatManagerService mChatSerivce = null;
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
		mHandler.post(initChat);
	}
	
	private OnImageLoaderListener ll = new OnImageLoaderListener() {
		
		@Override
		public void onLoaderError(String mseeage) {
			
		}
		
		@Override
		public void onLoaderComplition(Bitmap bitmap) {
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
	
	private Runnable initChat = new Runnable() {
		
		@Override
		public void run() {
			try {
				while(MyApplication.getInstance().getChatService() == null) {
					Thread.sleep(500);
				}
				mChatSerivce = MyApplication.getInstance().getChatService();
				mChatSerivce.registerChatManagerListener(mChatListener);
				mChatSerivce.connect("http://rm2.xingmeng.com:9527");
			} catch (RemoteException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	
	protected void onDestroy() {
		super.onDestroy();
		try {
			mChatSerivce.unregisterChatManagerListener(mChatListener);
			mChatListener = null;
			mChatSerivce = null;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
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

	@Override
	public void handleMessage(Message msg) {
		switch(msg.what) {
		case MSG_SOCKET_IO_RECV_USER_LIST_MSG:
			UserListMessage.getInstance().refreshUserList((String) msg.obj);
			break;
		}
	}
	
	private IChatManagerServiceListener mChatListener = new IChatManagerServiceListener.Stub() {

		@Override
		public void onClose() throws RemoteException {
			MyLog.v(TAG, "onClose:");
		}

		@Override
		public void onConnect() throws RemoteException {
			MyLog.v(TAG, "onConnect:");
			mChatSerivce.sendEvent(new EnterRoom("363280966", "955", "MzcyMzk4NzY0LTEtMTQwNjcwODM1MTc5Ny0xMDAw-9C6F7C8BD6979EB345080BC71417C58D", "dt_1_" + Utils.getIMEI()));
		}

		@Override
		public void onError(String arg0) throws RemoteException {
			MyLog.v(TAG, "onError:");
		}
		
		@Override
		public void onReceiveEvent(String event, List<String> args) throws RemoteException {
			for(String item:args) {
				MyLog.v(TAG, "onReceiveEvent:" + event + " & detail =  " + item);
				if (event.equals(UserListMessage.EVENT_USER_LIST)) {
                    Message msg = new Message();
                    msg.what = MSG_SOCKET_IO_RECV_USER_LIST_MSG;
                    msg.obj = item;
                    mHandler.sendMessage(msg);
                }
			}
		}

		@Override
		public void onReceiveMessage(String arg0) throws RemoteException {
			
		}
		
	};
}
