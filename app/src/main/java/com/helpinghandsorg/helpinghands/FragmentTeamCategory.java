package com.helpinghandsorg.helpinghands;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTeamCategory extends Fragment {

    public FragmentTeamCategory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button showAllMembers = getView().findViewById(R.id.button_show_all_members);
        CardView Team1 = getView().findViewById(R.id.cardView_designingTeam);
        CardView Team2 = getView().findViewById(R.id.cardView_webTeam);
        CardView Team3 = getView().findViewById(R.id.cardView_seoTeam);
        CardView Team4 = getView().findViewById(R.id.cardView_appTeam);
        CardView Team5 = getView().findViewById(R.id.cardView_surveyTeam);
        CardView Team6 = getView().findViewById(R.id.cardView_projectTeam);

        showAllMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentTeamCategory.this)
                        .navigate(R.id.action_fragmentTeamCategory_to_allMembersList);
            }
        });
        Team1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentTeamCategory.this)
                        .navigate(R.id.action_fragmentTeamCategory_to_designingTeam);
            }
        });
        Team2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentTeamCategory.this)
                        .navigate(R.id.action_fragmentTeamCategory_to_webTeam);
            }
        });
        Team3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentTeamCategory.this)
                        .navigate(R.id.action_fragmentTeamCategory_to_seoTeam);
            }
        });
        Team4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentTeamCategory.this)
                        .navigate(R.id.action_fragmentTeamCategory_to_appTeam);
            }
        });
        Team5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentTeamCategory.this)
                        .navigate(R.id.action_fragmentTeamCategory_to_surveyTeam);
            }
        });
        Team6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentTeamCategory.this)
                        .navigate(R.id.action_fragmentTeamCategory_to_projectTeam);
            }
        });
    }
}
