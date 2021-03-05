package com.hepicode.porolooniladu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.hepicode.porolooniladu.adapter.FabricsOutListAdapter;
import com.hepicode.porolooniladu.model.FabricsOutLine;
import com.hepicode.porolooniladu.util.FabricsOutBottomSheetDialog;
import com.hepicode.porolooniladu.util.Util;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FabricsOutActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, FabricsOutListAdapter.OnCheckBoxClickListener, FabricsOutBottomSheetDialog.BottomSheetListener {

    private GoogleAccountCredential mCredential;
    private ProgressDialog mProgress;
    private RecyclerView recyclerView;
    private FabricsOutListAdapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FabricsOutLine mFabricsOutLine;

    private ArrayList<FabricsOutLine> lineArrayList;

    private int i;

    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fabrics_out);

        recyclerView = findViewById(R.id.fabrics_out_recyclerView);
        swipeRefreshLayout = findViewById(R.id.fabrics_out_refresh_layout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lineArrayList = new ArrayList<>();

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Laen andmeid...");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getResultsFromApi();
            }
        });

        getResultsFromApi();

    }

    private void getResultsFromApi() {

        lineArrayList.clear();

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
//            mErrorText.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
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

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mErrorText.setVisibility(View.VISIBLE);
//                    mErrorText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                FabricsOutActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    private class MakeRequestTask extends AsyncTask<Void, Void, List<FabricsOutLine>> {
        private Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets Test Java")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<FabricsOutLine> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * @return List of dates, product codes, quantities and boolean.
         * @throws IOException
         */
        private List<FabricsOutLine> getDataFromApi() throws IOException {
            String spreadsheetId = Util.SHEET_ID;
            String range = Util.SHEET_RANGE;
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                i = 1;
                String value = null;

                for (List row : values) {
                    i++;

                    //if quantity has comma, we replace comma with dot.

                    //row A - date
                    //row B - products code
                    //row E - given quantity
                    //row F - quantity
                    //row H - given out
                    //row J - urgent

                    value = row.get(5).toString();

                    if (value.contains(",")){
                        value = value.replace(",",".");
                    }

                    FabricsOutLine line = new FabricsOutLine();
                    line.setId(i);
                    line.setDate(row.get(0).toString());
                    line.setProductCode(row.get(1).toString());
                    line.setQuantity(Float.valueOf(value));

                    if (row.get(4).toString().isEmpty()){
                        line.setGivenQuantity(0);
                    } else {
                        line.setGivenQuantity(Float.valueOf(row.get(4).toString()));
                    }

                    line.setIsFilled(row.get(7).toString());
                    line.setIsUrgent(row.get(9).toString());

                    if (line.getIsFilled().equals("FALSE")) {
                        lineArrayList.add(line);
                    }
                }

            }
            return lineArrayList;
        }


        @Override
        protected void onPreExecute() {

            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<FabricsOutLine> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {

                Toast.makeText(FabricsOutActivity.this, "Pole ühtegi rida", Toast.LENGTH_SHORT).show();
            } else {
                recyclerViewAdapter = new FabricsOutListAdapter(FabricsOutActivity.this, lineArrayList, FabricsOutActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);
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
                            FabricsOutActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(FabricsOutActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FabricsOutActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //From Recyclerview adapter, when checkbox is clicked
    @Override
    public void onCheckBoxClick(int position) {

        FabricsOutLine line = lineArrayList.get(position);
        line.setIsFilled("TRUE");

        sendDataToApi(line);

        lineArrayList.remove(line);
        recyclerViewAdapter.notifyDataSetChanged();

        if (lineArrayList.size() < 1) {
            Toast.makeText(this, "Kõik kangad väljastatud!", Toast.LENGTH_SHORT).show();
        }

    }

    //From BottomSheetDialog, when save button is clicked
    @Override
    public void onButtonClicked(int id, String isGivenOut, float partialQuantity, String weekNumber, FabricsOutLine line) {

        mFabricsOutLine = line;

        if (mFabricsOutLine != null){

            if (mFabricsOutLine.getGivenQuantity() >= mFabricsOutLine.getQuantity()){

                //if quantity was bigger, then update quantity and add checkbox
                sendQtyDataToApi(mFabricsOutLine);
                sendDataToApi(mFabricsOutLine);

            } else {
                //if quantity was smaller, then update only quantity
                sendQtyDataToApi(mFabricsOutLine);
            }
        }

        //Refresh data
        getResultsFromApi();

    }

    private void sendDataToApi(FabricsOutLine line) {

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
//            mErrorText.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        } else {
            new SendDataTask(mCredential, line).execute();
        }
    }

    private class SendDataTask extends AsyncTask<Void, Void, List<FabricsOutLine>> {
        private Sheets mService = null;
        private Exception mLastError = null;
        private FabricsOutLine line;

        SendDataTask(GoogleAccountCredential credential, FabricsOutLine line) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets Test Java")
                    .build();

            this.line = line;
        }

        @Override
        protected List<FabricsOutLine> doInBackground(Void... params) {
            try {
                setDataToApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        private void setDataToApi() throws IOException {
            String spreadsheetId = Util.SHEET_ID;

            ValueRange body = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList((Object) new Boolean(true))
                    ));

            UpdateValuesResponse response = mService.spreadsheets().values()
                    .update(spreadsheetId, "H" + line.getId(), body)
                    .setValueInputOption("RAW")
                    .execute();
        }

        @Override
        protected void onPreExecute() {

//            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<FabricsOutLine> output) {

            mProgress.hide();

            recyclerViewAdapter.notifyDataSetChanged();

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
                            FabricsOutActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(FabricsOutActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FabricsOutActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendQtyDataToApi(FabricsOutLine line) {

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
//            mErrorText.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        } else {
            new SendQtyDataTask(mCredential, line).execute();
        }
    }

    private class SendQtyDataTask extends AsyncTask<Void, Void, List<FabricsOutLine>> {
        private Sheets mService = null;
        private Exception mLastError = null;
        private FabricsOutLine line;


        SendQtyDataTask(GoogleAccountCredential credential, FabricsOutLine line) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets Test Java")
                    .build();

            this.line = line;
        }

        @Override
        protected List<FabricsOutLine> doInBackground(Void... params) {
            try {
                setDataToApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        private void setDataToApi() throws IOException {
            String spreadsheetId = Util.SHEET_ID;

            ValueRange body = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList((Object) line.getGivenQuantity())
                    ));

            UpdateValuesResponse response = mService.spreadsheets().values()
                    .update(spreadsheetId, "E" + line.getId(), body)
                    .setValueInputOption("RAW")
                    .execute();
        }

        @Override
        protected void onPreExecute() {

//            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<FabricsOutLine> output) {

            mProgress.hide();

            recyclerViewAdapter.notifyDataSetChanged();

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
                            FabricsOutActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(FabricsOutActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FabricsOutActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
