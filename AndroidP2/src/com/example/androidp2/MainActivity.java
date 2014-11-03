package com.example.androidp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.androidp2.fragments.FragmentAlertDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author Artur Olech Mainactivity or controller, handles the whole application
 *         and communication with the server.
 *
 */
public class MainActivity extends Activity {

	// Google Maps
	private GoogleMap gMaps;
	private boolean onResume = false;

	// Fragments
	FragmentAlertDialog dialog;

	// TCP/IP Threads
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	// Controller
	public MainActivity controller;

	// SharedPreferences storage
	SharedPreferences sharedPref;

	/**
	 * OnCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

	}

	/**
	 * OnResume Controller get its application context Map get's created with
	 * its listener for GPS. Connection to server is established.
	 */
	@Override
	protected void onResume() {
		// OnResume boolean, that tells if OnResumed is called.
		onResume = true;

		// Controller
		controller = (MainActivity.this);

		// Connect to server.
		connect();

		// Google Maps
		initMap();

		super.onResume();
	}

	/**
	 * OnPause Tells that appliction is paused. Stopps connection from being
	 * used.
	 */
	@Override
	protected void onPause() {
		// OnResume boolean, that tells if OnResumed is called.
		onResume = false;

		// Disconnect from server.
		disconnect();

		// MainActivity
		controller = null;

		super.onPause();
	}

	/**
	 * OnCreateOptionsMenu creates the options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * OnOptionsItemSelected When stuff gets clicked here is your method of
	 * choice.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
		case R.id.server_connect:
			toastUi("Connecting...");
			connect();
			return true;

		case R.id.server_disconnect:
			toastUi("Disconneting...");
			disconnect();
			return true;

		case R.id.user_register:
			try {
				toastUi("Register...");
				requestGroups();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;

		case R.id.user_unregister:

			try {
				unRegister();
				toastUi("Unregister...");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Starts the connection to server.
	 */
	public void connect() {
		
		// TCP/IP Threads
		
		mConnectThread = new ConnectThread("195.178.232.7", 7117,this);
		mConnectThread.start();
	}

	/**
	 * Disconnect from server.
	 */
	public void disconnect() {
		// TCP/IP Threads

		try {
			unRegister();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			mConnectedThread.cancel();
			mConnectedThread = null;
			mConnectThread = null;
		} catch (Exception e) {
			Log.d("Disconnect", "Error during disconnect");
			e.printStackTrace();
		}
	}

	/**
	 * initMap Handles the googlemaps and registers the location listener.
	 */
	private void initMap() {
		Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
		MapFragment mapFragment = (MapFragment) fragment;
		gMaps = mapFragment.getMap();

		if (gMaps != null) {
			gMaps.setMyLocationEnabled(true);
			gMaps.getUiSettings().setZoomControlsEnabled(false);
			gMaps.getUiSettings().setMyLocationButtonEnabled(true);
			gMaps.setOnMyLocationChangeListener(myLocationChangeListener);
		}
	}

	/**
	 * initComs initiates communication to the server.
	 */
	private void initComs() {
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(getResources().getString(R.string.user_registered), false);
		editor.commit();
		
		// Register member to group
		try {
			register();
		} catch (JSONException e) {
			Log.d("initComs", "Failed to initComs!");
			e.printStackTrace();
		}
	}

