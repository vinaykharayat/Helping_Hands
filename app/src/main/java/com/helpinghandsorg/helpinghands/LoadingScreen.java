package com.helpinghandsorg.helpinghands;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoadingScreen extends AppCompatActivity {

    private Animation topToBottom, bottomToTop;
    private ImageView logo;
    private TextView slogan;
    private Button startAppButton;
    private long SPLASH_SCREEN_DURATION;
    private FirebaseAuth mAuth;
    private  AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
        dialog = builder.show();
        try{
            mAuth.getCurrentUser().getUid();
            readData(new FirebaseCallBack() {
                @Override
                public void Callback(String adminStatus) {
                    try {
                        if (adminStatus.equals("true")) {
                            //Update UI with admin activity
                            Intent intent = new Intent(LoadingScreen.this, Main2Activity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else if (adminStatus.equals("false")) {
                            //Update UI with user activity
                            Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }catch (NullPointerException e){
                        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
                            startActivity(new Intent(LoadingScreen.this, LoginActivity.class));
                            finishAffinity();
                        }
                    }
                }
            });
        }catch (NullPointerException e) {
            dialog.dismiss();
            setContentView(R.layout.activity_loading_screen);
            //hides status bar
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //Assigning animation to variables
            topToBottom = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_animation);
            bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_animation);
            logo = findViewById(R.id.imageViewLogo);
            slogan = findViewById(R.id.textViewSlogan);
            startAppButton = findViewById(R.id.buttonEnterApp);
            logo.setAnimation(topToBottom);
            slogan.setAnimation(bottomToTop);
            SPLASH_SCREEN_DURATION = bottomToTop.getDuration() + 1000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAppButton.setVisibility(View.VISIBLE);
                }
            }, SPLASH_SCREEN_DURATION);
            startAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser cu = FirebaseAuth.getInstance().getCurrentUser();
                    if (cu == null) {
                        startActivity(new Intent(LoadingScreen.this, RegisterActivity.class));
                        finishAffinity();
                    }
                    startActivity(new Intent(LoadingScreen.this, MainActivity.class));
                    finishAffinity();
                }
            });
        }
    }

    //Read data from firebase
    private void readData(final FirebaseCallBack firebaseCallBack) {
        DatabaseReference volunteerRef = FirebaseDatabase.getInstance().getReference().child("Volunteer").child("Member");
        String uid = mAuth.getCurrentUser().getUid();
        volunteerRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String adminStatus = (String) dataSnapshot.child("admin").getValue();
                firebaseCallBack.Callback(adminStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("dberror", databaseError.getMessage());
            }
        });
    }

    //Used to fetch data outside the onDataChange() method and checks if data is downloaded
    private interface FirebaseCallBack {
        void Callback(String adminStatus);
    }
}
