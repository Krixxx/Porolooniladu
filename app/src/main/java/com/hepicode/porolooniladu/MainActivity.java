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
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hepicode.porolooniladu.model.OrderLine;
import com.hepicode.porolooniladu.model.OrderLineViewModel;
import com.hepicode.porolooniladu.model.OrderNumber;
import com.hepicode.porolooniladu.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_REQUEST_CODE = 42;
    public static final int DELETE_REQUEST_CODE = 55;
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    Button foamInButton, foamOutButton, fabricOutButton;
    ImageButton foamInDownload, foamInDeleted, foamInEdit;
    private int quantity, arrivedQuantity = 0, isArrived = 0;
    private String productCode, filename;
    private OrderLineViewModel orderLineViewModel;
    private OrderLine orderLine;
    private OrderNumber orderNumber;
    private ArrayList<String> spinnerItems = new ArrayList<>();
    private int firstOrderNum;

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

        foamInButton.setOnClickListener(this);
        foamOutButton.setOnClickListener(this);
        fabricOutButton.setOnClickListener(this);
        foamInDownload.setOnClickListener(this);
        foamInEdit.setOnClickListener(this);
        foamInDeleted.setOnClickListener(this);

        orderLineViewModel.getAllOrderNumbers().observe(this, new Observer<List<OrderNumber>>() {
            @Override
            public void onChanged(List<OrderNumber> orderNumbers) {

                if (orderNumbers != null) {

                    spinnerItems.clear();

                    for (OrderNumber number : orderNumbers) {
                        String num = String.valueOf(number.getOrderNumber());
                        spinnerItems.add(num);
                    }
                }

            }
        });

        orderLineViewModel.getAllOrderLines().observe(this, new Observer<List<OrderLine>>() {
            @Override
            public void onChanged(List<OrderLine> orderLineList) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_foam_in:

                if (spinnerItems.size() != 0) {

                    Intent intent = new Intent(MainActivity.this, FoamInActivity.class);
                    intent.putStringArrayListExtra("spinner_list", spinnerItems);
                    startActivity(intent);
                    break;

                } else {

                    Toast.makeText(this, R.string.no_orders_warning, Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.btn_foam_out:

                Toast.makeText(this, R.string.toast_nothing_to_show, Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_fbrcs_out:

                Toast.makeText(this, R.string.toast_nothing_to_show, Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_foamin_download:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                    } else {
                        downloadFile();
                    }
                }
                break;

            case R.id.btn_foamin_edit:

                if (spinnerItems.size() != 0) {

                    Intent intent1 = new Intent(MainActivity.this, FoamInDeletedActivity.class);
                    startActivity(intent1);
                    break;

                } else {
                    Toast.makeText(this, R.string.nothing_to_edit, Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.btn_foamin_deleted:

                if (spinnerItems.size() != 0) {

                    Intent intent2 = new Intent(MainActivity.this, FoamInToDeBeletedActivity.class);
                    startActivity(intent2);
                    break;

                } else {
                    Toast.makeText(this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
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
                downloadFile();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

    //select file from storage
    public void downloadFile() {
        Intent intentDownload = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentDownload.addCategory(Intent.CATEGORY_OPENABLE);
        intentDownload.setType("text/*");
        startActivityForResult(intentDownload, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

                Uri uri = data.getData();

                String fullFilename = getFileName(uri);
                String[] filenameWithPath = fullFilename.split(".txt");
                filename = filenameWithPath[0];

                if (spinnerItems.contains(filename)) {
                    Toast.makeText(this, R.string.txt_order_already_loaded, Toast.LENGTH_SHORT).show();
                } else {
                    readText(uri);
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
                productCode = row[0];
                quantity = Integer.valueOf(row[1]);
                i++;

                orderLine = new OrderLine(productCode, quantity, arrivedQuantity, isArrived, Integer.valueOf(filename));
                orderLineViewModel.insert(orderLine);
            }
            br.close();

            orderNumber = new OrderNumber(Integer.valueOf(filename));
            orderLineViewModel.insertNr(orderNumber);
            spinnerItems.add(String.valueOf(orderNumber));

            Toast.makeText(this, "Tellimus " + filename + " laetud. Ridu kokku: " + i, Toast.LENGTH_SHORT).show();

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
