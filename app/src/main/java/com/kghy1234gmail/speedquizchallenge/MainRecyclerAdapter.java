package com.kghy1234gmail.speedquizchallenge;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainRecyclerAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MainItem> themes;

    public MainRecyclerAdapter(Context context, ArrayList<MainItem> themes) {
        this.context = context;
        this.themes = themes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item_main, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(themes.get(position).imgPath != null){
            ((ViewHolder)holder).img.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).tv.setVisibility(View.GONE);
            Glide.with(context).load(themes.get(position).imgPath).into(((ViewHolder)holder).img);
        }
        else{
            ((ViewHolder)holder).img.setVisibility(View.GONE);
            ((ViewHolder)holder).tv.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).tv.setText(themes.get(position).theme);
        }

    }



    @Override
    public int getItemCount() {
        return themes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView tv;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ItemSelectedActivity.class);
                    intent.putExtra("theme", themes.get(getLayoutPosition()).theme);


                    ActivityOptions activityOptions = null;
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity)context, new Pair<View, String>(itemView, "view"));



                    context.startActivity(intent, activityOptions.toBundle());

                }
            });

            img = (ImageView) itemView.findViewById(R.id.img);
            tv = (TextView)itemView.findViewById(R.id.tv);

        }


    }

}
