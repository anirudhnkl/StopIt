package com.anirudh.stopit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class CameraActivity extends ActionBarActivity
{

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int myID = 15;
    public File imgFile;

    public String fileNameN;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    ProgressDialog dialog;
    String path, fileName;
    public File jsonFile;

    public Button pictureButton;
    public Button videoButton;
    public Button faceButton;

    protected Uri mediaUri;
    String upLoadServerUri = null;

    int serverResponseCode = 0;


    public void buttonClick(int num)
    {
        switch (num)
        {
            case 0: Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mediaUri = getOutMediaFileUri(MEDIA_TYPE_IMAGE);
                if(mediaUri == null)
                    Toast.makeText(CameraActivity.this, "There was a problem accesing your device's external storage", Toast.LENGTH_LONG).show();
                else
                {
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                }
                break;
            case 1: Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                mediaUri = getOutMediaFileUri(MEDIA_TYPE_VIDEO);
                if(mediaUri == null)
                    Toast.makeText(CameraActivity.this, "There was a problem accesing your device's external storage", Toast.LENGTH_LONG).show();
                else
                {
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
                }
                break;
        }
    }

    private Uri getOutMediaFileUri(int mediaType)
    {
        if(isExternalStorageAvailable())
        {
            String appName = CameraActivity.this.getString(R.string.app_name);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

            if(!mediaStorageDir.exists())
            {
                if(!mediaStorageDir.mkdir())
                {
                    Log.e(TAG, "Failed to create directory");
                    return  null;
                }
            }

            File mediaFile;
            Date now = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            path = mediaStorageDir.getPath() + File.separator;
            if(mediaType == MEDIA_TYPE_IMAGE)
            {
                mediaFile = new File("image1.jpg");
                fileNameN = mediaFile.getName();
            }

            else if(mediaType == MEDIA_TYPE_VIDEO)
            {
                mediaFile = new File("vid1.mp4");
            }
            else
                return null;

            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

            return Uri.fromFile(mediaFile);
        }

        return null;
    }

    private boolean isExternalStorageAvailable()
    {
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        pictureButton = (Button)findViewById(R.id.pictureButton);
        videoButton = (Button)findViewById(R.id.videoButton);

        pictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonClick(0);
            }
        });
        jsonFile = new File("Default");
        imgFile = new File("Default");

        faceButton = (Button)findViewById(R.id.faceRecogButton);
        faceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CameraActivity.this, MainActivityFace.class);

            }
        });

        upLoadServerUri = "http://45.79.128.82/imgupload/upload_image.php";
        videoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonClick(1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

         // When an Image is picked
        if (resultCode == RESULT_OK)
        {
            imgFile = new File(mediaUri.getPath());
            fileNameN = imgFile.getName();
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });

                    uploadFile(mediaUri);

                }
            }).start();
        }
    }

    public int uploadFile(Uri mediaUri)
    {

        Log.d(TAG, fileNameN);
        Log.d(TAG, mediaUri.getPath());
        final String fileName = mediaUri.getPath();

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +fileName);

            runOnUiThread(new Runnable() {
                public void run() {
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + fileName + "" + lineEnd);


                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +fileNameN;

                            Toast.makeText(CameraActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CameraActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(CameraActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(TAG,"Upload file to server Exception, Exception : "+ e.getMessage());
            }
            //dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    private void makeJsonFile()
    {
        JSONObject object = new JSONObject();

        object.put("Name", fileName);
        object.put("UserID", myID);
        object.put("ImageLink", fileName + ".jpg");

        JSONArray listOfPeople = new JSONArray();
        listOfPeople.add("...");

        File file = new File("hi");
        try {

            // Writing to a file
            file = new File(fileName+".json");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(object.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonFile = file;
    }
}
