package lib.twoosh.twooshlib.adapters;


import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.models.ChatListItem;
import lib.twoosh.twooshlib.models.PeopleListItem;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.PeopleViewHolder>{


    List<PeopleListItem> ppllist = new ArrayList<PeopleListItem>();
    //private final PublishSubject<String> onClickSubject = PublishSubject.create();
    OnItemClickListener mItemClickListener;

    public class PeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView ppl_name, ppl_tags, ppl_online;
        public ImageView ppl_dp;

        public PeopleViewHolder(View view){
            super(view);


            ppl_dp = (ImageView) view.findViewById(R.id.ppl_dp);
            ppl_name = (TextView) view.findViewById(R.id.ppl_name);
            ppl_tags = (TextView) view.findViewById(R.id.ppl_tags);
            ppl_online = (TextView) view.findViewById(R.id.ppl_online);
            view.setOnClickListener(this);
        }


            @Override
            public void onClick(View v) {

                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(v, getPosition());
                }

            }


    }

    public interface OnItemClickListener{

        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    public PeopleListAdapter(){

    }
    @Override
    public PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people_list_item, parent, false);

        return new PeopleViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(PeopleViewHolder holder, int position) {

        PeopleListItem pplitem = ppllist.get(position);

        if(pplitem.pic!=""){

            try {
                URL thumb_u = new URL(pplitem.pic);
                Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                holder.ppl_dp.setImageDrawable(thumb_d);
            }
            catch (Exception e) {
                // handle it

            }

        }
        holder.ppl_name.setText(pplitem.name);
        holder.ppl_name.setText(pplitem.name);
        holder.ppl_tags.setText("#"+pplitem.taglist);
        holder.ppl_online.setText("Offline");
    }

    @Override
    public int getItemCount() {
        return ppllist.size();
    }


    public PeopleListItem getItem(int position){
        return ppllist.get(position);
    }
    public void add(PeopleListItem object)
    {

        ppllist.add(object);

    }
}