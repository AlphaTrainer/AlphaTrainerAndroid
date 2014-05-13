package dk.itu.alphatrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import dk.itu.alphatrainer.cloud.PostToCloudService;
import dk.itu.alphatrainer.settings.ActivitySettings;
import dk.itu.alphatrainer.ui.UiUtils;

//public class ActivityMain extends FragmentActivityLifecycleManagementFullscreen {
public class ActivityMain extends FragmentActivity {	

	private final static String TAG = ActivityMain.class.getName();
	private Button btnTraining;
	private Button btnHistory;
	private Button btnSettings;
	private Button btnAbout;
	// private ImageView imgLogo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		UiUtils.changeFonts((ViewGroup) findViewById(android.R.id.content));
		
		
		/*
		 * Get reference to views
		 */
		btnTraining = (Button) findViewById(R.id.btn_training);
		btnHistory = (Button) findViewById(R.id.btn_history);
		btnSettings = (Button) findViewById(R.id.btn_settings);
		btnAbout = (Button) findViewById(R.id.btn_about);
		
		/*
		 * Setup button click handlers
		 */
		btnTraining.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startTraining();
			}
		});
		
		btnHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startHistory();
			}
		});
		
		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSettings();
			}
		});
		
		btnAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startInstructions();
			}
		});

	}


	@Override
	protected void onStart() {
		super.onStart();
		startService(new Intent(ActivityMain.this, PostToCloudService.class));
	}
	
	private void startTraining() {
		startActivity(new Intent(this, ActivityTraining.class));
	}
	
	private void startHistory() {
		startActivity(new Intent(this, ActivityHistory.class));
	}

	private void startSettings() {
		startActivity(new Intent(this, ActivitySettings.class));
	}
	
	private void startInstructions() {
		startActivity(new Intent(this, ActivitySettings.class));
	}
	

	
}
