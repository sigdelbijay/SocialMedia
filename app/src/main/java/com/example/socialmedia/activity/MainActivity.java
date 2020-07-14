package com.example.socialmedia.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.socialmedia.R;
import com.example.socialmedia.fragment.FriendsFragment;
import com.example.socialmedia.fragment.NewsFeedFragment;
import com.example.socialmedia.fragment.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    NewsFeedFragment newsFeedFragment;
    NotificationFragment notificationFragment;
    FriendsFragment friendsFragment;
    @BindView(R.id.search)
    ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigation.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigation.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigation.setItemTextColor(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        bottomNavigation.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));

        newsFeedFragment = new NewsFeedFragment();
        notificationFragment = new NotificationFragment();
        friendsFragment = new FriendsFragment();

        //directing to proper fragment if coming from notification
        Bundle bundle = getIntent().getExtras();
        String isFromNotification = "false";
        if(bundle != null) {
            isFromNotification = getIntent().getExtras().getString("isFromNotification", "false");
            if(isFromNotification.equals("true")) {
                bottomNavigation.getMenu().findItem(R.id.profile_notification).setChecked(true);
                setFragment(notificationFragment);
            } else {
                setFragment(newsFeedFragment);
            }
        } else {
            setFragment(newsFeedFragment);
        }
        
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.newsfeed_fragment:
                        setFragment(newsFeedFragment);
                        return true;
                    case R.id.profile_fragment:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        return false;
                    case R.id.profile_notification:
                        setFragment(notificationFragment);
                        return true;
                    case R.id.profile_friends:
                        setFragment(friendsFragment);
                        return true;
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.search)
    public void onViewClicked() {
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
    }
}