package com.example.socialmedia.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
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
     * 4 = people are unknown(you can send friend request
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
            loadOtherProfile();
        }

        profileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("current_state", current_state+"");
                profileOptionBtn.setEnabled(false);
                if (current_state == 5) {
                    CharSequence options[] = new CharSequence[]{"Change profile picture", "Change profile cover", "View profile picture", "View cover picture"};
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
                                //"View profile picture"
                                viewFullImage(profileImage, profileUrl);

                            } else {
                                //"View cover picture"
                                viewFullImage(profileCover, coverUrl);

                            }
                        }
                    });
                    builder.show();
                } else if(current_state == 4) {
                    CharSequence options[] = new CharSequence[]{"Send Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setOnDismissListener(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                profileOptionBtn.setText("Processing...");
                                performAction(current_state);
                            }
                        }
                    });
                    builder.show();
                } else if(current_state == 2) {
                    CharSequence options[] = new CharSequence[]{"Cancel Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setOnDismissListener(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                profileOptionBtn.setText("Processing...");
                                performAction(current_state);
                            }
                        }
                    });
                    builder.show();
                } else if(current_state == 3) {
                    CharSequence options[] = new CharSequence[]{"Accept Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setOnDismissListener(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                profileOptionBtn.setText("Processing...");
                                performAction(current_state);
                            }
                        }
                    });
                    builder.show();
                } else if(current_state == 1) {
                    CharSequence options[] = new CharSequence[]{"Unfriend"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setOnDismissListener(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                profileOptionBtn.setText("Processing...");
                                performAction(current_state);
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    private void performAction(int i) {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Call<Integer> call = userInterface.performAction(new performAction(i+"", FirebaseAuth.getInstance().getCurrentUser().getUid(), uid));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                profileOptionBtn.setEnabled(true);
                if(response.body()==1) {
                    if(i==4) {
                        current_state = 2;
                        profileOptionBtn.setText("Request Sent");
                        Toast.makeText(ProfileActivity.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                    } else if(i == 2) {
                        current_state = 4;
                        profileOptionBtn.setText("Send Request");
                        Toast.makeText(ProfileActivity.this, "Request cancelled successfully", Toast.LENGTH_SHORT).show();
                    } else if(i == 3) {
                        current_state = 1;
                        profileOptionBtn.setText("Friends");
                        Toast.makeText(ProfileActivity.this, "You are friends in Social Media now !", Toast.LENGTH_SHORT).show();
                    }else if(i == 1) {
                        current_state = 4;
                        profileOptionBtn.setText("Send Request");
                        Toast.makeText(ProfileActivity.this, "You are no more friends", Toast.LENGTH_SHORT).show();
                    } else {
                        profileOptionBtn.setEnabled(false);
                        profileOptionBtn.setText("Error...");
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    private void viewFullImage(View view, String url) {
        Intent intent = new Intent(ProfileActivity.this, FullImageActivity.class);
        intent.putExtra("imageUrl", url);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //for transition
            Pair pairs[] = new Pair[1];
            pairs[0] = new Pair<View, String>(view, "shared");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

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
                    showUserData(response.body());
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

    private void loadOtherProfile() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        params.put("profileId", uid);
        Call<User> call = userInterface.loadOtherProfile(params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    showUserData(response.body());
                    if(response.body().getState().equalsIgnoreCase("1")) {
                        current_state = 1;
                        profileOptionBtn.setText("Friends");
                    } else if(response.body().getState().equalsIgnoreCase("2")) {
                        current_state = 2;
                        profileOptionBtn.setText("Cancel Request");
                    } else if(response.body().getState().equalsIgnoreCase("3")) {
                        current_state = 3;
                        profileOptionBtn.setText("Accept Request");
                    } else if(response.body().getState().equalsIgnoreCase("4")) {
                        current_state = 4;
                        profileOptionBtn.setText("Send Request");
                    } else {
                        current_state = 0;
                        profileOptionBtn.setText("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Something went wrong... Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserData(User user) {

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), 1, user.getId(), user.getState());
        ViewPagerProfile.setAdapter(profileViewPagerAdapter);

        profileUrl = user.getProfileUrl();
        coverUrl = user.getCoverUrl();
        collapsingToolbar.setTitle(user.getName());

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

        //add click event to images once images are loaded
        addImageCoverClick();
    }

    private void addImageCoverClick() {
        profileCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullImage(profileCover, coverUrl);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullImage(profileImage, profileUrl);
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        profileOptionBtn.setEnabled(true);
    }

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

    public static class performAction {
        String operationType, userId, profileId;

        public performAction(String operationType, String userId, String profileId) {
            this.operationType = operationType;
            this.userId = userId;
            this.profileId = profileId;
        }
    }
}