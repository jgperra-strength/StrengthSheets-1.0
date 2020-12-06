package com.example.powersheet.APIControllers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.powersheet.MyApp;
import com.example.powersheet.Spreadsheet.SheetFile;
import com.example.powersheet.Spreadsheet.TrainingSheet;
import com.example.powersheet.Util.FileSystemUtil;
import com.example.powersheet.ViewSheetFileActivity;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CreateDeveloperMetadataRequest;
import com.google.api.services.sheets.v4.model.DataFilter;
import com.google.api.services.sheets.v4.model.DeveloperMetadata;
import com.google.api.services.sheets.v4.model.DeveloperMetadataLocation;
import com.google.api.services.sheets.v4.model.DeveloperMetadataLookup;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateDeveloperMetadataRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.photos.library.v1.PhotosLibrarySettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SheetsAPIController extends Activity {
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    ProgressDialog mProgress;

    GoogleAccountCredential credentials;
    PhotosLibrarySettings settings;

    Activity activity;
    Context context;

    public SheetsAPIController(Context c) {
        activity = (Activity) c;
        context = c;
        mProgress = new ProgressDialog(context);
        mProgress.setCanceledOnTouchOutside(false);

        mProgress.setMessage("Loading...");
        mProgress.setCancelable(false);
        credentials = ((MyApp) context.getApplicationContext()).credentials;
    }

    public void readDataFromSheet(String sheetId) {
        ArrayList<Object> params = new ArrayList<>();
        params.add((Object) sheetId);

        try {
            new MakeRequestTask(credentials, 0, params).execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

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

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                switch (action) {
                    case 0:
                        readSpreadsheet((String) additionalParams.get(0));
                        //getMetaData((String) additionalParams.get(0));
                        break;
                }
            }
            catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }

        private HashMap<String, String> getMetaData(String spreadsheetId, Spreadsheet ss) throws IOException {
            if (ss == null) ss = this.mService.spreadsheets().get(spreadsheetId).execute();
            Sheet targetSheet = null;
            String targetSheetName = "Training";
            for (Sheet s : ss.getSheets()) {
                if (s.getProperties().getTitle().equals(targetSheetName)) {
                    targetSheet = s;
                    break;
                }
            }

            HashMap<String, String> metaDataMap = new HashMap<>();

            if (targetSheet != null) {
                for (DeveloperMetadata metadata : targetSheet.getDeveloperMetadata()) {
                    metaDataMap.put(metadata.getMetadataKey(), metadata.getMetadataValue());
                    System.out.println("METADATA KEY = " + metadata.getMetadataKey() + " " + metadata.getMetadataValue());
                }
            }

            return metaDataMap;
        }

        private void updateMetaData(String spreadsheetId) throws IOException {
            Spreadsheet ss = this.mService.spreadsheets().get(spreadsheetId).execute();
            Sheet targetSheet = null;
            String targetSheetName = "Training";
            int sheetId = 0;

            for (int i = 0; i < ss.getSheets().size(); ++i) {
                if (ss.getSheets().get(i).getProperties().getTitle().equals(targetSheetName)) {
                    sheetId = i;
                    targetSheet = ss.getSheets().get(i);
                    break;
                }
            }

            if (targetSheet != null) {
                List<Request> requests = new ArrayList<>();
                Request r = new Request();
                //CreateDeveloperMetadataRequest createDeveloperMetadataRequest = new CreateDeveloperMetadataRequest();
                UpdateDeveloperMetadataRequest updateDeveloperMetadataRequest = new UpdateDeveloperMetadataRequest();

                List<DataFilter> filters = new ArrayList<DataFilter>();
                filters.add(new DataFilter().setDeveloperMetadataLookup(new DeveloperMetadataLookup().setMetadataId(1)));

                updateDeveloperMetadataRequest.setDataFilters(filters);
                updateDeveloperMetadataRequest.setFields("metadataKey,metadataValue");

                DeveloperMetadata metadata = new DeveloperMetadata()
                        .setMetadataId(1)
                        .setMetadataKey("dimensions")
                        .setMetadataValue("3,3,3,4 3,3,3,4 3,3,3,4 3,3,3,4:")
                        .setLocation((new DeveloperMetadataLocation()).setSheetId(sheetId))
                        .setVisibility("PROJECT");
                updateDeveloperMetadataRequest.setDeveloperMetadata(metadata);

                r.setUpdateDeveloperMetadata(updateDeveloperMetadataRequest);
                requests.add(r);

                BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
                requestBody.setRequests(requests);

                Sheets.Spreadsheets.BatchUpdate request = this.mService.spreadsheets().batchUpdate(spreadsheetId, requestBody);
                BatchUpdateSpreadsheetResponse response = request.execute();
            }


            /*Sheets.Spreadsheets.BatchUpdate request =
                    sheetsService.spreadsheets().batchUpdate(spreadsheetId, requestBody);

            BatchUpdateSpreadsheetResponse response = request.execute();*/
        }

        private void readSpreadsheet(String spreadsheetId) throws IOException {
            Spreadsheet ss = this.mService.spreadsheets().get(spreadsheetId).execute();
            java.lang.String range = ss.getSheets().get(0).getProperties().getTitle() + "!A:ZZZ";

            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    //.setMajorDimension("COLUMNS")
                    .execute();

            List<List<Object>> values = response.getValues();

            SheetFile sf = new SheetFile(ss.getProperties().getTitle());
            sf.setTrainingSheet(new TrainingSheet(values, ss.getSheets().get(0).getProperties().getTitle(), getMetaData(spreadsheetId, ss)));
            //sf.setTrainingSheet(new TrainingSheet(values, ss.getSheets().get(0).getProperties().getTitle(), "PowerSheet", 4, 2, 5, 1 ));

            //TrainingSheet ts = new TrainingSheet(values, "Training");
            //sf.getTrainingSheet().printCells();

            FileSystemUtil.setBaseDirectory(context.getCacheDir().toString());
            FileSystemUtil.createSpreadsheetsDirectory();
            FileSystemUtil.createSheetFileDirectory(sf.getSheetFileTitle());
            sf.serializeSheetFile();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(context, ViewSheetFileActivity.class);
                    i.putExtra("sheetFileTitle", "Test");
                    context.startActivity(i);
                }
            });
            //String dir = getCacheDir() + File.separator + "spreadsheets" + File.separator + "test.txt";
            //sf.getTrainingSheet().serialize(FileSystemUtil.getTrainingSheetDirectory(sf.getSheetFileTitle()), MainActivity.this);
            // sf.getTrainingSheet().deserialize(dir);
            // sf.getTrainingSheet().printCells();

            /*if (values != null) {
                int num = 0;
                //results.add("Name, Major");
                for (List row : values) {
                    for (Object o : row) {
                        results.add((java.lang.String) o);
                    }
                    //results.add(row.get(0) + ", " + row.get(1) + ", " + row.get(2));
                }
            }
            return results;*/
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
