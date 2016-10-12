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

public class RoomDock extends AppCompatActivity implements Callbacker{

    private ViewPager rdPager;
    private PagerAdapter rdPagerAdapter;
    PostListAdapter adapter ;
    private static final int NUM_PAGES = 1;
    int postcount = 0;


    Firebase fref = null;
    Firebase.AuthResultHandler authResultHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_dock);


        initActivity();
        setAdapters();



    }



    public void initActivity(){

        //getApplicationContext().getSharedPreferences("info.twoosh.TwooshUserPref", 0).edit().clear().commit();
        Intent i = getIntent();
        String room = i.getStringExtra("room");
        User.current_room = room;
        FService.roomdockactive = true;
        // touch User
        touchUser();




        this.getSupportActionBar().setSubtitle("#everything");
//        this.getSupportActionBar().setSubtitle("Twoosh - You are connected.");



    }

    public void touchUser(){

        Prefs prefs = new Prefs(getApplicationContext());
        if(prefs.prefExists()){

            FService.caller = this;
            if(!FService.isRunning){
                Intent fservice = new Intent(getApplicationContext(), FService.class);
                fservice.putExtra("payload","1");
                startService(fservice);
            }
            initApp();



        }else{
            showLoginScreen();
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
        setRoomListener();

    }
    @Override
    public void callback(String data){


    }

    public void setRoomListener(){


        this.fref = Fref.fref_base.child(User.current_room);
        this.fref.keepSynced(true);
        Query orderedposts = this.fref.orderByChild("timestring");
        orderedposts.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {


                System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
                PostListItemTs plo = snapshot.getValue(PostListItemTs.class);
                PostListItem pl = new PostListItem(plo.twoosh_text,plo.twoosh_id,plo.user_name,plo.user_id,plo.users_count,plo.replies_count,plo.online_count,plo.twoosh_time);
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
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    PostListItemTs plo = postSnapshot.getValue(PostListItemTs.class);
                    PostListItem pl = new PostListItem(plo.twoosh_text,plo.twoosh_id,plo.user_name,plo.user_id,plo.users_count,plo.replies_count,plo.online_count,plo.twoosh_time);


                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
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
                intent.putExtra("twoosh_text", m.twoosh_text);
                intent.putExtra("twoosh_id", m.twoosh_id);
                intent.putExtra("username", m.user_name);
                intent.putExtra("user_id", m.user_id);
                intent.putExtra("replies", "0");
                intent.putExtra("following", "0");
                intent.putExtra("twoosh_time", m.twoosh_time);
                startActivity(intent);

            }


        });


    }

    @Override
    public void onDestroy(){

        FService.roomdockactive = false;
        super.onDestroy();
    }

}
