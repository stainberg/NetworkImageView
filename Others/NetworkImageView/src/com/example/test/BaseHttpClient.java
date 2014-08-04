package com.example.test;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class BaseHttpClient {
	private static BaseHttpClient mHttpClient;
	private DefaultHttpClient mBaseHttpClient;
	private static final String CHARSET = HTTP.UTF_8;
	private static Object mMutex = new Object();
	
	public static BaseHttpClient getHttpObject() {
		if(mHttpClient == null) {
			synchronized (mMutex) {
				if(mHttpClient == null) {
					mHttpClient = new BaseHttpClient();
				}
			}
		}
		return mHttpClient;
	}
	
	private BaseHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpProtocolParams.setUserAgent(params, "device::" + android.os.Build.MODEL + "|system::android_" + android.os.Build.VERSION.RELEASE);
        ConnManagerParams.setTimeout(params, 4000);
        HttpConnectionParams.setConnectionTimeout(params, 4000);
        HttpConnectionParams.setSoTimeout(params, 4000);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        mBaseHttpClient = new DefaultHttpClient(conMgr, params);
	}
	
	public DefaultHttpClient getHttpClient() {
		return mBaseHttpClient;
	}
}
