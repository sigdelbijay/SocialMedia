package com.example.socialmedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activity.ProfileActivity;
import com.example.socialmedia.model.FriendsModel;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {


    List<FriendsModel.Friend> friends;
    Context context;

    public FriendAdapter(List<FriendsModel.Friend> friends, Context context) {
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendsModel.Friend friend = friends.get(position);
        holder.activityTitleSingle.setText(friend.getName());
        if(!friend.getProfileUrl().isEmpty()) {
            Picasso.with(context).load(friend.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.activityProfileSingle, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(friend.getProfileUrl()).into(holder.activityProfileSingle);
                }
            });

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("context", context+"");
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", friend.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.activity_profile_single)
        CircleImageView activityProfileSingle;
        @BindView(R.id.activity_title_single)
        TextView activityTitleSingle;
        @BindView(R.id.action_btn)
        Button actionBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            actionBtn.setVisibility(View.GONE);
        }
    }

}
