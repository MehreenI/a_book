package com.example.customAdsPackage;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.book.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class GoogleAdMobManager2 {

    private Context context;
    private RewardedAd rewardedAd;

    public GoogleAdMobManager2(Context context) {
        this.context = context;
        // Initialize the Mobile Ads SDK with your App ID.
        MobileAds.initialize(context, initializationStatus -> {
            // Initialization complete, you can add any additional logic here if needed.
        });
        // Load the rewarded ad
        loadRewardedAd();
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                Toast.makeText(context, "Rewarded ad loaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Toast.makeText(context, "Rewarded ad failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ShowRewardedAd(Activity c, Runnable successCallback) {
        Log.d(c.toString().toUpperCase(), "ShowRewardedAd");
        Activity activityContext = c;
        rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                Log.d("Google Ad", "The user earned the reward.");
                if (successCallback != null) {
                    successCallback.run();
                }
            }
        });
        loadRewardedAd();
    }

    // Method to manually load the rewarded ad
    public void loadAdManually() {
        loadRewardedAd();
    }
}
