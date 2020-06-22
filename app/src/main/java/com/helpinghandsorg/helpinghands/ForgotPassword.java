package com.helpinghandsorg.helpinghands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class ForgotPassword extends AppCompatActivity {

    private Button resetButton;
    private TextInputLayout email;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private BlurView blurView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetButton = findViewById(R.id.buttonResetPassword);
        email = findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        blurView = findViewById(R.id.blurView);
        blurBackground();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
                dialog = builder.show();
                try {
                    mAuth.sendPasswordResetEmail(email.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                            dialog.dismiss();
                            finishAffinity();
                        }
                    });
                }catch (Exception e){
                    new AlertDialog.Builder(ForgotPassword.this)
                            .setTitle("Oops!")
                            .setMessage(e.getMessage())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    email.requestFocus();
                                }
                            }).show();
                }
            }
        });
    }

    private void blurBackground() {
        float radius = 22f;

        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }
}
