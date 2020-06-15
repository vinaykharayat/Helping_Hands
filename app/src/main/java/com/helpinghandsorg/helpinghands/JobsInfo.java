package com.helpinghandsorg.helpinghands;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.wasabeef.richeditor.RichEditor;

public class JobsInfo extends AppCompatActivity {
    private String jobID;
    private TextView post, eligibility, agelimit, qualifiction, fees, lastdate, totalpost, process, benefits, salary, company;
    private RichEditor editor;
    private Button saveToDevice;
    private JobDetails jobDetails;
    private ScrollView scrollView;
    private int height, width, STORAGE_PERMISSON_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_info);
        jobID = getIntent().getExtras().getString("jobID");
        post = findViewById(R.id.textViewPostName);
        scrollView = findViewById(R.id.scrollview);
        saveToDevice = findViewById(R.id.buttonSaveToDevice);
        saveToDevice.setVisibility(View.GONE);
        eligibility = findViewById(R.id.textViewEligibility);
        agelimit = findViewById(R.id.textViewAgeLimit);
        qualifiction = findViewById(R.id.textViewQualification);
        fees = findViewById(R.id.textViewFees);
        lastdate = findViewById(R.id.textViewLastDate);
        totalpost = findViewById(R.id.textViewTotalPost);
        //process = findViewById(R.id.textViewProcess);
        benefits = findViewById(R.id.textViewBenefits);
        salary = findViewById(R.id.textViewSalary);
        company = findViewById(R.id.textViewCompany);
        editor = findViewById(R.id.textViewProcess);
        editor.setEditorHeight(400);
        //editor.setPadding(10,10,10,10);

        getJobData(new FirebaseCallback() {
            @Override
            public void Callback(final JobDetails jobDetails) {
                post.setText(jobDetails.getPost());
                eligibility.setText(jobDetails.getEligibility());
                agelimit.setText(jobDetails.getAgelimit());
                qualifiction.setText(jobDetails.getQualifiction());
                fees.setText(jobDetails.getFees());
                lastdate.setText(jobDetails.getLastdate());
                totalpost.setText(jobDetails.getTotalpost());
                //process.setText(jobDetails.getProcess());
                benefits.setText(jobDetails.getBenefits());
                salary.setText(jobDetails.getSalary());
                company.setText(jobDetails.getCompany());
                editor.setHtml(jobDetails.getProcess());
                editor.setInputEnabled(false);
                saveToDevice.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        saveToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(JobsInfo.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    savePdfToDevice();
                    Toast.makeText(JobsInfo.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    requestStoragePermission();
                }
            }
        });

    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to save the file to storage")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(JobsInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSON_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSON_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSON_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this , "Permission Granted!", Toast.LENGTH_SHORT).show();
                savePdfToDevice();

            } else{
                Toast.makeText(this , "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePdfToDevice() {
        height = scrollView.getHeight();
        width = scrollView.getWidth();
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c1 = new Canvas(b);
        scrollView.draw(c1);
        PdfDocument pd = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            pd = new PdfDocument();

            PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(width, height, 1).create();
            PdfDocument.Page p = pd.startPage(pi);
            Canvas c = p.getCanvas();
            c.drawBitmap(b, 0, 0, new Paint());
            pd.finishPage(p);

            try {
                //make sure you have asked for storage permission before this
                //File f = new File(Environment.getExternalStorageDirectory() + File.separator + jobDetails.getPost() + "helping_hands.pdf");
                /*File path = new File(getFilesDir(), "Helping Hands Jobs");
                path.mkdir();
                File f = new File(path + File.separator + jobDetails.getPost() + "helping_hands.pdf");*/
                Log.d("Savefile", Environment.getExternalStorageState());
                File f = new File(Environment.getExternalStorageDirectory() + "/"+"HelpingHands");
                Log.d("Savefile", String.valueOf(f));

                if(!f.exists()){
                    boolean test = f.mkdir();
                    Log.d("Savefile", String.valueOf(test));
                }
                File outputfile = new File(f , jobDetails.getPost() + ".pdf");
                Log.d("Savefile", outputfile.toString());


                pd.writeTo(new FileOutputStream(outputfile));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            pd.close();
        }
    }

    private interface FirebaseCallback {
        void Callback(JobDetails jobDetails);
    }

    private void getJobData(final FirebaseCallback firebaseCallback) {
        jobDetails = new JobDetails();
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs");
        jobRef.child(jobID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobDetails = dataSnapshot.getValue(JobDetails.class);
                firebaseCallback.Callback(jobDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
