package dk.itu.alphatrainer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;

/**
 * 
 * LoadView
 * 
 * Can be used for highly customized loaders otherwise use:
 * 
 * - UiUtils.createProgressDialog(context)
 * 
 * or
 * 
 * - the plain ProgressDialog progressDialog 
 * 
 */
public class LoadView {

	
private static final int LAYOUT_ADDED = 999999;
	
	private RelativeLayout parent;
	private RelativeLayout loadView;
	private Context context;
	private LayoutInflater inflater;

	public LoadView(RelativeLayout parent) {
		this.parent = parent;
		this.context = (Context) App.getInstance();
		
		inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		init();
	}

	/*
	 * Draw load with no text 
	 * - quick fixed for now
	 */
	public void drawLoad() {
		draw();
	}
	
	public void drawLoad(String text) {
		TextView txtLoad = (TextView) loadView.findViewById(R.id.txt_load);
		txtLoad.setText(text);
		txtLoad.setVisibility( text == null ? View.GONE : View.VISIBLE);
		draw();
	}

	
	private void draw(){
		// center in parent
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		loadView.setLayoutParams(params);
		
		if (loadView.getTag() != null && (Integer) loadView.getTag() == LAYOUT_ADDED) {
			loadView.setVisibility(View.VISIBLE);
		}
		else {
			parent.addView(loadView);
			loadView.setTag(LAYOUT_ADDED);
		}
	}
	
	
	
	public void hideLoad() {
		if (loadView != null) {
			loadView.setVisibility(View.GONE);
		}
	}
	
	public void showLoad() {
		if (loadView != null) {
			loadView.setVisibility(View.VISIBLE);
		}
	}
	
	
	private void init() {
		loadView = (RelativeLayout) inflater.inflate(R.layout.load_view, null);
	}
	
}
