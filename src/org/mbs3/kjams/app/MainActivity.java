package org.mbs3.kjams.app;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mbs3.kjams.Service;
import org.mbs3.kjams.app.Constants;
import org.mbs3.kjams.model.Singer;

public class MainActivity extends Activity implements OnClickListener {

	private Service kJamsService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int[] buttons = { R.id.button_connect, R.id.button_exit };
		for (int bId : buttons) {
			Button button = (Button) findViewById(bId);
			button.setOnClickListener(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.button_connect) {
			Log.i(Constants.TAG, "Connect!");

			TextView textView = (TextView) findViewById(R.id.server_address);
			String url2 = textView.getText().toString();
			if (url2 == null || url2.trim().equals("")) {
				Toast.makeText(this, "Must provide a URL!", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			// should move this into the library
			if (!url2.endsWith("/"))
				url2 += "/";

			final String url = url2;

			TextView usernameView = (TextView) findViewById(R.id.username);
			final String txtUsername = usernameView.getText().toString();
			TextView passwordView = (TextView) findViewById(R.id.password);
			final String txtPassword = passwordView.getText().toString();

			final MainActivity ui = this;

			AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					try {
						kJamsService = new Service(url);
						// success

						List<Singer> singers = kJamsService.getSingers();
						for (Singer singer : singers) {
							if (singer.SNGR.equals(txtUsername)) {
								return "Found singer " + singer.SNGR;
							}
						}

						kJamsService.createNewSinger(txtUsername,
								txtPassword, txtPassword);
						
						return "Created singer " + txtUsername;
					} catch (Exception ex) {
						Log.e(Constants.TAG, ex.toString());
						return "Could not connect to " + url;
					}
				}
				
				@Override
				protected void onPostExecute(String result) {
					Toast.makeText(ui, result, Toast.LENGTH_SHORT).show();
					super.onPostExecute(result);
				}

			};

			task.execute();

		} else if (arg0.getId() == R.id.button_exit) {
			finish();
		}
	}
}
