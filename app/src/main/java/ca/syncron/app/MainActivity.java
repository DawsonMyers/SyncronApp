package ca.syncron.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ca.syncron.app.chat.ChatActivity;
import ca.syncron.app.system.Syncron;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

	public static String p1 = "2", p2 = "3", p3 = "4", p4 = "5";
	public static boolean[] val = new boolean[4];
	public        Syncron   app = null;
	//app = (Syncron) getApplicationContext();
	//(Syncron) getApplicationContext();
	Button       btn1 = null;
	Button       btn2 = null;
	Button       btn3 = null;
	Button       btn4 = null;
	MainActivity m    = null;
	private TextView status;
	Handler handler;

	//app = (Syncron) getApplicationContext();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		m = this;
		app = (Syncron) getApplicationContext();
		app.setRef(this);
		init();
		handler = new Handler();
		//startService(new Intent(this, SyncronService.class));
		// app.sendIntent();
		status = (TextView) findViewById(R.id.statusText);
		btn1 = (Button) findViewById(R.id.b1);
		btn2 = (Button) findViewById(R.id.b2);
		btn3 = (Button) findViewById(R.id.b3);
		btn4 = (Button) findViewById(R.id.b4);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
	}

	private void init() {

		Handler handler = new Handler();
		 handler.postDelayed(() ->app.startService(), 500);
		//new Thread(() -> Toast.makeText(m,"Running in Thread",Toast.LENGTH_LONG).show()).start();
		//Toast.makeText(this,"Trying",Toast.LENGTH_LONG).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.b1:
			//	app.startService();
				app.setPine(p1, pin(0));
				break;
			case R.id.b2:
				app.setPine(p2, pin(1));
				break;
			case R.id.b3:
				app.setPine(p3, pin(2));
				break;
			case R.id.b4:
				//app.setPine(p4, pin(3));
				startActivity(new Intent(MainActivity.this, ChatActivity.class));
//				ChatActivity chatActivity = new ChatActivity();
//				chatActivity.start(this);
				break;
			default:
				break;
		}

	}

	public boolean pin(int i) {
		val[i] = val[i]? false:true;
		return val[i];
	}

	public void updateStatus(boolean con) {

		handler.post(() -> {
			Toast.makeText(this, "Connected to server", Toast.LENGTH_LONG).show();
			status.setText(con ? "Connected" : "Disconnected");
			status.setTextColor(con ? Color.GREEN : Color.RED);
		});

	}
}
