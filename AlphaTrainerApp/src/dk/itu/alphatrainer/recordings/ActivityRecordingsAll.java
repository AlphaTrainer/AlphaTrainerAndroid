package dk.itu.alphatrainer.recordings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.androidplot.xy.FillDirection;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.model.Recording;

public class ActivityRecordingsAll extends Activity {

	private static final String TAG = "ActivityRecordingsAll";
	private TextView txtHeadline;
	private XYPlot xy;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recordings_all);
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
        
	    plotPoints();
	}

	
	private void plotPoints() {
		
		List<Recording> recordings = App.getInstance().getDAO().getRecordings();

		txtHeadline.setText("All Recordings");

		List<Number> baselineX = new ArrayList<Number>();
		List<Number> baselineY = new ArrayList<Number>();
		List<Number> feedbackX = new ArrayList<Number>();
		List<Number> feedbackY = new ArrayList<Number>();
		
		for (int i=0; i<recordings.size(); i++) {
			if (recordings.get(i).getType().equals(Recording.TYPE_BASELINE)) {
				baselineX.add(i);
				baselineY.add(recordings.get(i).getAverageAlphaLevel());
				Log.i(TAG, Recording.TYPE_BASELINE + " added to xy series - y: " + recordings.get(i).getAverageAlphaLevel());
			}
			else if (recordings.get(i).getType().equals(Recording.TYPE_FEEDBACK)) {
				feedbackX.add(i);
				feedbackY.add(recordings.get(i).getAverageAlphaLevel());
				Log.i(TAG, Recording.TYPE_FEEDBACK + " added to xy series - y: " + recordings.get(i).getAverageAlphaLevel());
			}
		}
		
		
		
		// series and formatter for our baselines
		XYSeries seriesBaseline = new SimpleXYSeries(
			baselineX, baselineY,         		// SimpleXYSeries takes a List so turn our array into a List
            "baseline");                          // Set the display title of the series
		    
	    // Create a formatter to use for drawing a series using LineAndPointRenderer:
	    LineAndPointFormatter formatterBaseline = new LineAndPointFormatter(
	            null,      					// line color, null if no line wanted
	            Color.rgb(0, 0, 0),     // point color
	            null,                		// fill color (none)
	            null, 						// point label formatter - e.g. new PointLabelFormatter(Color.rgb(200, 200, 200)),
	            FillDirection.BOTTOM);
	    
	    // series and formatter for our feedback
	    XYSeries seriesFeedback = new SimpleXYSeries(
			feedbackX, feedbackY,         		// SimpleXYSeries takes a List so turn our array into a List
            "feedback");                          // Set the display title of the series
		    
	    // Create a formatter to use for drawing a series using LineAndPointRenderer:
	    LineAndPointFormatter formatterFeedback = new LineAndPointFormatter(
	            null,      					// line color, null if no line wanted
	            Color.rgb(255, 0, 0),     // point color
	            null,                		// fill color (none)
	            null, 						// point label formatter - e.g. new PointLabelFormatter(Color.rgb(200, 200, 200)),
	            FillDirection.BOTTOM);
		
	    // add a new series' to the xyplot:
	    xy.addSeries(seriesBaseline, formatterBaseline);
	    xy.addSeries(seriesFeedback, formatterFeedback);
	 
	    // reduce the number of range labels
	    xy.setTicksPerRangeLabel(3);
	    
	    // add a little padding
	    xy.getGraphWidget().setGridPaddingRight(10);
	    xy.getGraphWidget().setGridPaddingTop(10);
		
	}
	
}
