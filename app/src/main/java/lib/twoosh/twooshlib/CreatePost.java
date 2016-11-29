package lib.twoosh.twooshlib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.ServerValue;
import com.firebase.client.Transaction;
import com.google.firebase.database.DatabaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import lib.twoosh.twooshlib.models.Fref;
import lib.twoosh.twooshlib.models.PostListItem;
import lib.twoosh.twooshlib.models.Prefs;
import lib.twoosh.twooshlib.models.RoomListItem;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.NotifObj;

public class CreatePost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initActivity();
        attachListeners();
    }

    public void initActivity(){

        touchUser();

    }

    public void touchUser(){

        Prefs p = new Prefs(getApplicationContext());
        if(p.prefExists()){

            p.setUserStatics();
            initFirebase();
        }

    }

    public void initFirebase(){


    }

    public void performTwoosh(String twooshraw){



        JSONObject jObj = new JSONObject();
        Date utildate = new Date();
        String twoosh_ts =  Long.toString(utildate.getTime()/1000);
        String twooshid = "T"+twoosh_ts;
        PostListItem newpost = new PostListItem(twooshraw,"T"+twoosh_ts,User.name, User.userid, "0","0","0",twoosh_ts);
        newpost.ts = ServerValue.TIMESTAMP;
        Fref.fref_base.child("posts").child(User.current_room).push().setValue(newpost);


        // subscribe this post and room
        Prefs.subscribeRoom(User.current_room);
        Prefs.subscribePost(twooshid);
        Prefs.saveUserStatics();


        // set notification channel
        NotifObj notifobj = new NotifObj();
        notifobj.notif_type = "NP";
        notifobj.room = User.current_room;
        notifobj.user_name = User.name;
        notifobj.head = "#"+User.current_room;
        notifobj.body = twooshraw;
        notifobj.timestring = ServerValue.TIMESTAMP;
        notifobj.twoosh_id = twooshid;
        Fref.fref_notifs.push().setValue(notifobj);


        // update post count for this room

        Fref.fref_base.child("rooms").child(User.appname).child(User.current_room+"/hash_posts").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                System.out.println("Transaction completed");
            }
        });


    }

    private String getHashTags(String text){

        return "";
    }
    private boolean sanitizeText(String twooshraw){
        if(twooshraw.equals("")){
            return false;
        }
        return true;
    }

    public String publishTwooshRemote(JSONObject twooshobj){

        // POST - publish twoosh remmote
        HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject publishtwoosh_resp = new JSONObject(response);
                    JSONObject response_data = publishtwoosh_resp.getJSONObject("response");


                    int inserted = response_data.getInt("inserted");
                    String id = response_data.getString("id");
                    int matched = response_data.getInt("matched");

                    if((publishtwoosh_resp.get("status").equals("Success")) && ((inserted == 1) || (matched == 1))){


                        renderDock();


                    }else{

                        Toast.makeText(CreatePost.this, "In else part", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    Toast.makeText(CreatePost.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        String host = getResources().getString(R.string.local_host);
        String createpostapi = getResources().getString(R.string.createpostapi);

        String createposturl = host + createpostapi;
        try{
            twooshobj.put("q",twooshobj.getString("twoosh_text"));

            twooshobj.put("token",User.access_token);
        }
        catch (Exception e){}


        httpclient.Post(this, createposturl, twooshobj);
        return "1";

    }


    public void renderDock(){

            Intent i = new Intent(CreatePost.this, RoomDock.class);
            startActivity(i);
    }

    public void attachListeners(){

       Button twoosh_btn = (Button)findViewById(R.id.twoosh_btn);
       final EditText twoosh = (EditText)findViewById(R.id.twoosh_edittext_main);
       twoosh_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {



               String twooshraw = twoosh.getText().toString().trim();
              // Toast.makeText(CreatePost.this,twooshraw,Toast.LENGTH_SHORT).show();


               boolean sanitize_twooshraw = sanitizeText(twooshraw);
               if(sanitize_twooshraw){

                    if(twooshraw.length()>140){
                        Snackbar.make(v, "Max 140 characters limit crossed...", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{

                        if(User.f_access_token.equals("")){
                            User.pending_twoosh = twooshraw;
                            Intent signup = new Intent(CreatePost.this, Signup.class);
                            startActivity(signup);
                        }else{
                            performTwoosh(twooshraw);
                            twoosh.setText("");
                            finish();
                            finishActivity(1);
//                            Intent afterpost = new Intent(CreatePost.this, RoomDock.class);
//                            startActivity(afterpost);
                        }

                    }


               }else{

                  // Toast.makeText(CreatePost.this,"Please make a valid input...",Toast.LENGTH_SHORT).show();
                   Snackbar.make(v, "Please enter some text...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
               }

           }
       });
    }




}
