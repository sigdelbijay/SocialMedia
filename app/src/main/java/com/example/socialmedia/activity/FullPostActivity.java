package com.example.socialmedia.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.example.socialmedia.R;
import com.example.socialmedia.adapter.PostAdapter;
import com.example.socialmedia.fragment.bottomsheets.CommentBottomSheet;
import com.example.socialmedia.model.PostModel;
import com.example.socialmedia.rest.ApiClient;
import com.example.socialmedia.rest.services.UserInterface;
import com.example.socialmedia.util.AgoDateParse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullPostActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.post_user_image)
    ImageView postUserImage;
    @BindView(R.id.post_user_name)
    TextView postUserName;
    @BindView(R.id.privacy)
    ImageView privacy;
    @BindView(R.id.post_date)
    TextView postDate;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.post_image)
    ImageView postImage;
    @BindView(R.id.top_rel)
    RelativeLayout topRel;
    @BindView(R.id.like_img)
    ImageView likeImg;
    @BindView(R.id.like_txt)
    TextView likeTxt;
    @BindView(R.id.like_section)
    LinearLayout likeSection;
    @BindView(R.id.comment_txt)
    TextView commentTxt;
    @BindView(R.id.comment_section)
    LinearLayout commentSection;
    @BindView(R.id.reaction_card)
    CardView reactionCard;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.comment_send)
    ImageView commentSend;
    @BindView(R.id.comment_send_wrapper)
    RelativeLayout commentSendWrapper;
    @BindView(R.id.comment_bottom_part)
    LinearLayout commentBottomPart;
    @BindView(R.id.top_hide_show)
    RelativeLayout topHideShow;

    PostModel postModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);
        ButterKnife.bind(this);

        boolean isLoadFromNetwork = getIntent().getBundleExtra("postBundle").getBoolean("isLoadFromNetwork", false);
        String postId = getIntent().getBundleExtra("postBundle").getString("postId", "0");
        if(isLoadFromNetwork) {
            getPostDetails(postId);
        } else {
            postModel = Parcels.unwrap(getIntent().getBundleExtra("postBundle").getParcelable("postModel"));
            setData(postModel);
        }
//        if(postModel == null) {
//            Toast.makeText(FullPostActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
//            onBackPressed();
//            finish();
//        }

        //setting the tool with back button
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getPostDetails(String postId) {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String, String > params = new HashMap<String, String>();
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        params.put("postId", postId);
        Call<PostModel> call = userInterface.postdetails(params);
        call.enqueue(new Callback<PostModel>() {
            @Override
            public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                setData(response.body());
            }

            @Override
            public void onFailure(Call<PostModel> call, Throwable t) {
                Toast.makeText(FullPostActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                FullPostActivity.super.onBackPressed();
            }
        });
    }

    private void setData(PostModel postModel) {

        if (postModel.getLiked()) {
            likeImg.setImageResource(R.drawable.icon_like_selected);
        } else {
            likeImg.setImageResource(R.drawable.icon_like);
        }

        if(postModel.getLikeCount().equals(0) || postModel.getLikeCount().equals(1)) {
            likeTxt.setText(postModel.getLikeCount() + " Like");
        } else {
            likeTxt.setText(postModel.getLikeCount() + " Likes");
        }
        if(postModel.getCommentCount().equals(0) || postModel.getCommentCount().equals(1)) {
            commentTxt.setText(postModel.getCommentCount() + " Comment");
        } else {
            commentTxt.setText(postModel.getCommentCount() + " Comments");
        }

        commentSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CommentBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable("postModel", Parcels.wrap(postModel));
                bottomSheetDialogFragment.setArguments(bundle);
                FragmentActivity fragmentActivity = (FragmentActivity) FullPostActivity.this;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "commentFragment");
            }
        });

        likeSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeSection.setEnabled(false);
                if (!postModel.getLiked()) {
                    //like operation here
                    operationLike(postModel);
                    UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
                    Call<Integer> call = userInterface.likeunlike(new PostAdapter.AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), postModel.getId(), postModel.getPostUserId(), "1"));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            likeSection.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            likeSection.setEnabled(true);
//                            operationUnlike(postModel);
//                            Toast.makeText(FullPostActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //unlike operation here
                    operationUnlike(postModel);
                    UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
                    Call<Integer> call = userInterface.likeunlike(new PostAdapter.AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), postModel.getId(), postModel.getPostUserId(), "0"));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            likeSection.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            likeSection.setEnabled(true);
//                            operationLike(postModel);
//                            Toast.makeText(FullPostActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        postUserName.setText(postModel.getName());
        if(!postModel.getUserProfile().isEmpty()) {
            Picasso.with(FullPostActivity.this).load(postModel.getUserProfile()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(postUserImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(FullPostActivity.this).load(postModel.getUserProfile()).placeholder(R.drawable.img_default_user).into(postUserImage);
                }
            });
        }
        if(postModel.getPrivacy().equals("0")) {
            privacy.setImageResource(R.drawable.icon_friends);
        } else if(postModel.getPrivacy().equals("1")) {
            privacy.setImageResource(R.drawable.icon_onlyme);
        } else {
            privacy.setImageResource(R.drawable.icon_public);
        }

        postDate.setText(AgoDateParse.getTimeAgo(postModel.getStatusTime()));
        status.setText(postModel.getPost());
        if(!postModel.getStatusImage().isEmpty()) {
            Picasso.with(FullPostActivity.this).load(postModel.getStatusImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(postImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(FullPostActivity.this).load(postModel.getStatusImage()).placeholder(R.drawable.default_image_placeholder).into(postImage);
                }
            });
        } else {
            postImage.setVisibility(View.GONE);
        }
    }

    private void operationLike(PostModel postModel) {
        likeImg.setImageResource(R.drawable.icon_like_selected);
        int count = postModel.getLikeCount();
        count++;
        if (count == 0 || count == 1) {
            likeTxt.setText(count + " Like");
        } else {
            likeTxt.setText(count + " Likes");
        }

        postModel.setLikeCount(count);
        postModel.setLiked(true);
    }

    private void operationUnlike(PostModel postModel) {
        likeImg.setImageResource(R.drawable.icon_like);
        int count = postModel.getLikeCount();
        count--;
        if (count == 0 || count == 1) {
            likeTxt.setText(count + " Like");
        } else {
            likeTxt.setText(count + " Likes");
        }

        postModel.setLikeCount(count);
        postModel.setLiked(false);
    }
}