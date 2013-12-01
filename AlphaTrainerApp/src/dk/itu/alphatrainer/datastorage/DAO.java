package dk.itu.alphatrainer.datastorage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import dk.itu.alphatrainer.App;
import dk.itu.alphatrainer.model.AlphaLevel;
import dk.itu.alphatrainer.model.Recording;
import dk.itu.alphatrainer.util.Utils;


/**
 * Data access object
 *  
 * <h2>
 * Time stamps
 * 
 * <p>
 * Lets use unix time stamps (for example 1381752987) which then have type long. 
 * Then its more convenient to serve date to outside java e.g. for JSON and
 * we can have a simple date field on object models etc. of type Long. Otherwise 
 * we pass something like "2013-10-14 12:11:23" around.   
 * 
 * <code>
 * strftime('%s', 'now')
 * // instead of 
 * DATETIME('now')
 * 
 * 
 * Unix time could take the value: 1381748811
 *  
 */
public class DAO extends SQLiteOpenHelper {

	private static final String TAG = "DAO";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AlphaTrainerAppDB";
	private static final String FOLDER_BRAINAPP = "/AlphaTrainerApp";
	private static final String DATESTRINGFORMAT = "yyyy-MM-dd hh:mm:ss.SSS";
	private static final String TABLE_ALPHA_LEVEL = "table_alpha_level";
	private static final String TABLE_RECORDING = "table_recording";
	private static final String COL_ID = "id";
	private static final String COL_TIMESTAMP = "timestamp";
	private static final String COL_ALPHA_LEVEL_RAW = "alpha_level_raw";
	private static final String COL_ALPHA_LEVEL_NORMALIZED = "alpha_level_normalized";
	private static final String COL_ALPHA_LEVEL_MIN = "alpha_level_min";
	private static final String COL_ALPHA_LEVEL_MAX = "alpha_level_max";
	private static final String COL_ALPHA_PEAK_FQ = "alpha_peak_fq";
	private static final String COL_ALPHA_LEVEL_AVG = "alpha_level_avg";
	private static final String COL_ALPHA_LEVEL_START_ID = "alpha_level_start_id";
	private static final String COL_ALPHA_LEVEL_END_ID = "alpha_level_end_id";
	private static final String COL_TYPE = "type";
	private static final String COL_LENGTH = "length";
	private static final String COL_SERVICE_DATA_ID = "service_data_id";
	private static final String COL_SERVICE_DATA_UPDATED = "service_data_updated";
	private static final String COL_SERVICE_FILE_UPDATED = "service_file_updated";
	private static final String COL_RAW_EEG_FILE = "raw_eeg_file";
	private static final String COL_FEEDBACK_UI_TYPE = "feedback_ui_type";
	private static final String COL_HEADSET_TYPE = "headset_type";
	// private static final long SECONDS_PER_DAY = 60 * 60 * 24;

	
	private static final String CREATE_TABLE_ALPHA_LEVEL = "CREATE TABLE "
			+ TABLE_ALPHA_LEVEL + " ("
			+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_TIMESTAMP + " TIMESTAMP DEFAULT (strftime('%s', 'now')), "
			+ COL_ALPHA_LEVEL_RAW + " REAL, "
			+ COL_ALPHA_LEVEL_NORMALIZED + " INTEGER "
			+ ");";
	
