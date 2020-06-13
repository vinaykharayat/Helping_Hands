package com.helpinghandsorg.helpinghands.ui.profile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.helpinghandsorg.helpinghands.LoginActivity;
import com.helpinghandsorg.helpinghands.MainActivity;
import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.Volunteer;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class profile extends Fragment {

    private ProfileViewModel mViewModel = new ProfileViewModel();
    private Volunteer volunteer = new Volunteer();
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private CircleImageView mProfilePicture;
    private String mProfilePictureUrl;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private Task urlTask;

    public static profile newInstance() {
        return new profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logoutButton = view.findViewById(R.id.buttonProfileLogout);
        mProfilePicture = view.findViewById(R.id.imageViewAvatar);
        final TextView mTextViewEmail = view.findViewById(R.id.textViewProfileEmail);
        final TextView mTextViewName = view.findViewById(R.id.textViewProfileFullName);
        final TextView mTextViewGender = view.findViewById(R.id.textViewProfileGender);
        final TextView mTextViewTaskAlloted = view.findViewById(R.id.textViewProfileTask);
        final TextView mTextViewTaskFinsihed = view.findViewById(R.id.textViewProfileTaskFinished);
        final TextView mTextViewDesignation = view.findViewById(R.id.textViewDesignation);
        final TextView mTextViewJoiningDate = view.findViewById(R.id.textViewProfileJoiningDate);
        final ProgressBar mProgressBar = view.findViewById(R.id.progressBar3);
        final String[] chooseImageOptionList = getActivity().getResources().getStringArray(R.array.profile_picture_options);

        //sets path of database to Volunteer/UID
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Volunteer").child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Select Your Choice")
                        .setItems(chooseImageOptionList, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        openFileChooser();
                                        break;
                                    case 1:
                                        removeProfilePicture();
                                        break;

                                }
                            }
                        }).show();
            }
        });

        setUserProfile(new FirebaseCallBack() {
            @Override
            public void Callback(Volunteer data) {
                mTextViewEmail.setText(data.getEmail());
                mTextViewName.setText(data.getFullName());
                mTextViewGender.setText(data.getGender());
                mTextViewTaskAlloted.setText(String.valueOf(data.getTaskAlloted()));
                mTextViewTaskFinsihed.setText(String.valueOf(data.getTaskFinished()));
                mTextViewDesignation.setText(String.valueOf(data.getDesignation()));
                mTextViewJoiningDate.setText(String.valueOf(data.getJoiningDate()));
                if(!data.getProfilePicUrl().equals("null")){
                    Picasso.get().load(data.getProfilePicUrl())
                            .fit()
                            .into(mProfilePicture);
                }else {
                    mProfilePicture.setImageResource(R.drawable.avatar_male);
                }
                mProgressBar.setVisibility(View.GONE);
            }
        });
        //TODO: Set this list to appropriate views
        //volunteer = mViewModel.getUserProfile().getValue();


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setMessage("You will be logged out permanantly")
                        .setTitle(R.string.confirm_text)
                        .setPositiveButton(R.string.positive_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                try {
                                    getActivity().finishAffinity();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.negative_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        Button backButton = view.findViewById(R.id.button_Back);
        backButton.setVisibility(View.GONE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
                try {
                    finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    //Removes profile picture from database
    private void removeProfilePicture() {
        FirebaseDatabase.getInstance().getReference().child("Volunteer").child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("profilePicUrl")
                .setValue("null");
        FirebaseStorage.getInstance().getReference()
                .child("Volunteer")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .child("profile_picture")
                .delete();
    }

    //Choose images from device
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    //THis method will be called when we picked our file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null){
            //Sets the path in storage to "Volunteer/EmailofUser/profile_picture"
            mStorageRef = FirebaseStorage.getInstance().getReference("Volunteer")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .child("profile_picture");

            //picked image from storage and put it into mImageUri
            mImageUri = data.getData();

            //Load Image to imageview avatar
            Picasso.get()
                    .load(mImageUri)
                    .fit()
                    .into(mProfilePicture);

            //puts file name as system_time_in_milliseconds.jpg
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+ "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            urlTask.continueWith(new Continuation() {
                                @Override
                                public Object then(@NonNull Task task) throws Exception {
                                    mProfilePictureUrl = task.getResult().toString();
                                    volunteer.setProfilePicUrl(mProfilePictureUrl);
                                    mDatabaseRef.setValue(volunteer);
                                    return volunteer;
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }
    }

    //Fetches data from database
    public void setUserProfile(final FirebaseCallBack firebaseCallBack){
        FirebaseAuth mAuth;
        DatabaseReference dbRef;
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        String uid;
        final DatabaseReference volunteerRef;
        volunteerRef = dbRef.child("Volunteer").child("Member");
        uid = mAuth.getCurrentUser().getUid();

        volunteerRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                volunteer.setFullName((String) dataSnapshot.child("fullName").getValue());
                volunteer.setAdmin((String) dataSnapshot.child("admin").getValue());
                volunteer.setEmail((String) dataSnapshot.child("email").getValue());
                volunteer.setGender((String) dataSnapshot.child("gender").getValue());
                volunteer.setDesignation((String) dataSnapshot.child("designation").getValue());
                volunteer.setJoiningDate((String) dataSnapshot.child("joiningDate").getValue());
                volunteer.setTaskAlloted((Long) dataSnapshot.child("taskAlloted").getValue());
                volunteer.setTaskFinished((Long) dataSnapshot.child("taskFinished").getValue());
                volunteer.setRating((Long) dataSnapshot.child("rating").getValue());
                volunteer.setId((String) dataSnapshot.child("id").getValue());
                volunteer.setProfilePicUrl((String) dataSnapshot.child("profilePicUrl").getValue());
                firebaseCallBack.Callback(volunteer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LoginActivity", "dberror: " + databaseError.getMessage());
            }
        });
    }

    public interface FirebaseCallBack {
        void Callback(Volunteer data);
    }
}

