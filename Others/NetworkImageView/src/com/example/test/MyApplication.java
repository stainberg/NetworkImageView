package com.example.test;

import com.youku.laifeng.libcuteroom.LibAppApplication;
import com.youku.laifeng.libcuteroom.model.port.aidl.IChatManagerService;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MyApplication extends Application {

	private IChatManagerService mIChatService = null;
	private static MyApplication instance = null;
	
	private void startService() {
        startService(new Intent("com.youku.laifeng.service.REMOTE_DATA_SERVICE"));
        bindService(new Intent(IChatManagerService.class.getName()), mConnection, Context.BIND_AUTO_CREATE);
    }

	public static MyApplication getInstance() {
		return instance;
	}
	
	public IChatManagerService getChatService() {
		return mIChatService;
	}
	
    private void stopService() {
        stopService(new Intent("com.youku.laifeng.service.REMOTE_DATA_SERVICE"));
    }
    
	private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        	mIChatService = IChatManagerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        	mIChatService = null;
        }

    };
	
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        startService();
        new LibAppApplication(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unbindService(mConnection);
        stopService();
    }
    
}
