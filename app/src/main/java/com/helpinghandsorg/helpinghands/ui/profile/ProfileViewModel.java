package com.helpinghandsorg.helpinghands.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.helpinghandsorg.helpinghands.Volunteer;
import com.helpinghandsorg.helpinghands.repositories.UserprofileRepositories;

public class ProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<Volunteer> userProfileDetails;
    private UserprofileRepositories mRepo = new UserprofileRepositories();

    public void setUserProfileDetails(){
        if(userProfileDetails!=null){
            return;
        }
        //Save data of Volunteer to MultableData<VOlunteer>
        userProfileDetails = mRepo.getUserprofile();
    }

    public LiveData<Volunteer> getUserProfile(){
        Log.d("Profile", " Insdie viewModel getUserProfile Called"+ userProfileDetails.toString());
        return userProfileDetails;
    }



}
