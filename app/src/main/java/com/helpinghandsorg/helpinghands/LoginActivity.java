package com.helpinghandsorg.helpinghands;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class LoginActivity extends AppCompatActivity {

    String adminStatus;
    private TextInputLayout editEmail, editPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Volunteer newVolunteer = new Volunteer();
    private AlertDialog dialog;
    private BlurView blurView;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        Button loginButton = findViewById(R.id.buttonLogin);
        Button registerButton = findViewById(R.id.buttonSubmitIssue);
        TextView forgotPassword = findViewById(R.id.textViewForgotPassword);
        editEmail = findViewById(R.id.username);
        editPassword = findViewById(R.id.password);
        blurView = findViewById(R.id.blurView);
        SignInButton signInButtonGoogle = findViewById(R.id.google_sign_in);
        blurBackground();

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
                            dialog = builder.show();
                            loginUser();
                        }
                    });
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
                    dialog = builder.show();
                    loginUser();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder().requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUsingGoogle();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void signInUsingGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                Toast.makeText(LoginActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                FirebaseGoogleAUth(acc);
            }catch (ApiException e){
                Toast.makeText(LoginActivity.this, "ApiEception:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void FirebaseGoogleAUth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                readData(new FirebaseCallBack() {
                    @Override
                    public void Callback(Volunteer data) {
                        try {
                            if (data.isAdmin().equals("true")) {
                                Toast.makeText(LoginActivity.this, "Welcome! " + data.getFullName(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Firebase Google Auth:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new MaterialAlertDialogBuilder(LoginActivity.this)
                        .setTitle("Oops")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blurBackground() {
        float radius = 20f;

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

    private void createUser() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void loginUser() {
        String email = editEmail.getEditText().getText().toString();
        String password = editPassword.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Please enter your name");
            editEmail.requestFocus();
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Please enter your name");
            editPassword.requestFocus();
        }
        if (isUserNameValid(email) && isPasswordValid(password)) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("login", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user, task);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("login", "signInWithEmail:failure", task.getException());
                                }
                            } else {
                                dialog.dismiss();
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setMessage("You can't proceed, please verify email first!")
                                        .setTitle("Email not verifed")
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setPositiveButton("Resend Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("Exit app", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                System.exit(0);
                                            }
                                        })
                                        .setCancelable(false).show();
                            }

                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage(e.getMessage())
                            .setTitle("Oops")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editEmail.requestFocus();
                                }
                            }).show();
                }
            });
        }else{
            new AlertDialog.Builder(LoginActivity.this).setMessage("Incorrect email or password")
                    .setTitle("Error")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog1, int which) {
                            dialog1.dismiss();
                            dialog.dismiss();

                        }
                    }).show();
        }
    }

    private void updateUI(FirebaseUser user, Task task) {
        if (user == null) {
            Toast.makeText(this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            dialog.dismiss();
            startActivity(intent);
            finishAffinity();
        }

        //Update UI with admin activity

        readData(new FirebaseCallBack() {
            @Override
            public void Callback(Volunteer data) {
                try {
                    if (data.isAdmin().equals("true")) {
                        Toast.makeText(LoginActivity.this, "Welcome! " + data.getFullName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });


    }

    //Read data from firebase
    private void readData(final FirebaseCallBack firebaseCallBack) {
        DatabaseReference volunteerRef;
        volunteerRef = dbRef.child("Volunteer").child("Member");
        String uid = mAuth.getCurrentUser().getUid();
        volunteerRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newVolunteer.setFullName((String) dataSnapshot.child("fullName").getValue());
                newVolunteer.setAdmin((String) dataSnapshot.child("admin").getValue());
                newVolunteer.setEmail((String) dataSnapshot.child("email").getValue());
                newVolunteer.setGender((String) dataSnapshot.child("gender").getValue());
                newVolunteer.setDesignation((String) dataSnapshot.child("designation").getValue());
                newVolunteer.setJoiningDate((String) dataSnapshot.child("joiningDate").getValue());
                newVolunteer.setTaskAlloted((Long) dataSnapshot.child("taskAlloted").getValue());
                newVolunteer.setTaskFinished((Long) dataSnapshot.child("taskFinished").getValue());
                newVolunteer.setRating((Long) dataSnapshot.child("rating").getValue());

                firebaseCallBack.Callback(newVolunteer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LoginActivity", "dberror: " + databaseError.getMessage());
            }
        });
    }

    //Used to fetch data outside the onDataChange() method and checks if data is downloaded
    private interface FirebaseCallBack {
        void Callback(Volunteer data);

    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
