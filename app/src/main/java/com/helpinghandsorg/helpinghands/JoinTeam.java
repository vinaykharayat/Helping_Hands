package com.helpinghandsorg.helpinghands;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


/**
 * A simple {@link Fragment} subclass.
 */
public class JoinTeam extends Fragment {

    private EditText[] ans = new EditText[7];
    private String[] interviewAnswers = new String[7];
    private Button submitAnswers, editAnswers;
    private RelativeLayout relativeLayout;
    private BlurView blurView;
    private ScrollView scrollView;
    View view;

    public JoinTeam() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] interViewQuestions = getActivity().getResources().getStringArray(R.array.interview_questions);


        DatabaseReference AnsRef = FirebaseDatabase.getInstance().getReference("interViewAnswers")
                .child(FirebaseAuth.getInstance().getUid());
        AnsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(String.valueOf(6)).getValue() != null) {
                    for (int i = 0; i < 7; i++) {
                        ans[i].setText(dataSnapshot.child(String.valueOf(i)).getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextView qs1 = view.findViewById(R.id.textViewQs1);
        qs1.setText(interViewQuestions[0]);

        TextView qs2 = view.findViewById(R.id.textViewQs2);
        qs2.setText(interViewQuestions[1]);

        TextView qs3 = view.findViewById(R.id.textViewQs3);
        qs3.setText(interViewQuestions[2]);

        TextView qs4 = view.findViewById(R.id.textViewQs4);
        qs4.setText(interViewQuestions[3]);

        TextView qs5 = view.findViewById(R.id.textViewQs5);
        qs5.setText(interViewQuestions[4]);

        TextView qs6 = view.findViewById(R.id.textViewQs6);
        qs6.setText(interViewQuestions[5]);

        TextView qs7 = view.findViewById(R.id.textViewQs7);
        qs7.setText(interViewQuestions[6]);

        ans[0] = view.findViewById(R.id.editTextAns1);
        //interviewAnswers[0]=ans[0].getText().toString().trim();

        ans[1] = view.findViewById(R.id.editTextAns2);
        //interviewAnswers[1]=ans[1].getText().toString().trim();

        ans[2] = view.findViewById(R.id.editTextAns3);
        //interviewAnswers[2]=ans[2].getText().toString().trim();

        ans[3] = view.findViewById(R.id.editTextAns4);
        //interviewAnswers[3]=ans[3].getText().toString().trim();

        ans[4] = view.findViewById(R.id.editTextAns5);
        //interviewAnswers[4]=ans[4].getText().toString().trim();

        ans[5] = view.findViewById(R.id.editTextAns6);
        //interviewAnswers[5]=ans[5].getText().toString().trim();

        ans[6] = view.findViewById(R.id.editTextAns7);
        //interviewAnswers[6]=ans[6].getText().toString().trim();
        submitAnswers = view.findViewById(R.id.buttonSubmit);
        editAnswers = view.findViewById(R.id.editAnswers);
        blurView = view.findViewById(R.id.blurView);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        scrollView = view.findViewById(R.id.scrollview);
        submitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAnswers()) {
                    submitAnswersToFirebase(interviewAnswers);
                }
            }
        });

        editAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurView.setBlurEnabled(false);
                relativeLayout.setVisibility(View.GONE);
            }
        });

    }

    private boolean checkAnswers(){
        for (int i = 0; i < interviewAnswers.length; i++) {
            interviewAnswers[i] = ans[i].getText().toString().trim();
            if (TextUtils.isEmpty(interviewAnswers[i])) {
                ans[i].setError("This field must not be empty");
                ans[i].requestFocus();
                return false;
            }
        }
        return true;
    }

    private void blurBackground() {
        float radius = 22f;

        View decorView = getActivity().getWindow().getDecorView();

        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getContext()))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

    private void submitAnswersToFirebase(String[] interviewAnswers) {
        HashMap<String, Object> hashMap = new HashMap<>();
        DatabaseReference AnsRef = FirebaseDatabase.getInstance().getReference("interViewAnswers")
                .child(FirebaseAuth.getInstance().getUid());
        for (int i = 0; i < interviewAnswers.length; i++) {
            hashMap.put(String.valueOf(i), interviewAnswers[i]);
        }
        AnsRef.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(getView(), "Answers Submitted Successfully", BaseTransientBottomBar.LENGTH_LONG).show();
                relativeLayout.setVisibility(View.VISIBLE);
                blurBackground();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_team, container, false);
    }
}
