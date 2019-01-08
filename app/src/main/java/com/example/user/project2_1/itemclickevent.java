package com.example.user.project2_1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class itemclickevent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemclickevent);


        // 이전 액티비티로부터 넘어온 데이터를 꺼낸다.
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String mobile = getIntent().getStringExtra("mobile");
        Bitmap bitmap = getIntent().getParcelableExtra("photo");

        bitmap = resizingBitmap(bitmap);
        TextView tname = (TextView)findViewById(R.id.tname);
        TextView temail = (TextView)findViewById(R.id.temail);
        TextView tmobile = (TextView)findViewById(R.id.tmobile);
        ImageView tphoto = (ImageView)findViewById(R.id.tphoto);


        tname.setText(name);
        temail.setText(email);
        tmobile.setText(mobile);
        tphoto.setImageBitmap(bitmap);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        /*
        Button button = (Button) findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
*/

    }


    public Bitmap resizingBitmap (Bitmap oBitmap){
        if (oBitmap == null)
            return null;
        float width = oBitmap.getWidth();
        float height = oBitmap.getHeight();
        float resizing_size = 120;
        Bitmap rBitmap = null;
        if (width > resizing_size) {
            float mWidth = (float) (width / 100);
            float fScale = (float) (resizing_size / mWidth);
            width *= (fScale / 100);
            height *= (fScale / 100);

        } else if (height > resizing_size) {
            float mHeight = (float) (height / 100);
            float fScale = (float) (resizing_size / mHeight);
            width *= (fScale / 100);
            height *= (fScale / 100);
        }

        //Log.d("rBitmap : " + width + ", " + height);
        rBitmap = Bitmap.createScaledBitmap(oBitmap, (int) width, (int) height, true);
        return rBitmap;
    }




}
