package ca.syncron.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import ca.syncron.app.system.Syncron;

public class SyncronService1 extends Service {

	Syncron app = null;


	public SyncronService1() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onCreate() {
		super.onCreate();
		app = Syncron.getInstance();
		app.toast("Service Started");
	}
}
