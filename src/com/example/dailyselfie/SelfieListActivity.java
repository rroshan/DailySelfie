package com.example.dailyselfie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class SelfieListActivity extends ListActivity {

	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private ImageAdapter adapter;
	private ArrayList<Uri> arr = new ArrayList<Uri>();
	private Uri fileUri;
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent;
	private static final long INITIAL_ALARM_DELAY = 1 * 60 * 1000L;
	protected static final long JITTER = 5000L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.selfie_listview);
		try
		{
			File myFile = new File("/sdcard/selfie_uri.txt");
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(
					new InputStreamReader(fIn));
			String aDataRow = "";
			String aBuffer = "";
			while ((aDataRow = myReader.readLine()) != null) {
				arr.add(Uri.parse(aDataRow));
			}
			myReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		adapter = new ImageAdapter(this, arr);
		setListAdapter(adapter);
	}

	@Override
	public void onPause() {
		super.onPause();
		try
		{
			File myFile = new File("/sdcard/selfie_uri.txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = 
					new OutputStreamWriter(fOut);
			
			for(int i=0;i<arr.size();i++)
			{
				myOutWriter.write(arr.get(i).toString()+"\n");
			}
			
			if(arr.size() < 1)
			{
				myOutWriter.write("");
			}

			myOutWriter.close();
			fOut.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		//get selected items
		Uri selectedValue = arr.get((Integer) adapter.getItem(position));
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(selectedValue, "image/*");
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.selfie_list, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == R.id.selfie_camera) 
		{
			//launch front camera

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

			// start the image capture Intent

			startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			return true;
		} 
		else if (item.getItemId() == R.id.set_alarm) 
		{
			mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			if(mNotificationReceiverPendingIntent != null)
			{
				mAlarmManager.cancel(mNotificationReceiverPendingIntent);
			}

			mNotificationReceiverIntent = new Intent(SelfieListActivity.this,
					AlarmNotificationReceiver.class);

			mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
					SelfieListActivity.this, 0, mNotificationReceiverIntent, 0);

			// Set repeating alarm
			mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
					INITIAL_ALARM_DELAY, //less time for testing
					mNotificationReceiverPendingIntent);

			Toast.makeText(this, "Repeating alarm is set", Toast.LENGTH_SHORT).show();
			return true;
		}
		else if(item.getItemId() == R.id.delete_selfie)
		{
			arr.clear();
			adapter.notifyDataSetChanged();
			return true;
		}
		else 
		{
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) 
		{
			if (resultCode == RESULT_OK) 
			{
				// Image captured and saved to fileUri specified in the Intent
				//update list view here
				arr.add(fileUri);
				adapter.notifyDataSetChanged();

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		Log.i("Roshan", "Before mediaStorageDir");
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		Log.i("Roshan", "After creating directory");
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else {
			return null;
		}

		Log.i("Roshan", "Path::"+mediaStorageDir.getPath() + File.separator +
				"IMG_"+ timeStamp + ".jpg");
		return mediaFile;
	}

}
