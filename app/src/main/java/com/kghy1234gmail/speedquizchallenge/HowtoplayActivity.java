package com.kghy1234gmail.speedquizchallenge;

import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class HowtoplayActivity extends AppCompatActivity {

    FrameLayout rootlayout;

    Transition transition;
    Scene scene1, scene2, scene3;

    int cnt = 1;

    GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_howtoplay);

        setTitle("");

        rootlayout = (FrameLayout)findViewById(R.id.howtoplay_rootlayout);

        gestureDetector = new GestureDetectorCompat(this, listener);

        transition = TransitionInflater.from(this).inflateTransition(R.transition.howtoplay_tran);

        scene1 = Scene.getSceneForLayout(rootlayout, R.layout.activity_howtoplay_scene1, this);
        scene2 = Scene.getSceneForLayout(rootlayout, R.layout.activity_howtoplay_scene2, this);
        scene3 = Scene.getSceneForLayout(rootlayout, R.layout.activity_howtoplay_scene3, this);

        scene1.enter();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            float result = v - v1;
            if(result > 0){
                if(cnt > 1) {
                    cnt--;

                    switch (cnt){
                        case 1:
                            TransitionManager.go(scene1, transition);
                            break;
                        case 2:
                            TransitionManager.go(scene2, transition);
                            break;
                        case 3:
                            TransitionManager.go(scene3, transition);
                            break;
                    }
                }

            }else {
                if(cnt < 3){
                    cnt++;

                    switch (cnt){
                        case 1:
                            TransitionManager.go(scene1, transition);
                            break;
                        case 2:
                            TransitionManager.go(scene2, transition);
                            break;
                        case 3:
                            TransitionManager.go(scene3, transition);
                            break;
                    }

                }else {
                    finishAfterTransition();
                    overridePendingTransition(0, R.anim.howtoplay_out_anim);
                }

            }

            return true;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.howtoplay_out_anim);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.howtoplay_out_anim);
    }
}
