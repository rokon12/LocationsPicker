package com.locationspicker.util;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class SQLiteManager {

	/* Database fields */
	public static final String KEY_ROWID = "id";
	public static final String KEY_LATITUDE = "lat";
	public static final String KEY_LONGITUDE = "lng";
	private static final String DATABASE_TABLE = "Points";
	private Context context;
	private SQLiteDatabase database;
	private SQLiteDBHelper dbHelper;

	public SQLiteManager(Context context) {
		this.context = context;
	}

	public SQLiteManager open() throws SQLException {
		dbHelper = new SQLiteDBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}
	
	/*Create a new point. If the point is successfully created return the new
	  rowId for that note, otherwise return a -1 to indicate failure.*/
	public long addPoint(Location loc) {
		ContentValues initialValues = createContentValues(loc.getLatitude(), loc.getLongitude());
		return database.insert(DATABASE_TABLE, null, initialValues);
	}
	
	/*Update the point (NOT BEING USED)*/
	public boolean updatePoint(long rowId, double lat, double lng) {
		ContentValues updateValues = createContentValues(lat, lng);
		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="+ rowId, null) > 0;
	}
	
	/* Deletes point (NOT BEING USED)*/
	public boolean deletePoint(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	/* Return a Cursor over the list of all points in the database @return Cursor over all points*/
	public Cursor fetchAllPoints() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_LATITUDE, KEY_LONGITUDE }, null, null, null, null, null);
	}
	
	public List<Location> getPoints(){
        List<Location> result = new LinkedList<Location>();
		Cursor cursor = fetchAllPoints();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            result.add(Util.toLocation(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
	}

	/* Deletes all points*/
	public boolean deletePoints() {
		return database.delete(DATABASE_TABLE, null, null) > 0;
	}
	
	/*Return a Cursor positioned at the defined point (NOT BEING USED)*/
	public Cursor fetchPoint(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_LATITUDE, KEY_LONGITUDE},KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	private ContentValues createContentValues(double lat, double lng) {
		ContentValues values = new ContentValues();
		values.put(KEY_LATITUDE, lat);
		values.put(KEY_LONGITUDE, lng);
		return values;
	}
	
	private class SQLiteDBHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "locationspicker";
		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_CREATE = "CREATE TABLE Points (id INTEGER PRIMARY KEY AUTOINCREMENT, lat REAL not null, lng REAL not null);";

		public SQLiteDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/*Called during the creation of database*/
		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
		}

		/*Called during an upgrade of the database*/
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			database.execSQL("DROP TABLE IF EXISTS Points");
			onCreate(database);
		}
	}
}


