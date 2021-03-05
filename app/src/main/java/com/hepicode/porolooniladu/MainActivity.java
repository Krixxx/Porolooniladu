package com.hepicode.porolooniladu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hepicode.porolooniladu.model.FoamOutLine;
import com.hepicode.porolooniladu.model.FoamOutWeekNumber;
import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.model.OrderNumber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_REQUEST_FOAM_IN_CODE = 42;
    public static final int READ_REQUEST_FOAM_OUT_CODE = 50;
    public static final int DELETE_REQUEST_CODE = 55;
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    Button foamInButton, foamOutButton, fabricOutButton;
    ImageButton foamInDownload, foamInDeleted, foamInEdit, foamOutDownload, foamOutEdit, foamOutDeleted;
    private int quantity, foamOutQuantity, arrivedQuantity = 0, partialQuantity = 0, isArrived = 0, isGivenOut = 0;
    private String foamInProductCode, filename, foamOutProductCode, foamOutFilename, foamOutDate, foamOutWeekNumber;
    private OrderLineViewModel orderLineViewModel;
    private OrderLine orderLine;
    private FoamOutLine foamOutLine;
    private OrderNumber orderNumber;
    private FoamOutWeekNumber foamOutWeekNr;
    private ArrayList<String> foamInSpinnerItems = new ArrayList<>();
    private ArrayList<String> foamOutSpinnerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        orderLineViewModel = ViewModelProviders.of(this).get(OrderLineViewModel.class);

        foamInButton = findViewById(R.id.btn_foam_in);
        foamOutButton = findViewById(R.id.btn_foam_out);
        fabricOutButton = findViewById(R.id.btn_fbrcs_out);
        foamInDownload = findViewById(R.id.btn_foamin_download);
        foamInEdit = findViewById(R.id.btn_foamin_edit);
        foamInDeleted = findViewById(R.id.btn_foamin_deleted);
        foamOutDownload = findViewById(R.id.btn_foamout_download);
        foamOutEdit = findViewById(R.id.btn_foamout_edit);
        foamOutDeleted = findViewById(R.id.btn_foamout_deleted);

        foamInButton.setOnClickListener(this);
        foamOutButton.setOnClickListener(this);
        fabricOutButton.setOnClickListener(this);
        foamInDownload.setOnClickListener(this);
        foamInEdit.setOnClickListener(this);
        foamInDeleted.setOnClickListener(this);
        foamOutDownload.setOnClickListener(this);
        foamOutEdit.setOnClickListener(this);
        foamOutDeleted.setOnClickListener(this);

        orderLineViewModel.getAllOrderNumbers().observe(this, new Observer<List<OrderNumber>>() {
            @Override
            public void onChanged(List<OrderNumber> orderNumbers) {

                if (orderNumbers != null) {

                    foamInSpinnerItems.clear();

                    for (OrderNumber number : orderNumbers) {
                        String num = String.valueOf(number.getOrderNumber());
                        foamInSpinnerItems.add(num);
                    }
                }

            }
        });

        orderLineViewModel.getAllWeekNumbers().observe(this, new Observer<List<FoamOutWeekNumber>>() {
            @Override
            public void onChanged(List<FoamOutWeekNumber> foamOutWeekNumbers) {

                if (foamOutWeekNumbers != null){

                    foamOutSpinnerItems.clear();

                    for (FoamOutWeekNumber number: foamOutWeekNumbers){
                        String num = number.getWeekNumber();
                        foamOutSpinnerItems.add(num);
                    }
                }

            }
        });

