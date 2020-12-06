package com.example.powersheet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.powersheet.APIControllers.PhotosAPIController;
import com.example.powersheet.APIDataStructures.PhotosAlbum;
import com.example.powersheet.APIDataStructures.PhotosMediaItem;
import com.example.powersheet.CustomAdapters.AlbumAdapter;
import com.example.powersheet.CustomAdapters.PhotosMediaAdapter;
import com.example.powersheet.UserData.User;
import com.example.powersheet.Util.BitmapDataObject;
import com.example.powersheet.Util.FileSystemUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Calendar;

public class PhotosLibraryActivity extends AppCompatActivity {
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_TAKE_GALLERY_MEDIA = 2001;

    private ArrayList<PhotosAlbum> albums;
    private AlbumAdapter albumAdapter;
    private ListView albumsListView;
    private User user;

    private int selectedAlbum = -1;
    private ArrayList<PhotosMediaItem> mediaItems;
    private PhotosMediaAdapter mediaAdapter;
    private ListView mediaListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_library);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);

        albumsListView = (ListView) this.findViewById(R.id.albumsListView);

        user = ((MyApp) getApplicationContext()).user;

        albums = user.getPhotosAlbums();

        albumAdapter = new AlbumAdapter(this, albums, user, R.layout.genric_entry_view);
        if (!albums.isEmpty()) {
            albumsListView.setAdapter(albumAdapter);
        }
        else {
            albumsListView.setAdapter(null);
        }

        setAlbumsListViewOnClick();


    }

    private void setAlbumsListViewOnClick() {
        albumsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAlbum = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(PhotosLibraryActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View popup = inflater.inflate(R.layout.popup_media_album, null);

                ((TextView) popup.findViewById(R.id.titleTextView)).setText(((TextView) view.findViewById(R.id.titleTextView)).getText().toString() + " Media");

                mediaListView = (ListView) popup.findViewById(R.id.mediaListView);
                mediaListView.setTag(albums.get(selectedAlbum).getAlbumId());

                ProgressBar pbar = (ProgressBar) popup.findViewById(R.id.progressBar);

                //mediaItems = new ArrayList<>();
                mediaItems = albums.get(selectedAlbum).getMediaItems();
                mediaAdapter = new PhotosMediaAdapter(PhotosLibraryActivity.this, mediaItems, R.layout.photos_media_entry, false, false);
                if (!mediaItems.isEmpty()) mediaListView.setAdapter(mediaAdapter);
                //PhotosAPIController photosAPIController = new PhotosAPIController(PhotosLibraryActivity.this);
                //photosAPIController.getAlbumItems(albums.get(selectedAlbum), mediaAdapter, mediaListView, pbar);

                setupUploadButton(((Button) popup.findViewById(R.id.uploadButton)));
                setMediaListViewOnClick(mediaListView);

                builder.setView(popup);
                builder.show();
            }
        });
    }

    private void setMediaListViewOnClick(ListView mediaListView) {
        mediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhotosAPIController photosAPIController = new PhotosAPIController(PhotosLibraryActivity.this);
                photosAPIController.loadAndDisplayMedia(mediaItems.get(i).getMediaItemId());
            }
        });
    }

    private void setupUploadButton(Button uploadButton) {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                Intent intent = new Intent();
                intent.setType("video/*,image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Media"), REQUEST_TAKE_GALLERY_MEDIA);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_MEDIA) {
                Uri selectedMediaUri = data.getData();

                Bitmap thumbnail = null;
                BitmapDataObject bitmapDataObject = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedMediaUri);
                    bitmapDataObject = new BitmapDataObject(thumbnail);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                if (thumbnail == null) {
                    try {
                        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
                        mMMR.setDataSource(this, selectedMediaUri);

                        thumbnail = mMMR.getFrameAtTime();
                        bitmapDataObject = new BitmapDataObject(thumbnail);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (thumbnail != null && selectedAlbum != -1) {
                   PhotosMediaItem video = new PhotosMediaItem("", Calendar.getInstance().getTime(), bitmapDataObject, selectedMediaUri);
                   PhotosAPIController photosAPIController = new PhotosAPIController(this);
                   photosAPIController.uploadVideo(albums.get(selectedAlbum), video, mediaAdapter, mediaListView);
                }
            }
        }
    }

    private Bitmap scale(Bitmap bitmap, int maxWidth, int maxHeight) {
        // Determine the constrained dimension, which determines both dimensions.
        int width;
        int height;
        float widthRatio = (float)bitmap.getWidth() / maxWidth;
        float heightRatio = (float)bitmap.getHeight() / maxHeight;
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth;
            height = (int)(((float)width / bitmap.getWidth()) * bitmap.getHeight());
        }
        // Height constrained.
        else {
            height = maxHeight;
            width = (int)(((float)height / bitmap.getHeight()) * bitmap.getWidth());
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float ratioX = (float)width / bitmap.getWidth();
        float ratioY = (float)height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public void addAlbum(View v) {
        addOrEditAlbum(-1);
    }

    public void addOrEditAlbum(int albumIdx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View popup = inflater.inflate(R.layout.popup_album_add, null);

        ((TextView) popup.findViewById(R.id.actionTextView)).setText("Add Album");

        EditText albumTitle = (EditText) popup.findViewById(R.id.editTitleView);
        Spinner indexSpinner = (Spinner) popup.findViewById(R.id.indexSpinner);

        int sizeSpinner = albumIdx == -1 ? albums.size() + 1 : albums.size();
        String[] arraySpinner = new String[sizeSpinner];
        for (int i = 0; i < albums.size(); ++i) {
            arraySpinner[i] = Integer.toString(i + 1);
        }
        if (albumIdx == -1) arraySpinner[arraySpinner.length - 1] = Integer.toString(arraySpinner.length);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indexSpinner.setAdapter(adapter);
        if (albumIdx == -1) indexSpinner.setSelection(arraySpinner.length - 1);
        else indexSpinner.setSelection(albumIdx);

        builder.setView(popup);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!albumTitle.getText().toString().isEmpty() && verificationCheck()) {
                    PhotosAlbum album = new PhotosAlbum(albumTitle.getText().toString());
                    //TODO call photos api to create album
                    PhotosAPIController photosAPIController = new PhotosAPIController(PhotosLibraryActivity.this);
                    photosAPIController.createAlbum(album, albumAdapter, albumsListView, Integer.parseInt(indexSpinner.getSelectedItem().toString()) - 1);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean verificationCheck() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (!isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.",
                    Toast.LENGTH_LONG).show();
        } else {
            return true;
        }

        return false;
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}