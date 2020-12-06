package com.example.powersheet;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powersheet.APIControllers.SheetsAPIController;
import com.example.powersheet.CustomAdapters.Helper.OnStartDragListener;
import com.example.powersheet.CustomAdapters.Helper.SimpleItemTouchHelperCallback;
import com.example.powersheet.CustomAdapters.RecyclerAdapterTest;
import com.example.powersheet.UserData.User;
import com.example.powersheet.Util.FileSystemUtil;
import com.example.powersheet.Spreadsheet.SheetFile;
import com.example.powersheet.Spreadsheet.TrainingSheet;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.types.proto.MediaItem;
import com.google.photos.types.proto.MediaMetadata;
import com.google.photos.types.proto.Video;
import com.google.photos.types.proto.VideoProcessingStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button readButton;
    private Button writeButton;
    private EditText writeData;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS, "https://www.googleapis.com/auth/photoslibrary.sharing", "https://www.googleapis.com/auth/photoslibrary"};
    private static final String[] SCOPES2 = { "https://www.googleapis.com/auth/photoslibrary.readonly" };

    GoogleAccountCredential photosCredential;
    PhotosLibrarySettings settings;

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FileSystemUtil.setBaseDirectory(getCacheDir().toString());

        User user = FileSystemUtil.deserializeUser();
        if (user == null) {
            user = new User();
            FileSystemUtil.serializeUserObject(user);
        }

        ((MyApp) getApplicationContext()).user = user;

        /*LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);*/

        //mCallApiButton = new Button(this);
        writeButton = (Button) this.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeButton.setEnabled(false);
                writeDataToSheet();
                writeButton.setEnabled(true);
            }
        });

        readButton = (Button) this.findViewById(R.id.readButton);
        //mCallApiButton.setText(BUTTON_TEXT);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readButton.setEnabled(false);
                mOutputText.setText("");
                readDataFromSheet();
                readButton.setEnabled(true);
            }
        });
        //activityLayout.addView(mCallApiButton);

        //OutputText = new TextView(this);
        mOutputText = (TextView) this.findViewById(R.id.outputText);
        //mOutputText.setLayoutParams(tlp);
        //mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        //mOutputText.setText(
        //        "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
        //activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        //setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        chooseAccount();
        if (verificationCheck()) {
            ((MyApp) getApplicationContext()).credentials = mCredential;
        }

        ((Button) this.findViewById(R.id.photosButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                photosTest();
                view.setEnabled(true);
            }
        });

        ArrayList<String> animalNames = new ArrayList<>();
        for (int i = 0; i < 1; ++i) {
            animalNames.add("Horse");
            animalNames.add("Cow");
            animalNames.add("Camel");
            animalNames.add("Sheep");
            animalNames.add("Goat");
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OnStartDragListener dragListener = new OnStartDragListener();
        RecyclerAdapterTest adapter = new RecyclerAdapterTest(this, dragListener, animalNames);

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, this);
        dragListener.mItemTouchHelper = new ItemTouchHelper(callback);
        dragListener.mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void photosTest() {
        /*if (verificationCheck()) {
            PhotosAPIController photosAPIController = new PhotosAPIController(this);
            photosAPIController.loadAndPlayVideo("AJFR02J6oYZTJtt9ZlE33qSLFi7Iyvci92ufzJTm8xDVnvQnlmYR7wZUoUlQHjEJujC6XshaT2z8");
        }*/
        Intent i = new Intent(MainActivity.this, PhotosLibraryActivity.class);
        startActivity(i);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void readDataFromSheet() {
        SheetsAPIController sheetsAPIController = new SheetsAPIController(this);
        sheetsAPIController.readDataFromSheet("1O88KlAfJhpBPPOrpcq-DCYfkbclqONJ3OwkrdDQxtmE");
       /* if (verificationCheck()) {
            try {
                new MakeRequestTask(mCredential, 0).execute();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    private void writeDataToSheet() {
        if (verificationCheck()) {
            new MakeRequestTask(mCredential, 1).execute();
        }
    }

    private boolean verificationCheck() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.",
                    Toast.LENGTH_LONG).show();
        } else {
            return true;
        }

        return false;
    }


    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String accountName = preferences.getString(PREF_ACCOUNT_NAME, null);
            System.out.println("ACCOUNT NAME = " + accountName);
            //String accountName = activity.getPreferences(Context.MODE_PRIVATE)
            //      .getString(PREF_ACCOUNT_NAME, null);

            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                //readDataFromSheet();
            } else {
                // Start a dialog from which the user can choose an account
                this.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                        //activity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        editor.commit();
                        mCredential.setSelectedAccountName(accountName);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //readDataFromSheet();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
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
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        private int action;

        MakeRequestTask(GoogleAccountCredential credential, int which) {
            System.out.println("WHICH = " + which);
            action = which;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Power Sheet")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            System.out.println("ACTION = " + action);
            try {
                switch (action) {
                    case 0:
                        return getDataFromApi();
                    case 1:
                        writeDataUsingApi();
                        return new ArrayList<String>();
                    case 2:
                        callPhotosAPI();
                        break;
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }

        private void callPhotosAPI() {
            System.out.println("CALLING PHOTOS API");
            try {
                settings = PhotosLibrarySettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(UserCredentials.newBuilder()
                                        .setClientId("193018023298-hc1o49pag50a9ne6hk1j66m80v56rf4c.apps.googleusercontent.com")
                                        .setClientSecret("")
                                        .setAccessToken(new AccessToken(mCredential.getToken(), null))
                                        .build()))
                        .build();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            try (PhotosLibraryClient photosLibraryClient =
                         PhotosLibraryClient.initialize(settings)) {

                // Create a new Album  with at title
                //Album createdAlbum = photosLibraryClient.createAlbum("My Album");

                //get all user albums
                /*InternalPhotosLibraryClient.ListSharedAlbumsPagedResponse albums = photosLibraryClient.listSharedAlbums();
                Iterable<Album> it = albums.iterateAll();
                Iterator<Album> iterator = it.iterator();
                while (iterator.hasNext()) {
                    Album alb = iterator.next();
                    System.out.println(alb.getProductUrl() + " " + alb.getId());
                }*/
                //Album album = photosLibraryClient.li

                // Get some properties from the album, such as its ID and product URL
                //String id = createdAlbum.getId();
                //String url = createdAlbum.getProductUrl();
                //System.out.println("PHOTOS = " + id + " " + url);

                InternalPhotosLibraryClient.SearchMediaItemsPagedResponse response = photosLibraryClient.searchMediaItems("AJFR02J6oYZTJtt9ZlE33qSLFi7Iyvci92ufzJTm8xDVnvQnlmYR7wZUoUlQHjEJujC6XshaT2z8");
                ArrayList<String> videoBaseUrls = new ArrayList<>();
                for (MediaItem item : response.iterateAll()) {
                    if (item.hasMediaMetadata()) {
                        MediaMetadata metadata = item.getMediaMetadata();
                        if (metadata.hasVideo()) {
                            // This media item is a video and has additional video metadata
                            Video videoMetadata = metadata.getVideo();
                            VideoProcessingStatus status = videoMetadata.getStatus();
                            if (status.equals(VideoProcessingStatus.READY)) {
                                videoBaseUrls.add(item.getBaseUrl() + "=dv");
                            }
                        }
                    }
                }

                if (!videoBaseUrls.isEmpty()) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimpleExoPlayer player = new SimpleExoPlayer.Builder(MainActivity.this).build();
                            PlayerView playerView = (PlayerView) MainActivity.this.findViewById(R.id.playerView);
                            playerView.setPlayer(player);


                            Uri uri = Uri.parse(videoBaseUrls.get(0));

                            // Create a data source factory.
                            DataSource.Factory dataSourceFactory =
                                    new DefaultHttpDataSourceFactory(Util.getUserAgent(MainActivity.this, "app-name"));
                            // Create a progressive media source pointing to a stream uri.
                            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                                    .createMediaSource(uri);
                            // Prepare the player with the media source.
                            player.prepare(mediaSource);
                        }
                    });

                    /*String url = videoBaseUrls.get(0); // your URL here
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    mediaPlayer.setDataSource(url);
                    SurfaceView surfaceView = (SurfaceView) MainActivity.this.findViewById(R.id.surfaceView);
                    VideoView videoView = (VideoView) MainActivity.this.findViewById(R.id.videoView);
                    mediaPlayer.setDisplay(videoView.getHolder());
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();*/
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Error during album creation
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            try {
                System.out.println("CREENTIALS TOKEN = " + mCredential.getToken());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("READ");
            //String spreadsheetId = "629235369054-3cc387mt7mlnacv1kiq8d30auhqrh278.apps.googleusercontent.com";
            //final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
            //String range = "Class Data!A2:E";
            String spreadsheetId = "1O88KlAfJhpBPPOrpcq-DCYfkbclqONJ3OwkrdDQxtmE";
            Spreadsheet ss = this.mService.spreadsheets().get(spreadsheetId).execute();
            String range = ss.getSheets().get(0).getProperties().getTitle() + "!A:ZZZ";

            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    //.setMajorDimension("COLUMNS")
                    .execute();

            List<List<Object>> values = response.getValues();

            SheetFile sf = new SheetFile(ss.getProperties().getTitle());
            //sf.setTrainingSheet(new TrainingSheet(values, ss.getSheets().get(0).getProperties().getTitle(), "PowerSheet", 4, 2, 5, 1 ));

            //TrainingSheet ts = new TrainingSheet(values, "Training");
            sf.getTrainingSheet().printCells();

            FileSystemUtil.setBaseDirectory(getCacheDir().toString());
            FileSystemUtil.createSpreadsheetsDirectory();
            FileSystemUtil.createSheetFileDirectory(sf.getSheetFileTitle());
            sf.serializeSheetFile();
            //String dir = getCacheDir() + File.separator + "spreadsheets" + File.separator + "test.txt";
            //sf.getTrainingSheet().serialize(FileSystemUtil.getTrainingSheetDirectory(sf.getSheetFileTitle()), MainActivity.this);
           // sf.getTrainingSheet().deserialize(dir);
           // sf.getTrainingSheet().printCells();

            if (values != null) {
                int num = 0;
                //results.add("Name, Major");
                for (List row : values) {
                    for (Object o : row) {
                        results.add((String) o);
                    }
                    //results.add(row.get(0) + ", " + row.get(1) + ", " + row.get(2));
                }
            }
            return results;
        }

        private void writeDataUsingApi() throws IOException {
            System.out.println("WRITE");
            String spreadsheetId = "1O88KlAfJhpBPPOrpcq-DCYfkbclqONJ3OwkrdDQxtmE";
            String range = "Sheet1!A1:C1";
            //List<List<Object>> values = new ArrayList<List<Object>>();
            //values.addAll(Arrays.asList("A","B","C"));


            List<List<Object>> values1 = Arrays.asList(
                    Arrays.asList((Object)"A", (Object)"B", (Object)"C")
                    // Additional rows ...
            );


            List<ValueRange> data = new ArrayList<>();
            data.add(new ValueRange()
                    .setRange("Training!A1:A3")
                    .setValues(values1)
                    .setMajorDimension("COLUMNS"));
            data.add(new ValueRange()
                    .setRange("Training!C1:C3")
                    .setValues(values1)
                    .setMajorDimension("COLUMNS"));
            data.add(new ValueRange()
                    .setRange("Training!E1:E3")
                    .setValues(values1)
                    .setMajorDimension("COLUMNS"));

            //Write multiple ranges.
            BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                    .setValueInputOption("RAW")
                    .setData(data);
            BatchUpdateValuesResponse result =
                    mService.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
            System.out.printf("%d cells updated.", result.getTotalUpdatedCells());

            //write 1 range
            /*ValueRange body = new ValueRange()
                    .setValues(values)
                    .setMajorDimension("COLUMNS");
            UpdateValuesResponse result =
                    this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                            .setValueInputOption("RAW")
                            .execute();*/
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText.setText(TextUtils.join("\n", output));
                System.out.println("OUTPUT SIZE = " + output.size());
            }

            if (action == 0) {
                Intent i = new Intent(MainActivity.this, ViewSheetFileActivity.class);
                i.putExtra("sheetFileTitle", "Test");
                startActivity(i);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
}