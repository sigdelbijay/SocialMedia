package com.example.socialmedia.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.socialmedia.R;
import com.example.socialmedia.adapter.ProfileViewPagerAdapter;
import com.example.socialmedia.model.User;
import com.example.socialmedia.rest.ApiClient;
import com.example.socialmedia.rest.services.UserInterface;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

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
    String profileUrl, coverUrl;
    ProgressDialog progressDialog;
    int imageUploadType = 0;
    File compressedImageFile;

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
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), 1);
        ViewPagerProfile.setAdapter(profileViewPagerAdapter);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        uid = getIntent().getExtras().getString("uid");
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
            current_state = 5;
            profileOptionBtn.setText("Edit profile");
            loadProfile();
        } else {

        }

        profileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_state == 5) {
                    CharSequence options[] = new CharSequence[]{"Change profile picture", "Change profile cover", "View cover picture", "View profile picture"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setOnDismissListener(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                //change profile picture
                                imageUploadType = 0;
                                ImagePicker.create(ProfileActivity.this)
                                        .folderMode(true)
                                        .single()
                                        .toolbarFolderTitle("Choose a folder")
                                        .toolbarImageTitle("Select a image")
                                        .start();


                            } else if (position == 1) {
                                //"Change cover picture"
                                imageUploadType = 1; //1 if profile
                                ImagePicker.create(ProfileActivity.this)
                                        .folderMode(true)
                                        .single()
                                        .toolbarFolderTitle("Choose a folder")
                                        .toolbarImageTitle("Select a image")
                                        .start();

                            } else if (position == 2) {
                                //"View cover picture"
                            } else {
                                //"View profile picture"
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    private void loadProfile() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call<User> call = userInterface.loadownProfile(params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    profileUrl = response.body().getProfileUrl();
                    coverUrl = response.body().getCoverUrl();
                    collapsingToolbar.setTitle(response.body().getName());

                    if (!profileUrl.isEmpty()) {
                        Picasso.with(ProfileActivity.this).load(profileUrl).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new com.squareup.picasso.Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(profileUrl).into(profileImage);
                            }
                        });
                    }

                    if (!coverUrl.isEmpty()) {
                        Picasso.with(ProfileActivity.this).load(coverUrl).networkPolicy(NetworkPolicy.OFFLINE).into(profileCover, new com.squareup.picasso.Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(coverUrl).into(profileCover);
                            }
                        });
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Something went wrong... Please try again later", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Something went wrong... Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);
            try {
                compressedImageFile = new Compressor(this)
                        .setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));

                uploadFile(compressedImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFile(File compressedImageFile) {
        progressDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("postUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        builder.addFormDataPart("imageUploadType", imageUploadType + "");
        builder.addFormDataPart("image", compressedImageFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), compressedImageFile));

        MultipartBody multipartBody = builder.build();
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Call<Integer> call = userInterface.uploadImage(multipartBody);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                if(response.body() != null && response.body() == 1) {
                    if(imageUploadType == 0) {
                        Picasso.with(ProfileActivity.this).load(compressedImageFile).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new com.squareup.picasso.Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(profileUrl).into(profileImage);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Profile picture changed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Picasso.with(ProfileActivity.this).load(compressedImageFile).networkPolicy(NetworkPolicy.OFFLINE).into(profileCover, new com.squareup.picasso.Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(profileUrl).into(profileCover);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Cover picture changed successfully", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}