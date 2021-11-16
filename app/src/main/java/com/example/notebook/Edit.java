package com.example.notebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Edit extends AppCompatActivity implements View.OnClickListener {

    private EditText title;
    private EditText content;
    private TextView date;
    private EditText author;
    private ImageView imageView;
    private ScrollView scrollView;
    private Button save;
    private Button cancel;
    private Button picture;
    private int _id = 0;
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    String Title;
    String Content;
    String Date;
    String Author;
    String imgP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        scrollView = findViewById(R.id.scrollView);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        date = findViewById(R.id.date);
        author = findViewById(R.id.author);
        imageView = findViewById(R.id.image_view);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        picture = findViewById(R.id.picture);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        picture.setOnClickListener(this);

        Intent intent = getIntent();
        _id=intent.getIntExtra("_id",-1);//获得intent传递的id值

        myDatabaseHelper = new MyDatabaseHelper(this,"Note.db",null,2);
        db = myDatabaseHelper.getWritableDatabase();

        Init();
    }
    public void Init(){//初始化
        cursor = db.rawQuery("select * from Notebook where _id="+_id,null);//根据id查找点击记录
        if(cursor.moveToNext())
        {
            Title = cursor.getString(1);
            Content = cursor.getString(2);
            Date = cursor.getString(3);
            Author = cursor.getString(4);
            imgP = cursor.getString(5);
        }
        title.setText(Title);
        content.setText(Content);
        date.setText(Date);
        author.setText(Author);
        Bitmap bitmap = BitmapFactory.decodeFile(imgP);
        imageView.setImageBitmap(bitmap);//显示图片
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK)
                {
                    imgP = data.getStringExtra("imgP");
                    Bitmap bitmap = BitmapFactory.decodeFile(imgP);
                    imageView.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.picture:
                Intent intent=new Intent(Edit.this,Picture.class);//Intent显式启动Picture活动
                startActivityForResult(intent,1);//需要返回值
                break;
            case R.id.save:
                Date date = new Date();
                ContentValues values = new ContentValues();
                String Title_new = String.valueOf(title.getText());
                String Content_new = String.valueOf(content.getText());
                String Author_new = String.valueOf(author.getText());
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("author_name",Author_new);
                editor.apply();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        //配置时间格式
                String Date_new = simpleDateFormat.format(date);
                if(Title_new.length()==0)
                {
                    Toast.makeText(this,"请输入标题",Toast.LENGTH_SHORT).show();
                }else {
                    values.put("title", Title_new);
                    values.put("content", Content_new);
                    SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                    if(pref.getString("author_name","")=="")
                    {
                        Author_new = "AUTHOR";
                    }
                    values.put("author",Author_new);
                    values.put("date",Date_new);
                    values.put("imgP",imgP);
                    db.update("Notebook",values,"_id= ?", new String[]{_id+""});
                    Edit.this.setResult(RESULT_OK, getIntent());
                    Edit.this.finish();
                }
                break;
            case R.id.cancel:
                Edit.this.setResult(RESULT_OK, getIntent());
                Edit.this.finish();
                break;
        }
    }
}