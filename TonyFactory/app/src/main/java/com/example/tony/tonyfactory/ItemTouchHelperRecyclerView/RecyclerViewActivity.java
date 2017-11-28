package com.example.tony.tonyfactory.ItemTouchHelperRecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tony.tonyfactory.ItemTouchHelperRecyclerView.model.Item;
import com.example.tony.tonyfactory.MyApplication;
import com.example.tony.tonyfactory.R;
import com.example.tony.tonyfactory.main.MainActivity;
import com.example.tony.tonyfactory.ItemTouchHelperRecyclerView.adapter.CartListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-11-21.
 */

public class RecyclerViewActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener, CartListAdapter.CartListAdapterListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Item> cartList;
    private CartListAdapter mAdapter;
    private CoordinatorLayout mCoordinatorLayout;
    private SearchView searchView;

    // url to fetch menu json
    private static final String URL = "https://api.androidhive.info/json/menu.json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        // Toolbar setting
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.my_cart));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new CartListAdapter(this, cartList, this);

        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        prepareCart();
    }

    /**
     * method make volley network call and parses json
     */
    private void prepareCart() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(RecyclerViewActivity.this, "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_SHORT).show();
                        }

                        List<Item> items = new Gson().fromJson(response.toString(), new TypeToken<List<Item>>() {
                        }.getType());

                        cartList.clear();
                        cartList.addAll(items);

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewholder, int direction, int position) {
        if (viewholder instanceof CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewholder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Item deletedItem = cartList.get(viewholder.getAdapterPosition());
            final int deletedIndex = viewholder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewholder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recycleractivity_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flag = view.getSystemUiVisibility();
            flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flag);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onItemSelected(Item item) {
        Toast.makeText(this, "Clicked : " + item.getName(), Toast.LENGTH_SHORT).show();
    }
}
