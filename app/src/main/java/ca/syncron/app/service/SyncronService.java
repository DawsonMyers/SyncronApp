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
import java.util.concurrent.ScheduledExecutorService;

public class SyncronService extends Service {

	public static String           user     = "Dawson";
	public static AndroidClientTcp client   = null;
	public static SyncronService   mService = null;
	public Syncron app;// = (Syncron) getApplicationContext();
	public ExecutorService executor = Executors.newCachedThreadPool();
	//public MyReceiver mMyReceiver = new MyReceiver();
	public boolean serviceRunning;
	Boolean updateUI = false;
	Handler handler;
	Thread            updateUIThread;
	IntentFilter      filter;
	BroadcastReceiver updateUIReciver;
	String id = this.getClass().getSimpleName();
	public volatile boolean mConnected    = false;
	public volatile boolean mReconnecting = false;

	public static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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
		//int i = strMsg.indexOf(":");
		String[] s = strMsg.split(":");
		strMsg = s[0] + ":\n" + s[1];
		final String msg = strMsg;
		toast(msg);
		if (msg.length() == 0) return;
		app = Syncron.getInstance();
		app.chat.update(strMsg);
		//app.chat.handler.post(()-> app.chat.addNewMessage(new Message(strMsg, false)));

	}

	public void connected() {
		toast("Connected to server");
	}

	public void isConnected(boolean con) {
		mConnected = con;
		//	Syncron.getInstance().updateConnectionStatus(con);
		app.updateConnectionStatus(con);
	}

	public boolean isConnected() {
		return mConnected;
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

	public void sendChatMessage(String msg) {
		msg = user + ":" + msg;
		client.sendChatMessage(msg);
	}

	public void disconnected() {
		client = null;
		System.out.println("trying to execute runnable");

		//client.mEventMachine.shutdown();
		//scheduler.scheduleAtFixedRate(() -> connect(), 0, 10, TimeUnit.SECONDS);
		connect();
	}

	public int attempt = 0;

	public void connect() {
		System.out.println("Getting connect runnable");
		if (mReconnecting) return;
//		executor.execute(() ->  {
		new Thread(() -> {

//			if (isConnected()) {
//				scheduler.shutdown();
//			}else (client = new AndroidClientTcp(this)).start();
			mReconnecting = true;
			while (!isConnected()) {
				Log.d(id, "mReconnecting " + mReconnecting);
				System.out.println("TRYING TO RECONNECT - " + attempt);
				try {
					Thread.sleep(10 * 1000);
					if (isConnected()) break;

					System.out.println("Trying to start new client");
					(client = new AndroidClientTcp(this)).start();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				attempt++;
			}
			attempt = 0;
			mReconnecting = false;
		}).start();
//		executor.execute(() -> {
//			try {
//				Log.d(id, "sleeping");
//				Thread.sleep(1500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			Log.d(id, "done sleeping");
//			(client = new AndroidClientTcp(this)).start();
//		});

	}
}
