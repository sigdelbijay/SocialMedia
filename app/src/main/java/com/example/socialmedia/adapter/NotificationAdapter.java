package com.example.socialmedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activity.FullPostActivity;
import com.example.socialmedia.activity.ProfileActivity;
import com.example.socialmedia.model.NotificationModel;
import com.example.socialmedia.util.AgoDateParse;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<NotificationModel> notificationModels;
    public NotificationAdapter(Context context, List<NotificationModel> notificationModels) {
        this.context = context;
        this.notificationModels = notificationModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*
        * 1. liked your post
        * 2. commented on your post
        * 3. replied on your comment
        * 4. send you friend request
        * 5. accepted your friend request
        * */

        final NotificationModel notificationModel = notificationModels.get(position);
        if(notificationModel.getType().equals(1)) {
            holder.notificationTitle.setText(notificationModel.getName() + " liked your post");
        } else if(notificationModel.getType().equals(2)) {
            holder.notificationTitle.setText(notificationModel.getName() + " commented on your post");
        } else if(notificationModel.getType().equals(3)) {
            holder.notificationTitle.setText(notificationModel.getName() + " replied on your comment");
        } else if(notificationModel.getType().equals(4)) {
            holder.notificationTitle.setText(notificationModel.getName() + " send you friend request");
        } else {
            holder.notificationTitle.setText(notificationModel.getName() + " accepted your friend request");
        }

        if(notificationModel.getType().equals(1) || notificationModel.getType().equals(2) || notificationModel.getType().equals(3)) {
            holder.notificationBody.setText(notificationModel.getPost());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullPostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isLoadFromNetwork", true);
                    bundle.putString("postId", notificationModel.getPostId());
                    intent.putExtra("postBundle", bundle);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.notificationBody.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("uid", notificationModel.getNotificationFrom());
                    context.startActivity(intent);
                }
            });
        }

        if(!notificationModel.getProfileUrl().isEmpty()) {
            Picasso.with(context).load(notificationModel.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.notficationSenderProfile, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(notificationModel.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.notficationSenderProfile);
                }
            });
        }

        holder.notificationDate.setText(AgoDateParse.getTimeAgo(notificationModel.getNotificationTime()));
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.notfication_sender_profile)
        CircleImageView notficationSenderProfile;
        @BindView(R.id.notification_title)
        TextView notificationTitle;
        @BindView(R.id.notification_body)
        TextView notificationBody;
        @BindView(R.id.notification_date)
        TextView notificationDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
