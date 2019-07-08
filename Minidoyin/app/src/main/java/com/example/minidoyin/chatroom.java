package com.example.minidoyin;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class chatroom extends AppCompatActivity {

    private EditText editText_say;
    private ImageView imageView_send;
    private TextView textView_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        editText_say = findViewById(R.id.ed_say);
        imageView_send = findViewById(R.id.btn_send_info);
        textView_context = findViewById(R.id.tv_content_info);

        imageView_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText_say.getText().toString();
                if(str.length()<=0){
                    Toast.makeText(chatroom.this,"文本输入为空",Toast.LENGTH_LONG).show();
                }else{
                    SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd mm:ss ");
                    String date = sdf.format(new Date());
                    String old_str = textView_context.getText().toString();
                    textView_context.setText(old_str+"\n"+
                            date +" :\n 我："
                            +str);
                }
                editText_say.setText("");
            }
        });




    }
}
