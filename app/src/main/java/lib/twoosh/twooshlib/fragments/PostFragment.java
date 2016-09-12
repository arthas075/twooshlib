package lib.twoosh.twooshlib.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.twoosh.twooshlib.Chatbox;
import lib.twoosh.twooshlib.CreatePost;
import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.RoomDock;
import lib.twoosh.twooshlib.adapters.PostListAdapter;
import lib.twoosh.twooshlib.adapters.RoomListAdapter;
import lib.twoosh.twooshlib.models.PostListItem;
import lib.twoosh.twooshlib.models.RoomListItem;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;

public class PostFragment extends Fragment {

    public int postcount = 0;
    PostListAdapter adapter ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.content_post_fragment, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        super.onActivityCreated(savedInstanceState);
        initActivity();
        attachListeners();


//        ImageView imageView = (ImageView) getView().findViewById(R.id.askaq);
////        Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
////        imageView.startAnimation(pulse);
//        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(imageView,
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


        adapter = new PostListAdapter();
        ListView list=(ListView)getView().findViewById(R.id.postList);
        list.setAdapter(adapter);
        if(postcount == 0){

           getPostsRemote();
        }
    }


    public void getPostsRemote(){


        Toast.makeText(getActivity(), "getting posts", Toast.LENGTH_SHORT).show();
        HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

            @Override
            public void onResponse(String response) {

                try {

                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    JSONObject roomlist_response = new JSONObject(response);
                    JSONArray roomlist = roomlist_response.getJSONArray("response");
                    addtoAdapter(roomlist);


                } catch (JSONException e) {

                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
            }
        });

        String getroomurl = getResources().getString(R.string.local_host)+"getposts";
        JSONObject getroomparams = new JSONObject();

        try {

            getroomparams.put("room", User.current_room);
            getroomparams.put("corp_referrer", User.corp_referrer);
            getroomparams.put("corp_auth", User.corp_auth_token);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String urlparams = "?params="+params;
        String urlparams = "{\"room\":\""+User.current_room+"\",\"corp_referrer\":\""+User.corp_referrer+"\",\"corp_auth\":\""+User.corp_auth_token+"\"}";
        httpClient.Get(getActivity(), getroomurl, urlparams);




    }

    public void addtoAdapter(JSONArray postlist){


        Toast.makeText(getActivity(), "Inside add adapter...", Toast.LENGTH_SHORT).show();
        if(postlist.length()>0){

            ViewFlipper vf = (ViewFlipper)getView().findViewById(R.id.postviewflipper);
            vf.showNext();
        }
        JSONObject twooshobj = null;
        PostListItem postitem;

        for(int i=0;i<postlist.length();i++){
            try {

                twooshobj = new JSONObject(postlist.get(i).toString());
                String twoosh_text = twooshobj.getString("twoosh_text");
                String twoosh_id = twooshobj.getString("_id");
                String user_name = twooshobj.getString("user_name");
                String user_id = twooshobj.getString("user_id");

                String users_count = "0";
                String replies_count = "0";
                String online_count = "0";
                String time = "00:00 GMT";

                postitem = new PostListItem(twoosh_text,twoosh_id, user_name,users_count,replies_count, online_count,time);
                adapter.add(postitem);
            } catch (JSONException e) {
                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }

        adapter.notifyDataSetChanged();



//            LinearLayout create_post_wrap= (LinearLayout)getView().findViewById(R.id.create_new_post_wrap);
//            create_post_wrap.setVisibility(View.INVISIBLE);
//
//            LinearLayout post_list_wrap= (LinearLayout)getView().findViewById(R.id.postList);
//            post_list_wrap.setVisibility(View.VISIBLE);




    }
    public void attachListeners(){

        Button askq_btn=(Button)getView().findViewById(R.id.askqbtn);


       askq_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Toast.makeText(getActivity(), "Wanna create a new post ??", Toast.LENGTH_SHORT).show();
               Intent i = new Intent(getActivity(), CreatePost.class);
               startActivity(i);


           }
       });


        ListView list=(ListView)getView().findViewById(R.id.postList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              PostListItem m = (PostListItem) parent.getAdapter().getItem(position);
                //TagListItem m = (TagListItem)view.getTag(R.id.tagList);
                //Toast.makeText(TwooshDock.this, m.tagdesc, Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(getActivity(), Chatbox.class);
                 intent.putExtra("twoosh_text",m.twoosh_text);
                 intent.putExtra("twoosh_id", m.twoosh_id);

               // intent.putExtra("tagname", m.tagname);
                startActivity(intent);
            }


        });


    }
}
