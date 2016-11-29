package lib.twoosh.twooshlib.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.models.PostListItem;
import lib.twoosh.twooshlib.utils.Utils;

/**
 * Created by satyam on 29/12/15.
 */
public class PostListAdapter extends BaseAdapter {

    List<PostListItem> twooshpostlist = new ArrayList<PostListItem>();
    //List<PostItem> twooshpostlist = PostItem.getPostItemList();
    HashMap hm = new HashMap();


    public PostListAdapter()
    {
        // add twoosh posts from local db
    }
    @Override
    public int getCount() {
        return twooshpostlist.size();
    }

    public void add(PostListItem object)
    {

        twooshpostlist.add(object);
        hm.put(object.twoosh_id,twooshpostlist.size()-1);
    }


    public PostListItem getItemfromMap(String key)
    {
        PostListItem m;
        int arrind = Integer.parseInt(hm.get(key).toString());
        m = (PostListItem)getItem(arrind);
        return m;

    }

//
//    public void add(JSONArray postlist)
//    {
//        PostListItem post ;
//        JSONObject poster ;
//        for(int i=0;i<postlist.length();i++)
//        {
//
//            try {
//
//                poster = new JSONObject(postlist.get(i).toString());
//                post = new PostListItem();
//                add(post);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        // twooshpostlist.add(post);
//
//
//    }

    public void update(JSONArray updatelist)
    {

//        PostListItem m ;
//        JSONObject j;
//        for(int i=0;i<updatelist.length();i++)
//        {
//            try {
//
//                j = new JSONObject(updatelist.get(i).toString());
//                m = getItemfromMap(j.getString("twooshdid"));
//                m.users_count = j.getLong("twoosh_distinct_users");
//                m.replies_count = j.getLong("twoosh_no_of_chats");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        }



    }

    public void mapTwooshArray()
    {

    }
    @Override
    public PostListItem getItem(int position) {


        return twooshpostlist.get(getCount() - position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(parent.getContext());
            row = inflater.inflate(R.layout.post_list_item, parent, false);
        }

        PostListItem postObj = getItem(position);


        TextView twooshpost = (TextView)row.findViewById(R.id.twoosh1);
        TextView twooshuserid = (TextView)row.findViewById(R.id.userid1);
        TextView postOnline = (TextView)row.findViewById(R.id.postOnline1);
        TextView postTime = (TextView)row.findViewById(R.id.postTime);

        TextView distinct_users = (TextView)row.findViewById(R.id.distinctusers);
        TextView chatreplies = (TextView)row.findViewById(R.id.chatreplies);
        twooshpost.setText(postObj.p);
        twooshuserid.setText(postObj.from_name);
        distinct_users.setText(postObj.following);
        chatreplies.setText(postObj.replies);


        String timezonestring = Utils.getTimeZoneString(postObj.twoosh_time);
        postTime.setText(timezonestring);
        //postTime.setText(postObj.getGMTDateString());
        postOnline.setText("Online : "+postObj.online_count);
        return row;

    }


}