	/**
	 * putMarker on Google Maps.
	 * 
	 * @param latlng
	 * @param name
	 */
	public synchronized void putMarker(LatLng latlng, String name) {
		// create marker
		final MarkerOptions marker = new MarkerOptions().position(new LatLng(latlng.latitude, latlng.longitude)).title(
				name);

		// Changing marker icon
		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

		// adding marker
		if (gMaps != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gMaps.addMarker(marker);
				}
			});
		}

	}

	/*
	 * Clear google map
	 */
	public synchronized void clearMarkers() {
		if (gMaps != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gMaps.clear();
				}
			});
		}
	}

	/*
	 * Asks server to register to a group
	 */
	public void register() throws JSONException {

		// If not registered to server already.
		if (sharedPref.getBoolean(getResources().getString(R.string.user_registered), false) != true) {

			String default_name = getResources().getString(R.string.user_default_name);
			String default_group = getResources().getString(R.string.user_default_group);

			String member_name = sharedPref.getString(getResources().getString(R.string.user_name), default_name);
			String group_name = sharedPref.getString(getResources().getString(R.string.user_group), default_group);

			JSONObject obj = new JSONObject();
			obj.put("type", "register");
			obj.put("group", group_name);
			obj.put("member", member_name);
			send(obj);
		} else {
			toastUi("Cannot register a user that is already registered");
		}
	}

	/**
	 * Unregister from group.
	 * @throws JSONException
	 */
	public void unRegister() throws JSONException {
		String id = sharedPref.getString(getResources().getString(R.string.user_id), null);

		if (id != null) {
			JSONObject obj = new JSONObject();
			obj.put("type", "unregister");
			obj.put("id", id);
			send(obj);
		} else {
			Log.d("unregister", "Could not send unregister, id null");
		}
	}

	/**
	 * Update user information.
	 * @param name
	 * @param group
	 */
	public void updateUserInfo(String name, String group) {

		// If not registered to server already.
		if (sharedPref.getBoolean(getResources().getString(R.string.user_registered), false) != true) {

			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(getResources().getString(R.string.user_name), name);
			editor.putString(getResources().getString(R.string.user_group), group);
			editor.commit();
		}
		try {
			register();
		} catch (JSONException e) {
			Log.e("UpdateUserInfo", "Could not register user");
			e.printStackTrace();
		}
	}

	public void requestGroups() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("type", "groups");
		send(obj);
	}

	/*
	 * Send users current location to server
	 */
	public void sendLocation(LatLng latlng) throws JSONException {
		String id = sharedPref.getString(getResources().getString(R.string.user_id), null);
		boolean user_registered = sharedPref.getBoolean(getResources().getString(R.string.user_registered), false);

		if (id != null && user_registered) {
			JSONObject obj = new JSONObject();
			obj.put("type", "location");
			obj.put("id", id);
			obj.put("longitude", "" + latlng.longitude);
			obj.put("latitude", "" + latlng.latitude);
			send(obj);
		} else {
			Log.d("Location", "Could not send location, id null");
		}

	}

	/**
	 * Google Maps onlocationlistener.
	 */
	private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
		@Override
		public void onMyLocationChange(Location location) {
			LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

			// Send position to server
			try {
				sendLocation(loc);
			} catch (JSONException e) {
				Log.d("sendLocation", "Error, could not sendLocation " + e.getMessage());
				e.printStackTrace();
			}

			if (gMaps != null && onResume) {
				gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
				onResume = false;
			}
		}
	};

	/**
	 * ConnectThread
	 * 
	 * @author Artur Olech Runs when trying to connect.
	 * 
	 */
	private class ConnectThread extends Thread {
		private final Socket mmSocket = new Socket();
		private int timeout = 3000;
		private InetSocketAddress remoteAddr;
		private String host;
		private int port;
		private MainActivity controller;

		public boolean isConnected() {
			return mmSocket.isConnected();
		}

		public ConnectThread(String host, int port, MainActivity controller) {
			this.host = host;
			this.port = port;
			this.controller = controller;
		}

		public void run() {
			setName("connecting to server...");
			remoteAddr = new InetSocketAddress(host, port);
			try {
				mmSocket.connect(remoteAddr, timeout);
			} catch (IOException e) {
				try {
					mmSocket.close();
				} catch (IOException e2) {

				}
				e.printStackTrace();
				toastUi(e.getMessage());
				return;
			}

			synchronized (MainActivity.this) {
				mConnectThread = null;
			}

			mConnectedThread = new ConnectedThread(mmSocket);
			mConnectedThread.start();
			toastUi("connected");

			if (controller == null)
				Log.d("ERROR!!!!!", "Controller=null!");
			controller.initComs();
		}
	}

	/**
	 * ConnectedThread
	 * 
	 * @author Artur Olech Runs when connected.
	 * 
	 */
	private class ConnectedThread extends Thread {
		private Socket mmSocket;
		private InputStream mmInStream;
		private OutputStream mmOutStream;
		private DataInputStream mmDataInputStream;
		private DataOutputStream mmDataOutputStream;

		public ConnectedThread(Socket socket) {
			mmSocket = socket;
			try {
				mmInStream = mmSocket.getInputStream();
				mmOutStream = mmSocket.getOutputStream();
				mmDataInputStream = new DataInputStream(mmInStream);
				mmDataOutputStream = new DataOutputStream(mmOutStream);

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		public void run() {
			while (true) {
				try {
					String result = mmDataInputStream.readUTF();
					try {
						JSONObject messageObj = new JSONObject(result);
						String type = messageObj.get("type").toString();

						switch (type) {
						case "register":
							String register_id = messageObj.getString("id");

							if (register_id != null && register_id.length() > 3) {
								SharedPreferences.Editor editor = sharedPref.edit();
								editor.putString(getResources().getString(R.string.user_id), register_id);
								editor.putBoolean(getResources().getString(R.string.user_registered), true);
								editor.commit();

								toastUi("Register successfull");
							} else {
								SharedPreferences.Editor editor = sharedPref.edit();
								editor.putBoolean(getResources().getString(R.string.user_registered), false);
								editor.commit();
								toastUi("Register unsuccessfull");
							}

							break;

						case "unregister":
							String unregister_id = messageObj.getString("id");
							String cached_id = sharedPref.getString(getResources().getString(R.string.user_id), null);

							Log.d("PARSER: unregister id", unregister_id);
							Log.d("PARSER: unregister cached id", cached_id);

							if (cached_id != null && unregister_id.equals(cached_id)) {
								SharedPreferences.Editor editor = sharedPref.edit();
								editor.putBoolean(getResources().getString(R.string.user_registered), false);
								editor.commit();
								toastUi("Unregister successfull");
							}

							break;

						case "exception":
							Log.d("PARSER: exception", messageObj.getString("message"));
							Log.d("PARSER: exception", messageObj.toString(4));
							break;

						case "members":
							Log.d("PARSER: members", messageObj.getString("group"));
							// Get stuff
							break;

						case "groups":
							Log.d("PARSER: groups", messageObj.getString("groups"));
							JSONArray arr_groups = messageObj.getJSONArray("groups");
							ArrayList<String> groups = new ArrayList<String>();

							for (int i = 0; i < arr_groups.length(); i++) {
								JSONObject obj = arr_groups.getJSONObject(i);
								groups.add(obj.getString("group"));
							}

							// Add new group option
							groups.add("New Group");

							// Launch the Registration Dialog
							new FragmentAlertDialog().newInstance(groups).show(getFragmentManager(),
									"Register Dialog Launch");

							break;

						case "location":
							Log.d("PARSER: location", messageObj.getString("id"));
							// Ignore?
							break;

						case "locations":
							Log.d("PARSER: locations", messageObj.getString("group"));

							JSONArray arr_locations = messageObj.getJSONArray("location");
							clearMarkers();
							for (int i = 0; i < arr_locations.length(); i++) {
								JSONObject obj = arr_locations.getJSONObject(i);
								LatLng latlng = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
								putMarker(latlng, obj.getString("member"));
							}

							break;

						default:
							Log.d("PARSER: ERROR", "unidentified type: " + type);
							Log.d("FROM SERVER", messageObj.toString(4));
							break;
						}

						Log.d("FROM SERVER", messageObj.toString(4));

					} catch (JSONException e) {
						Log.e("PARSER: RESULT ERROR", "Could not parse JSON String");
						e.printStackTrace();
					}

				} catch (IOException e) {
					break;
				}

			}
		}

		public synchronized void send(String message) {
			try {
				mmDataOutputStream.writeUTF(message);
				mmDataOutputStream.flush();
			} catch (IOException e) {
				 e.printStackTrace();
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				 e.printStackTrace();
			}
		}

	}

	public synchronized void send(JSONObject message) throws JSONException {
		if (mConnectedThread != null) {
			Log.d("ConnectedThread: Send", message.toString(4));
			mConnectedThread.send(message.toString());

		} else {
			Log.d("ConnectionThread: Send", "Could not send msg: " + message.toString(4));
			toastUi("Connection error, could not send data");
		}
	}

	public void toastUi(String string) {
		final String text = string;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
