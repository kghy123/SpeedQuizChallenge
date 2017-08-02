package com.kghy1234gmail.speedquizchallenge;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ItemSelectedActivity extends AppCompatActivity {

    TextView tv;

    String key;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selected);

        rootLayout = (RelativeLayout)findViewById(R.id.item_selected_root);
        rootLayout.setTransitionName("view");



        tv = (TextView)findViewById(R.id.item_selected_tv);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.8);
        int height = (int) (display.getHeight() * 0.7);

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;


        setTitle(getIntent().getStringExtra("theme"));
        key = getIntent().getStringExtra("theme");

        new DescTask().execute();

        setFinishOnTouchOutside(false);


    }

    @Override
    public void finish() {
        super.finish();
    }

    public void clickCancel(View v){

        onBackPressed();

    }

    public void clickPlay(View v){
        Intent intent = new Intent(this, IngameActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
        finish();
    }

    class DescTask extends AsyncTask<Void, Void, Void>{

        String desc;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                FileInputStream fis = openFileInput("themes.json");
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                StringBuffer buffer = new StringBuffer();

                String line = br.readLine();
                while(line != br.readLine()){
                    buffer.append(line);
                    line = br.readLine();
                }



                JSONObject obj = new JSONObject(buffer.toString());

                Log.e("string", obj.toString());
                obj = obj.getJSONObject(key);

                desc = obj.getString("desc");

                publishProgress();

                br.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            tv.setText(desc);

        }
    }


}
