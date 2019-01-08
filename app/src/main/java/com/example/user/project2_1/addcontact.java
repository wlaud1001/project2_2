package com.example.user.project2_1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class addcontact extends AppCompatActivity {

    Button add;
    EditText addname;
    EditText addnb;
    EditText addemail;

    String name;
    String email;
    String phonenb;

    private ProgressDialog pDialog;
    private static String url = "http://socrip4.kaist.ac.kr:4380/contacts";
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);

        addname = findViewById(R.id.addname);
        addemail = findViewById(R.id.addemail);
        addnb = findViewById(R.id.addnb);





        add = findViewById(R.id.add);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = addname.getText().toString();
                email = addemail.getText().toString();
                phonenb = addnb.getText().toString();

                new GetContacts().execute();



                Log.i("edit",name);
                Log.i("edit",email);
                Log.i("edit",phonenb);



                onBackPressed();
            }
        });

    }
/*
    public String getname() {
        return name;
    }
    public String getEmail(){
        return email;
    }

    public String getPhonenb(){
        return email;
    }
*/


    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show loading dialog

            pDialog = new ProgressDialog(addcontact.this);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(false);
            pDialog.dismiss();

                pDialog.show();


        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.i("Hi",jsonStr);
            Log.e(TAG, "Response from url: " + jsonStr);

            ////////////////////////////////////받기

            try {

                SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();

                String pid = time.format(calendar.getTime());


                    JSONObject jsonObject = new JSONObject();

                    Log.i("add","!!!!!!!!!!!!!!!");
                    Log.i("add",name);
                    Log.i("add",email);
                    Log.i("add",phonenb);
                    jsonObject.accumulate("pid", pid);
                    jsonObject.accumulate("name", name);
                    jsonObject.accumulate("email", email);
                    jsonObject.accumulate("phonenb", phonenb);
                    jsonObject.accumulate("photo", "Nop");

                    HttpURLConnection con = null;
                    BufferedReader reader = null;

                    try {
                        URL url = new URL("http://socrip4.kaist.ac.kr:4380/contacts");
                        //연결을 함
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");//POST방식으로 보냄
                        con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                        con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                        con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                        con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                        con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                        con.connect();
                        //서버로 보내기위해서 스트림 만듬
                        OutputStream outStream = con.getOutputStream();
                        //버퍼를 생성하고 넣음
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                        writer.write(jsonObject.toString());
                        writer.flush();
                        writer.close();//버퍼를 받아줌
                        //서버로 부터 데이터를 받음

                        InputStream stream = con.getInputStream();

                        reader = new BufferedReader(new InputStreamReader(stream));

                        StringBuffer buffer = new StringBuffer();

                        String line = "";

                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }
                        //return ;//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (con != null) {
                            con.disconnect();
                        }
                        try {
                            if (reader != null) {
                                reader.close();//버퍼를 닫아줌
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Dismiss the dialog
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

        }
    }


}


