package dk.itu.alphatrainer.recordings;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.model.Recording;

public class ActivityRecordings extends Activity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "ActivityRecordings";
	private ListView listViewRecordings;
	private ListAdapterRecordings adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recordings);
		
		listViewRecordings = (ListView) findViewById(R.id.listview_recordings);
		listViewRecordings.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Recording r = (Recording) listViewRecordings.getItemAtPosition(position);
				Intent i = new Intent(getApplicationContext(), ActivityRecording.class);
            	i.putExtra("recordingId", r.getId());
            	startActivity(i);
			}
		});
		
		// get recordings
		List<Recording> recordings = App.getInstance().getDAO().getRecordings();
		adapter = new ListAdapterRecordings(this, R.layout.listitem_recordings, recordings);
		listViewRecordings.setAdapter(adapter);
		
	}


}
