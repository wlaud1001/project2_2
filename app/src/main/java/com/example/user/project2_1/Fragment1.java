package com.example.user.project2_1;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    private  Button addcontact;
    private ListView lv;
    private static String url = "http://socrip4.kaist.ac.kr:4380/contacts";

    ArrayList<HashMap<String, String>> contactList;
    ArrayList<HashMap<String, String>> listinphone;

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;


    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        contactList = new ArrayList<>();
        listinphone = new ArrayList<>();

        //승인나면
        if (askForContactPermission(getActivity())) {
            //loadContacts();

            new GetContacts().execute();

        }

        super.onCreate(savedInstanceState);
    }

    //리스트 눌렀을 때 화면전환
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//////////////
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);

        //contactList = new ArrayList<>();
        //listinphone = new ArrayList<>();
        lv = (ListView) view.findViewById(R.id.ListView);

        addcontact = (Button) view.findViewById(R.id.addcont);


        Log.i("length", Integer.toString(contactList.size()));

        ListAdapter adapter = new ExtendedSimpleAdapter(
                getActivity(), contactList,
                R.layout.list_item, new String[]{"name","email","mobile","photo"},
                new int[] {R.id.name,R.id.email, R.id.mobile,R.id.photo});
        lv.setAdapter(adapter);

        Log.i("length2", Integer.toString(contactList.size()));



        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.example.user.project2_1.addcontact.class);

                startActivity(intent);

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Data data = contactList.get(position);
                HashMap<String,String> data = contactList.get(position);
//값 전달
                Intent intent = new Intent(getActivity().getApplicationContext(),itemclickevent.class);

                Bitmap bitmap;

                if(data.get("photo").equals("Nop"))
                {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kakao);
                }
                else {

                    bitmap = StringToBitmap(data.get("photo"));

                }

                bitmap = resizingBitmap(bitmap);
                bitmap = getRoundedBitmap(bitmap, 60);

                intent.putExtra("photo", bitmap);
                intent.putExtra("name", data.get("name"));
                intent.putExtra("email", data.get("email"));
                intent.putExtra("mobile", data.get("mobile"));
                startActivity(intent);
            }
        });

        return view;
    }


    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show loading dialog

            pDialog = new ProgressDialog(getActivity());
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
                loadContacts();

                for(int i=0; i<listinphone.size();i++){

                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

                JSONObject jsonObject = new JSONObject();

                HashMap<String, String> data = listinphone.get(i);

                jsonObject.accumulate("pid", data.get("pid"));

                jsonObject.accumulate("name", data.get("name"));
                jsonObject.accumulate("email", data.get("email"));
                jsonObject.accumulate("phonenb", data.get("mobile"));
                jsonObject.accumulate("photo", data.get("photo"));

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

            }
            } catch (Exception e) {
                e.printStackTrace();
            }


            /////////////////////////////////////////////////
            contactList.clear();
            jsonStr = sh.makeServiceCall(url);
            if(jsonStr != null){
                try{
                    JSONArray contacts = new JSONArray(jsonStr);

                    //looping through all contacts
                    for(int i=0; i<contacts.length(); i++)
                    {
                        JSONObject c = contacts.getJSONObject(i);

                        String pid = c.getString("pid");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String mobile = c.getString("phonenb");
                        String photo = c.getString("photo");
                        HashMap<String, String> contact = new HashMap<>();

                        //adding each child node to hashmap
                        contact.put("name",name);
                        contact.put("email",email);
                        contact.put("mobile",mobile);
                        contact.put("photo",photo);

                        //adding contact to contact list
                        contactList.add(contact);

                    }

                }

                catch (final JSONException e){
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "JSON parsing error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
            else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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


            ListAdapter adapter = new ExtendedSimpleAdapter(
                    getActivity(), contactList,
                    R.layout.list_item, new String[]{"name","email","mobile","photo"},
                    new int[] {R.id.name,R.id.email, R.id.mobile,R.id.photo});
            lv.setAdapter(adapter);

            /*
            //update inf json data to listview
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "email", "mobile"},
                    new int[] {R.id.name, R.id.email, R.id.mobile});

            lv.setAdapter(adapter);
        */
        }
    }



    /**추가*/
    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
*/

    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.refresh:
                // User chose the "Settings" item, show the app settings UI...
                loadContacts();
                new GetContacts().execute();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    //연락처 불러오기
    private void loadContacts() {

        //contactList.clear();
        listinphone.clear();
        contactList.clear();
        //내부저장소에서 불러오기
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                queryContactInfo(Integer.parseInt(id));
            }
        }



/*
        ListAdapter adapter = new ExtendedSimpleAdapter(
                getActivity(), listinphone,
                R.layout.list_item, new String[]{"name","email","mobile","photo"},
                new int[] {R.id.name,R.id.email, R.id.mobile,R.id.photo});
        lv.setAdapter(adapter);
*/


    }


    //내부저장소에서 불러오는 진짜 코드 받아서 contactlist에 넣어둠
    private void queryContactInfo(int rawContactId) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor c = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_ID

                }, ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[] { Integer.toString(rawContactId) }, null);

        String pid = Integer.toString(rawContactId);
        String name = "";
        String number = "Phone Number";
        String email ="Email";
        //int photoId = 555;
        HashMap<String, String> contact = new HashMap<>();
        Bitmap bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.kakao);
        String photo;

        if (c != null) {
            if (c.moveToFirst()) {
                name = c.getString(2);
                number = c.getString(0);
                //int type = c.getInt(1);

                int photoId = c.getInt(3);

               if(photoId != 0)
                    bitmap = queryContactImage(photoId);

                //if(photoId != 0)
                 //   photo = Integer.toString(photoId);


                Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{Integer.toString(rawContactId)}, null);

                while (emails.moveToNext())
                {
                    email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }

                emails.close();


                //showSelectedNumber(type, number, name, bitmap);
            }
            c.close();
        }


        //
        //bitmap = getRoundedBitmap(bitmap, 71);
        //bitmap = convertRoundedBitmap(bitmap);


        photo = BitmapToString(bitmap);
        //photo = getStringFromBitmap(bitmap);
       // Log.i("bitmap",photo);
        contact.put("pid",pid);
        contact.put("name",name);
        contact.put("mobile",number);
        contact.put("email",email);
        contact.put("photo",photo);

        listinphone.add(contact);

    }





    //////////아래로는 비트맵

    public static String BitmapToString (Bitmap bitmap){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }


    public static Bitmap StringToBitmap (String encodedString){
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }




    private Bitmap queryContactImage(int imageDataRow) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor c = contentResolver.query(ContactsContract.Data.CONTENT_URI, new String[] {
                ContactsContract.CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[] {
                Integer.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }






    public static Bitmap getRoundedBitmap(Bitmap bitmap, int cornerRadius) {

        if (bitmap == null) {
            return null;
        }
        if (cornerRadius < 0) {
            cornerRadius = 0;
        }
        // Create plain bitmap
        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawARGB(0,0,0,0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return canvasBitmap;
    }


    public Bitmap resizingBitmap(Bitmap oBitmap) {
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




    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////이 아래로는 permission
    public static final int PERMISSION_REQUEST_CONTACT = 123;

    public boolean askForContactPermission(final Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( context);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions( (Activity) context,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                return false;
            }
            else
            {
                return true;
            }
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getActivity(), "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }







}