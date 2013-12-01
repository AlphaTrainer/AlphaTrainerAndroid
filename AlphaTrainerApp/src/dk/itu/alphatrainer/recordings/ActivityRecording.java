package dk.itu.alphatrainer.recordings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.FillDirection;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.model.Recording;

public class ActivityRecording extends Activity {

	private static final String TAG = "ActivityRecording";
	private TextView txtHeadline;
	private XYPlot xy;
	int recordingId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);
		txtHeadline = (TextView) findViewById(R.id.txt_headline);
		xy = (XYPlot) findViewById(R.id.plot_baseline);
		
		// Format XY Plot
	    xy.getBackgroundPaint().setColor(Color.WHITE);
		xy.setBorderStyle(XYPlot.BorderStyle.ROUNDED, .1f, .1f);
		xy.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
		xy.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
		
	    // Remove legend
	    //xy.getLayoutManager().remove(xy.getLegendWidget());
	    xy.getLayoutManager().remove(xy.getDomainLabelWidget());
	    xy.getLayoutManager().remove(xy.getRangeLabelWidget());
	    xy.getLayoutManager().remove(xy.getTitleWidget());
        
        // Get data from intent
		recordingId = getIntent().getIntExtra("recordingId", -1);
		Log.v(TAG, "recording: " + recordingId);
		
		if (recordingId > 0) {
    		plotPoints(recordingId);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_recording, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
	    	case R.id.action_delete_recording:
	    		boolean delete = App.getInstance().getDAO().deleteRecording(recordingId);
	    		if (delete){
		    		Toast toast = Toast.makeText(this, R.string.text_delete_recording_succes, Toast.LENGTH_SHORT);
		    		toast.show();	    			
	    			startActivity(new Intent(this, ActivityRecordings.class));
	    			this.finish();
	    		} else {
		    		Toast toast = Toast.makeText(this, R.string.text_delete_recording_wrong, Toast.LENGTH_SHORT);
		    		toast.show();	    		
		    		return true;	    			
	    		}
	    	default:
		return super.onOptionsItemSelected(item);
		}
	}

	
	private void plotPoints(int id) {
		
		Recording r = App.getInstance().getDAO().getRecording(id, true);
		
		txtHeadline.setText(r.getType() + " | AVG: " + r.getAverageAlphaLevel());

		XYSeries series = new SimpleXYSeries(
            r.getAlphaLevelsForXYPlot(),          		// SimpleXYSeries takes a List so turn our array into a List
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, 	// Y_VALS_ONLY means use the element index as the x value
            "relative alpha");                          // Set the display title of the series
		    
	    // Create a formatter to use for drawing a series using LineAndPointRenderer:
	    LineAndPointFormatter formatter = new LineAndPointFormatter(
	            Color.rgb(0, 100, 0),     	// line color
	            Color.rgb(0, 100, 100),     // point color
	            null,                		// fill color (none)
	            null, 						// point label formatter - e.g. new PointLabelFormatter(Color.rgb(200, 200, 200)),
	            FillDirection.BOTTOM);
		
	    // add a new series' to the xyplot:
	    xy.addSeries(series, formatter);
	 
	    // reduce the number of range labels
	    xy.setTicksPerRangeLabel(3);
	    
	    // add a little padding
	    xy.getGraphWidget().setGridPaddingRight(10);
	    xy.getGraphWidget().setGridPaddingTop(10);
		
	}
	
}
