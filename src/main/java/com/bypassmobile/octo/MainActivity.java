package com.bypassmobile.octo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bypassmobile.octo.adapters.EmptyAdapter;
import com.bypassmobile.octo.adapters.UserCardAdapter;
import com.bypassmobile.octo.model.User;

import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity {

    private ActionBar actionBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIComponents();

        Bundle bundle = getIntent().getBundleExtra("USER");
        if (bundle != null && bundle.getString("NAME") != null) {
            fetchUserFollowings(bundle.getString("NAME"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            fetchAllUsers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((UserCardAdapter) recyclerView.getAdapter()).filterSearchCategory(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((UserCardAdapter) recyclerView.getAdapter()).filterSearchCategory(newText.toLowerCase());
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ((UserCardAdapter) recyclerView.getAdapter()).restoreOriginalUsers();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_search: {
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /******************
     * Public Methods *
     ******************/

    public void launchBrowseUserFollowingActivity(String userName) {
        Bundle bundle = new Bundle();
        bundle.putString("NAME", userName);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER", bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*******************
     * Private Methods *
     *******************/

    private void initializeUIComponents() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Repo Stalker");
        }

        recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_browse_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);

        // initialize to an empty adapter until we actually fetched real user data from the endpoint
        if (recyclerView != null) {
            recyclerView.setAdapter(new EmptyAdapter());
        }
    }

    private void updateUserAdapterData(List<User> users) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.id_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        Collections.sort(users);
        if (recyclerView != null) {
            recyclerView.setAdapter(new UserCardAdapter(this, users));
        }
        progressBar.setVisibility(View.GONE);
    }

    private void fetchAllUsers() {
        getEndpoint().getOrganizationMember("bypasslane", new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                updateUserAdapterData(users);
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void fetchUserFollowings(String user) {
        getEndpoint().getFollowingUser(user, new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                updateUserAdapterData(users);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
