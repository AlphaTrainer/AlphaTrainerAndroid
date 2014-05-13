package dk.itu.alphatrainer.cloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.model.Recording;

/**
 * Post recording to service (mongolab)
 * 
 * Simply use it:
 * 
 * <code>
 * // save recording to external service
 * new PostRecordingToServiceTask().execute(recordingId);
 * 
 * 
 * <h2>
 * Gson/JSON
 * 
 * <p>
 * We use Gson to build the JSON from model.
 * 
 * Its possible to do:
 * 
 * <code>
 *  Gson gson = new Gson();
 *  String json = gson.toJson(r); 
 *  
 *  <p>
 *  We use excludeFieldsWithoutExposeAnnotation() because we don't want all fields
 *  of the model to be added.
 *  
 *  <p>
 *  Another approach is to mark a field not to be serialized (which also always are not done on static fields.):
 *  
 *  <code>
 *  **transient**
 *  
 * 
 * <h2>
 * Mongolab
 * 
 * <p>
 * A post of a document, recording results in a response with the full data of the posted data including the 
 * id alternative post document in a Json array [ {training} ] but then the response will be something like:
 * { n: 1 } and we'll get not id.
 * 
 * Read more https://support.mongolab.com/entries/20433053-rest-api-for-mongodb
 * 
 */
public class PostRecordingToServiceTask extends AsyncTask<Integer, Void, Void> {

	private static final String TAG = PostRecordingToServiceTask.class
			.getName();
	
	// Note: for real deployment insert right(TM) keys:
	public static final String SERVICE_URL =
			App.getInstance().useTestDatabase() ?
					"https://data-api.mongolab.com/v2/apis/8e9v5m4vg24d9/collections/trainings/documents"
					: "https://data-api.mongolab.com/v2/apis/8e9v5m4vg24d9/collections/trainings/documents";
	
	// we are patient here concerning timeout:
	public static final int POST_TO_SERVICE_TIMEOUT = 100000;
	
	@Override
	protected Void doInBackground(Integer... ids) {

		Log.d(TAG, "doInBackground() - ids[0]: "+ids[0]);
		int recordingId;
		int index = 0;
		do {
			
			recordingId=ids[index];
			// get a recording including all alpha levels
			Recording r = App.getInstance().getDAO().getRecording(recordingId, true);
			if (r == null) {
				break;
			}

			Gson jsonBuilder = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create();
			String json = jsonBuilder.toJson(r);
			Log.d(TAG, "json: " + json.toString());

			/* no ssl approach
			// Create a new HttpClient and Post Header
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, POST_TO_SERVICE_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, POST_TO_SERVICE_TIMEOUT);
			HttpClient httpclient = new DefaultHttpClient(httpParams);
			*/
			
			// ssl friendly approach 
			HttpClient httpclient = getNewHttpClient();

			try {
				HttpPost mHttpPost = new HttpPost(SERVICE_URL);
				mHttpPost.setHeader("Content-type", "application/json");
				mHttpPost.setEntity(new StringEntity(json));
				HttpResponse response = httpclient.execute(mHttpPost);
				int status = response.getStatusLine().getStatusCode();
				Log.d(TAG, "status: "+status);
				
				// only do if the response is successfully and we get a created code 201 (api v. 2)
				if (status == 201) {
					String jsonResponse = EntityUtils.toString(response
							.getEntity());
					Log.d(TAG, "jsonResponse: "+jsonResponse);
					JSONObject object;
					try {
						object = (JSONObject) new JSONTokener(jsonResponse)
								.nextValue();
						String jsonId = object.getString("_id");
						object = (JSONObject) new JSONTokener(jsonId)
								.nextValue();
						String UUID = object.getString("$oid");
						Log.d(TAG, UUID);
						
						boolean updated = App.getInstance().getDAO().updateRecordingServiceDataUpdated(recordingId, UUID);
						if(!updated){
							Log.d(TAG, "Recording "+recordingId+" didn't update right even if if was succesfully posted to service and got UUID/oid: "+UUID);
						}

					} catch (JSONException e) {
						Log.wtf(TAG, "Parsing the json response went wrong", e);
					}
				}
			} catch (ClientProtocolException e) {
				Log.wtf(TAG, "Something went wrong when do post to service", e);
			} catch (UnsupportedEncodingException e) {
				Log.wtf(TAG, "Something probably wrong with the response", e);
			} catch (IOException e) {
				Log.d(TAG,
						"Device probably not connected to the internet - might be in flight mode", e);
			}
			// increment
			index++;
		} while (index < ids.length);

		return null;
	}

	
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        // timeout
	        HttpConnectionParams.setConnectionTimeout(params, POST_TO_SERVICE_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, POST_TO_SERVICE_TIMEOUT);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}

}
