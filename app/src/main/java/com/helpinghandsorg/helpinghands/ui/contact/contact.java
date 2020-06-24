package com.helpinghandsorg.helpinghands.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.helpinghandsorg.helpinghands.R;

public class contact extends Fragment {

    private ContactViewModel mViewModel;
    private ImageView
            mFacebookButton,
            mInstagramButton,
            mLinkedinButton,
            mWhatsappButton,
            mWebsiteButton,
            mGmailButton;

    public static contact newInstance() {
        return new contact();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFacebookButton = view.findViewById(R.id.imageViewFacebook);
        mInstagramButton = view.findViewById(R.id.imageViewInstagram);
        mLinkedinButton = view.findViewById(R.id.imageViewLinkedin);
        mWhatsappButton = view.findViewById(R.id.imageViewWhatsapp);
        mWebsiteButton = view.findViewById(R.id.imageViewWebsite);
        mGmailButton = view.findViewById(R.id.imageViewGmail);

        mFacebookButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent facebookIntent = openfacebook(getContext());
                startActivity(facebookIntent);
            }
        });

        mInstagramButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent instagramIntent = openInstagram(getContext());
                startActivity(instagramIntent);
            }
        });
        mGmailButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent gmailIntent = openGmail();
                if(gmailIntent == null){
                    Toast.makeText(getContext(),"Gmail App is not installed", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(gmailIntent);
                }
            }
        });

        mWebsiteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent websiteIntent = openWebsite(getContext());
                startActivity(websiteIntent);
            }
        });

        mWhatsappButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone="+"+917906980871";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mLinkedinButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent linkedinintent = openLinkedin(getContext());
                startActivity(linkedinintent);
            }
        });

    }

    public static Intent openGmail(){
        try{
            //context.getPackageManager().getPackageInfo("com.google.android.gm", 0);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:www.werrhelpinghands@gmail.com"));
            return intent;
        }catch (Exception e){
            return null;
        }
    }

    public static Intent openWebsite(Context context){
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://werrhelpinghands.githib.io"));
    }

    public static Intent openInstagram(Context context){
        try{
            context.getPackageManager().getPackageInfo("com.instagram.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/we_r_helping_hands"));
        }catch (Exception e){
            return new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/we_r_helping_hands"));
        }
    }

    public static Intent openLinkedin(Context context){
        try{
            context.getPackageManager().getPackageInfo("com.linked.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://we-r-helping-hands"));
        }catch (Exception e){
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/we-r-helping-hands"));
        }
    }

    public static Intent openfacebook(Context context){
        try{
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1694445080614369"));
        }catch (Exception e){
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/helpinghands4world"));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        // TODO: Use the ViewModel
    }

}
