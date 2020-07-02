package com.example.socialmedia.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.socialmedia.R;
import com.example.socialmedia.model.PostModel;
import com.example.socialmedia.util.AgoDateParse;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        postModel = Parcels.unwrap(getIntent().getBundleExtra("postBundle").getParcelable("postModel"));
        if(postModel == null) {
            Toast.makeText(FullPostActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }

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
        setData(postModel);
    }

    private void setData(PostModel postModel) {
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
}