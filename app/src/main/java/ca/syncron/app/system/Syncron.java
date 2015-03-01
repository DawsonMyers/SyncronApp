package ca.syncron.app.system;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import ca.syncron.app.service.SyncronService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Dawson on 2/27/2015.
 */
public class Syncron extends Application {
	public SyncronService mService = null;
	public Context        syncronContext;
//	public Handler                  mHandler  = new Handler();
	public ExecutorService          executor  = Executors.newCachedThreadPool();
	public ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
	// ///////////////////////////////////////////////////////////////////////////////////

	private static Syncron syncron;// = new Syncron();

	//	access with: Syncron controller = Syncron.getSingletonInstance();
	public Syncron() {

//scheduler.scheduleWithFixedDelay()
		//executor.execute(() -> startService());
		//mHandler.postDelayed(() -> startService(),5000);
		//ExecutorService executor = Executors.newFixedThreadPool(5);
		//ExecutorService cachedPool = Executors.newCachedThreadPool();

	}
	public void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	public synchronized static Syncron getInstance() {
		return syncron;
	}

	public synchronized void setServiceRef(SyncronService service){
		mService = service;
	}
// ///////////////////////////////////////////////////////////////////////////////////


	//public static Syncron getInstance() {
//		return singleton;
//	}

	@Override
	public void onCreate() {
		super.onCreate();

		syncron = this;

	}

	public void startService() {
		//mService = new SyncronService();
		//Intent i = new Intent(Syncron.this, SyncronService.class);
		//mService.startService(i);
		startService(new Intent(Syncron.this, SyncronService.class));
	}

	public void setPine(String pin, boolean value) {
		mService = SyncronService.getInstance();
		//	mService.testConnect();
		mService.setPin(pin, value);
	}

}
