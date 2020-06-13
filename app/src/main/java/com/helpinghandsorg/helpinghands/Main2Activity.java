package com.helpinghandsorg.helpinghands;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.helpinghandsorg.helpinghands.teams.Teams;

public class Main2Activity extends AppCompatActivity {
    TabItem tabTasks,tabJobs,tabChats;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Adding fragments to tabs
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager= findViewById(R.id.view_pager);

        ViewpagerAdaptor viewpagerAdaptor = new ViewpagerAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(viewpagerAdaptor);
        viewpagerAdaptor.addFragment(new FirstFragment(R.layout.content_main2), "Tasks");
        viewpagerAdaptor.addFragment(new JobsFragment(R.layout.content_main2), "Jobs");
        viewpagerAdaptor.addFragment(new MessageList(), "Chats");

        ImageButton imageButton = findViewById(R.id.openUserListButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, Teams.class));
            }
        });

        viewPager.setAdapter(viewpagerAdaptor);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {
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
