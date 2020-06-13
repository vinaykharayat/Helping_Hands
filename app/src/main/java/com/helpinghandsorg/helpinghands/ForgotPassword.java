package com.helpinghandsorg.helpinghands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Button resetButton;
    private EditText email;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetButton = findViewById(R.id.buttonResetPassword);
        email = findViewById(R.id.editTextEmail);
        mAuth = FirebaseAuth.getInstance();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
                dialog = builder.show();
                try {
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
