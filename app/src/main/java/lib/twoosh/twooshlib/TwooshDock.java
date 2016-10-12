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
import lib.twoosh.twooshlib.interfaces.Callbacker;
import lib.twoosh.twooshlib.models.Fref;
import lib.twoosh.twooshlib.models.PostListItem;
import lib.twoosh.twooshlib.models.Prefs;
import lib.twoosh.twooshlib.models.RoomListItem;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.notifs.Notifs;
import lib.twoosh.twooshlib.services.SocketService;
import lib.twoosh.twooshlib.services.FService;

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




public class TwooshDock extends AppCompatActivity implements Callbacker{




    Context c;
    Socket socket = null;
    JSONObject userdetails ;
    RoomListAdapter adapter ;
    String twoosher = "";
    public static Firebase.AuthResultHandler authResultHandler = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twoosh_dock);





        // initialise activity
        initActivity();






    }




    public void initActivity(){



        // touch user
        touchUser();

        // touch access
        touchAccess();

        // update data
        updateData();





    }




    public void touchUser(){

        // check regular user or corp user

        Prefs prefs = new Prefs(getApplicationContext());
        if(prefs.prefExists()){

            prefs.setUserStatics();
            //renderDock();
            initApp();
            initFirebase();
            if(!FService.isRunning){


                FService.authcallback = this;
                Intent fservice = new Intent(getApplicationContext(), FService.class);
                fservice.putExtra("payload","1");
                startService(fservice);
            }

        }else{

            showLoginScreen();
        }
//        if(ifPrefsExists()){
//
//            User.newuser = false;
//            try {
//                userdetails = new JSONObject(this.twoosher);
//                try{
//                    String otp_verified = userdetails.getString("otp_verified");
//                    String access_token = userdetails.getString("access_token");
//                    User.access_token = access_token;
//
//                    if(otp_verified.equals("1") && (access_token.equals(""))){
//
//                        getAccessToken();
//                    }else{
//
//                        setUserStatics();
//                        Intent i = new Intent(TwooshDock.this, RoomDock.class);
//                        startActivity(i);
//                        renderDock();
//                    }
//                }
//                catch (Exception e){}
//
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }else{
//
//            User.newuser = true;
//            if(User.corp_token != ""){
//
//                 if(User.name!="" && User.mobile!="" && User.tags!=""){
//
//                        String[] taglist = User.tags.split(",");
//                        JSONObject params = new JSONObject();
//                        try{
//                            params.put("corp_token",User.corp_token);
//                            params.put("name",User.corp_token);
//                            params.put("mobile",User.corp_token);
//                            params.put("taglist", taglist);
//
//                        }
//                        catch (Exception e){
//
//                        }
//                       registerUser(params);
//                 }
//            }else{
//
//                showLoginScreen();
//            }
//        }

    }


    public void initApp(){

        // put this function in launcher activity

        System.out.println("Firebase auth success callback");
        // recognize user
        attachListeners();

        // renderAdpapter
        renderAdapter();

    }



    @Override
    public void callback(String data){

        Toast.makeText(TwooshDock.this, "Init firebase listeners now", Toast.LENGTH_SHORT).show();


    }
    public void showLoginScreen(){

        Intent i = new Intent(this, Signup.class);
        startActivity(i);

    }


    public void initFirebase(){

       // FService.registerRoomListeners();
        Firebase.setAndroidContext(getApplicationContext());
        try {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        }catch (Exception e){}
        if(Fref.fref_base==null){
            Fref.fref_base = new Firebase("https://twooshapp-763a4.firebaseio.com");
        }

        Fref.fref_rooms = Fref.fref_base.child("Twoosh").child("rooms");

        this.authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

                System.out.println("Authentication successfull...");

            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError

                getFirebaseAuth();
            }
        };

        Fref.fref_base.authWithCustomToken(User.f_access_token, this.authResultHandler);
        Fref.fref_rooms.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                RoomListItem roomitem = snapshot.getValue(RoomListItem.class);
                adapter.add(roomitem);

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



        Fref.fref_rooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void getFirebaseAuth(){
        HttpClient httpClient = new HttpClient(new HttpClient.PostBack(){

            @Override
            public void onResponse(String response) {

                try {

                    //Toast.makeText(TwooshDock.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject roomlist_response = new JSONObject(response);
                    if(roomlist_response.getString("status").equals("Success") && (roomlist_response.getString("response").length()>0)){
                        User.f_access_token = roomlist_response.getString("response");
                        //  fref.authWithCustomToken(User.f_access_token, authResultHandler);

                        Prefs.saveUserStatics();
                        Fref.fref_base.authWithCustomToken(User.f_access_token, authResultHandler);
                        // dock.putExtra("work","getaccess");
//                        Intent dock = new Intent(VerifyOTP.this, RoomDock.class);
//                        startActivity(dock);

                    }


                } catch (JSONException e) {
                    //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        String host = getResources().getString(R.string.local_host);
        String getfaccessapi = getResources().getString(R.string.getfaccessapi);

        //String host = getResources().getString(R.string.local_host);
        //String getroomsapi = getResources().getString(R.string.getfaccessapi);
        String getfaccessurl = host+getfaccessapi;
        JSONObject getfaccess = new JSONObject();
        try{

            getfaccess.put("mobile",User.mobile);
            getfaccess.put("pwd",User.pwd);
        }
        catch (Exception e){}

        httpClient.Post(getApplicationContext(), getfaccessurl, getfaccess);



    }

    public void registerUser(final JSONObject signupobj){


        // POST - publish twoosh remmote
        HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject publishtwoosh_resp = new JSONObject(response);
                    JSONObject response_data = publishtwoosh_resp.getJSONObject("response");


                    String inserted = response_data.getString("inserted");
                    String id = response_data.getString("id");
                    String mobile = response_data.getString("mobile");


                    //int matched = response_data.getInt("matched");
                    String otp_verified = response_data.getString("otp_verified");
                    if((publishtwoosh_resp.get("status").equals("Success")) && ((inserted.equals("1")) || (otp_verified.equals("0")))){


                        //renderDock();
                        // verifyotp screen
                        //startVerifyOTP(mobile);


                    }else{

                        Toast.makeText(TwooshDock.this, "In else part", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        String host = getResources().getString(R.string.local_host);
        String signupapi = getResources().getString(R.string.signupapi);

        String signupurl = host + signupapi;



        httpclient.Post(this, signupurl, signupobj);


    }


    public void touchAccess(){

    }



    public void updateData(){

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






    public void getAccessToken(){




        HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
            @Override
            public void onResponse(String response) {
                // Do Something after the request callback comes has finished


                try {

                    JSONObject register_resp = new JSONObject(response);
                    if( register_resp.get("status").equals("Success") && !(register_resp.get("response").equals(""))) {


                        // store user details to Shared Prefs

                        userdetails.put("access_token", register_resp.get("response"));
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                "info.twoosh.TwooshUserPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String twoosh_user_data = userdetails.toString();
                        editor.putString("twoosher", twoosh_user_data);
                        editor.commit();
                        twoosher = twoosh_user_data;
                        setUserStatics();
                        renderDock();


                    }else{

                        Toast.makeText(TwooshDock.this, "Please reset your password.", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                   e.printStackTrace();
                }
            }
        });

        String host = getResources().getString(R.string.local_host);
        String getaccesspi = getResources().getString(R.string.getaccessapi);
        String getaccess_url = host+getaccesspi;
        JSONObject proauthdata = new JSONObject();
        //{"name":"Satyam","mobile":"9945325886","email":"satyam.nitt@gmail.com","gender":"","dob":"","location":"","tags":"dermatologist,orthopaedics","corp_referrer":"1","corp_auth_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb3JwaWQiOiI1N2RjZjE5NTI1ZWNlNjYxMDVkNmFiMTUiLCJlbWFpbCI6InNhamlkLmlzbGFtOTBAZ21haWwuY29tIiwiZXhwIjoxNDg5NjUwNjk2LCJtb2JpbGUiOiI3MzkwODk0MTExIn0.QvWtS1G4o5PSW1gKJVXcDYzA-gyPPT7g4ISRTRsVpQk","city":"Bangalore"}

        try{


            proauthdata.put("mobile",userdetails.getString("mobile"));
            proauthdata.put("pwd", userdetails.getString("pwd"));



        }
        catch (Exception e){}
        httpclient.Post(this, getaccess_url, proauthdata);

    }




    public boolean sanitizeUser(){


        // user can be 2 types - newuser, corpuser


        // categorize user
        if(User.corp_auth_token!=""){
            User.user_type="C";

        }
        else{
            User.user_type="T";
            User.appname = "Twoosh";
            User.corpid = "Twoosh";

        }
        Intent i = new Intent(TwooshDock.this, RoomDock.class);
            startActivity(i);



        return true;
    }




    public boolean ifPrefsExists()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("info.twoosh.TwooshUserPref", Context.MODE_PRIVATE);
        //
        String twoosher = prefs.getString("twoosher", null);
        if(twoosher==null){
            //Toast.makeText(this, "twoosher null...", Toast.LENGTH_SHORT).show();
            return false;
        } else
        {
            //Toast.makeText(this, "twoosher recognized : "+twoosher, Toast.LENGTH_SHORT).show();
            this.twoosher = twoosher;
            return true;

        }


    }




    public void setUserStatics(){
        try {


            JSONObject user_data = new JSONObject(twoosher);
            User.twoosh_user_prefs = user_data;
            User.userid = user_data.getString("userid");
            User.name = user_data.getString("name");
            User.mobile = user_data.getString("mobile");
            User.access_token = user_data.getString("access_token");
            User.appname = "Twoosh";
            this.getSupportActionBar().setTitle("Twoosh Chat");
            this.getSupportActionBar().setSubtitle("You are connected.");


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


    public void renderDock(){


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

        String urlparams;
        if(User.user_type.equals("C")){
            urlparams = "{\"corpid\":\""+User.corpid+"\",\"type\":\"corp\"}";
        }else{
            urlparams = "{\"type\":\"community\"}";
        }

        httpClient.Get(this, getroomsurl, urlparams);

    }




    // add room objects to adapter and local firebase
    public void addtoAdapter(JSONArray roomlist){

        //wordlistref = fbaserootref.child(User.corpid).child("wordlist");
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
                //wordlistref.child(roomname).setValue(roomitem);
                adapter.add(roomitem);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        adapter.notifyDataSetChanged();
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
                intent.putExtra("tag_id", m.tag_id);
                intent.putExtra("room", m.tag_name);
                User.current_room = m.tag_name;

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
