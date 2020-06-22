package com.helpinghandsorg.helpinghands;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    CardView credits,appversion;
    TextView appVersionText, creditsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        credits = findViewById(R.id.cardview_credits);
        appversion = findViewById(R.id.cardview_appversion);
        appVersionText = findViewById(R.id.textView14);
        creditsText = findViewById(R.id.textView144);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionText.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        creditsText.setText("Information about credits.");
        appversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppversionDialog(v);
            }
        });
        
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreditsDialog(v);
            }
        });
    }

    private void openCreditsDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.creditsdialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openAppversionDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.appversiondialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView appVersionD = dialogView.findViewById(R.id.textView16);
        TextView developerInfoD = dialogView.findViewById(R.id.textView1336);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionD.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        developerInfoD.setText("Vinay Kharayat");
    }
}
