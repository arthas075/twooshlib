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

import com.firebase.client.ServerValue;

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
//        if(User.newuser && User.access_token.equals("")){

            // show name and mobile inputs
           // showNameAndEmailInputs();
       // }
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
    public void showNameAndEmailInputs(){

//        LinearLayout entername = (LinearLayout)findViewById(R.id.twoosh_enter_name);
//        LinearLayout entermobile = (LinearLayout)findViewById(R.id.twoosh_enter_mobile);
//        entername.setVisibility(View.VISIBLE);
//        entermobile.setVisibility(View.VISIBLE);
    }
    public void performTwoosh(String twooshraw){



        JSONObject jObj = new JSONObject();
        Date utildate = new Date();
        String twoosh_ts =  Long.toString(utildate.getTime()/1000);
        String twooshid = "T"+twoosh_ts;
        PostListItem newpost = new PostListItem(twooshraw,"T"+twoosh_ts,User.name, User.userid, "1","0","0",twoosh_ts);
        newpost.timestring = ServerValue.TIMESTAMP;
        Fref.fref_base.child(User.current_room).child(twooshid).setValue(newpost);

        // set notification channel
        NotifObj notifobj = new NotifObj();
        notifobj.notif_type = "NP";
        notifobj.room = User.current_room;
        notifobj.user_name = User.name;
        notifobj.head = "#"+User.current_room;
        notifobj.body = twooshraw;
        notifobj.timestring = ServerValue.TIMESTAMP;
        Fref.fref_notifs.push().setValue(notifobj);

//        String hashtags = getHashTags(twooshraw);
//        try {
//
//            jObj.put("twoosh_text", twooshraw);
//
//
//            jObj.put("twoosh_ts", twoosh_ts);
//            //jObj.put("userid",Integer.toString(thisuser.userid));
//            jObj.put("user_id", User.userid);
//            jObj.put("room", User.current_room);
//            jObj.put("user_name",User.name);
//            jObj.put("hash_tags", "");
////            jObj.put("corp_referrer", User.corpid);
////            jObj.put("corp_auth", User.corp_auth_token);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        //wordlistref.push().setValue(roomitem);
        //wordlistref.child(roomname).setValue(roomitem);

//        Intent i = new Intent(CreatePost.this, RoomDock.class);
//        startActivity(i);
       // String resp = publishTwooshRemote(jObj);
//        if (resp.equals("1")){



//        }
//        serversocket.emit("twooosher",jObj.toString());
//        persistTwooshRemote(jObj);
//        persistTwooshLocal(jObj);
//        goToDock();


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

//                    JSONObject syncdata = userresp.getJSONObject("data");
//                    JSONArray updatedata = syncdata.getJSONArray("update");
//                    JSONArray insertdata = syncdata.getJSONArray("insert");
//                    if(userresp.get("status").equals("success"))
//                    {
//
//                        syncAdapter(insertdata,updatedata);
//                        syncLocalHash(insertdata,updatedata);
//                        //insertSyncLocalTags(userresp.getJSONArray("data"),localId);
//
//                    }


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
