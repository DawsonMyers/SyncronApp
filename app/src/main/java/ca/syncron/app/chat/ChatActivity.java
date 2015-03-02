package ca.syncron.app.chat;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import ca.syncron.app.R;
import ca.syncron.app.system.Syncron;

import java.util.ArrayList;
import java.util.Random;

public class ChatActivity extends ListActivity {
	static Random rand = new Random();
	static String sender;
	public Handler handler = new Handler();
	public Syncron app     = null;
	ArrayList<Message> messages;
	AwesomeAdapter     adapter;
	EditText           text;

	//app = (Syncron) getApplicationContext();
//	public void start(Activity context){
//	startActivity(new Intent(context, ChatActivity.class));
//}
	public Handler getHandler() {
		return handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);

		app = (Syncron) getApplicationContext();
		app.setRef(this);
		app.chat = this;

		text = (EditText) this.findViewById(R.id.text);

		sender = Utility.sender[rand.nextInt(Utility.sender.length - 1)];
		this.setTitle(sender);
		messages = new ArrayList<Message>();

		adapter = new AwesomeAdapter(this, messages);
		setListAdapter(adapter);
		addNewMessage(new Message("Welcome to Syncron Chat", true));
	}

	public void sendMessage(View v) {
		String newMessage = text.getText().toString().trim();
		if (newMessage.length() > 0) {
			text.setText("");
			addNewMessage(new Message(newMessage, true));
			//new SendMessage().execute();
			app.sendChatMessage(newMessage);
		}
	}

	public void addNewMessage(Message m) {

		messages.add(m);
		adapter.notifyDataSetChanged();
		getListView().setSelection(messages.size() - 1);
	}

	public void update(String msg) {
		final String m = msg;
		handler.post(() -> addNewMessage(new Message(m, false)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_chat, menu);
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

	private class SendMessage extends AsyncTask<Void, String, String> {
		@Override
		protected String doInBackground(Void... params) {
//			try {
//				Thread.sleep(2000); //simulate a network call
//			}catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			this.publishProgress(String.format("%s started writing", sender));
//			try {
//				Thread.sleep(2000); //simulate a network call
//			}catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			this.publishProgress(String.format("%s has entered text", sender));
//			try {
//				Thread.sleep(3000);//simulate a network call
//			}catch (InterruptedException e) {
//				e.printStackTrace();
//			}


			return Utility.messages[rand.nextInt(Utility.messages.length - 1)];


		}

		@Override
		public void onProgressUpdate(String... v) {

			if (messages.get(messages.size() - 1).isStatusMessage)//check wether we have already added a status message
			{
				messages.get(messages.size() - 1).setMessage(v[0]); //update the status for that
				adapter.notifyDataSetChanged();
				getListView().setSelection(messages.size() - 1);
			} else {
				addNewMessage(new Message(true, v[0])); //add new message, if there is no existing status message
			}
		}

		@Override
		protected void onPostExecute(String text) {
			if (messages.get(messages.size() - 1).isStatusMessage)//check if there is any status message, now remove it.
			{
				messages.remove(messages.size() - 1);
			}

			addNewMessage(new Message(text, false)); // add the orignal message from server.
		}


	}
}
