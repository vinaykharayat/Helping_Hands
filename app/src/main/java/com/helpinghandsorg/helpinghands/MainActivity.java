package com.helpinghandsorg.helpinghands;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.ui.profile.profile;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    String userName = "Username";
    String userEmail = "Useremail@email.com";
    private  int destinationId;
    private DrawerLayout drawer;
    private Menu menuOptions;


    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //If current user is null go to login activity
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //Setting text
        // on navigation header


        try {
            readData(new FirebaseCallBack() {
                @Override
                public void Callback(String userNameNew, String emailNew, String imageUrl) {
                    //Setting text on navigation header
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    TextView navUserName = headerView.findViewById(R.id.textViewNav_username);
                    TextView navUserEmail = headerView.findViewById(R.id.textViewNav_useremail);
                    ImageView mNavAvatar = headerView.findViewById(R.id.imageViewProfilePicture);
                    if (!imageUrl.equals("null")) {
                        Picasso.get().load(imageUrl)
                                .fit()
                                .into(mNavAvatar);
                    }
                    navUserName.setText(userNameNew);
                    navUserEmail.setText(emailNew);
                }
            });
        } catch (NullPointerException e) {
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home/*, R.id.nav_contact, R.id.nav_about, R.id.nav_chats, R.id.nav_join_team*/)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //Creates hamburger icon
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        MenuItem shareApp;
        Menu menu = navigationView.getMenu();
        shareApp = menu.findItem(R.id.nav_share);
        shareApp.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), "Share button clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                destinationId = destination.getId();
                Log.d("destinationid", String.valueOf(destinationId));
                Log.d("destinationid", String.valueOf(R.id.profile));
            }
        });

    }



    private void readData(final FirebaseCallBack firebaseCallBack) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference volunteerRef = dbRef.child("Volunteer").child("Member").child(currentUser.getUid());
        volunteerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userEmail = (String) dataSnapshot.child("email").getValue();
                String userName = (String) dataSnapshot.child("fullName").getValue();
                String imageUrl = (String) dataSnapshot.child("profilePicUrl").getValue();
                firebaseCallBack.Callback(userName, userEmail, imageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallBack {
        void Callback(String userName, String email, String imageUrl);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is already signed in
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav__drawer, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if(destinationId == R.id.profile){
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.action_profile_to_nav_home);

        }else if(destinationId == R.id.fragmentTaskDetail2) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.action_fragmentTaskDetail2_to_nav_home);
        }else{
            new AlertDialog.Builder(this).setTitle("Quit Application")
                    .setMessage(R.string.confirm_text)
                    .setPositiveButton(R.string.positive_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).setNegativeButton(R.string.negative_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }
}
