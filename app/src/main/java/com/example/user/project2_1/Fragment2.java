package com.example.user.project2_1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.mbms.StreamingServiceInfo;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment {

    private ArrayList<String> thumbsDataList;
    private ArrayList<String> thumbsIDList;


    private static String url = "http://socrip4.kaist.ac.kr:4380/users/photo";

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;



    List<Bitmap> lowList = new ArrayList<>();
    // 원본용(해당 파일의 경로 리스트)
    ArrayList<String> highList = new ArrayList<>();
    ArrayList<String> btmstring = new ArrayList<>();
    ArrayList<HashMap<String,String>> btmlist = new ArrayList<>();
    String flag = "";
    View view;
    ImageView imageView;
    GridView gridView;


    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {


        super.onAttach(context);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters

    /*
    public static Fragment2 newInstance(String param1, String param2) {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

*/



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //getAlbum();
        //getphotos();


    }

/*
    public void getphotos(){

        thumbsDataList = new ArrayList<String>();
        thumbsIDList = new ArrayList<String>();
        btmlist = new ArrayList<String>();

        getThumbInfo(thumbsIDList, thumbsDataList);

        for(int i=0; i<thumbsDataList.size(); i++) {
            BitmapFactory.Options bo = new BitmapFactory.Options();
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(i), bo);

            //bitmap = bmp;
            btmlist.add(BitmapToString((bmp)));
            Log.i("photo",btmlist.get(i));
        }


    }
    */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_fragment2, container, false);

        Button button = view.findViewById(R.id.load);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbum();

            }
        });


        gridView = view.findViewById(R.id.ImgGridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity().getApplicationContext(), showimage.class);

                Bitmap bitmap;
                bitmap = StringToBitmap(btmlist.get(position).get("btm"));
                bitmap = resizingBitmap(bitmap);
                String photoid;
                photoid = btmlist.get(position).get("photoid");
                intent.putExtra("bitmap",bitmap);
                intent.putExtra("photoid", photoid);

                startActivity(intent);

            }
        });



        Log.i("lowlist",Integer.toString(lowList.size()));

        new GetPhoto().execute();

        return view;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
            boolean a = checkPermissionREAD_EXTERNAL_STORAGE(getActivity());
            if(checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {
                //loadImage(view,getActivity());
                Log.i("fragment2","fragment2");
            }

            //new GetPhoto().execute();

            /*
            MyAdapter adapter = new MyAdapter(
                    getContext(),
                    R.layout.imageview,
                    btmlist
            );

            GridView gridView = view.findViewById(R.id.ImgGridView);
            gridView.setAdapter(adapter);
*/
            /*
            MyAdapter adapter = new MyAdapter(
                    getContext(),
                    R.layout.imageview,
                    btmlist
            );

            GridView gridView = view.findViewById(R.id.ImgGridView);
            gridView.setAdapter(adapter);
            */

            Log.i("getphoto","getphoto");


        }
        else
        {
            //preload 될때(전페이지에 있을때)
        }
    }



    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.refresh:

                new GetPhoto().execute();

                Log.i("refresh","refresh");
                //onCreateView(inf,cont,saved);
                //디버깅용 삭제

                // User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


