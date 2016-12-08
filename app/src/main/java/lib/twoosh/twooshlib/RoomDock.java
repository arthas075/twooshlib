package lib.twoosh.twooshlib;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import lib.twoosh.twooshlib.adapters.PostListAdapter;
import lib.twoosh.twooshlib.constants.AppConstants;
import lib.twoosh.twooshlib.fragments.PeopleFragment;
import lib.twoosh.twooshlib.fragments.PostFragment;
import lib.twoosh.twooshlib.interfaces.Callbacker;
import lib.twoosh.twooshlib.models.Fref;
import lib.twoosh.twooshlib.models.PostListItem;
import lib.twoosh.twooshlib.models.PostListItemTs;
import lib.twoosh.twooshlib.models.Prefs;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.NotifObjTs;
import lib.twoosh.twooshlib.notifs.Notifs;
import lib.twoosh.twooshlib.notifs.Toasts;
import lib.twoosh.twooshlib.services.FService;



// display list of posts and create post icon
public class RoomDock extends AppCompatActivity implements Callbacker{



    // class members
    PostListAdapter adapter ;
    Firebase fref = null;
    Firebase.AuthResultHandler authResultHandler = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_dock);


        initActivity();




    }



    public void initActivity(){


        // touch User
        touchUser();


        //getApplicationContext().getSharedPreferences("info.twoosh.TwooshUserPref", 0).edit().clear().commit();
//        Intent i = getIntent();
//        String room = i.getStringExtra("room");
//        Long time = i.getLongExtra("time",0);
//        String notifed = i.getStringExtra("notifed");
//        if(notifed!=null && notifed.equals("1")){
//            markLastSeen();
//        }
//        User.current_room = room;
//        FService.roomdockactive = true;



        //this.getSupportActionBar().setTitle("#everything");
//        this.getSupportActionBar().setSubtitle("Twoosh - You are connected.");
        //invalidateOptionsMenu();
    }


    public void touchUser(){


        Prefs prefs = new Prefs(getApplicationContext());
        if(!prefs.prefExists()){

            showLoginScreen();

        }else{

            // start service n wait for authentication
            FService.caller = this;
            if(!FService.isRunning){
                Intent fservice = new Intent(getApplicationContext(), FService.class);
                fservice.putExtra("payload","1");
                startService(fservice);
            }
            initApp();
            setRoomListener();

        }
    }
    public void markLastSeen(){

        JSONObject jObj = new JSONObject();
        Date utildate = new Date();
        Long twoosh_ts =  utildate.getTime();
        try{
            User.last_seen.put("R-"+User.current_room,twoosh_ts);
            Prefs.saveUserStatics();

        }catch (Exception e){

        }
    }




    public void showLoginScreen(){

        Intent i = new Intent(this, Signup.class);
        startActivity(i);

    }
    public void setAdapters(){


        adapter = new PostListAdapter();
        ListView list=(ListView)findViewById(R.id.postList);
        list.setAdapter(adapter);
        attachListeners();




    }

    public void initApp(){

        // put this function in launcher activity

        System.out.println("Firebase auth success callback");
        setAdapters();


    }
    @Override
    public void callback(String data){



    }

    public void setRoomListener(){

        User.current_room = "everything";
        Firebase.setAndroidContext(getApplicationContext());
        try {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        }catch (Exception e){}
        if(Fref.fref_base==null){
            Fref.fref_base = new Firebase("https://twooshapp-763a4.firebaseio.com");
        }
        this.fref = Fref.fref_base.child("posts").child(User.current_room);
        this.fref.keepSynced(true);
        Query orderedposts = this.fref.orderByChild("timestring");
        orderedposts.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                PostListItemTs plo = null;
                plo = snapshot.getValue(PostListItemTs.class);
                String postkey = snapshot.getKey();
//                System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
//                for (DataSnapshot messageSnapshot: snapshot.getChildren()) {
//                    plo = messageSnapshot.getValue(PostListItemTs.class);
//                }
                String ts = Long.toString(plo.ts/1000);
                PostListItem pl = new PostListItem(plo.p,postkey,plo.from_name,plo.from_id,plo.following,plo.replies,plo.online_count,ts);
//                Object e = snapshot.getValue();
//                HashMap<String, String> map = (HashMap<String, String>) e;
//
//                String ts = Long.toString(Long.parseLong(map.get("ts"))/1000);
//                PostListItem pl = new PostListItem(map.get("p"),snapshot.getKey(),map.get("from_name"),map.get("from_id"),map.get("following"),map.get("replies"),"0",ts);

                //PostListItemTs plo = snapshot.getValue(PostListItemTs.class);


                adapter.add(pl);

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



        orderedposts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
//                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                    PostListItemTs plo = postSnapshot.getValue(PostListItemTs.class);
//                    PostListItem pl = new PostListItem(plo.twoosh_text,plo.twoosh_id,plo.user_name,plo.user_id,plo.users_count,plo.replies_count,plo.online_count,plo.twoosh_time);
//
//
//                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        // authenticate firebase for writes
        Fref.fref_base.authWithCustomToken(User.f_access_token, this.authResultHandler);this.authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData



                System.out.println("The read failed: ");
                //showToastMsg("User authenticated success...");

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
               // showToastMsg("User authenticated failed...");
                //showToastMsg("FAccessToken - "+User.f_access_token);
                getFirebaseAuth();
            }
        };

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_roomdock, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        try{
            if(User.subscribed_rooms.has(User.current_room)){
                if(User.subscribed_rooms.getString(User.current_room).equals("1")){
                    menu.findItem(R.id.action_follow_room).setVisible(false);
                }else{
                    menu.findItem(R.id.action_unfollow_room).setVisible(false);
                }

            }else{
                menu.findItem(R.id.action_unfollow_room).setVisible(false);
            }

        }
        catch (Exception err){
            System.out.println("Error in prepare options "+err.toString());
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_follow_room) {
            try{

                User.subscribed_rooms.put(User.current_room,"1");
                Prefs.saveUserStatics();
            }
            catch (Exception err){}
            invalidateOptionsMenu();

        }else if(id == R.id.action_unfollow_room){

            try{
                User.subscribed_rooms.put(User.current_room,"0");
                Prefs.saveUserStatics();
            }
            catch (Exception err){}
            invalidateOptionsMenu();


        }

        return super.onOptionsItemSelected(item);
    }



    public void attachListeners(){

//        Button askq_btn=(Button)findViewById(R.id.askqbtn);
//
//
//        askq_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                Intent i = new Intent(RoomDock.this, CreatePost.class);
//                startActivity(i);
//
//
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                POSTkbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(RoomDock.this,CreatePost.class);
                startActivity(intent);
            }
        });

        ListView list=(ListView)findViewById(R.id.postList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PostListItem m = (PostListItem) parent.getAdapter().getItem(position);
                Intent intent = new Intent(RoomDock.this, Chatbox.class);
                intent.putExtra("twoosh_text", m.p);
                intent.putExtra("twoosh_id", m.twoosh_id);
                intent.putExtra("user_name", m.from_name);
                intent.putExtra("user_id", m.from_id);
                intent.putExtra("replies", m.replies);
                intent.putExtra("following", m.following);
                intent.putExtra("twoosh_time", m.twoosh_time);
                startActivity(intent);

            }


        });


    }

    @Override
    public void onDestroy(){

        FService.roomdockactive = false;

        markLastSeen();
        User.current_room = "";
        super.onDestroy();
    }

}
