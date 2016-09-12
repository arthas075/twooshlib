package lib.twoosh.twooshlib;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        String roomname = i.getStringExtra("tagname");
        User.current_room = roomname;
        this.getSupportActionBar().setTitle(roomname);
        this.getSupportActionBar().setSubtitle("Twoosh - You are connected.");

        rdPager = (ViewPager) findViewById(R.id.roomdock_pager);
        rdPagerAdapter = new RoomDockPagerAdapter(getSupportFragmentManager());
        rdPager.setAdapter(rdPagerAdapter);

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
            }

            return new PostFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
