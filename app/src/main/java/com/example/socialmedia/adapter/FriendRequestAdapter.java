package com.example.socialmedia.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activity.ProfileActivity;
import com.example.socialmedia.model.FriendsModel;
import com.example.socialmedia.rest.ApiClient;
import com.example.socialmedia.rest.services.UserInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    List<FriendsModel.Request> requests;
    Context context;
    public FriendRequestAdapter(List<FriendsModel.Request>requests, Context context) {
        this.requests = requests;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.ViewHolder holder, int position) {
        FriendsModel.Request request = requests.get(position);
        holder.activityTitleSingle.setText(request.getName());
        if(!request.getProfileUrl().isEmpty()) {
            Picasso.with(context).load(request.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.activityProfileSingle, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(request.getProfileUrl()).into(holder.activityProfileSingle);
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("context", context+"");
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", request.getId()));
            }
        });

        holder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading");
                progressDialog.show();

                holder.actionBtn.setText("Loading...");
                holder.actionBtn.setEnabled(false);

                UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
                Call<Integer> call = userInterface.performAction(new ProfileActivity.performAction(3+"", FirebaseAuth.getInstance().getCurrentUser().getUid(), request.getId()));
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        progressDialog.dismiss();
                        if(response.body()!= null) {
                            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", request.getId()));
                            Toast.makeText(context, "You are now friends in Social Media.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
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
        }
    }
}