	private static final String CREATE_TABLE_RECORDING = "CREATE TABLE "
			+ TABLE_RECORDING + " ("
			+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_TIMESTAMP + " TIMESTAMP DEFAULT (strftime('%s', 'now')), "
			+ COL_TYPE + " VARCHAR, "
			+ COL_ALPHA_LEVEL_MIN + " REAL, "
			+ COL_ALPHA_LEVEL_MAX + " REAL, "
			+ COL_ALPHA_PEAK_FQ + " REAL, "
			+ COL_ALPHA_LEVEL_AVG + " REAL, "
			+ COL_LENGTH + " INTEGER, "
			+ COL_ALPHA_LEVEL_START_ID + " INTEGER REFERENCES " + TABLE_ALPHA_LEVEL + " (" + COL_ID + "), " // ON DELETE CASCADE, "
			+ COL_ALPHA_LEVEL_END_ID + " INTEGER REFERENCES " + TABLE_ALPHA_LEVEL + " (" + COL_ID + "), " // ON DELETE CASCADE"
			+ COL_SERVICE_DATA_ID + " TEXT, "
			+ COL_SERVICE_DATA_UPDATED + " INTEGER DEFAULT 0, "
			+ COL_SERVICE_FILE_UPDATED + " INTEGER DEFAULT 0, "
			+ COL_RAW_EEG_FILE + " TEXT, "
			+ COL_HEADSET_TYPE + " VARCHAR, "
			+ COL_FEEDBACK_UI_TYPE + " VARCHAR"
			+ ");";
	
	
	private static final String SELECT_TABLE_RECORDING = "SELECT " + 
			COL_ID + ", " + 
			COL_TIMESTAMP + ", " +
			COL_TYPE + ", " + 
			COL_ALPHA_LEVEL_MIN + ", " + 
			COL_ALPHA_LEVEL_MAX + ", " + 
			COL_ALPHA_PEAK_FQ + ", " + 
			COL_ALPHA_LEVEL_AVG + ", " +
			COL_ALPHA_LEVEL_START_ID + ", " +
			COL_ALPHA_LEVEL_END_ID + ", " +
			COL_LENGTH + ", " +
			COL_FEEDBACK_UI_TYPE + ", " +
			COL_HEADSET_TYPE +
			" FROM " + TABLE_RECORDING;

	private static final String SELECT_TABLE_RECORDING_NOT_UPDATED_TO_SERVICE = "SELECT " + 
			COL_ID + " FROM " + TABLE_RECORDING + " WHERE "+ COL_SERVICE_DATA_UPDATED +"=0";
	
	private static final String SELECT_AGG_AVG_ALPHA = "SELECT " + 
			"AVG(" + COL_ALPHA_LEVEL_AVG + ")" +
			" FROM " + TABLE_RECORDING;
	
	

