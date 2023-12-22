package com.example.customAdsPackage;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class GoogleAdMobManager {

    //region Attributes
    private final String TAG = "GoogleAdMobManager";

    //region AdMobIds
    private static final boolean TESTING_MODE = true;
    private static String REWARDED_ID;
    private static String REWARDED_INTERSTITIAL_ID;
    private static final String REWARDED_TEST_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String REWARDED_INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/5354046379";
    private static final String ADMOB_REWARDED_ID = "ca-app-pub-3940256099942544~3347511713";
    private static final String ADMOB_REWARDED_INTERSTITIAL_ID = "here type app id provided by google admob";
    //endregion AdMobIds

    //region Singleton Pattern
    private static GoogleAdMobManager instance;

    private GoogleAdMobManager() {
    }

    public static synchronized GoogleAdMobManager getInstance() {
        if (instance == null) {
            instance = new GoogleAdMobManager();
        }
        return instance;
    }
    //endregion Singleton Pattern

    private Context context;
    //endregion Attributes

    //region Initialization
    boolean isInitialized;

    public void Initialize(Context c) {
        context = c;
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                isInitialized = true;
                if (TESTING_MODE) {
                    REWARDED_ID = REWARDED_TEST_ID;
                    REWARDED_INTERSTITIAL_ID = REWARDED_INTERSTITIAL_TEST_ID;
                } else {
                    REWARDED_ID = ADMOB_REWARDED_ID;
                    REWARDED_INTERSTITIAL_ID = ADMOB_REWARDED_INTERSTITIAL_ID;
                }
                Log.d(TAG, "GoogleAdMobManager: Initialize:\nTESTING_MODE: " + TESTING_MODE);
                LoadRewardedAd();
                LoadRewardedInterstitialAd();
            }
        });
    }

    public boolean IsAdMobInitialized() {
        return isInitialized;
    }
    //endregion Initialization

    //region Rewarded Ad
    private RewardedAd rewardedAd;
    boolean isLoadingRewarded;

    public boolean IsRewardedAdAvailable() {
        return rewardedAd != null;
    }

    public void LoadRewardedAd() {
        if (IsAdMobInitialized()) {
            if (IsRewardedAdAvailable()) {
                Log.d(TAG, "RewardedAd is already Loaded");
            } else {
                if (!isLoadingRewarded) {
                    isLoadingRewarded = true;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    Log.d(TAG, "context: " + context + "\nREWARDED_ID: " + REWARDED_ID + "\nadRequest: " + adRequest);
                    RewardedAd.load(context, REWARDED_ID, adRequest,
                            new RewardedAdLoadCallback() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    Log.e(TAG, "Ad failed to load: " + loadAdError.getMessage());
                                    isLoadingRewarded = false;
                                    rewardedAd = null;
                                }

                                @Override
                                public void onAdLoaded(@NonNull RewardedAd ad) {
                                    Log.d(TAG, "Rewarded Ad got Loaded");
                                    rewardedAd = ad;
                                    isLoadingRewarded = false;
                                }
                            });
                } else {
                    Log.d(TAG, "Rewarded Ad is being loaded");
                }
            }
        } else {
            Log.d(TAG, "AdMobManager not initialized");
        }
    }

    public void SetContentCallBacks() {
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                rewardedAd = null;
                LoadRewardedAd(); // Reload the ad here
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                rewardedAd = null;
                LoadRewardedAd();
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    public void ShowRewardedAd(Activity c, Runnable successCallback) {
        Log.d(c.toString().toUpperCase(), "ShowRewardedAd");
        if (IsRewardedAdAvailable()) {
            Activity activityContext = c;
            SetContentCallBacks();
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    Log.d(TAG, "The user earned the reward.");
                    if (successCallback != null) {
                        successCallback.run();
                    }
                }
            });
            LoadRewardedAd();
        } else {
            Log.d(TAG, "The rewarded ad isn't ready yet.");
        }
    }
    //endregion Rewarded Ad

    //region Rewarded Interstitial Ad
    private RewardedInterstitialAd rewardedInterstitialAd;
    boolean isLoadingRewardedInter;

    public boolean IsRewardedInterstitialAdAvailable() {
        return rewardedInterstitialAd != null;
    }

    public void LoadRewardedInterstitialAd() {
        if (IsAdMobInitialized()) {
            if (IsRewardedInterstitialAdAvailable()) {
                isLoadingRewardedInter = false;
                Log.d(TAG, "RewardedInterstitialAd is already Loaded");
            } else {
                if (!isLoadingRewardedInter) {
                    isLoadingRewardedInter = true;
                    RewardedInterstitialAd.load(context, REWARDED_INTERSTITIAL_ID,
                            new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(RewardedInterstitialAd ad) {
                                    Log.d(TAG, "Ad was loaded.");
                                    isLoadingRewardedInter = false;
                                    rewardedInterstitialAd = ad;
                                    SetRewardedInterstitialContentCallBacks();
                                }

                                @Override
                                public void onAdFailedToLoad(LoadAdError loadAdError) {
                                    Log.d(TAG, loadAdError.toString());
                                    isLoadingRewardedInter = false;
                                    rewardedInterstitialAd = null;
                                }
                            });
                } else {
                    Log.d(TAG, "Rewarded Interstitial Ad is being loaded");
                }
            }
        } else {
            Log.d(TAG, "AdMobManager not initialized");
        }
    }

    public void SetRewardedInterstitialContentCallBacks() {
        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                rewardedInterstitialAd = null;
                LoadRewardedInterstitialAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                rewardedInterstitialAd = null;
                LoadRewardedInterstitialAd();
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    public void ShowRewardedInterstitialAd(Activity c, Runnable successCallback) {
        Log.d(c.toString().toUpperCase(), "ShowRewardedInterstitialAd");
        if (IsRewardedInterstitialAdAvailable()) {
            Activity activityContext = c;
            SetRewardedInterstitialContentCallBacks();
            rewardedInterstitialAd.show(
                    activityContext,
                    new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            if (successCallback != null) {
                                successCallback.run();
                            }
                        }
                    });
            LoadRewardedInterstitialAd();
        } else {
            Log.d(TAG, "The rewarded interstitial ad isn't ready yet.");
        }
    }
    //endregion Rewarded Interstitial Ad
}
