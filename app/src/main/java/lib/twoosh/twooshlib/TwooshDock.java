package lib.twoosh.twooshlib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import lib.twoosh.twooshlib.adapters.RoomListAdapter;
import lib.twoosh.twooshlib.models.RoomListItem;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.services.SocketService;

//import io.socket.client.IO.Options;
//import io.socket.client.IO;
//import io.socket.client.*;
//import com.github.nkzawa.*;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TwooshDock extends AppCompatActivity {


    Context c;
    Socket socket = null;
    JSONObject userdetails ;
    RoomListAdapter adapter ;
    String userid = "";
    String twoosher = "";
    public static Firebase fbaserootref;
    public static Firebase wordlistref;
   // public Socket socket=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twoosh_dock);


        // initialise activity
        initActivity();

        // recognize user
        attachListeners();


        // renderAdpapter
        renderAdapter();


    }


    public void initActivity(){

        this.getSupportActionBar().setTitle("Twoosh Chat");
        this.getSupportActionBar().setSubtitle("Twoosh - You are connected.");

        recognizeUser();


        registerSocket();




        //getApplicationContext().getSharedPreferences("info.twoosh.TwooshUserPref", 0).edit().clear().commit();

    }

    public void renderAdapter(){

        adapter = new RoomListAdapter();
        ListView list=(ListView)findViewById(R.id.roomList);
        list.setAdapter(adapter);

    }

    public void registerSocket(){

        SocketService ss = new SocketService();
        ss.connectSocket();
        Socket socketobj = ss.getSocketInstance();


    }
    public void recognizeUser(){
    // recognize user

        // sanitize user details
        boolean sanity = sanitizeUser();

        if(sanity){

            // check if new user
            boolean newuser = IfNewUser();
            if(newuser){

                //Toast.makeText(this, "New User...Registering", Toast.LENGTH_SHORT).show();
                registerUser();
            }
            else{
                //Toast.makeText(this, "Found old User...Entering Dock", Toast.LENGTH_SHORT).show();
                setUserStatics();
                renderDock();

            }

        }
        //get user details
        //JSONObject userdetails = getUserDetails();


       // String application_name = getApplication().getClass().getCanonicalName();

        //Toast.makeText(this, "Making get request to twoosh.info", Toast.LENGTH_SHORT).show();
        //registerUser();

    }

    public boolean sanitizeUser(){




        // check if valid details present
//        String name = User.name;
//        String mobile = User.mobile;
//        String email = User.email;
//        String gender = User.gender;
//        String dob = User.dob;
//        String location = User.location;
//        String city = User.city;
//        String tags = User.tags;
//        String corp_referrer = User.corp_referrer;
//        String corp_auth_token = User.corp_auth_token;


       String name = "Satyam";
       String mobile = "9945325885";
       String email = "satyam.nitt@gmail.com";
       String gender = "M";
       String dob = "25091990";
       String location = "bangalore";
       String city = "bangalore";
       String tags = "neurology";
       String corp_referrer = "1";
       String corp_auth_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb3JwaWQiOiI1N2RjZjE5NTI1ZWNlNjYxMDVkNmFiMTUiLCJlbWFpbCI6InNhamlkLmlzbGFtOTBAZ21haWwuY29tIiwiZXhwIjoxNDg5NjUwNjk2LCJtb2JpbGUiOiI3MzkwODk0MTExIn0.QvWtS1G4o5PSW1gKJVXcDYzA-gyPPT7g4ISRTRsVpQk";

       User.name = name;
       User.mobile = mobile;
       User.email = email;
       User.gender = gender;
       User.dob = dob;
       User.location = location;
       User.tags = tags;
       User.corp_referrer = corp_referrer;
       User.corp_auth_token = corp_auth_token;
       User.city = city;



        if(User.name == "" || User.mobile=="" || User.email == "" || User.gender == "" || User.dob == "" || User.location == "" || User.corp_referrer == "" || User.corp_auth_token == "" ){

            //Toast.makeText(this, "Invalid user details...", Toast.LENGTH_SHORT).show();
            return false;

        }

        try {
            userdetails = new JSONObject();
            userdetails.put("name",name);
            userdetails.put("mobile",mobile);
            userdetails.put("email",email);
            userdetails.put("gender",gender);
            userdetails.put("dob",dob);
            userdetails.put("location", location);
            userdetails.put("city", city);
            userdetails.put("tags",tags);
            userdetails.put("corp_referrer",corp_referrer);
            userdetails.put("corp_auth_token", corp_auth_token);



        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;



    }


    public boolean IfNewUser()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("info.twoosh.TwooshUserPref", Context.MODE_PRIVATE);
        //
        String twoosher = prefs.getString("twoosher", null);
        if(twoosher==null){
            //Toast.makeText(this, "twoosher null...", Toast.LENGTH_SHORT).show();
            return true;
        } else
        {
            //Toast.makeText(this, "twoosher recognized : "+twoosher, Toast.LENGTH_SHORT).show();
            this.twoosher = twoosher;
            return false;

        }


    }

    public void setUserStatics(){
        try {


            JSONObject user_data = new JSONObject(twoosher);
            User.userid = (String)user_data.get("id");
            User.access_token = (String)user_data.get("access_token");
            User.appname = (String)user_data.get("corp_appname");
            User.corpid = (String)user_data.get("corp_id");

//            String username = (String)user_data.get("name");
//            User.userid = userid;
//            User.name =  username;
//            User.corp_referrer = (String)user_data.get("corp_ref");
//            User.corp_auth_token = (String)user_data.get("corp_auth");

           // Toast.makeText(this, "userid - "+User.userid, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "access token - "+User.access_token, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "appname - "+User.appname, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setFirebase();

    }

    public void setFirebase(){


//        Firebase wordlistold = null;
        Firebase.setAndroidContext(getApplicationContext());
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        fbaserootref = new Firebase("https://twooshapp-763a4.firebaseio.com");

        wordlistref = fbaserootref.child(User.corpid).child("wordlist");
        wordlistref.keepSynced(true);


        wordlistref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
                RoomListItem room_local = snapshot.getValue(RoomListItem.class);
                Toast.makeText(getApplicationContext(), "Total local objects - " + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                adapter.add(room_local);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
//
//        wordlistref.addListenerForSingleValueEvent(new ValueEventListener() {
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                System.out.println("We're done loading the initial " + dataSnapshot.getChildrenCount() + " items");
//                adapter.notifyDataSetChanged();
//                Toast.makeText(getApplicationContext(), "Add data change called ", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onCancelled(FirebaseError firebaseError) {
//            }
//        });

        wordlistref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        //adapter.notifyDataSetInvalidated();
        // Attach an listener to read the data at our posts reference
//        wordlistref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    RoomListItem roomoff = postSnapshot.getValue(RoomListItem.class);
//                    //System.out.println(post.getAuthor() + " - " + post.getTitle());
//                   // Toast.makeText(getApplicationContext(), "Room - "+roomoff.tagname, Toast.LENGTH_SHORT).show();
//                    adapter.add(roomoff);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        });

//        fbaserootref.addAuthStateListener(new Firebase.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(AuthData authData) {
//                if (authData != null) {
//                    // user is logged in
//                } else {
//                    // user is not logged in
//                }
//            }
//        });
//


        // Create a handler to handle the result of the authentication
//        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
//            @Override
//            public void onAuthenticated(AuthData authData) {
//                // Authenticated successfully with payload authData
//                Toast.makeText(TwooshDock.this, "Firebase auth success", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onAuthenticationError(FirebaseError firebaseError) {
//                // Authenticated failed with error firebaseError
//                Toast.makeText(TwooshDock.this, "Firebase auth error", Toast.LENGTH_SHORT).show();
//            }
//        };
        //fbaserootref.authAnonymously(authResultHandler);

    }

    public void registerUser(){

            //HttpClient httpclient = new HttpClient();
        //Toast.makeText(TwooshDock.this,"Registering User...",Toast.LENGTH_SHORT).show();
        HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
            @Override
            public void onResponse(String response) {
                // Do Something after the request callback comes has finished

               // Toast.makeText(TwooshDock.this,"POST RESPONSE : "+response,Toast.LENGTH_SHORT).show();

                // parse register user post response
                try {

                    JSONObject register_resp = new JSONObject(response);
                    JSONObject response_data = register_resp.getJSONObject("response");
                    int inserted = response_data.getInt("inserted");
                    int matched = response_data.getInt("matched");

                    if( register_resp.get("status").equals("Success")) {

                        // store user details to Shared Prefs
                       // Toast.makeText(TwooshDock.this, "Inside api success", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                "info.twoosh.TwooshUserPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String twoosh_user_data = response_data.toString();
                        editor.putString("twoosher",twoosh_user_data);
                        editor.commit();
                        twoosher = twoosh_user_data;
                        setUserStatics();
                        renderDock();


                    }else{

                       // Toast.makeText(TwooshDock.this, "API resp status failure ", Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        String host = getResources().getString(R.string.local_host);
        String proauthapi = getResources().getString(R.string.proauthapi);
        String proauth_url = host+proauthapi;
        JSONObject proauthdata = new JSONObject();
        //{"name":"Satyam","mobile":"9945325886","email":"satyam.nitt@gmail.com","gender":"","dob":"","location":"","tags":"dermatologist,orthopaedics","corp_referrer":"1","corp_auth_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb3JwaWQiOiI1N2RjZjE5NTI1ZWNlNjYxMDVkNmFiMTUiLCJlbWFpbCI6InNhamlkLmlzbGFtOTBAZ21haWwuY29tIiwiZXhwIjoxNDg5NjUwNjk2LCJtb2JpbGUiOiI3MzkwODk0MTExIn0.QvWtS1G4o5PSW1gKJVXcDYzA-gyPPT7g4ISRTRsVpQk","city":"Bangalore"}

        try{


            proauthdata.put("name",User.name);
            proauthdata.put("mobile",User.mobile);
            proauthdata.put("email",User.email);
            proauthdata.put("gender",User.gender);
            proauthdata.put("dob",User.dob);
            proauthdata.put("location",User.location);
            proauthdata.put("tags",User.tags);
            proauthdata.put("corp_referrer",User.corp_referrer);
            proauthdata.put("corp_auth_token", User.corp_auth_token);
            proauthdata.put("city",User.city);


        }
        catch (Exception e){}
        httpclient.Post(this, proauth_url, proauthdata);

//        task.execute();

    }


    public void renderDock(){

        this.getSupportActionBar().setTitle(User.appname+" Chat");


        getRoomList();

    }

    public void getRoomList(){

        //Toast.makeText(TwooshDock.this,"Getting rooms list..."+User.name,Toast.LENGTH_SHORT).show();
        HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

            @Override
            public void onResponse(String response) {

                try {

                    //Toast.makeText(TwooshDock.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject roomlist_response = new JSONObject(response);
                    JSONArray roomlist = roomlist_response.getJSONArray("response");
                    addtoAdapter(roomlist);


                } catch (JSONException e) {
                    //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        String host = getResources().getString(R.string.local_host);
        String getroomsapi = getResources().getString(R.string.getroomsapi);
        String getroomsurl = host+getroomsapi;
        String urlparams = "{\"corpid\":\""+User.corpid+"\"}";
        httpClient.Get(this, getroomsurl, urlparams);

    }

    // add room objects to adapter and local firebase
    public void addtoAdapter(JSONArray roomlist){

        wordlistref = fbaserootref.child(User.corpid).child("wordlist");
        JSONObject room = null;
        RoomListItem roomitem;
        for(int i=0;i<roomlist.length();i++){
            try {

                room = new JSONObject(roomlist.get(i).toString());
                String roomname = room.getString("mapname");
                String roomstrength = room.getString("users_count");
                String roomposts = "0";

                roomitem = new RoomListItem(roomname,roomname,roomstrength, roomposts);
                //wordlistref.push().setValue(roomitem);
                wordlistref.child(roomname).setValue(roomitem);
                //adapter.add(roomitem);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
       // adapter.notifyDataSetChanged();
    }

    public void attachListeners(){



        ListView list=(ListView) findViewById(R.id.roomList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RoomListItem m = (RoomListItem) parent.getAdapter().getItem(position);
                //TagListItem m = (TagListItem)view.getTag(R.id.tagList);
                //Toast.makeText(TwooshDock.this, m.tagdesc, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TwooshDock.this, RoomDock.class);
                // intent.putExtra("tagtitle",m.tagname);
                intent.putExtra("tagid", m.hashid);
                intent.putExtra("tagname", m.tagname);
                User.current_room = m.hashid;
                startActivity(intent);
            }


        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twoosh_dock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
