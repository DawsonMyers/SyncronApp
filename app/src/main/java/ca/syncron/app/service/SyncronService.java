package ca.syncron.app.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import ca.syncron.app.connect.client.AndroidClientTcp;
import ca.syncron.app.system.Syncron;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncronService extends Service {

	public Syncron app;// = (Syncron) getApplicationContext();
	Boolean updateUI = false;
	Handler handler;
	public static AndroidClientTcp client   = null;
	public static SyncronService   mService = null;
	public        ExecutorService  executor = Executors.newCachedThreadPool();
	Thread            updateUIThread;
	IntentFilter      filter;
	BroadcastReceiver updateUIReciver;
	//public MyReceiver mMyReceiver = new MyReceiver();
	public boolean serviceRunning;
	String id = this.getClass().getSimpleName();
	//Log.d(id,"sleeping");
	public SyncronService() {
		app = Syncron.getInstance();
		app.setServiceRef(this);
		Toast.makeText(app, "Service Started", Toast.LENGTH_LONG).show();
		executor.execute(() -> {
			try {
				Log.d(id, "sleeping");
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d(id, "done sleeping");
			(client = new AndroidClientTcp(this)).start();
		});
		mService = this;
	}

	public static SyncronService getInstance() {
		return mService;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void toast(String msg) {
		app = Syncron.getInstance();
		//Toast.makeText(app, msg, Toast.LENGTH_SHORT).show();
	}

	public void setPin(String pin, Boolean value) {
//		client = new AndroidClientTcp(this);
//		client.start();
		if (pin == "2") {
			//(client = new AndroidClientTcp(this)).start();
		}
		client.sendDigitalMessage(pin, value ? "1" : "0");
	//executor.execute(() -> client.sendDigitalMessage(pin, value? "1" : "0"));
		String m = value ? "ON" : "OFF";//"Pin #"+ pin;
		m += " set to ";
		m += value? "ON" : "OFF";
		//	toast(m);
	}

	//  Chat Callback
	public void chatReceived(String strMsg) {
		toast(strMsg);
	}

	public void connected() {
		toast("Connected to server");
	}

	public void testConnect() {
		new Thread(() -> {
			try {
				InetAddress ip = InetAddress.getByName("192.168.1.109");
				Socket socket = new Socket("localhost", 6500);
				if (socket.isConnected()) Log.d("ConnectTest", "Connected to server");
				else Log.d("ConnectTest", "Faild to connect");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
