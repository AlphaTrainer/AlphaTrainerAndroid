package dk.itu.alphatrainer;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.androidplot.xy.XYPlot;

import dk.itu.alphatrainer.model.Recording;
import dk.itu.alphatrainer.recordings.ActivityRecordings;
import dk.itu.alphatrainer.recordings.ActivityRecordingsAll;
import dk.itu.alphatrainer.ui.UiUtils;

public class ActivityHistory extends FragmentActivity {
	
	@SuppressWarnings("unused")
	private final static String TAG = ActivityHistory.class.getName();
	private Button btnRecordings;
	private Button btnRecordingsAll;
	private TextView txtPerformanceDay;
	private TextView txtPerformanceWeek;
	private TextView txtPerformanceMonth;
	private XYPlot plotBaselines;
	private XYPlot plotFeedbacks;
	private boolean manualMode;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		UiUtils.changeFonts((ViewGroup) findViewById(android.R.id.content));
		
		setMode();
		
		/*
		 * Get reference to views
		 */
		btnRecordings = (Button) findViewById(R.id.btn_recordings);
		btnRecordingsAll = (Button) findViewById(R.id.btn_recordings_all);
		txtPerformanceDay = (TextView) findViewById(R.id.txt_performance_day);
		txtPerformanceWeek = (TextView) findViewById(R.id.txt_performance_week);
		txtPerformanceMonth = (TextView) findViewById(R.id.txt_performance_month);
		plotBaselines = (XYPlot) findViewById(R.id.plot_baseline);
		plotFeedbacks= (XYPlot) findViewById(R.id.plot_feedback);
		
		setPerformance();
		
		setupPlots();
		
		
		if (manualMode) {
			
			/*
			 * Setup button click handlers
			 */
			btnRecordings.setVisibility(View.VISIBLE);
			btnRecordings.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startRecordings();
				}
			});
			
			btnRecordingsAll.setVisibility(View.VISIBLE);
			btnRecordingsAll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startRecordingsAll();
				}
			});
			
			
			/*
			 * Squash plots a little bit to make room for buttons
			 */
			
			int squashFactor = 3;
			LayoutParams params;
			
			params = (LayoutParams) plotFeedbacks.getLayoutParams();
			params.height = params.height - params.height/squashFactor;
			plotFeedbacks.setLayoutParams(params);
			
			params = (LayoutParams) plotBaselines.getLayoutParams();
			params.height = params.height - params.height/squashFactor;
			plotBaselines.setLayoutParams(params);
			
		}
		
	}
	
	
	private void setMode() {
		manualMode = App.getInstance().getSessionManager().getAppMode().equals(getString(R.string.mode_manual));
	}
	
	private void setPerformance() {
		txtPerformanceDay.setText(App.getInstance().getDAO().getPerformanceDay(Recording.TYPE_FEEDBACK) + "%");
		txtPerformanceWeek.setText(App.getInstance().getDAO().getPerformanceWeek(Recording.TYPE_FEEDBACK) + "%");
		txtPerformanceMonth.setText(App.getInstance().getDAO().getPerformanceMonth(Recording.TYPE_FEEDBACK) + "%");
	}
	
	
	private void setupPlots() {
		
		List<Number> feedbacks = App.getInstance().getDAO().getNewestRecordings(Recording.TYPE_FEEDBACK, 10);
		List<Number> baselines = App.getInstance().getDAO().getNewestRecordings(Recording.TYPE_BASELINE, 10);
		
		UiUtils.formatXYPlot(plotFeedbacks);
		plotFeedbacks.addSeries(UiUtils.getFormattedXYSeries(feedbacks), UiUtils.getLineAndPointFormatter());
		
		UiUtils.formatXYPlot(plotBaselines);
		plotBaselines.addSeries(UiUtils.getFormattedXYSeries(baselines), UiUtils.getLineAndPointFormatter());
	
	}
	
	
	private void startRecordings() {
		startActivity(new Intent(this, ActivityRecordings.class));
	}
	
	private void startRecordingsAll() {
		startActivity(new Intent(this, ActivityRecordingsAll.class));
	}

}
