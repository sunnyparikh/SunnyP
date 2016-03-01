package com.xtl.sunny.drive;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Button upload;
    final static int REQUEST_ACCOUNT_PICKER = 1;
    final static int RESULT_STORE_FILE = 10;
    GoogleApiClient mGoogleApiClient;


    public void new1(View view) {
        Intent i  = new Intent(this,Drive2.class);
        startActivity(i);
    }

    public void new2(View view) {
        Intent intent = new Intent(MainActivity.this,Capture.class);
        startActivity(intent);
    }

    public void new3(View view) {
        Intent intent = new Intent(MainActivity.this,Video.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        upload = (Button) findViewById(R.id.btn);
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_STORE_FILE);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(com.google.android.gms.drive.Drive.API)
                    .addScope(com.google.android.gms.drive.Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_STORE_FILE && resultCode == Activity.RESULT_OK && null != data) {
            Uri mFileUri = data.getData();
            Log.e("path", String.valueOf(data.getData()));
            // Save the file to Google Drive
//               saveFileToDrive();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = this.getContentResolver().query(mFileUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            mFileUri = Uri.fromFile(new java.io.File(picturePath));


            // File's binary content
            java.io.File fileContent = new java.io.File(mFileUri.getPath());
            FileContent mediaContent = new FileContent("image/jpeg", fileContent);

            // File's metadata.
            File body = new File();
            body.setTitle(fileContent.getName());
            body.setMimeType("image/jpeg");

            Log.e("show", fileContent.getName());

//                        Drive.Files fl = m_client.files();
//                        Drive.Files.Insert file = m_client.files().insert(body, mediaContent);
            //                            file = fl.insert(body, mediaContent);
//                            Toast.makeText(MainActivity.this,"Photo uploaded: ", Toast.LENGTH_SHORT).show();

//                Drive.Files.Insert insert = m_client.files().insert(body, mediaContent);
//                File fl = insert.execute();


            //startCameraIntent();

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

  /*  private void saveFileToDrive() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                Uri mFileUri;
                mFileUri = Uri.fromFile(new java.io.File());
                java.io.File fileContent = new java.io.File(mFileUri.getPath());
                FileContent mediaContent = new FileContent("image/jpeg", fileContent);
// File's meta data.
                File body = new File();
                body.setTitle(fileContent.getName());
                body.setMimeType("image/jpeg");
                com.google.api.services.drive.Drive.Files f1 = mService.files();
                com.google.api.services.drive.Drive.Files.Insert i1 = null;
                try {
                    i1 = f1.insert(body, mediaContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    assert i1 != null;
                    File file = i1.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        t.start();
    }*/



