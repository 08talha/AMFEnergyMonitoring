package com.example.talha.amfenergymonitoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MsgDb extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Dbmsg.db";

    //tablo adları
    public static final String CONTACTS_TABLE_GUNLUK = "gunluk";
    public static final String CONTACTS_TABLE_AYLIK="aylik";
    public static final String CONTACTS_TABLE_YILLIK="yillik";
    public static final String CONTACTS_TABLE_Saatlik="saatlik";

    public static final String CONTACTS_TABLE_Veriler = "Veriler";

    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_WatSaat = "wattSaat";
    public static final String CONTACTS_COLUMN_Ay = "Ay";
    public static final String CONTACTS_COLUMN_Yil = "yil";
    public static final String CONTACTS_COLUMN_Tarih = "tarih";
    public static final String CONTACTS_COLUMN_No = "no";
    public static final String CONTACTS_COLUMN_Sayac = "sayac";
    public static final String CONTACTS_COLUMN_Saatlik ="saatlik";
    public static String CONTACTS_TABLE_UPdate="konum";
    public MsgDb(Context context){
        super(context, DATABASE_NAME , null, 1);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE "+CONTACTS_TABLE_GUNLUK +
                        "(id integer primary key,no int, wattSaat double, tarih text)"
        );
        db.execSQL(
                "CREATE TABLE "+CONTACTS_TABLE_AYLIK +
                        "(id integer primary key, no int, wattSaat double, ay text)"
        );
        db.execSQL(
                "CREATE TABLE "+CONTACTS_TABLE_YILLIK +
                        "(id integer primary key, no int, wattSaat double)"
        );
        db.execSQL(
                "CREATE TABLE "+CONTACTS_TABLE_Saatlik +
                        "(id integer primary key,no int,tarih text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_UPdate);
        onCreate(db);

    }


    public boolean upDateAnlik (int id, String tarih) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery="UPDATE "+CONTACTS_TABLE_Saatlik+" SET  "+
                CONTACTS_COLUMN_Tarih+"= '"+tarih+"' where no="+id+"";
        db.execSQL(updateQuery);

        return true;
    }





    public boolean insertSaatlik (int no, String tarih) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no", no);
        contentValues.put("tarih", tarih);

        db.insert(CONTACTS_TABLE_Saatlik, null, contentValues);
        return true;
    }

    public boolean insertGunluk (int no, double wattSaat, String tarih) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no", no);
        contentValues.put("wattSaat", wattSaat);
        contentValues.put("tarih", tarih);

        db.insert(CONTACTS_TABLE_GUNLUK, null, contentValues);
        return true;
    }




    public boolean insertAylik (int no, double wattSaat, String ay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no", no);
        contentValues.put("wattSaat",wattSaat );
        contentValues.put("ay", ay);
        db.insert(CONTACTS_TABLE_AYLIK, null, contentValues);
        return true;

    }
    public boolean insertYillik (int no, double wattSaat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no", no);
        contentValues.put("wattSaat",wattSaat );

        db.insert(CONTACTS_TABLE_YILLIK, null, contentValues);
        return true;

    }


    public Cursor getSaatlik(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select tarih from "+CONTACTS_TABLE_Saatlik+" where no="+id, null );
        return res;
    }
    public Cursor getGunluk(int id,String drm) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select "+ drm +" from "+CONTACTS_TABLE_GUNLUK+" where id="+id, null );
        return res;
    }
    public Cursor getAylik(int id,String drm) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res2 =  db.rawQuery( "select "+ drm +" from "+CONTACTS_TABLE_AYLIK+" where id="+id+"", null );
        return res2;
    }
    public Cursor getYillik(int id,String drm) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res3 =  db.rawQuery( "select "+ drm +" from "+CONTACTS_TABLE_YILLIK+" where id="+id+"", null );
        return res3;
    }
    public Cursor getBoyutAlma(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res4 =  db.rawQuery( "select count('wattSaat') as sayac from "+tableName, null );
        return res4;
    }
    public void deleteTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "DELETE from "+tableName
        );
    }

}
