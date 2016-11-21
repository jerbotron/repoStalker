package com.bypassmobile.octo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bypassmobile.octo.MainActivity;
import com.bypassmobile.octo.R;
import com.bypassmobile.octo.image.CircleImageTransformation;
import com.bypassmobile.octo.image.ImageLoader;
import com.bypassmobile.octo.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.UserCardHolder>{

    private Context callingContext;
    private List<User> usersList, userListCopy;
    private String prevFilter;

    /***********************************************************************************************
     **************************************** ADAPTER METHODS **************************************
     **********************************************************************************************/
    static class UserCardHolder extends RecyclerView.ViewHolder {
        ImageView userPhoto;
        TextView userName;

        private UserCardHolder(View itemView) {
            super(itemView);
            userPhoto = (ImageView) itemView.findViewById(R.id.id_user_card_profile_photo);
            userName = (TextView) itemView.findViewById(R.id.id_user_card_name);
        }
    }

    public UserCardAdapter(Context callingContext, List<User> usersList) {
        this.callingContext = callingContext;
        this.usersList = usersList;
        this.userListCopy = new ArrayList<>(usersList);
        prevFilter = "";
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public UserCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_user_profile, parent, false);
        return new UserCardHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserCardHolder userCardHolder, int position) {
        final User user = usersList.get(position);
        ImageLoader.createImageLoader(callingContext)
                .load(user.getProfileURL())
                .transform(new CircleImageTransformation())
                .into(userCardHolder.userPhoto);

        userCardHolder.userName.setText(user.getName());

        userCardHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) callingContext).launchBrowseUserFollowingActivity(user.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void filterSearchCategory(String filter) {
        if (filter.length() < prevFilter.length())  // restart filtering if user deleted prev filter
            usersList = new ArrayList<>(userListCopy);

        ArrayList<User> filteredUsers = new ArrayList<>();
        for (User user : usersList) {
            if (user.getName().toLowerCase().startsWith(filter)) {
                filteredUsers.add(user);
            }
        }
        usersList = filteredUsers;
        prevFilter = filter;
        notifyDataSetChanged();
    }

    public void restoreOriginalUsers() {
        usersList = new ArrayList<>(userListCopy);
        notifyDataSetChanged();
    }
}
