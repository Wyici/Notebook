package com.example.notebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private List<Note> notes;
    private ListView listView;
    private Button create;
    private SimpleCursorAdapter simpleCursorAdapter;
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        create = findViewById(R.id.create);

        myDatabaseHelper = new MyDatabaseHelper(this,"Note.db",null,2);//版本是1的被我删了，一开始没加图片
        db = myDatabaseHelper.getWritableDatabase();//获得数据库的可读写对象实例
        cursor = db.rawQuery("select * from Notebook", null);

        Init();//初始化

        simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[]{"title","date"},new int[]{R.id.title,R.id.date},0);//SimpleCursorAdapter适配器
        listView.setAdapter(simpleCursorAdapter);//通过适配器将SQLite里内容显示到Listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//item点击事件监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = notes.get(position);//获得点击对象
                int _id = note.get_id();//获得点击对象的id
                Intent intent = new Intent(MainActivity.this,Edit.class);//通过Intent显式启动Edit活动
                intent.putExtra("_id",_id);//传递id
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//item长按事件监听器
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {//长按出现对话框提示删除
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note note = notes.get(position);//获得点击对象
                        int _id = note.get_id();//获得点击对象的id
                        db.delete("Notebook","_id = ?",new String[]{_id+""});//删除
                        new RefreshList().execute();//采用后台方式更新
                        Init();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
                return true;
            }
        });

        create.setOnClickListener(new View.OnClickListener() {//新建一条记录
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Create.class);//用显式intent启动Create活动
                startActivity(intent);
            }
        });
    }

    public void Init(){//初始化
        notes = new ArrayList<Note>();
        notes.clear();
        cursor = db.rawQuery("select * from Notebook", null);
        while(cursor.moveToNext())
        {
            int _id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            String date = cursor.getString(3);
            String author = cursor.getString(4);
            String imgP = cursor.getString(5);
            Note note = new Note(_id,title,content,date,author,imgP);
            notes.add(note);
        }
    }

    private class RefreshList extends AsyncTask<Void,Void,Cursor>{//通过后台线程AsyncTask来读取数据库，放入更换Cursor
        @Override
        protected Cursor doInBackground(Void... voids) {//在后台线程中从数据库读取，返回新的游标newCursor
            Cursor newCursor = db.rawQuery("select * from Notebook",null);
            return newCursor;
        }
        protected void onPostExecute(Cursor newCursor){//线程最后执行步骤，更换adapter的游标，并将原游标关闭，释放资源
            simpleCursorAdapter.changeCursor(newCursor);//换cursor
            cursor.close();
            cursor = newCursor;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        new RefreshList().execute();//后台线程更新
        Init();
    }
}