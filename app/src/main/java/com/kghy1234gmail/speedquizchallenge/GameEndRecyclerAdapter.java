package com.kghy1234gmail.speedquizchallenge;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class GameEndRecyclerAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<GameEndItem> gameEndItems;

    public GameEndRecyclerAdapter(Context context, ArrayList<GameEndItem> gameEndItems) {
        this.context = context;
        this.gameEndItems = gameEndItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item_game_end, parent, false);
        GameEndViewHolder holder = new GameEndViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        GameEndViewHolder mholder = (GameEndViewHolder)holder;
        switch (gameEndItems.get(position).correct){
            case 0:
                mholder.tv.setTextColor(Color.RED);
                mholder.tv.setText(gameEndItems.get(position).content);
                break;
            case 1:
                mholder.tv.setTextColor(Color.BLUE);
                mholder.tv.setText(gameEndItems.get(position).content);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return gameEndItems.size();
    }


    class GameEndViewHolder extends RecyclerView.ViewHolder{

        TextView tv;

        public GameEndViewHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.game_end_recycler_tv);
        }

    }

}
