package com.anirudh.stopit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.SurfaceView;

import com.anirudh.stopit.R;
import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;

public class ScanActivity extends Activity implements AutoScannerSession.Listener {

    private AutoScannerSession session = null;
    private static final int TYPES = Result.Type.IMAGE | Result.Type.QRCODE | Result.Type.EAN13;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        SurfaceView preview = (SurfaceView)findViewById(R.id.preview);

        try {
            session = new AutoScannerSession(this, Scanner.get(), this, preview);
            session.setResultTypes(TYPES);
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraOpenFailed(Exception e) {
        // Implemented in a few steps
    }

    @Override
    public void onWarning(String debugMessage) {
        // Implemented in a few steps
    }

    @Override
    public void onResult(Result result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.resume();
            }
        });
        builder.setTitle(result.getType() == Result.Type.IMAGE ? "Image:" : "Barcode:");
        builder.setMessage(result.getValue());
        result.getValue();//EXPORT THIS VALUE
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        session.stop();
    }
}