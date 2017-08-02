package com.kghy1234gmail.speedquizchallenge;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<MainItem> themes = new ArrayList<>();
    MainRecyclerAdapter adapter;

    ThemeTask task;

    ImageView btnHowtoplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnHowtoplay = (ImageView)findViewById(R.id.btn_main_howtoplay);

        GridLayoutManager manager = new GridLayoutManager(this, 2);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(manager);

        adapter = new MainRecyclerAdapter(this, themes);
        recyclerView.setAdapter(adapter);

        task = new ThemeTask();
        task.execute();


    }

    class ThemeTask extends AsyncTask<Void, Void, Void>{

        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buffer = new StringBuffer();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            themes.clear();

            try {
                URL url = new URL("http://kghy123.dothome.co.kr/SpeedQuizChallenge/themes.json");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));


                String line = br.readLine();

                while(line!=null){
                    buffer.append(line);
                    line = br.readLine();
                }
                conn.disconnect();

                JSONObject obj = new JSONObject(buffer.toString());
                Iterator<String> keys = obj.keys();

                Log.e("jsonString",buffer.toString());

                while(keys.hasNext()){
                    String key = keys.next();
                    MainItem item = new MainItem();
                    item.theme = key;

                    JSONObject object = obj.getJSONObject(key);
                    item.imgPath = object.getString("image");

                    themes.add(item);
                    publishProgress();
                }


            } catch (Exception e) {

            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                FileOutputStream fos = openFileOutput("themes.json", MODE_PRIVATE);
                PrintWriter pw = new PrintWriter(fos);

                JSONObject obj = new JSONObject(buffer.toString());
                pw.write(obj.toString());
                pw.flush();
                pw.close();

            } catch (FileNotFoundException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void clickHowtoplay(View v){

        Intent intent = new Intent(this, HowtoplayActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.howtoplay_anim, 0);

    }
}
