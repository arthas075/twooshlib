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

import lib.twoosh.twooshlib.fragments.PeopleFragment;
import lib.twoosh.twooshlib.fragments.PostFragment;
import lib.twoosh.twooshlib.models.User;

public class RoomDock extends AppCompatActivity {

    private ViewPager rdPager;
    private PagerAdapter rdPagerAdapter;
    private static final int NUM_PAGES = 2;
    int postcount = 0;

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
        String roomname = i.getStringExtra("tagid");
        User.current_room = roomname;
        this.getSupportActionBar().setTitle(roomname);
        this.getSupportActionBar().setSubtitle("Twoosh - You are connected.");



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("People"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        rdPager = (ViewPager) findViewById(R.id.roomdock_pager);
        rdPagerAdapter = new RoomDockPagerAdapter(getSupportFragmentManager());
        rdPager.setAdapter(rdPagerAdapter);


        rdPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               //rdPager.setCurrentItem(tab.getPosition());
                int position = tab.getPosition();
                rdPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
                case 1:
                    return new PeopleFragment();
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

}
