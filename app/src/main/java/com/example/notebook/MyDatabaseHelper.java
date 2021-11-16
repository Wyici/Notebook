package com.example.notebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper { //创建数据库
    public static final String CREATE_NOTE = "create table Notebook ("
            + "_id integer primary key autoincrement,"//为了配合simpleadpatercursor，所以必须有一列是_id
            + "title text not null,"
            + "content text,"
            + "date datetime not null default current_time,"
            + "author text not null,"
            + "imgP text)";
    private Context mContext;
    public MyDatabaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
    }//创建

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//升级

    }
}
