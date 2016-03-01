package com.xtl.sunny.drive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Video extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final int RESULT_STORE_FILE = 1;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    //    GoogleAccountCredential credential;
//    Button up;
//    ImageView img;
//    private final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
//    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    GoogleApiClient mGoogleApiClient;
    Bitmap mBitmapToSave;
    String picturePath;
    ProgressDialog progress;

    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.w(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.w(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.w(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                       image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.w(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("video/*").setTitle(".mp4").build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_ACCOUNT_PICKER, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.w(TAG, "Failed to launch file chooser.");
                        }
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
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
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

    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:

                if (resultCode == Activity.RESULT_OK) {
//                    // Store the image data as a bitmap for writing later.
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};

                    Cursor cursor = this.getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);

                    try{
//                        InputStream imInputStream = getContentResolver().openInputStream(data.getData());
//                        mBitmapToSave = BitmapFactory.decodeStream(imInputStream);
//                        smallImagePath = saveGalaryImageOnLitkat(bitmap);
//                        logoImg.setImageBitmap(BitmapFactory.decodeFile(smallImagePath));

                      mBitmapToSave=  ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MINI_KIND);
                        Log.e("show", String.valueOf(data));
                        Log.w("path", data.getData().getPath());



                    }catch (Exception e){
                        Log.e(TAG,e.toString());
                    }
//                    mBitmapToSave = (Bitmap) data.getExtras().get("data");


                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.w(TAG, "Video successfully saved.");
                    Toast.makeText(getApplicationContext(),"video is uploading....",Toast.LENGTH_SHORT).show();


                    mBitmapToSave = null;
                    // Just start the camera again for another photo.
//                    final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
//                   intent.setType("video/*");
//                    startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
                      startActivity(new Intent(this,MainActivity.class));

                }
                break;
        }

    }

    /*   Uri selectedImage = data.getData();
           String[] filePathColumn = {MediaStore.Images.Media.DATA};

           Cursor cursor = this.getContentResolver().query(selectedImage,
                   filePathColumn, null, null, null);
           cursor.moveToFirst();

           int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);

           Log.w("path", picturePath);

*/

//           img.setImageBitmap(BitmapFactory.decodeFile(picturePath));




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.w(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.w(TAG, "Exception while starting resolution activity", e);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.w(TAG, "API client connected.");
        if (mBitmapToSave == null) {
            // This activity has no UI of its own. Just start the camera.
            final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
         intent.setType("video/*");
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
        return;
        }
        saveFileToDrive();
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.w(TAG, "GoogleApiClient connection suspended");
    }

}