	public DAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * By default foreign key constraints are disable 
	 * 
	 * 
	 * To enable it do:
	 * 
	 * <code>
	 * if (!db.isReadOnly()) {
	 *     // Enable foreign key constraints
	 *     db.execSQL("PRAGMA foreign_keys=ON;");
	 * }
	 * 
	 * Cascading delete Only supported by Sqlite version 3.6.19 / Android 2.2. (api level 8)
	 * 
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
    	// disabled because we currently don't use foreign key's 
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_ALPHA_LEVEL);
		db.execSQL(CREATE_TABLE_RECORDING);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropAllTables();
		onCreate(db);
	}

	public void hardResetDatabase() {
		dropAllTables();
		onCreate(this.getReadableDatabase());
	}

	/*
	 * CRUD(Create, Read, Update, Delete) Operations
	 */

	/*
	 * example usage (tested):
	 * 
	 * App.getInstance().getDAO().addAlphaLevel(1.1f, 11, 1.1f, 1.1f, 1.1f,
	 * 1.1f); App.getInstance().getDAO().addAlphaLevel(2.2f, 22, 2.2f, 2.2f,
	 * 2.2f, 2.2f);
	 */
	public int addAlphaLevel(float raw, float normalized) {
		int generatedId;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_ALPHA_LEVEL_RAW, raw);
		values.put(COL_ALPHA_LEVEL_NORMALIZED, normalized);
		generatedId = (int) db.insert(TABLE_ALPHA_LEVEL, null, values);
		db.close();
		return generatedId;
	}

	/*
	 * example usage (tested):
	 * 
	 * int[] daoTest = App.getInstance().getDAO().getAllNormalizedAlphaValues();
	 * for (int i=0; i<daoTest.length; i++) { Log.d(TAG, "daoTest[" + i + "] - "
	 * + daoTest[i]); }
	 */
	public int[] getAllNormalizedAlphaValues() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT " + COL_ALPHA_LEVEL_NORMALIZED + " FROM "
				+ TABLE_ALPHA_LEVEL;
		Cursor cursor = db.rawQuery(countQuery, null);
		int[] normalizedAlphaValues = new int[cursor.getCount()];
		int i = 0;
		if (cursor.moveToFirst()) {
			do {
				normalizedAlphaValues[i++] = cursor.getInt(cursor
						.getColumnIndex(COL_ALPHA_LEVEL_NORMALIZED));
			} while (cursor.moveToNext());
		}
		db.close();

		return normalizedAlphaValues;
	}

	public int getAlphaLevelCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT " + COL_ID + " FROM " + TABLE_ALPHA_LEVEL;
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/*
	 * Alpha Level Date
	 * 
	 * example usage:
	 * 
	 * <code>
	 * String testDate = App.getInstance().getDAO().getAlphaLevelDate(0);
	 * Log.d(TAG, "testDate: " + testDate);
	 * 
	 */
	public String getAlphaLevelDate(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String dateQuery = "SELECT " + COL_ID + "," + COL_TIMESTAMP + " FROM "
				+ TABLE_ALPHA_LEVEL + " WHERE " + COL_ID + " = " + id;
		Cursor cursor = db.rawQuery(dateQuery, null);
		Long date = 0l;
		Log.d(TAG,
				"getAlphaLevelDate() - cursor.count() - " + cursor.getCount());
		if (cursor.moveToFirst()) {
			// long dateTemp = cursor.getLong(1);
			// date = formatDate(dateTemp);
			date = cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP));
		}
		cursor.close();
		return formatDate(date);
	}
	
	
	public int addRecording(float min, float max, float alphaPeakFq, float average, int recordingStart, 
							int recordingEnd, String type, int length, String feedbackUiType, String headsetType) {
		int generatedId;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_ALPHA_LEVEL_MIN, min);
		values.put(COL_ALPHA_LEVEL_MAX, max);
		values.put(COL_ALPHA_PEAK_FQ, alphaPeakFq);
		values.put(COL_ALPHA_LEVEL_AVG, average);
		values.put(COL_ALPHA_LEVEL_START_ID, recordingStart);
		values.put(COL_ALPHA_LEVEL_END_ID, recordingEnd);
		values.put(COL_TYPE, type);
		values.put(COL_LENGTH, length);
		values.put(COL_FEEDBACK_UI_TYPE, feedbackUiType);
		values.put(COL_HEADSET_TYPE, headsetType);
		generatedId = (int) db.insert(TABLE_RECORDING, null, values);
		db.close();
		return generatedId;
	}
	
	/*
	 * Delete a recording
	 * - because we have set up support for foreign key constraints
	 *   we expect the deletion of a recording to delete the related 
	 *   alpha recordings as well.
	 */
	public boolean deleteRecording(int recoringId) {
		Recording r = getRecording(recoringId, false); 
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause = COL_ID + "=" + recoringId;
		int affectedRows = db.delete(TABLE_RECORDING, whereClause, null);
		if (affectedRows==1) {
			String deleteQuery = "DELETE FROM "+TABLE_ALPHA_LEVEL+" WHERE ID BETWEEN "+r.getAlphaLevelStartId()+" AND "+r.getAlphaLevelEndId() ;
			Cursor cursor = db.rawQuery(deleteQuery, null);
			int count = cursor.getCount();
			Log.d(TAG, "deleteQuery Count: " + count);
			cursor.close();
			return true;
		}
		return false;
	}

	/*
	 * Update if a recording is updated to service
	 *  
	 * Recall sqlite don't have boolean type use 0 | 1
	 */
	public boolean updateRecordingServiceDataUpdated(int recoringId, String UUID) { 
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause = COL_ID + "=" + recoringId;
		ContentValues args = new ContentValues();
		args.put(COL_SERVICE_DATA_UPDATED, 1);
		args.put(COL_SERVICE_DATA_ID, UUID);
		int affectedRows = db.update(TABLE_RECORDING, args, whereClause, null);
		if (affectedRows==1) {
			return true;
		}
		return false;
	}

	/*
	 * Get recordings that are not updated to external service yet
	 */
	public List<Integer> getRecordingsNotUpdatedToService() {
		List<Integer> recordings = new ArrayList<Integer>();
		final String selectRecordingQuery = SELECT_TABLE_RECORDING_NOT_UPDATED_TO_SERVICE; 		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectRecordingQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				recordings.add(cursor.getInt(cursor.getColumnIndex(COL_ID)));
			}
			while (cursor.moveToNext());
		}
		db.close();
		return recordings;
	}
		
	
	/*
	 * example usage:
	 * 
	 * int recordingCount = App.getInstance().getDAO().getRecordingCount();
	 * Log.d(TAG, "recordingCount: " + recordingCount);
	 */
	public int getRecordingCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT " + COL_ID + " FROM " + TABLE_RECORDING;
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}
	
	/*
	 * example usage:
	 * 
	 * <code>
	 * List<Recording> recordings = App.getInstance().getDAO().getRecordings();
	 *	 for (Recording r : recordings) {
	 *		Log.d(TAG, "RECORDING | ID: " + r.getId() + " | TYPE: " + r.getType() + " | PEAK: " + r.getAlphaPeakFq() + " | MIN: " + r.getMinAlphaLevel() + " | MAX: " + r.getMaxAlphaLevel());
	 *	 }
	 *
	 * NIECETOHAVE: merge with getRecordings(int id ...)
	 * 
	 */
	public List<Recording> getRecordings() {
		List<Recording> recordings = new ArrayList<Recording>();
		
		final String selectRecordingQuery = SELECT_TABLE_RECORDING; 
				
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectRecordingQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				recordings.add(new Recording(
						cursor.getInt(cursor.getColumnIndex(COL_ID)), 
						cursor.getString(cursor.getColumnIndex(COL_TYPE)),
						cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_MIN)),
						cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_MAX)),
						cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_PEAK_FQ)),
						cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_AVG)),
						cursor.getInt(cursor.getColumnIndex(COL_ALPHA_LEVEL_START_ID)),
						cursor.getInt(cursor.getColumnIndex(COL_ALPHA_LEVEL_END_ID)),
						App.getInstance().getSessionManager().getUserId(),
						cursor.getInt(cursor.getColumnIndex(COL_LENGTH)),
						cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP)),
						cursor.getString(cursor.getColumnIndex(COL_FEEDBACK_UI_TYPE)),
						cursor.getString(cursor.getColumnIndex(COL_HEADSET_TYPE))
						));
			}
			while (cursor.moveToNext());
		}
		db.close();	
		return recordings;
	}

	/*
	 * example usage:
	 * 
	 * <code>
	 * Recording eight = App.getInstance().getDAO().getRecording(8);
	 * Log.d(TAG, "RECORDING | ID: " + eight.getId() + " | TYPE: " + eight.getType() + " | ALPHA LEVELS: " + eight.getAlphaLevels().length);
	 * 
	 * NIECETOHAVE: merge with getRecordings(int id ...)
	 * 
	 */
	public Recording getRecording(int id, boolean deep) {
		
		final String selectRecordingQuery = SELECT_TABLE_RECORDING.concat(" WHERE " + COL_ID + " = " + id);
		
		SQLiteDatabase db = this.getReadableDatabase();
		Recording r = null;
		Cursor cursor = db.rawQuery(selectRecordingQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			r = new Recording(
					cursor.getInt(cursor.getColumnIndex(COL_ID)), 
					cursor.getString(cursor.getColumnIndex(COL_TYPE)),
					cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_MIN)),
					cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_MAX)),
					cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_PEAK_FQ)),
					cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_AVG)),
					cursor.getInt(cursor.getColumnIndex(COL_ALPHA_LEVEL_START_ID)),
					cursor.getInt(cursor.getColumnIndex(COL_ALPHA_LEVEL_END_ID)),
					App.getInstance().getSessionManager().getUserId(),
					cursor.getInt(cursor.getColumnIndex(COL_LENGTH)),
					cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP)),
					cursor.getString(cursor.getColumnIndex(COL_FEEDBACK_UI_TYPE)),
					cursor.getString(cursor.getColumnIndex(COL_HEADSET_TYPE))
					);
			
			if (deep) {
				int start = cursor.getInt(cursor.getColumnIndex(COL_ALPHA_LEVEL_START_ID));
				int end = cursor.getInt(cursor.getColumnIndex(COL_ALPHA_LEVEL_END_ID));
				
				final String selectAlphaLevelsQuery = "SELECT " + COL_ID + ", " + COL_ALPHA_LEVEL_RAW + ", " + 
					  COL_ALPHA_LEVEL_NORMALIZED + ", " + COL_TIMESTAMP + 
				      " FROM " + TABLE_ALPHA_LEVEL + " WHERE " + COL_ID + " BETWEEN " + start + " AND " + end;
				
				cursor = db.rawQuery(selectAlphaLevelsQuery, null);
				
				if (cursor.moveToFirst()) {
					do {
						r.addAlphaLevel(new AlphaLevel(
							cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_RAW)),
							cursor.getInt(cursor.getColumnIndex(COL_ALPHA_LEVEL_NORMALIZED)),
							cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP))
						));
					}
					while (cursor.moveToNext());
				}
			}
		}
		db.close();	
		return r;
	}
	
	
	public int getMostRecentPerformance(String type) {
		
		final int count = 2;
		
		List<Number> recentPerformances = getNewestRecordings(type, count);
		
		if (recentPerformances.size() == count) {
			
			float performanceAvg = (Float) recentPerformances.get(1);
			float referenceAvg = (Float) recentPerformances.get(0);
			
			if (referenceAvg != 0.0f) {
				return Math.round(performanceAvg / referenceAvg * 100);
			}
		}
		
		// if only one training has been performed, it is of course 100% of itself
		return 100; 
	}
	
	public int getPerformanceDay(String type) {
		
		long secondsPassedSinceMidnight = Utils.getSecondsPassedSinceMidnight();
		Log.d(TAG, "secondsPassedSinceMidnight: " + secondsPassedSinceMidnight);
		
		// in order to calculate start time (one day before now), we subtract x days from now. The divide by 1000L converts from miliseconds to unix time stamp
		long start = App.getInstance().getDate().getTime() / 1000L - secondsPassedSinceMidnight;
		
		logPerformanceAggregationCount("count day: ", type, start);
		
		return getPerformance(type, start);
	}
	
	public int getPerformanceWeek(String type) {
		
		long secondsPassedSinceStartOfWeek = getSecondsPassedSinceStartOfWeek();
		Log.d(TAG, "secondsPassedSinceStartOfWeek: " + secondsPassedSinceStartOfWeek);
		
		// in order to calculate start time (one day before now), we subtract x days from now. The divide by 1000L converts from miliseconds to unix time stamp
		long start = App.getInstance().getDate().getTime() / 1000L - secondsPassedSinceStartOfWeek;
		
		logPerformanceAggregationCount("count week: ", type, start);
		
		return getPerformance(type, start);
	}
	
	public int getPerformanceMonth(String type) {
		
		long secondsPassedSinceStartOfMonth = getSecondsPassedSinceStartOfMonth();
		Log.d(TAG, "secondsPassedSinceStartOfMonth: " + secondsPassedSinceStartOfMonth);
		
		// in order to calculate start time (one day before now), we subtract x days from now. The divide by 1000L converts from miliseconds to unix time stamp
		long start = App.getInstance().getDate().getTime() / 1000L - secondsPassedSinceStartOfMonth;
		
		logPerformanceAggregationCount("count month: ", type, start);
		
		return getPerformance(type, start);
	}
	
	
	public List<Number> getNewestRecordings(String type, int count) {
		
		Log.d(TAG, "getNewestRecordings() - type:" + type + " - count: " + count);
		
		List<Number> baselines = new ArrayList<Number>();
		
		final String newestRecordingsQuery = getMostRecentPerformancesQuery(type).concat(" LIMIT " + count); // SELECT_AVG_ALPHA_BASELINE.concat(" WHERE " + COL_TYPE + " = '" + type + "'" + " LIMIT " + count);
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(newestRecordingsQuery, null);
		
		if (cursor.moveToLast()) {
			do {
				float avg = cursor.getFloat(cursor.getColumnIndex(COL_ALPHA_LEVEL_AVG));
				baselines.add(avg);
			}
			while (cursor.moveToPrevious());
		}
		
		return baselines;
	}
	
	
	
	
	/*
	 * private helper functions
	 */
	
	private String getMostRecentPerformancesQuery(String type) {
		
		return "SELECT " + 
				COL_ID + ", " +
				COL_TIMESTAMP + ", " +
				COL_TYPE + ", " +
				COL_ALPHA_LEVEL_AVG +
				" FROM " + TABLE_RECORDING +
				" WHERE " + COL_TYPE + " = '" + type + "'" +
				" ORDER BY id DESC";
	}

	
	private long getSecondsPassedSinceStartOfWeek() {
		// day of week goes from Sunday=1 to Saturday = 7
		// we want week to start monday which is why we subtract one from day of week and set case sunday = 1
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7 : Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		Log.d(TAG, "dayOfWeek: " + dayOfWeek);
		
		return Utils.DAY_MILLIS / 1000L * (dayOfWeek-1) + Utils.getSecondsPassedSinceMidnight();
	}
	
	private long getSecondsPassedSinceStartOfMonth() {
		int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		Log.d(TAG, "dayOfMonth: " + dayOfMonth);
		
		return Utils.DAY_MILLIS / 1000L * (dayOfMonth-1) + Utils.getSecondsPassedSinceMidnight();
	}
	
	private int getPerformance(String type, long start) {
		
		int performance = -1;
		
		final String selectAvgAlphaQuery = SELECT_AGG_AVG_ALPHA.concat(" WHERE " + COL_TIMESTAMP + " >= " + start + " AND " + COL_TYPE + " = '" + type + "'");
		final String selectAvgAlphaReferenceQuery = SELECT_AGG_AVG_ALPHA.concat(" WHERE " + COL_TYPE + " = '" + type + "'");
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor performanceCursor = db.rawQuery(selectAvgAlphaQuery, null);
		
		if (performanceCursor.moveToFirst()) {
			
			Cursor referenceCursor = db.rawQuery(selectAvgAlphaReferenceQuery, null);
			
			if (referenceCursor.moveToFirst()) {
				
				float performanceAvg = performanceCursor.getFloat(0);
				float referenceAvg = referenceCursor.getFloat(0);
				
				Log.d(TAG, "performanceAvg: " + performanceAvg + " - referenceAvg: " + referenceAvg);
				
				if (referenceAvg != 0.0f) {
					return Math.round(performanceAvg / referenceAvg * 100);
				}
				
			}
			
		}
		
		return performance;
	}
	
	
	private void logPerformanceAggregationCount(String text, String type, long start) {
		
		// Log how many recordings are base for avg aggregation
		final String countQuery = "SELECT id,type,timestamp from table_recording WHERE timestamp >= " + start + " AND " + COL_TYPE + " = '" + type + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		Log.d(TAG, text + cursor.getCount());
	}
	
	
	private void dropAllTables() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALPHA_LEVEL);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_RECORDING);
	}

	/*
	 * Format date - example usage:
	 * 
	 * <code> String test = formatDate(82233213123L, "dd/MM/yyyy hh:mm:ss.SSS");
	 */
	private String formatDate(long milliSeconds) {
		DateFormat formatter = new SimpleDateFormat(DATESTRINGFORMAT, Locale.US);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	private static long now() {
		return App.getInstance().getDate().getTime();
	}

	/*
	 * Save byte array to SD card
	 * 
	 * - example usage (tested):
	 * 
	 * <pre>
	 * {@code
	 * float[] testArray = new float[100]; 
	 * 		for (int i=0; i<100; i++) {
	 * 			testArray[i] = (float) Math.random(); 
	 * 		} 
	 * 		byte[] rawBytes =	Arrays.toString(testArray).getBytes(); 
	 * 		try { 
	 * 			boolean success = App.getInstance().getDAO().saveByteArrayToSDCard("testBytes.txt",
	 * rawBytes, true); 
	 * 			Log.d(TAG, "testArray saved successfully to sd card: " + success); 
	 * 		} catch (IOException e) {}
	 * }
	 * </pre>
	 * 
	 * <h2>
	 * Future
	 * 
	 * <p>
	 * NIECETOHAVE: save to edf file
	 */
	public boolean saveByteArrayToSDCard(String fileName, byte[] bytes,
			boolean addTimestamp) throws IOException {

		if (addTimestamp) {
			fileName = (formatDate(now()) + "_" + fileName).replace(' ', '_');
		}

		String root = Environment.getExternalStorageDirectory().toString();
		File rootDir = new File(root + FOLDER_BRAINAPP);
		rootDir.mkdirs();

		if (rootDir.canWrite()) {
			File file = new File(rootDir, fileName);
			if (file.exists())
				file.delete();
			BufferedOutputStream bos = null;
			FileOutputStream fos = null;

			try {
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos);
				bos.write(bytes);
				bos.flush();
				bos.close();
				return true;
			} catch (Exception e) {
			} finally {
				try {
					bos.close();
				} catch (Exception e) {
				}
			}

		}
		return false;

	}

}
