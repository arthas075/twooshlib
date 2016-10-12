package lib.twoosh.twooshlib.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.models.ChatListItem;

public class ChatListAdapter extends BaseAdapter{


    List<ChatListItem> chatmessagelist = new ArrayList<ChatListItem>();

    public ChatListAdapter(){

    }

    @Override
    public int getCount() {
        return chatmessagelist.size();
    }

    @Override
    public ChatListItem getItem(int position) {


        return chatmessagelist.get(position);
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
            row = inflater.inflate(R.layout.single_chat_line, parent, false);
        }

        ChatListItem chatitem = getItem(position);
        TextView chat_msg, chat_from, chat_time;
        chat_from = (TextView) row.findViewById(R.id.chatFrom);
        chat_msg = (TextView) row.findViewById(R.id.chatMsg);
        chat_time = (TextView) row.findViewById(R.id.chatTime);

        chat_msg.setText(chatitem.chatmsg);
        chat_from.setText(chatitem.chat_fromuserid);
        chat_time.setText(chatitem.chat_unixtime);
        return row;

    }


    public void add(ChatListItem object)
    {

        chatmessagelist.add(object);

    }
}