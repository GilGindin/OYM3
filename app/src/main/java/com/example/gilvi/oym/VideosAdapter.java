package com.example.gilvi.oym;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/* Adapter to convert the videos we got from the API to UI list
 the videos list in the UI is shown inside a view called RecyclerView. this view has several features, e.g. scrolling.
 you can see the RecyclerView inside activity_main.xml line 41
 no need to understand the implementation here - just understand what 'adapter' does
 Adapter takes a list of items (here - ArrayList of videos), and creates the UI to present them
 we have an xml file to represent the UI of a single element in the list - look at video_row_item.xml
 line 37-38 says to the adapter to use that xml to render each item. lines 47-50 populate each item*/
class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private ArrayList<Video> data;
    private Context mContext;

    public VideosAdapter(ArrayList<Video> videos, Context context) {

        this.data = videos;
        this.mContext = context;
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_row_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(VideosAdapter.ViewHolder holder, int position) {
        final Video video = data.get(position);
        holder.title.setText(video.title);
        //holder.artist.setText(video.artist);
        Picasso.with(this.mContext).load(video.thumbnailUrl).into(holder.image);

        // when we click on an image - we want to open the VideoPlayer activity and play that video
        // so we add onClickListener. Once the button is clicked - we will use intent to navigate to the VideoPlayer activity
        // sometimes (here for example) we want to pass parameters from one intent to another.
        // this is done using intent.putExtra in the origin Intent and getIntent().getExtra in the target intent
        // see line 28 in VideoPlayer.java
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideosAdapter.this.mContext, VideoPlayer.class);
                intent.putExtra("videoId", video.id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
       // public TextView artist;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)(itemView.findViewById(R.id.txtView_title));
           // artist = (TextView)(itemView.findViewById(R.id.txtView_artist));
            image = (ImageView)(itemView.findViewById(R.id.imageView_thumbnail));
        }
    }
}
