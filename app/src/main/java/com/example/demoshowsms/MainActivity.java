package com.example.demoshowsms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvSms;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSms = findViewById(R.id.tv);
        btnRetrieve = findViewById(R.id.btnRetrieve);
        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);
                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(uri, reqCols, null, null, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSms.setText(smsBody);
            }

        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnRetrieve.performClick();
                } else {
                    Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
