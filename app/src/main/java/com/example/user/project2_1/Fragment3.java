package com.example.user.project2_1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Random;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment3 extends Fragment implements RoomListener{
    //ScaleDrone
    private String channelID = "qGkEdaTpJSNcnDq2";
    private String roomName = "observable-room";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private ImageButton sendButton;

    //MemberData data;
    private String UserName;

    boolean isLoggedIn = false;
    private boolean isKeyboardShow = false;

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_fragment3, container, false);

        final InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        editText = (EditText) layout.findViewById(R.id.chat_editText);
        sendButton = (ImageButton) layout.findViewById(R.id.chat_sendButton);

        messageAdapter = new MessageAdapter(getActivity());
        messagesView = (ListView) layout.findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        final MemberData data = new MemberData(getRandomName(), getRandomColor());

        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                scaledrone.subscribe(roomName, Fragment3.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });

        //editText를 누르면 키보드가 사라지고, 누르면 키보드가 올라오는 이벤트
        //final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Auto-generated method stub
                if(isKeyboardShow){
                    mgr.showSoftInput(editText, 0);
                    isKeyboardShow = false;
                }

                else {
                    mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    isKeyboardShow = true;
                }
            }
        });

        //sendButton을 누르면 메세지가 전송됨.
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v, data);
            }
        });

        return layout;
    }

    public void sendMessage(View v, MemberData data) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish(roomName, message);

            switch(message.toLowerCase()){
                case "@@help":
                    scaledrone.publish(roomName, "< 명령어 목록 >\n1. @@game\n2. @@maker\n3. @@whoami\n4. @@go:웹사이트 주소");

                    break;

                case "@@game":
                    scaledrone.publish(roomName, "이 방에서 게임하지 마세요!!");
                    break;

                case "@@maker":
                    scaledrone.publish(roomName, "김지명, 이오형");
                    break;

                case "@@whoami":
                    scaledrone.publish(roomName, data.getName());
                    break;

                default:
                    break;
            }
            //@@go:뒤에 웹사이트주소를 입력하면 크롬으로 넘어가
            if(message.contains("@@go:")){
                String website = message.substring(message.lastIndexOf(":")+1);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + website));
                intent.setPackage("com.android.chrome");

                startActivity(intent);
            }

            editText.getText().clear();
        }
    }

    @Override
    public void onOpen(Room room) {
        Log.e("qwer1234", "Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        Log.e("qwer1234",  ex.toString());
    }

    @Override
    public void onMessage(Room room, final JsonNode json, final Member member){
        final ObjectMapper mapper = new ObjectMapper();
        try{
            final MemberData data = mapper.treeToValue(member.getClientData(), MemberData.class);

            boolean belongstoCurrentUser = member.getId().equals(scaledrone.getClientID());

            final Message message = new Message(json.asText(), data, belongstoCurrentUser);

            getActivity().runOnUiThread(new Runnable(){
               @Override
               public void run(){
                   messageAdapter.add(message);
                   messagesView.setSelection(messagesView.getCount() - 1);
               }
            });
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }
    }

    //유저이름을 무작위로 설정
    private String getRandomName() {
        //String[] adjs = {"autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless"};
        //String[] nouns = {"waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star"};

        String[] adjs = {"User"};
        String[] nouns = {"1", "2", "3", "4", "5", "6"};

        return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        " " +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }

    //유저의 구분 색상을 무작위로 설정
    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }
}

//Public Class MemberData
class MemberData{
    private String name;
    private String color;

    public MemberData(String name, String color){
        this.name = name;
        this.color = color;
    }

    public MemberData(){

    }

    public String getName(){return name;}

    public String getColor(){return color;}

    public void ChangeName(String Changename){
        this.name = Changename;
        return;
    }

    @Override
    public String toString(){
        return "MemberData{" + "name = '" + name + "\'" + ", color = '" + color + "\'" + "}";
    }

}