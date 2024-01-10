package com.example.book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.book.databinding.ActivitySplashScreenBinding;
import com.example.book.ui.signin.loginActivity;

public class SplashScreen extends AppCompatActivity {

    private Thread t;

    //region Attributes
    //region Class Constants
    private ActivitySplashScreenBinding actBinding;
    private Activity activity;
    private final String TAG = "SplashScreen";
    //endregion Class Constants

    private static final int SPLASH_DURATION = 2000;
    //endregion Attributes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(actBinding.getRoot());
        activity = this;
        AppController.getInstance().setCurrentActivity(activity);

        Log.d(TAG, "activity: " + activity);
        GoogleAdMobManager.getInstance().Initialize(activity);
        if(AppController.getInstance().loadLoginPrefs()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: 1 " + activity);

                    AppController.getInstance().initialize();
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DURATION);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: 2 " + activity);
                    Intent intent = new Intent(SplashScreen.this, loginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DURATION);
        }
        animScaleUp(actBinding.splashApplogo, 500, 0, false);
        animScaleUp(actBinding.splashAppname, 200, 200, false);
        animScaleUp(actBinding.splashApptagline, 200, 400, false);
    }

    //region Methods

    public void ChangeActivity(){
        if (!isInternetConnected()) {
//            DialogManager.getInstance().showDialog(Enums.DialogType.NOINTERNET);
        }else{
            t = new Thread()
            {
                public void run()
                {
                    try {
//                        sleep(10000);
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    }

    private <T extends View> void animScaleUp(T view, long duration, long offset, boolean _changeActivity) {
        view.setVisibility(View.VISIBLE);

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(duration);
        scaleAnimation.setStartOffset(offset);
        scaleAnimation.setFillAfter(true);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
                if(_changeActivity){
                    ChangeActivity();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(scaleAnimation);
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }
    //endregion Methods
}