//        orderLineViewModel.getAllOrderLines().observe(this, new Observer<List<OrderLine>>() {
//            @Override
//            public void onChanged(List<OrderLine> orderLineList) {
//
//            }
//        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_foam_in:

                if (foamInSpinnerItems.size() != 0) {

                    Intent intent = new Intent(MainActivity.this, FoamInActivity.class);
                    intent.putStringArrayListExtra("spinner_list", foamInSpinnerItems);
                    startActivity(intent);

                    break;

                } else {

                    Toast.makeText(this, R.string.no_orders_warning, Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.btn_foam_out:

                if (foamOutSpinnerItems.size() != 0){

                    Intent intent = new Intent(MainActivity.this, FoamOutActivity.class);
                    intent.putStringArrayListExtra("spinner_list", foamOutSpinnerItems);
                    startActivity(intent);
                    break;

                } else {

                    Toast.makeText(this, R.string.txt_no_plans_loaded, Toast.LENGTH_SHORT).show();
                    break;
                }



            case R.id.btn_fbrcs_out:

                Intent intent = new Intent(MainActivity.this, FabricsOutActivity.class);
                startActivity(intent);

                break;

            case R.id.btn_foamin_download:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                    } else {
                        downloadFile(READ_REQUEST_FOAM_IN_CODE);
                    }
                }
                break;

            case R.id.btn_foamin_edit:

                if (foamInSpinnerItems.size() != 0) {

                    Intent intent1 = new Intent(MainActivity.this, FoamInDeletedActivity.class);
                    startActivity(intent1);
                    break;

                } else {
                    Toast.makeText(this, R.string.nothing_to_edit, Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.btn_foamin_deleted:

                if (foamInSpinnerItems.size() != 0) {

                    Intent intent2 = new Intent(MainActivity.this, FoamInToDeBeletedActivity.class);
                    startActivity(intent2);
                    break;

                } else {
                    Toast.makeText(this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.btn_foamout_download:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                    } else {
                        downloadFile(READ_REQUEST_FOAM_OUT_CODE);
                    }
                }
                break;

            case R.id.btn_foamout_edit:
                if (foamOutSpinnerItems.size() != 0) {

                    Intent intent4 = new Intent(MainActivity.this, FoamOutEditActivity.class);
                    startActivity(intent4);
                    break;

                } else {
                    Toast.makeText(this, R.string.no_plan_to_edit, Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.btn_foamout_deleted:

                if (foamOutSpinnerItems.size() != 0) {

                    Intent intent3 = new Intent(MainActivity.this, FoamOutToBeDeletedActivity.class);
                    startActivity(intent3);
                    break;

                } else {
                    Toast.makeText(this, R.string.no_plan_to_delete, Toast.LENGTH_SHORT).show();
                    break;
                }
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                downloadFile();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

    //select file from storage
    public void downloadFile(int requestCode) {
        Intent intentDownload = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentDownload.addCategory(Intent.CATEGORY_OPENABLE);
        intentDownload.setType("text/*");
        startActivityForResult(intentDownload, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_FOAM_IN_CODE && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();

            String fullFilename = getFileName(uri);
            String[] filenameWithPath = fullFilename.split(".txt");
            filename = filenameWithPath[0];

            if (foamInSpinnerItems.contains(filename)) {
                Toast.makeText(this, R.string.txt_order_already_loaded, Toast.LENGTH_SHORT).show();
            } else {
                readText(uri);
            }
        } else if (requestCode == READ_REQUEST_FOAM_OUT_CODE && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();

            String fullFilename = getFileName(uri);
            String[] filenameWithPath = fullFilename.split(".txt");
            foamOutFilename = filenameWithPath[0];

            if (foamOutSpinnerItems.contains(foamOutFilename)) {
                Toast.makeText(this, R.string.txt_list_already_loaded, Toast.LENGTH_SHORT).show();
            } else {
                readFoamOutFile(uri);
            }
        }

    }

    //read content of the file
    public void readText(Uri uri) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {

                String[] row = line.split(",");
                foamInProductCode = row[0];
                quantity = Integer.valueOf(row[1]);
                i++;

                orderLine = new OrderLine(foamInProductCode, quantity, arrivedQuantity, isArrived, Integer.valueOf(filename));
                orderLineViewModel.insert(orderLine);
            }
            br.close();

            orderNumber = new OrderNumber(Integer.valueOf(filename));
            orderLineViewModel.insertNr(orderNumber);
            foamInSpinnerItems.add(String.valueOf(orderNumber));

            Toast.makeText(this, "Tellimus " + filename + " laetud. Ridu kokku: " + i, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void readFoamOutFile(Uri uri) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {

                String[] row = line.split(",");
                foamOutProductCode = row[0];
                foamOutQuantity = Integer.valueOf(row[1]);
                foamOutDate = row[2];
                i++;

                foamOutLine = new FoamOutLine(foamOutProductCode, foamOutQuantity, partialQuantity, foamOutDate, isGivenOut, foamOutFilename);
                orderLineViewModel.insertFoamOutLine(foamOutLine);
            }
            br.close();

            foamOutWeekNr = new FoamOutWeekNumber(foamOutFilename);
            orderLineViewModel.insertFoamOutWeekNumber(foamOutWeekNr);
            foamOutSpinnerItems.add(String.valueOf(foamOutWeekNr));

            Toast.makeText(this, foamOutFilename + " plaan laetud. Ridu kokku: " + i, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
