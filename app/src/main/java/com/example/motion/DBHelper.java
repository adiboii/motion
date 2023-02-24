package com.example.motion;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.motion.helpers.MotionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    List<UserData> userDataList = new ArrayList<>();

    public DBHelper(Context context) {
        super(context, "Motion.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Motion (id INTEGER PRIMARY KEY, pose TEXT, datetime DATETIME, accuracy DOUBLE, consistency DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Motion");
        onCreate(db);
    }



    public boolean Insert(String pose, String datetime, int accuracy, int consistency ) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(datetime);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pose", pose);
        contentValues.put("datetime",date.getTime());
        contentValues.put("accuracy",accuracy);
        contentValues.put("consistency",consistency);

        long result = db.insert("Motion", null, contentValues);

        if(result==-1)
            return false;
        else
            return true;
    }


    public List <UserData> GetData() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * FROM MOTION", null);
        if(cursor.moveToNext()){
            userDataList = new ArrayList<>();
            do{
                UserData user = new UserData();
                user.setPose(cursor.getString(1));
                long time = cursor.getLong(2);
                Date date = new Date(time);
                user.setDateTime(date);
                user.setAccuracy(cursor.getInt(3));
                user.setConsistency(cursor.getInt(4));
                userDataList.add(user);

            } while(cursor.moveToNext());
            return userDataList;
        } else {
            return null;
        }
    }

    public boolean AvailData(){
        if(userDataList.isEmpty() || userDataList == null){
            return false;
        } else
            return true;
    }



}
