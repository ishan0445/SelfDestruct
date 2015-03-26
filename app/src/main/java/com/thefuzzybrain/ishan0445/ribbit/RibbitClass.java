package com.thefuzzybrain.ishan0445.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;

public class RibbitClass extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "TGA2Jkrlic4WoHZfUexQFEHVtD3e2pUMNGJ4nYwq",
                "DX2ZGMdvxfuOmtT41ZqPnbFoPY9oO3U681QxAiJP");

    }
}
