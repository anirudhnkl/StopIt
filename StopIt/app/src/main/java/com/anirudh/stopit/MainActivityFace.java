package com.anirudh.stopit;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;
import com.moodstocks.android.Scanner.SearchOption;
import com.moodstocks.android.advanced.Image;

import java.io.IOException;
import java.io.InputStream;

public class MainActivityFace extends Activity implements Scanner.SyncListener{
    private static final String API_KEY    = "x6aswpr5gxi9vn1ejcde";
    private static final String API_SECRET = "U2PhZN2qYx5pQbPX";

    private boolean compatible = false;
    private Scanner scanner;

    public void processCustomImage() {
        AssetManager assetManager = this.getAssets();
        InputStream istr;
        Bitmap bmp = null;
        try {
            istr = assetManager.open("test.jpg");
            bmp = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Build the `Image` object:
        Image img = null;
        try {
            img = new Image(bmp);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }

        try {
            Result result = scanner.search(img, SearchOption.DEFAULT, Result.Extra.NONE);
            if (result != null) {
                Log.d("MainActivity", "[Local search] Result found: "+result.getValue());
            }
            else {
                Log.d("MainActivity", "[Local search] No result found");
            }
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_face);

        compatible = Scanner.isCompatible();
        if (compatible) {
            try {
                scanner = Scanner.get();
                String path = Scanner.pathFromFilesDir(this, "scanner.db");
                scanner.open(path, API_KEY, API_SECRET);
                scanner.setSyncListener(this);
                scanner.sync();
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSyncStart() {
        Log.d("Moodstocks SDK", "Sync will start.");
    }

    @Override
    public void onSyncComplete() {
        try {
            Log.d("Moodstocks SDK", "Sync succeeded ("+ scanner.count() + " images)");
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSyncFailed(MoodstocksError e) {
        Log.d("Moodstocks SDK", "Sync error #" + e.getErrorCode() + ": " + e.getMessage());
    }

    @Override
    public void onSyncProgress(int total, int current) {
        int percent = (int) ((float) current / (float) total * 100);
        Log.d("Moodstocks SDK", "Sync progressing: " + percent + "%");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compatible) {
            try {
                scanner.close();
                scanner.destroy();
                scanner = null;
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
    }

    public void onScanButtonClicked(View view) {
        if (compatible) {
            startActivity(new Intent(this, ScanActivity.class));
        }
    }
}
