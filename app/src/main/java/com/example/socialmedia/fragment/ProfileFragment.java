package com.example.socialmedia.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.adapter.PostAdapter;
import com.example.socialmedia.model.PostModel;
import com.example.socialmedia.rest.ApiClient;
import com.example.socialmedia.rest.services.UserInterface;
import com.google.firebase.auth.FirebaseAuth;

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

public class ProfileFragment extends Fragment {

    Context context;
    @BindView(R.id.newsfeed)
    RecyclerView newsfeed;
    @BindView(R.id.newsfeedProgressBar)
    ProgressBar newsfeedProgressBar;
    Unbinder unbinder;

    int limit = 2;
    int offset = 0;
    boolean isFromStart = true;
    PostAdapter postAdapter;
    List<PostModel> postModels = new ArrayList<>();

    String uid = "0";
    String current_state = "0";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        newsfeed.setLayoutManager(linearLayoutManager);

        postAdapter = new PostAdapter(context, postModels);
        newsfeed.setAdapter(postAdapter);
        uid = getArguments().getString("uid");
        current_state = getArguments().getString("current_state");

        newsfeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int passVisibleItems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                Log.i("visible items", "visibleItemCount -> " + visibleItemCount + "totalItemCount ->" + totalItemCount + "passVisibleItems ->"+ passVisibleItems);

                if((passVisibleItems + visibleItemCount) > offset + 1 && (passVisibleItems + visibleItemCount) >= totalItemCount) {
                    isFromStart = false;
                    newsfeedProgressBar.setVisibility(View.VISIBLE);
                    offset = offset + limit;
                    loadProfilePost();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        isFromStart = true;
        offset = 0;
        loadProfilePost();
    }

    private void loadProfilePost() {
        Map<String, String> params = new HashMap<String, String>();
        if(current_state == "5"){
            params.put("uid", uid);
        }
        else {
            params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            params.put("profileId", uid);
        }
        params.put("limit", limit + "");
        params.put("offset", offset + "");
        params.put("current_state", current_state);
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Call<List<PostModel>> call = userInterface.getProfilePosts(params);
        call.enqueue(new Callback<List<PostModel>>() {
            @Override
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                newsfeedProgressBar.setVisibility(View.GONE);
                if(response.body() != null) {
                    postModels.addAll(response.body());
                    if (!isFromStart) {
                        newsfeed.setAdapter(postAdapter);
                    } else {
                        postAdapter.notifyItemRangeInserted(postModels.size(), response.body().size());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                newsfeedProgressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Something went wronggg !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        postModels.clear();
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
