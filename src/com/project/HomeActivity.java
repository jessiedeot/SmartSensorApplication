package com.project;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HomeActivity extends ActionBarActivity {

	public ListView mListView;
	public EditText editText;
	private String username = null;
	private String password = null;
	public CustomListAdapter adapter;
	ArrayList<PlaceInfo> places = new ArrayList<PlaceInfo>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.password = i.getStringExtra("password");

		ActionBar actionBar = getActionBar();
		// add the custom view to the action bar
		actionBar.setCustomView(R.layout.actionbar_view);
		EditText search = (EditText) actionBar.getCustomView().findViewById(R.id.searchfield);
		search.addTextChangedListener( new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2,
                        int arg3) {

                    adapter.getFilter().filter(cs.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                        int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                  
                }
		});
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
	
		mListView = (ListView) findViewById(android.R.id.list);

		try {
			getSearchResults();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void getSearchResults() throws IOException {

		
		String url = "http://smarthomeapp.appspot.com/smartapp/getplaces?uemail="
				+ this.username + "&password=" + this.password;
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			DownloadWebPageTask task = new DownloadWebPageTask();
			task.execute(url);
		} else {

		}

	}

	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {

			String response = "";
			DefaultHttpClient client;
			HttpPost httpPost;
			HttpResponse res = null;
			httpPost = null;
			int code = 0;
			client = new DefaultHttpClient();

			Log.v("url", urls[0]);
			httpPost = new HttpPost(urls[0]);

			try {
				Log.v("Status", "Before execute");
				res = client.execute(httpPost);
				Log.v("Status", "After execute");
				code = res.getStatusLine().getStatusCode();

				if (code == 200) {

					HttpEntity r_entity = res.getEntity();
					String searchcontent = EntityUtils.toString(r_entity);

					JSONArray jso = new JSONArray(searchcontent);

					for (int i = 0; i < jso.length(); i++) {
						PlaceInfo c = new PlaceInfo();
						String tokenVal = jso.getString(i);
						c.setPlaceToken(jso.getString(i));
						c.setPlaceUrl(urls[2] + "&token=" + tokenVal);
						response = "success";
						HttpClient client1 = new DefaultHttpClient();
						code = 0;
						String PlaceUrl = urls[1] + "&token=" + tokenVal;
						// urls[1] += "&token=" + tokenVal;
						httpPost = new HttpPost(PlaceUrl);
						Log.v("url", urls[1]);
						Log.v("Status", "Before execute Place Meta");
						HttpResponse resp = client1.execute(httpPost);
						Log.v("Status", "After execute Place Meta");
						code = resp.getStatusLine().getStatusCode();

						if (code == 200) {

							HttpEntity r_entity_meta = resp.getEntity();
							String Placecontent = EntityUtils
									.toString(r_entity_meta);

							JSONObject jsob = new JSONObject(Placecontent);

							c.setPlaceText(jsob.getString("name"));
									

							response = "success";

						} else {

							response = "Error in Getting Place MetaData";
						}

						places.add(c);
					}
				} else {

					response = "No Places to Display";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		protected void onPostExecute(String result) {

			if (result == "success") {

				adapter = new CustomListAdapter(HomeActivity.this,
						R.layout.activity_place_item, places);

				mListView.setAdapter(adapter);

				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						((CustomListAdapter) parent.getAdapter())
								.setSelectedPosition(position);
						PlaceInfo Place = (PlaceInfo) parent
								.getItemAtPosition(position);

						String PlaceUrl = Place.getPlaceUrl();
						String PlaceText = Place.getPlaceText();

						Intent t = new Intent(getApplicationContext(),
								PlaceItemActivity.class);
						t.putExtra("PlaceUrl", PlaceUrl);
						t.putExtra("PlaceText", PlaceText);

						startActivity(t);

					}
				});

			} else {
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	@SuppressLint("NewApi")
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);

		
		return super.onCreateOptionsMenu(menu);

	}

}