/**
    /*
    private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas){
        String[] proj = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};

        Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, null);

        if (imageCursor != null && imageCursor.moveToFirst()){
            String title;
            String thumbsID;
            String thumbsImageID;
            String thumbsData;
            String data;
            String imgSize;

            int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
            int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
            int num = 0;
            do {
                thumbsID = imageCursor.getString(thumbsIDCol);
                thumbsData = imageCursor.getString(thumbsDataCol);
                thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                imgSize = imageCursor.getString(thumbsSizeCol);
                num++;
                if (thumbsImageID != null){
                    thumbsIDs.add(thumbsID);
                    thumbsDatas.add(thumbsData);
                }
            }while (imageCursor.moveToNext());
        }
        imageCursor.close();
        return;
    }

*/


    final int REQUEST_TAKE_ALBUM = 1;

    private void getAlbum() {
        // 앨범 호출
        boolean isAlbum = checkPermissionREAD_EXTERNAL_STORAGE(getActivity());
        if (isAlbum) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(Intent.createChooser(intent, "다중 선택은 '포토'를 선택하세요."), REQUEST_TAKE_ALBUM);
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }
            } else {
                Log.e("kitkat under", "..");
            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "CALL");
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList imageList = new ArrayList<>();
        switch (requestCode) {
            case REQUEST_TAKE_ALBUM:
                Log.i("result", String.valueOf(resultCode));
                if (resultCode == Activity.RESULT_OK) {


                    // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                    if (data.getClipData() == null) {
                        Log.i("1. single choice", String.valueOf(data.getData()));
                        imageList.add(String.valueOf(data.getData()));
                    } else {

                        ClipData clipData = data.getClipData();
                        Log.i("clipdata", String.valueOf(clipData.getItemCount()));
                        if (clipData.getItemCount() > 30){
                            Toast.makeText(getActivity(), "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 멀티 선택에서 하나만 선택했을 경우
                        else if (clipData.getItemCount() == 1) {
                            String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
                            Log.i("2. clipdata choice", String.valueOf(clipData.getItemAt(0).getUri()));
                            Log.i("2. single choice", clipData.getItemAt(0).getUri().getPath());
                            imageList.add(dataStr);

                        } else if (clipData.getItemCount() > 1 && clipData.getItemCount() < 30) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Log.i("3. single choice", String.valueOf(clipData.getItemAt(i).getUri()));
                                imageList.add(String.valueOf(clipData.getItemAt(i).getUri()));
                            }
                        }
                    }

                }
                else {
                    Toast.makeText(getActivity(), "사진 선택을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

                default:
                    break;

        }

        String str;

        if(imageList.size() != 0) {
            str = (String) imageList.get(0);

            flag = str.substring(0, 7);

            highList.clear();
            // highList에는 일단 원본을 넣어둠
            for (int i = 0; i < imageList.size(); i++) {

                if (flag.equals("content")) {
                    // content:// -> /storage... (포토)
                    String path = getRealPathFromURI(Uri.parse((String) imageList.get(i)));
                    highList.add(path);
                } else {
                    // 갤러리 : 파일 절대 경로 리턴함(변환은 필요없고, file://를 빼줘야 업로드시 new File에서 이용)
                    String path = (String) imageList.get(i);
                    path = path.replace("file://", "");
                    highList.add(path);
                }
            }


            Log.i("Highlist", Integer.toString(highList.size()));
        }
        lowList.clear();
        btmstring.clear();

        for(int i=0; i<highList.size(); i++) {

            Uri uri = Uri.parse(highList.get(i));
            final String path = uri.getPath();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            lowList.add(bitmap);
            btmstring.add(BitmapToString(bitmap));
        }

        //imageView.setImageBitmap(lowList.get(0));

        new GetPhoto().execute();

        Log.i("btmstring",Integer.toString(btmstring.size()));

    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    class MyAdapter extends BaseAdapter {
        Context context;
        int layout;
        ArrayList<HashMap<String,String>> list;
        LayoutInflater inf;


        public MyAdapter(Context context, int layout, ArrayList<HashMap<String,String>> list) {
            this.context = context;
            this.layout = layout;
            this.list = list;
            inf = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }



        @Override
        public int getCount() {
            return btmlist.size();
        }

        @Override
        public Object getItem(int position) {
            return btmlist.get(position).get("btm");
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null)
                convertView = inf.inflate(layout, null);
            ImageView iv = (ImageView)convertView.findViewById(R.id.imageView);

            iv.setImageBitmap(StringToBitmap(btmlist.get(position).get("btm")));


            return convertView;
        }
    }


    private class GetPhoto extends AsyncTask<Void, Void, Void> {

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
            Log.e(TAG, "Response from url: " + jsonStr);

            ////////////////////////////////////받기

            try {
                //loadContacts();


                for(int i=0; i<btmstring.size();i++){

                    //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

                    JSONObject jsonObject = new JSONObject();



                    SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
                    Calendar calendar = Calendar.getInstance();

                    String photoid = time.format(calendar.getTime());
                    String ms = Integer.toString(calendar.get(Calendar.MILLISECOND));

                    photoid = photoid.concat(ms);
                    jsonObject.accumulate("photoid", photoid);
                    jsonObject.accumulate("btm", btmstring.get(i));
                    //jsonObject.accumulate("btm", "hhh");

                    HttpURLConnection con = null;
                    BufferedReader reader = null;

                    try {
                        URL url = new URL("http://socrip4.kaist.ac.kr:4380/users/photo");
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
                        Log.i("error","error1");
                    } catch (IOException e) {

                        e.printStackTrace();
                        Log.i("error","error2");
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

            btmlist.clear();
            jsonStr = sh.makeServiceCall(url);
            if(jsonStr != null){
                try{
                    JSONArray photos = new JSONArray(jsonStr);

                    //looping through all contacts
                    for(int i=0; i<photos.length(); i++)
                    {
                        JSONObject c = photos.getJSONObject(i);

                        //String photoid = c.getString("pid");
                        String photoid = c.getString("photoid");
                        String encoded = c.getString("btm");

                        HashMap<String,String> photo = new HashMap<>();

                        photo.put("photoid", photoid);
                        photo.put("btm", encoded);
                        btmlist.add(photo);
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

            for(int i = 1; i < btmlist.size(); i++) {
                long standard = Long.valueOf(btmlist.get(i).get("photoid")).longValue();
                          // 기준

                HashMap<String,String> std = btmlist.get(i);

                int aux = i - 1;   // 비교할 대상
                while (aux >= 0 && standard > Long.valueOf(btmlist.get(aux).get("photoid")).longValue())
                {

                    btmlist.set(aux+1, btmlist.get(aux));

                    aux--;
                }
                btmlist.set(aux + 1,  std);  // 기준값 저장
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


            MyAdapter adapter = new MyAdapter(
                    getContext(),
                    R.layout.imageview,
                    btmlist
            );

            GridView gridView = view.findViewById(R.id.ImgGridView);
            gridView.setAdapter(adapter);
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



        /*Permission*/
        @Override
        public void onRequestPermissionsResult ( int requestCode,
        String[] permissions, int[] grantResults){
            Log.i("fragment2", "onRequestPermissionsResult");
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // do your stuff
                    } else {
                        Toast.makeText(getActivity(), "GET_ACCOUNTS Denied",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions,
                            grantResults);
            }
        }

        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        public boolean checkPermissionREAD_EXTERNAL_STORAGE (
        final Context context){
            Log.i("fragment2", "checkPermissionREAD_EXTERNAL_STORAGE");
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            (Activity) context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialog("External storage", context,
                                Manifest.permission.READ_EXTERNAL_STORAGE);

                    } else {
                        ActivityCompat
                                .requestPermissions(
                                        (Activity) context,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }

            } else {
                return true;
            }
        }

        public void showDialog ( final String msg, final Context context,
        final String permission) {
            Log.i("fragment2", "showDialog");
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage(msg + " permission is necessary");
            alertBuilder.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{permission},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }

}





