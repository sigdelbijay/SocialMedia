package com.example.socialmedia.fragment.bottomsheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.adapter.CommentAdapter;
import com.example.socialmedia.adapter.PostAdapter;
import com.example.socialmedia.model.CommentModel;
import com.example.socialmedia.model.PostModel;
import com.example.socialmedia.model.User;
import com.example.socialmedia.rest.ApiClient;
import com.example.socialmedia.rest.services.UserInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentBottomSheet extends BottomSheetDialogFragment {

    Context context;
    @BindView(R.id.comments_txt)
    TextView commentsTxt;
    @BindView(R.id.top_section)
    LinearLayout topSection;
    @BindView(R.id.comment_recy)
    RecyclerView commentRecy;
    @BindView(R.id.comment_edittext)
    EditText commentEdittext;
    @BindView(R.id.comment_send)
    ImageView commentSend;
    @BindView(R.id.comment_send_wrapper)
    RelativeLayout commentSendWrapper;
    @BindView(R.id.comment_top_wrapper)
    LinearLayout commentTopWrapper;
    Unbinder unbinder;

    boolean isFlagZero = true;
    PostModel postModel;
    CommentAdapter commentAdapter;
    List<CommentModel.Result> results = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(context, R.layout.bottom_sheet_layout, null);
        postModel = Parcels.unwrap(getFragmentManager().findFragmentByTag("commentFragment").getArguments().getParcelable("postModel"));
        unbinder = ButterKnife.bind(this, view);
        dialog.setContentView(view);

        //to make transaparent and show rounded corner in comments top section
        View view1 = (View) view.getParent();
        view1.setBackgroundColor(Color.TRANSPARENT);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog dialog1 = (BottomSheetDialog) dialog;
                FrameLayout bottomsheet = dialog1.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomsheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        commentAdapter = new CommentAdapter(context, results, postModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        commentRecy.setLayoutManager(linearLayoutManager);
        commentRecy.setAdapter(commentAdapter);

        retrieveComments();
        if (postModel.getCommentCount().equals(0) || postModel.getCommentCount().equals(1)) {
            commentsTxt.setText(postModel.getCommentCount() + " Comment");
        } else {
            commentsTxt.setText(postModel.getCommentCount() + " Comments");
        }
        commentEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Drawable img1 = getResources().getDrawable(R.drawable.icon_before_comment_send);
                Drawable img2 = getResources().getDrawable(R.drawable.icon_after_comment_send);

                if (charSequence.toString().trim().length() == 0) {
                    isFlagZero = true;
                    commentSendWrapper.setBackgroundResource(R.drawable.icon_background_before_comment);
                    loadImageWithAnimation(context, img1);
                } else if (charSequence.toString().trim().length() != 0 && isFlagZero) {
                    isFlagZero = false;
                    commentSendWrapper.setBackgroundResource(R.drawable.icon_background_after_comment);
                    loadImageWithAnimation(context, img2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        commentSendWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlagZero) {
                    return;
                }
                final String comment = commentEdittext.getText().toString().trim();
                commentEdittext.setText("");
                //hide keyboard programmatically
                ((InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
                PostAdapter.AddComment addComment = new PostAdapter.AddComment(comment, FirebaseAuth.getInstance().getCurrentUser().getUid(), "0", "0", postModel.getId(), "0", postModel.getPostUserId(), "");
                Call<CommentModel> call = userInterface.postcomment(addComment);
                call.enqueue(new Callback<CommentModel>() {
                    @Override
                    public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                        if (response.body().getResults().size() > 0) {
                            Toast.makeText(context, "Comment Successful", Toast.LENGTH_SHORT).show();
                            postModel.setCommentCount(postModel.getCommentCount() + 1);
                            if (postModel.getCommentCount() == 1) commentsTxt.setText(postModel.getCommentCount() + " Comment");
                            else commentsTxt.setText(postModel.getCommentCount() + " Comments");

                            //once comment is sent load in our recyclerview
                            results.add(0, response.body().getResults().get(0));
//                            int position = results.indexOf(response.body().getResults().get(0));
                            commentAdapter.notifyItemInserted(0);
                            linearLayoutManager.scrollToPosition(0);

                            //update no. of comment is post's comment section as well

                        } else {
                            Toast.makeText(context, "Something went wrongg !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentModel> call, Throwable t) {
                        Toast.makeText(context, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void retrieveComments() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("postId", postModel.getId());
        Call<CommentModel> call = userInterface.retrievetopcomment(params);
        call.enqueue(new Callback<CommentModel>() {
            @Override
            public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                if (response.body().getResults().size() > 0) {
                    results.addAll(response.body().getResults());
                    commentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "No comments found !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentModel> call, Throwable t) {
                Toast.makeText(context, "Something went wrong !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadImageWithAnimation(Context context, final Drawable img1) {
        final Animation anim_out = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(context, R.anim.zoom_in);

        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                commentSend.setImageDrawable(img1);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                commentSend.startAnimation(anim_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        commentSend.startAnimation(anim_out);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        results.clear();
//        retrieveComments();
//    }
}
