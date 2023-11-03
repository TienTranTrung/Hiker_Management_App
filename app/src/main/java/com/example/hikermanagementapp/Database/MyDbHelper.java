package com.example.hikermanagementapp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDbHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "MHike.db";
    private static final int DATABASE_VERSION = 1;

    // Hike table
    private static final String TABLE_HIKE = "hike";
    private static final String COLUMN_HIKE_ID = "id";
    private static final String COLUMN_HIKE_NAME = "name";
    private static final String COLUMN_HIKE_LOCATION = "location";
    private static final String COLUMN_HIKE_DATE = "date";
    private static final String COLUMN_HIKE_PARKING = "parking";
    private static final String COLUMN_HIKE_LENGTH = "length";
    private static final String COLUMN_HIKE_DIFFICULTY = "difficulty";
    private static final String COLUMN_HIKE_DESCRIPTION = "description";
    private static final String COLUMN_HIKE_WEATHER = "weather";
    private static final String COLUMN_HIKE_TERRAIN = "terrain";
    private static final String COLUMN_HIKE_USER = "hikeUser";

    // Observation table
    private static final String TABLE_OBSERVATION = "observation";
    private static final String COLUMN_OBSERVATION_ID = "id";
    private static final String COLUMN_OBSERVATION_TEXT = "observationName";
    private static final String COLUMN_OBSERVATION_TIME = "time";
    private static final String COLUMN_OBSERVATION_COMMENTS = "comments";
    private static final String COLUMN_OBSERVATION_IMAGE = "observationImage";
    private static final String COLUMN_OBSERVATION_HIKE_ID = "hikeId";

    // User table
    public static final String TABLE_USER = "Login";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Create table query
    public MyDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // This is called the first time a database is accessed. There should be code in here to create a new database.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTablesHike(db);
        createTablesObservation(db);
        createTablesUser(db);
        insertDataUser(db);
    }

    private void insertDataUser(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, "admin");
        cv.put(COLUMN_PASSWORD, "admin");
        db.insert(TABLE_USER, null, cv);
        cv.put(COLUMN_USERNAME, "user");
        cv.put(COLUMN_PASSWORD, "user");
        db.insert(TABLE_USER, null, cv);
        cv.put(COLUMN_USERNAME, "tienttgcs200400@fpt.edu.vn");
        cv.put(COLUMN_PASSWORD, "@TienTT@123456");
        db.insert(TABLE_USER, null, cv);
    }

    private void createTablesUser(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_USER +
                " (" + COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT not null);";
        db.execSQL(query);
    }

    private void createTablesHike(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_HIKE +
                " (" + COLUMN_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HIKE_NAME + " TEXT, " +
                COLUMN_HIKE_LOCATION + " TEXT, " +
                COLUMN_HIKE_DATE + " TEXT, " +
                COLUMN_HIKE_PARKING + " TEXT, " +
                COLUMN_HIKE_LENGTH + " TEXT, " +
                COLUMN_HIKE_DIFFICULTY + " TEXT, " +
                COLUMN_HIKE_DESCRIPTION + " TEXT, " +
                COLUMN_HIKE_WEATHER + " TEXT, " +
                COLUMN_HIKE_TERRAIN + " TEXT, " +
                COLUMN_HIKE_USER + " TEXT);";
        db.execSQL(query);
    }

    private void createTablesObservation(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_OBSERVATION +
                " (" + COLUMN_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OBSERVATION_TEXT + " TEXT, " +
                COLUMN_OBSERVATION_TIME + " TEXT, " +
                COLUMN_OBSERVATION_COMMENTS + " TEXT, " +
                COLUMN_OBSERVATION_IMAGE + " TEXT, " + // the image will be stored as a URL string
                COLUMN_OBSERVATION_HIKE_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_OBSERVATION_HIKE_ID + ") REFERENCES " + TABLE_HIKE + "(" + COLUMN_HIKE_ID + "));";
        db.execSQL(query);
    }

    // Add methods for adding, updating, deleting, and retrieving Hike and Observation objects
    public long addHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIKE_NAME, hike.getName());
        values.put(COLUMN_HIKE_LOCATION, hike.getLocation());
        values.put(COLUMN_HIKE_DATE, hike.getDate());
        values.put(COLUMN_HIKE_PARKING, hike.getParkingAvailable());
        values.put(COLUMN_HIKE_LENGTH, hike.getLength());
        values.put(COLUMN_HIKE_DIFFICULTY, hike.getDifficulty());
        values.put(COLUMN_HIKE_DESCRIPTION, hike.getDescription());
        values.put(COLUMN_HIKE_WEATHER, hike.getWeatherCondition());
        values.put(COLUMN_HIKE_TERRAIN, hike.getTerrainType());
        values.put(COLUMN_HIKE_USER, hike.getHikeUser());
        return db.insert(TABLE_HIKE, null, values);
    }

    public long updateHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIKE_NAME, hike.getName());
        values.put(COLUMN_HIKE_LOCATION, hike.getLocation());
        values.put(COLUMN_HIKE_DATE, hike.getDate());
        values.put(COLUMN_HIKE_PARKING, hike.getParkingAvailable());
        values.put(COLUMN_HIKE_LENGTH, hike.getLength());
        values.put(COLUMN_HIKE_DIFFICULTY, hike.getDifficulty());
        values.put(COLUMN_HIKE_DESCRIPTION, hike.getDescription());
        values.put(COLUMN_HIKE_WEATHER, hike.getWeatherCondition());
        values.put(COLUMN_HIKE_TERRAIN, hike.getTerrainType());
        values.put(COLUMN_HIKE_USER, hike.getHikeUser());
        return db.update(TABLE_HIKE, values, COLUMN_HIKE_ID + "=?", new String[]{String.valueOf(hike.getId())});
    }

    public boolean deleteHike(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_HIKE, COLUMN_HIKE_ID + "=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    public List<Hike> getAllHikes(String hikeUser) {
        List<Hike> hikes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HIKE + " WHERE " + COLUMN_HIKE_USER + " = ?", new String[]{hikeUser});
        if (cursor.moveToFirst()) {
            do {
                Hike hike = new Hike();
                hike.setId(cursor.getInt(0));
                hike.setName(cursor.getString(1));
                hike.setLocation(cursor.getString(2));
                hike.setDate(cursor.getString(3));
                hike.setParkingAvailable(cursor.getString(4));
                hike.setLength(cursor.getString(5));
                hike.setDifficulty(cursor.getString(6));
                hike.setDescription(cursor.getString(7));
                hike.setWeatherCondition(cursor.getString(8));
                hike.setTerrainType(cursor.getString(9));
                hike.setHikeUser(cursor.getString(10));
                hikes.add(hike);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return hikes;
    }

    public long addObservation(Observation observation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_OBSERVATION_TEXT, observation.getObservationName());
        values.put(COLUMN_OBSERVATION_TIME, observation.getTime());
        values.put(COLUMN_OBSERVATION_COMMENTS, observation.getComments());
        values.put(COLUMN_OBSERVATION_IMAGE, observation.getObservationImage());
        values.put(COLUMN_OBSERVATION_HIKE_ID, observation.getHikeId());
        return db.insert(TABLE_OBSERVATION, null, values);
    }

    public long updateObservation(Observation observation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_OBSERVATION_TEXT, observation.getObservationName());
        values.put(COLUMN_OBSERVATION_TIME, observation.getTime());
        values.put(COLUMN_OBSERVATION_COMMENTS, observation.getComments());
        values.put(COLUMN_OBSERVATION_IMAGE, observation.getObservationImage());
        values.put(COLUMN_OBSERVATION_HIKE_ID, observation.getHikeId());
        return db.update(TABLE_OBSERVATION, values, COLUMN_OBSERVATION_ID + "=?", new String[]{String.valueOf(observation.getId())});
    }

    public boolean deleteObservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_OBSERVATION, COLUMN_OBSERVATION_ID + "=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    public List<Observation> getAllObservations(int hikeId) {
        List<Observation> observations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_OBSERVATION + " WHERE " + COLUMN_OBSERVATION_HIKE_ID + " = ?", new String[]{String.valueOf(hikeId)});
        if (cursor.moveToFirst()) {
            do {
                Observation observation = new Observation();
                observation.setId(cursor.getInt(0));
                observation.setObservationName(cursor.getString(1));
                observation.setTime(cursor.getString(2));
                observation.setComments(cursor.getString(3));
                observation.setObservationImage(cursor.getString(4));
                observation.setHikeId(cursor.getInt(5));
                observations.add(observation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return observations;
    }
    public Boolean checkUserPass(String user, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{user, pass});
        return cursor.getCount() > 0;
    }
    public void deleteAllHike() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HIKE); // delete all rows in a table
        db.execSQL("DELETE FROM " + TABLE_OBSERVATION); // delete all rows in a table
    }

    public void deleteAllObservation() {
        // delete all data of Observation table
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_OBSERVATION);
    }
    public Context getContext() {
        return context;
    }
}