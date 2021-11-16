package com.example.notebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Create extends AppCompatActivity implements View.OnClickListener{
    private EditText title;
    private EditText content;
    private TextView date;
    private EditText author;
    private ImageView imageView;
    private ScrollView scrollView;//通过scrollview实现内容与照片的滚动
    private Button save;
    private Button cancel;
    private Button picture;
    String imgP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        date = findViewById(R.id.date);
        author = findViewById(R.id.author);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        imageView = findViewById(R.id.image_view);
        scrollView = findViewById(R.id.scrollView);
        picture = findViewById(R.id.picture);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        picture.setOnClickListener(this);

        Date date1 = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        //配置时间格式
        String Date = simpleDateFormat.format(date1);
        date.setText(Date);

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK)
                {
                    imgP = data.getStringExtra("imgP");//获得图片的存储地址
                    Bitmap bitmap = BitmapFactory.decodeFile(imgP);
                    imageView.setImageBitmap(bitmap);//显式
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
                Intent intent = new Intent(Create.this,Picture.class);//Intent显式启动Picture活动
                startActivityForResult(intent,1);//需要返回值
                break;
            case R.id.save:
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this,"Note.db",null,2);
                SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                String Title_new = String.valueOf(title.getText());
                String Content_new = String.valueOf(content.getText());
                String Author_new = String.valueOf(author.getText());
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("author_name",Author_new);//把作者名通过sharedpreference键值对存储
                editor.apply();
                String Date_new = String.valueOf(date.getText());
                if(Title_new.length()==0)
                {
                    Toast.makeText(this,"请输入标题",Toast.LENGTH_SHORT).show();
                }else {
                    values.put("title", Title_new);
                    values.put("content", Content_new);
                    values.put("date",Date_new);
                    SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);//获取作者名
                    if(pref.getString("author_name","")=="")//如果作者名为空，则设置为默认值AUTHOR
                    {
                        Author_new = "AUTHOR";
                    }
                    values.put("author",Author_new);
                    values.put("imgP",imgP);
                    db.insert("Notebook",null,values);
                    db.close();
                    Create.this.setResult(RESULT_OK, getIntent());
                    Create.this.finish();
                }
                break;
            case R.id.cancel:
                Create.this.setResult(RESULT_OK, getIntent());
                Create.this.finish();
                break;
        }

    }
}