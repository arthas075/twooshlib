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
import android.widget.ImageView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import lib.twoosh.twooshlib.constants.AppConstants;
import lib.twoosh.twooshlib.fragments.PeopleFragment;
import lib.twoosh.twooshlib.fragments.PostFragment;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.Notifs;

public class RoomDock extends AppCompatActivity {

    private ViewPager rdPager;
    private PagerAdapter rdPagerAdapter;
    private static final int NUM_PAGES = 1;
    int postcount = 0;


    Firebase fref = null;
    Firebase.AuthResultHandler authResultHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_dock);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Intent i = getIntent();
        String tag_name = i.getStringExtra("tag_name");
        String tag_id = i.getStringExtra("tag_id");
        User.current_room = tag_id;
        this.getSupportActionBar().setTitle(tag_name);
        this.getSupportActionBar().setSubtitle("Twoosh - You are connected.");



//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
//        tabLayout.addTab(tabLayout.newTab().setText("People"));
//
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        rdPager = (ViewPager) findViewById(R.id.roomdock_pager);
        rdPagerAdapter = new RoomDockPagerAdapter(getSupportFragmentManager());
        rdPager.setAdapter(rdPagerAdapter);


        //rdPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//               //rdPager.setCurrentItem(tab.getPosition());
//                int position = tab.getPosition();
//                rdPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        initActivity();

//        ImageView askaq = (ImageView)findViewById(R.id.askaq);
//        //ImageView askaq = getResources().
//        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
//        askaq.startAnimation(pulse);

//        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(askaq,
//                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
//                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
//        scaleDown.setDuration(310);
//
//        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
//        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
//
//        scaleDown.start();

    }

    public void initActivity(){

        initFirebase();

    }
    private class RoomDockPagerAdapter extends FragmentStatePagerAdapter {
        public RoomDockPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){

                case 0:
                    return new PostFragment();
//                case 1:
//                    return new PeopleFragment();
                default:
                    return null;

            }


        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
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

    public void initFirebase(){

//        c = getBaseContext();
        Firebase.setAndroidContext(getApplicationContext());
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        fref = new Firebase("https://twooshapp-763a4.firebaseio.com");
        fref = fref.child("posts");
        fref.keepSynced(true);


        getFirebaseAuth();

        // set authentication handlers
        // Create a handler to handle the result of the authentication
        authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
               // showToastMsg("User authenticated...");
//                JSONObject notification_payload = new JSONObject();
//                try{
//                    notification_payload.put("head","Twoosh - You are connected");
//                    notification_payload.put("body","Firebase authentication successfull");
//                }
//                catch (Exception e){
//
//                }
//                Notifs notify = new Notifs();
//                notify.notify(getApplicationContext(), notification_payload);
               System.out.println("The read failed: " );
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
               // showToastMsg("User authenticated failed...");
                System.out.println("The read failed: " );
                getFirebaseAuth();
            }
        };
        //User.f_access_token = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ2IjowLCJkIjp7InVpZCI6ImprbWMifSwiaWF0IjoxNDc0OTE4NjAwfQ.njvr9yUzyaPBGIez2h1wdH-yPm_unyyhYXeYuVw43wo";
        ///showToastMsg("FAcess token - " + User.f_access_token);
        //fref.authWithCustomToken(User.f_access_token, authResultHandler);


        // set child event handlers
        fref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                // System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
                //showToastMsg("Fservice addChildEventListener - ");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                System.out.println("The " + dataSnapshot.getKey() + " dinosaur's score is " + dataSnapshot.getValue());
                //showToastMsg("Fservice addChildEventListener - ");
                JSONObject notification_payload = new JSONObject();
                try{
                    notification_payload.put("head","Twoosh - You are connected.");
                    notification_payload.put("body","Chat msg");
                }
                catch (Exception e){

                }
                Notifs notify = new Notifs();
                notify.notify(getApplicationContext(), notification_payload);

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




        // set add value event handlers
        fref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // System.out.println(snapshot.getValue());

//                String key = snapshot.getKey();
//                DataSnapshot d = snapshot.child(key);
//                ChatListItem post = d.getValue(ChatListItem.class);
//
//                showToastMsg("Fservice onaddvalueEVent - ");
//                JSONObject notification_payload = new JSONObject();
//                try{
//                    notification_payload.put("head","Twoosh - You are connected.");
//                    notification_payload.put("body","Chat msg");
//                }
//                catch (Exception e){
//
//                }
//                Notifs notify = new Notifs();
//                notify.notify(getApplicationContext(), notification_payload);
            }
            //
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }


    public void getFirebaseAuth(){
        HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

            @Override
            public void onResponse(String response) {

                try {

                    //Toast.makeText(TwooshDock.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject roomlist_response = new JSONObject(response);
                    if(roomlist_response.getString("status").equals("Success") && (roomlist_response.getString("response").length()>0)){
                        User.f_access_token = roomlist_response.getString("response");
                        fref.authWithCustomToken(User.f_access_token, authResultHandler);
                    }


                } catch (JSONException e) {
                    //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });



        String host = AppConstants.localhost;
        String getroomsapi = AppConstants.getfaccessapi;
        //String host = getResources().getString(R.string.local_host);
        //String getroomsapi = getResources().getString(R.string.getfaccessapi);
        String getroomsurl = host+getroomsapi;

        String urlparams;

       // showToastMsg("User access token before auth - "+User.access_token);
        urlparams = "{\"access_token\":\""+User.access_token+"\"}";
        httpClient.Get(RoomDock.this, getroomsurl, urlparams);



    }

}
