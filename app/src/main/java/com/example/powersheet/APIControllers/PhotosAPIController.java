package com.example.powersheet.APIControllers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.powersheet.APIDataStructures.PhotosAlbum;
import com.example.powersheet.APIDataStructures.PhotosMediaItem;
import com.example.powersheet.CustomAdapters.AlbumAdapter;
import com.example.powersheet.CustomAdapters.PhotosMediaAdapter;
import com.example.powersheet.MyApp;
import com.example.powersheet.R;
import com.example.powersheet.Util.BitmapDataObject;
import com.example.powersheet.Util.FileSystemUtil;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.BatchGetMediaItemsResponse;
import com.google.photos.library.v1.proto.MediaItemResult;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.proto.NewMediaItemResult;
import com.google.photos.library.v1.proto.ShareAlbumResponse;
import com.google.photos.library.v1.upload.UploadMediaItemRequest;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.library.v1.util.NewMediaItemFactory;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;
import com.google.photos.types.proto.MediaMetadata;
import com.google.photos.types.proto.SharedAlbumOptions;
import com.google.photos.types.proto.Video;
import com.google.photos.types.proto.VideoProcessingStatus;
import com.google.protobuf.Timestamp;
import com.google.rpc.Code;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PhotosAPIController extends Activity {
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = { "https://www.googleapis.com/auth/photoslibrary.sharing" };

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private AsyncTask<Void, Void, List<String>>  task;
    ProgressDialog mProgress;

    GoogleAccountCredential credentials;
    PhotosLibrarySettings settings;

    Activity activity;
    Context context;

    public PhotosAPIController(Context c) {
        activity = (Activity) c;
        context = c;
        mProgress = new ProgressDialog(context);
        mProgress.setCanceledOnTouchOutside(false);

        mProgress.setMessage("Loading...");
        mProgress.setCancelable(false);

        /*mProgress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgress.cancel();
                //mProgress.dismiss();//dismiss dialog
            }
        });

        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface dialog) {
                task.cancel(true);
                //finish();
            }
        });*/

        credentials = ((MyApp) context.getApplicationContext()).credentials;
    }

    public void loadAndDisplayMedia(String mediaItemId) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(mediaItemId);
        try {
            task = new MakeRequestTask(credentials, 0, params).execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAlbum(PhotosAlbum photosAlbum, AlbumAdapter albums, ListView albumsListView, int idx) {
        ArrayList<Object> params = new ArrayList<>();
        params.add((Object) photosAlbum);
        params.add((Object) albums);
        params.add((Object) albumsListView);
        params.add((Object) idx);

        try {
            task = new MakeRequestTask(credentials, 1, params).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void uploadVideo(PhotosAlbum photosAlbum, PhotosMediaItem video, PhotosMediaAdapter videoMediaAdapter, ListView mediaListView) {
        ArrayList<Object> params = new ArrayList<>();
        params.add((Object) photosAlbum);
        params.add((Object) video);
        params.add((Object) videoMediaAdapter);
        params.add((Object) mediaListView);

        try {
            task = new MakeRequestTask(credentials, 2, params).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAlbumItems(PhotosAlbum photosAlbum, PhotosMediaAdapter mediaAdapter, ListView mediaListView, ProgressBar pbar) {
        ArrayList<Object> params = new ArrayList<>();
        params.add((Object) photosAlbum);
        params.add((Object) mediaAdapter);
        params.add((Object) mediaListView);
        params.add((Object) pbar);

        try {
            task = new MakeRequestTask(credentials, 3, params).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getItemThumbnails(ArrayList<PhotosMediaItem> mediaItems, PhotosMediaAdapter mediaAdapter, ListView mediaListView, ProgressBar pbar) {
        ArrayList<Object> params = new ArrayList<>();
        params.add((Object) mediaItems);
        params.add((Object) mediaAdapter);
        params.add((Object) mediaListView);
        params.add((Object) pbar);

        try {
            task = new MakeRequestTask(credentials, 5, params).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItemFromAlbum(PhotosMediaAdapter photosMediaAdapter, ListView mediaListView, int idx) {
        ArrayList<Object> params = new ArrayList<>();
        params.add((Object) photosMediaAdapter);
        params.add((Object) mediaListView);
        params.add((Object) idx);

        try {
            task = new MakeRequestTask(credentials, 4, params).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        private String toastMessage = "";
        private int action;
        private ArrayList<Object> additionalParams;

        MakeRequestTask(GoogleAccountCredential credential, int whichAction, ArrayList<Object> params) {
            action = whichAction;
            additionalParams = params;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Power Sheet")
                    .build();
        }

        /**
         * Background task to call API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                initializeCredentials();
                switch (action) {
                    case 0:
                        loadMedia((String) additionalParams.get(0));
                        break;
                    case 1:
                        createAlbum((PhotosAlbum)  additionalParams.get(0), (AlbumAdapter) additionalParams.get(1),
                                (ListView) additionalParams.get(2), (Integer) additionalParams.get(3));
                        break;
                    case 2:
                        uploadMedia((PhotosAlbum)  additionalParams.get(0), (PhotosMediaItem) additionalParams.get(1),
                                (PhotosMediaAdapter) additionalParams.get(2), (ListView) additionalParams.get(3));
                        break;
                    case 3:
                        getAlbumItems((PhotosAlbum)  additionalParams.get(0), (PhotosMediaAdapter) additionalParams.get(1),
                                (ListView) additionalParams.get(2), (ProgressBar) additionalParams.get(3));
                        break;
                    case 4:
                        removeItemFromAlbum((PhotosMediaAdapter) additionalParams.get(0), (ListView) additionalParams.get(1),
                                (Integer) additionalParams.get(2));
                        break;
                    case 5:
                        getItemThumbnails((ArrayList<PhotosMediaItem>)  additionalParams.get(0), (PhotosMediaAdapter) additionalParams.get(1),
                                (ListView) additionalParams.get(2), (ProgressBar) additionalParams.get(3));
                        break;
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }

        private void initializeCredentials() {
            try {
                settings = PhotosLibrarySettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(UserCredentials.newBuilder()
                                        .setClientId("193018023298-hc1o49pag50a9ne6hk1j66m80v56rf4c.apps.googleusercontent.com")
                                        .setClientSecret("")
                                        .setAccessToken(new AccessToken(credentials.getToken(), null))
                                        .build()))
                        .build();
            }
            catch (UserRecoverableAuthException e) {
                activity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                e.printStackTrace();
                //mLastError = e;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void loadMedia(String mediaItemId) {
            mProgress.setMessage("Loading Media...");
            try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings)) {
                //InternalPhotosLibraryClient.SearchMediaItemsPagedResponse response = photosLibraryClient.searchMediaItems("AJFR02J6oYZTJtt9ZlE33qSLFi7Iyvci92ufzJTm8xDVnvQnlmYR7wZUoUlQHjEJujC6XshaT2z8");
                MediaItem item = photosLibraryClient.getMediaItem(mediaItemId);
                ArrayList<String> baseUrls = new ArrayList<>();
                boolean isVideo = false;
                if (item.hasMediaMetadata()) {
                    MediaMetadata metadata = item.getMediaMetadata();
                    if (metadata.hasVideo()) {
                        isVideo = true;
                        // This media item is a video and has additional video metadata
                        Video videoMetadata = metadata.getVideo();
                        VideoProcessingStatus status = videoMetadata.getStatus();
                        if (status.equals(VideoProcessingStatus.READY)) {
                            baseUrls.add(item.getBaseUrl() + "=dv");
                        }
                        else if (status.equals(VideoProcessingStatus.PROCESSING)) {
                            toastMessage = "Video is still processing.";
                        }
                        else {
                            toastMessage = "Could not load video.";
                        }
                    }
                    else if (metadata.hasPhoto()) {
                        baseUrls.add(item.getBaseUrl());
                    }
                }

                if (!baseUrls.isEmpty()) {
                    if (isVideo) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LayoutInflater inflater = activity.getLayoutInflater();
                                View popup = inflater.inflate(R.layout.popup_watch_video, null);

                                SimpleExoPlayer player = new SimpleExoPlayer.Builder(activity).build();
                                PlayerView playerView = (PlayerView) popup.findViewById(R.id.playerView);
                                playerView.setPlayer(player);

                                Uri uri = Uri.parse(baseUrls.get(0));

                                // Create a data source factory.
                                DataSource.Factory dataSourceFactory =
                                        new DefaultHttpDataSourceFactory(Util.getUserAgent(activity, "app-name"));
                                // Create a progressive media source pointing to a stream uri.
                                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(uri);
                                // Prepare the player with the media source.
                                player.prepare(mediaSource);

                                builder.setCancelable(false);
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        player.stop();
                                    }
                                });

                                builder.setView(popup);
                                builder.show();
                            }
                        });
                    }
                    else {
                        URL url = new URL(baseUrls.get(0));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap image = BitmapFactory.decodeStream(input);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LayoutInflater inflater = activity.getLayoutInflater();
                                View popup = inflater.inflate(R.layout.popup_view_image, null);

                                ImageView imageView = (ImageView) popup.findViewById(R.id.imageView);
                                ((ImageView) popup.findViewById(R.id.imageView)).setImageBitmap(image);
                                builder.setView(popup);
                                builder.show();
                            }
                        });
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void createAlbum(PhotosAlbum album, AlbumAdapter albumAdapter, ListView albumsListView, int idx) {
            mProgress.setMessage("Creating Album...");
            try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings)) {
                Album newAlbum = photosLibraryClient.createAlbum(album.getTitle());

                SharedAlbumOptions options =
                        // Set the options for the album you want to share
                        SharedAlbumOptions.newBuilder()
                                .setIsCollaborative(true)
                                .setIsCommentable(true)
                                .build();

                if (newAlbum != null) {
                    ShareAlbumResponse response = photosLibraryClient.shareAlbum(newAlbum.getId(), options);

                    album.setAlbumId(newAlbum.getId());
                    albumAdapter.getAlbums().add(idx, album);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            albumsListView.setAdapter(albumAdapter);
                            FileSystemUtil.serializeUserObject(((MyApp) context.getApplicationContext()).user);
                        }
                    });
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void getAlbumItems(PhotosAlbum photosAlbum, PhotosMediaAdapter mediaAdapter, ListView mediaListView, ProgressBar pbar) {
            mProgress.dismiss();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbar.setVisibility(View.VISIBLE);
                }
            });

            try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings)) {
                try {
                    InternalPhotosLibraryClient.SearchMediaItemsPagedResponse response = photosLibraryClient.searchMediaItems(photosAlbum.getAlbumId());

                    for (MediaItem item : response.iterateAll()) {
                        Timestamp timestamp = item.getMediaMetadata().getCreationTime();
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(timestamp.getSeconds() * 1000L);

                        URL url = new URL(item.getBaseUrl() + "=w200-h100");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();

                        mediaAdapter.getItems().add(new PhotosMediaItem(item.getId(), cal.getTime(), new BitmapDataObject(BitmapFactory.decodeStream(input))));
                    }

                    if (!mediaAdapter.getItems().isEmpty() && activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mediaListView.setAdapter(mediaAdapter);
                            }
                        });
                    }

                } catch (com.google.api.gax.rpc.ApiException e) {
                    // Handle error
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbar.setVisibility(View.GONE);
                }
            });
        }

        private void getItemThumbnails(ArrayList<PhotosMediaItem> mediaItems, PhotosMediaAdapter mediaAdapter, ListView mediaListView, ProgressBar pbar) {
            mProgress.dismiss();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbar.setVisibility(View.VISIBLE);
                }
            });

            try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings)) {
                try {
                    ArrayList<String> itemIds = new ArrayList<>();
                    for (PhotosMediaItem item : mediaItems) {
                        itemIds.add(item.getMediaItemId());
                    }

                    // Get a list of media items using their IDs
                    BatchGetMediaItemsResponse response = photosLibraryClient
                            .batchGetMediaItems(itemIds);

                    // Loop over each result
                    int count = 0;
                    for (MediaItemResult result : response.getMediaItemResultsList()) {

                        //Each MediaItemresult contains a status and a media item
                        if (result.hasMediaItem()) {
                            // The media item was successfully retrieved, get some properties
                            MediaItem item = result.getMediaItem();
                            URL url = new URL(item.getBaseUrl() + "=w200-h100");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            mediaItems.get(count++).setBitmapDataObject(new BitmapDataObject(BitmapFactory.decodeStream(input)));
                        }
                    }

                    if (!mediaAdapter.getItems().isEmpty() && activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mediaListView.setAdapter(mediaAdapter);
                            }
                        });
                    }
                }
                catch (com.google.api.gax.rpc.ApiException e) {
                    // Handle error
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbar.setVisibility(View.GONE);
                }
            });
        }

        private void uploadMedia(PhotosAlbum photosAlbum, PhotosMediaItem mediaItem, PhotosMediaAdapter videoMediaAdapter, ListView mediaListView) {
            mProgress.setMessage("Uploading Media...");
            String pathToFile = FileSystemUtil.setupMedia(context, mediaItem.getUri());
            try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings)) {
                try (RandomAccessFile file = new RandomAccessFile(pathToFile, "r")) {
                    // Create a new upload request
                    UploadMediaItemRequest uploadRequest =
                            UploadMediaItemRequest.newBuilder()
                                    // The media type (e.g. "image/png")
                                    .setMimeType(mediaItem.getMimeType(context))
                                    // The file to upload
                                    .setDataFile(file)
                                    .build();
                    // Upload and capture the response
                    UploadMediaItemResponse uploadResponse = photosLibraryClient.uploadMediaItem(uploadRequest);
                    if (uploadResponse.getError().isPresent()) {
                        // If the upload results in an error, handle it

                        com.google.photos.library.v1.upload.UploadMediaItemResponse.Error error = uploadResponse.getError().get();
                        throw (com.google.api.gax.rpc.ApiException) error.getCause();
                    } else {
                        // If the upload is successful, get the uploadToken
                        String uploadToken = uploadResponse.getUploadToken().get();
                        // Use this upload token to create a media item
                        try {
                            // Create a NewMediaItem with the following components:
                            // - uploadToken obtained from the previous upload request
                            // - filename that will be shown to the user in Google Photos
                            // - description that will be shown to the user in Google Photos
                            NewMediaItem newMediaItem = NewMediaItemFactory
                                    .createNewMediaItem(uploadToken, "app_media", "media");
                            List<NewMediaItem> newItems = Arrays.asList(newMediaItem);

                            BatchCreateMediaItemsResponse response = photosLibraryClient.batchCreateMediaItems(photosAlbum.getAlbumId(), newItems);
                            for (NewMediaItemResult itemsResponse : response.getNewMediaItemResultsList()) {
                                com.google.rpc.Status status = itemsResponse.getStatus();
                                if (status.getCode() == Code.OK_VALUE) {
                                    // The item is successfully created in the user's library
                                    MediaItem createdItem = itemsResponse.getMediaItem();
                                    mediaItem.setId(createdItem.getId());
                                    System.out.println("CREATED ITEM ID = " + mediaItem.getMediaItemId());
                                    videoMediaAdapter.getItems().add(mediaItem);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mediaListView.setAdapter(videoMediaAdapter);
                                            FileSystemUtil.serializeUserObject(((MyApp) context.getApplicationContext()).user);
                                        }
                                    });
                                } else {
                                    // The item could not be created. Check the status and try again
                                }
                            }
                        }
                        catch (Exception e) {
                            // Handle error
                            e.printStackTrace();
                        }
                    }
                }
                catch (com.google.api.gax.rpc.ApiException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    // Error accessing the local file
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void removeItemFromAlbum(PhotosMediaAdapter photosMediaAdapter, ListView mediaListView, int idx) {
            mProgress.setMessage("Removing Media...");
            try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings)) {
                try {
                    System.out.println("DELETED ITEM ID = " + photosMediaAdapter.getItems().get(idx).getMediaItemId());
                    // List of media item IDs to remove
                    List<String> mediaItemIds = Arrays.asList(photosMediaAdapter.getItems().get(idx).getMediaItemId());

                    // Remove all given media items from the album
                    photosLibraryClient.batchRemoveMediaItemsFromAlbum((String) mediaListView.getTag(), mediaItemIds);

                    photosMediaAdapter.getItems().remove(idx);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!photosMediaAdapter.getItems().isEmpty()) mediaListView.setAdapter(photosMediaAdapter);
                            else mediaListView.setAdapter(null);
                            FileSystemUtil.serializeUserObject(((MyApp) context.getApplicationContext()).user);
                        }
                    });

                } catch (ApiException e) {
                    // An exception is thrown if the media items could not be removed
                    e.printStackTrace();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            mProgress.dismiss();
            if (!toastMessage.isEmpty()) {
                Toast.makeText(context, toastMessage,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            mProgress.dismiss();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthException) {
                    startActivityForResult(
                            ((UserRecoverableAuthException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(context, "The following error occurred:\n"
                                    + mLastError.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Request cancelled.",
                        Toast.LENGTH_LONG).show();
            }
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
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
