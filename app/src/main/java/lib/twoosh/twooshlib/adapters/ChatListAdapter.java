package lib.twoosh.twooshlib.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.models.ChatListItem;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>{


    List<ChatListItem> chatmessagelist = new ArrayList<ChatListItem>();



    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public TextView chat_msg, chat_from, chat_time;
        public ChatViewHolder(View view){
            super(view);

            chat_from = (TextView) view.findViewById(R.id.chatFrom);
            chat_msg = (TextView) view.findViewById(R.id.chatMsg);
            chat_time = (TextView) view.findViewById(R.id.chatTime);
        }
    }

    public ChatListAdapter(){

    }
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_chat_line, parent, false);

        return new ChatViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {

        ChatListItem chatitem = chatmessagelist.get(position);
        holder.chat_msg.setText(chatitem.chatmsg);
        holder.chat_from.setText(chatitem.chatfrom);
        holder.chat_time.setText(chatitem.chattime);
    }

    @Override
    public int getItemCount() {
        return chatmessagelist.size();
    }

    public void add(ChatListItem object)
    {

        chatmessagelist.add(object);

    }
}