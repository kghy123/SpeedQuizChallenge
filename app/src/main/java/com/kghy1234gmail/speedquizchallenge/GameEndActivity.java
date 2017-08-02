package com.kghy1234gmail.speedquizchallenge;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;

public class GameEndActivity extends AppCompatActivity {

    ArrayList<String> contents;
    String path;
    ArrayList<Integer> corrects;

    ArrayList<GameEndItem> gameEndItems = new ArrayList<>();

    File file;

    boolean isSaved = false;

    RecyclerView recyclerView;
    GameEndRecyclerAdapter adapter;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_end);

        contents = getIntent().getStringArrayListExtra("contents");
        corrects = getIntent().getIntegerArrayListExtra("corrects");
        path = getIntent().getStringExtra("path");

        key = getIntent().getStringExtra("key");

        file = new File(path);

        for(int i=0; i<corrects.size(); i++){
            if(corrects.get(i)!=null) gameEndItems.add(new GameEndItem(contents.get(i), corrects.get(i)));
        }

        recyclerView = (RecyclerView)findViewById(R.id.game_end_recycler);
        adapter = new GameEndRecyclerAdapter(this, gameEndItems);
        recyclerView.setAdapter(adapter);



    }

    @Override
    protected void onDestroy() {
        if(!isSaved){
            file.delete();
            Log.d("FILE", "파일삭제 - 저장버튼 누르지 않음");
        }
        super.onDestroy();
    }

    public void clickRetry(View v){

        finish();

    }

    public void clickSave(View v){

        Snackbar.make(getWindow().getDecorView().getRootView(), "갤러리안의 SpeedQuizChallenge 폴더에 저장되었습니다.", Snackbar.LENGTH_SHORT).show();

        new MediaScanning(this, file);
        isSaved = true;

    }

    public void clickPreview(View v){

        Intent intent = new Intent(this, RecordPreviewActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);

    }


}
