package com.example.android.itcreditonline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.itcreditonline.Model.FeedItem;

import java.util.ArrayList;

/**
 * Created by Simeon Angelov on 1.10.2016 г..
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{
    private ArrayList<FeedItem> feedItems;
    Context activity;

    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(activity).inflate(R.layout.adapter_news,parent,false);
        return new MyViewHolder(row);
    }

    public NewsAdapter(ArrayList<FeedItem> feedItems, Context activity) {
        this.feedItems = feedItems;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.MyViewHolder holder, int position) {
        final FeedItem f = feedItems.get(position);
        holder.title.setText(f.getTitle());
        holder.pubDate.setText(f.getPubDate());
        holder.description.setText(f.getDescription());
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(f.getLink()));
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView pubDate;
        TextView description;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleTV);
            pubDate = (TextView) itemView.findViewById(R.id.pubDateTV);
            description = (TextView) itemView.findViewById(R.id.descriptionTV);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
