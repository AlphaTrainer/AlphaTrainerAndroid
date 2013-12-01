package dk.itu.alphatrainer.ui;


import java.io.IOException;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.Plot.BorderStyle;
import com.androidplot.xy.FillDirection;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParseException;
import com.larvalabs.svgandroid.SVGParser;

import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;


public class UiUtils {
	
	private final static String TAG = UiUtils.class.getName();

	/*
	 * Shouldn't be possible to initialize UiUtils constructor
	 */
	private UiUtils() {}
	
	
	/*
	 * takes a normalized alpha level as input and outputs a feedback color
	 * 
	 * example usage: FEEDBACK_UI_ELEMENT.setBackgroundColor(UiUtil.AlphaLevelToColor( ALPHA_LEVEL ));
	 */
	public static int AlphaLevelToColor(int alphaLevel) {

		if (alphaLevel <= 50) {
			return Color.rgb(255, (int)Math.round(alphaLevel * 5.1), 0);
		}
		else {
			return Color.rgb((int)Math.round(255-((alphaLevel-50) * 5.1)), 255, 0);
		}
	}
	
	
	public static void animateBackgroundColor(final View v, final int colorFrom, final int colorTo, int duration) {
		ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
		colorAnimation.setDuration(duration);
		colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

		    @Override
		    public void onAnimationUpdate(ValueAnimator animator) {
		        v.setBackgroundColor((Integer) animator.getAnimatedValue());
		    }

		});
		colorAnimation.start();
	}

	
	/*
	 * Get display size
	 * 
	 * NIECETOHAVE: Does this approach also work correctly if the fragment 
	 * is only a part/fragment of the bigger page ?
	 * 
	 * Within the fragment we normally can do:
	 * 
	 * <code>
	 * Display display = getActivity().getWindowManager().getDefaultDisplay();
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Point getDisplaySize(){
		Point size = new Point();
		WindowManager wm = (WindowManager) App.getInstance().getAppContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if(Build.VERSION.SDK_INT < 13) {
			size.x = display.getWidth();  // deprecated
			size.y = display.getHeight();  // deprecated
	    }
	    else {			
			display.getSize(size);
	    }
		return size;
	}	

	/*
	 * Global method for changing fonts throughout application. This could also be achieved by extending 
	 * widgets (e.g. TextView), but this method has some serious memory leaks:
	 * 
	 * http://stackoverflow.com/questions/2376250/custom-fonts-and-xml-layouts-android/7197867#7197867
	 */
	public static void changeFonts(ViewGroup root) {
		
		Typeface tf = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Raleway_Light.ttf");

        for(int i = 0; i <root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView ) {
                ((TextView)v).setTypeface(tf);
            }
            else if(v instanceof Button) {
                ((Button)v).setTypeface(tf);
            }
            else if(v instanceof EditText) {
                ((EditText)v).setTypeface(tf);
            }
            else if(v instanceof ViewGroup) {
                changeFonts((ViewGroup)v);
            }
        }
	
	}
	
	public static void changeFonts(TextView tv) {
		Typeface tf = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Raleway_Light.ttf");
		tv.setTypeface(tf);
	}

	/* 
	 * Create a centered progress dialog with no text
	 * - based upon http://stackoverflow.com/questions/16980404/display-progressdialog-without-text-android
	 * 
	 * Use it like this
	 * 
	 * <code/>
	 * final ProgressDialog progressDialog = UiUtils.createProgressDialog(root.getContext());
	 * progressDialog.show();
	 * 
	 * Or
	 * 
	 * Make your own with text etc.:
	 * 
	 * <code/>
	 * final ProgressDialog progress = ProgressDialog.show(this.getActivity(), "dialog title", "dialog message", true);
	 * progress.setCancelable(false);
	 * 
	 */
    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progressdialog);
        return dialog;
    }
    
    
    public static void setSvg(ImageView imgView, String svgPath) {
    	
    	try {
			setHardwareAccelerated(imgView, false);
			SVG svg = SVGParser.getSVGFromAsset(App.getInstance().getAssets(), svgPath);
			svg.createPictureDrawable().setBounds(0, 0, 100, 100);
			Drawable drawable = svg.createPictureDrawable();
			imgView.setImageDrawable(drawable);
			
		} catch (SVGParseException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
    	
    }
    
    
    public static void setHardwareAccelerated(View view, boolean enabled){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if(enabled)
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            else view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    
    
    
    /*
     * Simple show loading progression to indicate training is running
     * 
     * - always when we get 0 or else or every 4th number.
     */
    public static void simpleTextLoader(TextView text, int num) {

    	if(num == 0 || num % 4 == 0) {
			text.append(".");    		
    	}
    	
    }
    
    
    public static void formatXYPlot(XYPlot xy) {
    	
    	// Format XY Plot
		xy.getBackgroundPaint().setColor(Color.TRANSPARENT);
		xy.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
		xy.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
		
	    // Remove legend
	    xy.getLayoutManager().remove(xy.getLegendWidget());
	    xy.getLayoutManager().remove(xy.getDomainLabelWidget());
	    xy.getLayoutManager().remove(xy.getRangeLabelWidget());
	    xy.getLayoutManager().remove(xy.getTitleWidget());
	    
	    
	    
	    
	    
		
	 
	    // reduce the number of range labels
	    xy.setTicksPerRangeLabel(3);
	    
	    // add some padding
	    xy.getGraphWidget().setGridPaddingRight(60);
	    xy.getGraphWidget().setGridPaddingLeft(10);
	    xy.getGraphWidget().setGridPaddingTop(10);
	    xy.getGraphWidget().setGridPaddingBottom(10);
	    
	    // remove border
	    xy.setBorderStyle(BorderStyle.NONE, 0.0f, 0.0f);
	    
	    // remove y axis labels and grids
	    xy.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getDomainOriginLinePaint().setColor(Color.TRANSPARENT);
	    
	    // remove x axis labels and grids
	    xy.getGraphWidget().getRangeLabelPaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.TRANSPARENT);
	    xy.getGraphWidget().getRangeOriginLinePaint().setColor(Color.TRANSPARENT);
    	
    }
    
    
    public static XYSeries getFormattedXYSeries(List<Number> numbers) {
    	
    	return new SimpleXYSeries(
    			numbers,          						// SimpleXYSeries takes a List so turn our array into a List
	            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, 	// Y_VALS_ONLY means use the element index as the x value
	            "relative alpha");                          // Set the display title of the series
		
    }
    
    
    public static LineAndPointFormatter getLineAndPointFormatter() {
    	
    	// Create a formatter to use for drawing a series using LineAndPointRenderer:
	    return new LineAndPointFormatter(
	    		App.getInstance().getResources().getColor(R.color.Gray),	// line color
	    		App.getInstance().getResources().getColor(R.color.Gray),	// point color
	    		null,                										// fill color (none)
	    		null, 														// point label formatter - e.g. new PointLabelFormatter(Color.rgb(200, 200, 200)),
	    		FillDirection.BOTTOM);
    	
    }
	
}
