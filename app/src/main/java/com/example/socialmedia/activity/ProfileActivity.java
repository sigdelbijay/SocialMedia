package com.example.socialmedia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.socialmedia.R;
import com.example.socialmedia.adapter.ProfileViewPagerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_cover)
    ImageView profileCover;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.profile_option_btn)
    Button profileOptionBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.ViewPager_profile)
    ViewPager ViewPagerProfile;

    ProfileViewPagerAdapter profileViewPagerAdapter;
    String uid = "0";
    int current_state = 0;

    /*
    * 0 = profile still loading
    * 1 = two people are friends(unfriend)
    * 2 = this person has sent friend request to another friend(cancel request)
    * 3 = this person has received friend request from another friend(reject or accept request)
    * 4 = people are known(you can send friend request
    * 5 = own profile
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //hiding status bar, not needed now
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        uid = getIntent().getExtras().getString("uid");

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), 1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        ViewPagerProfile.setAdapter(profileViewPagerAdapter);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
            current_state = 5;
            profileOptionBtn.setText("Edit profile");
        } else {

        }

    }
}