package com.benjaminafallon.androidapps.ampr;

import android.app.Application;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
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

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                Digits.getSessionManager().clearActiveSession();
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
                Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_LONG).show();

            }
        };
    }

    public AuthCallback getAuthCallback(){
        return authCallback;
    }

}
