package com.benjaminafallon.androidapps.ampr;

import android.app.Application;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.Parse;
import com.parse.ParseObject;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;


public class AmprApplication extends Application {

    private AuthCallback authCallback;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "u0yYdcoXy1s8JyBbpcwksxNhc";
    private static final String TWITTER_SECRET = "ewJqKoUsazJHJkRKxKxDn5xCUv3RNutRaE03p9FgWC0BBKMO0N";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

    }

}
