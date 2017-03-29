package com.example.toni.receipt.HomeUi;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.toni.receipt.Adapter.HomeReceiptAdapter;
import com.example.toni.receipt.Helper.Global;
import com.example.toni.receipt.Model.HomeReceiptsModel;
import com.example.toni.receipt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeReceipts extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String URL = "http://192.168.43.201:80/Receipts/home_receipts/get_home_receipts.php?uuid=";
    private static final String TAG = HomeReceipts.class.getSimpleName();

    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<HomeReceiptsModel> homeReceiptsList;
    private String userID;
    private RequestQueue requestQueue;

    HomeReceiptAdapter homeReceiptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_receipts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home receipts");

        registerForContextMenu(findViewById(R.id.fab_homerecipts_filter));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_homerecipts_filter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForContextMenu(view);
                openContextMenu(view);
            }
        });

        //seting up views
        rv = (RecyclerView) findViewById(R.id.rv_homereceipts_display);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_homereceipts_refress);


        //rv properties
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 3);
        rv.setLayoutManager(lm);
        rv.addItemDecoration(new HomeReceipts.GridSpacingItemDecoration(3, dpToPx(6), true));
        rv.setItemAnimator(new DefaultItemAnimator());

        if (Global.isConnected(this)) {

            //get userID
            userID = getIntent().getExtras().getString("USER_ID");

            //load swipe progress bar
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });




            //getData
            loadHomeReceipts();
            initiateREfreshLayout();

        } else {

            Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setView(getLayoutInflater().inflate(R.layout.net_toast, (ViewGroup) findViewById(R.id.customError)));
            toast.show();
            finish();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select category");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_category_menu, menu);

        // loop for menu items
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem mi = menu.getItem(i);

            // check the Id as you wish
            if (mi.getItemId() == R.id.cat_maintainance) {
                if (mi.isChecked()){
                    mi.setChecked(true);
                }
            }else if (mi.getItemId() == R.id.cat_entertainment){

                if (mi.isChecked()){
                    mi.setChecked(true);
                }
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case(R.id.cat_maintainance):

                if(item.isChecked()) {
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                }

                break;
            case(R.id.cat_entertainment):
                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                break;
            case(R.id.cat_power):
                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                break;

            case(R.id.cat_rent):
                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                break;

            case(R.id.cat_tele):
                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                break;

            case(R.id.cat_water):
                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                break;

            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void initiateREfreshLayout() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHomeReceipts();
            }
        });

    }



    private void loadHomeReceipts() {

        //retrieve receipts...
        homeReceiptsList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getDataFromServer(userID));
    }

    private JsonArrayRequest getDataFromServer(String userID) {

        homeReceiptsList.clear();

        return new JsonArrayRequest(URL + userID, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                //suucess
                Log.d(TAG,String.valueOf(response));

                swipeRefreshLayout.setRefreshing(false);
                parseData(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //handle server error
                if(error != null){

                    Log.d(TAG, error.getMessage());
                    Toast.makeText(HomeReceipts.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    finish();

                }

            }
        });

    }

    private void parseData(JSONArray response) {

        //loop over the returned array
        for (int i = 0; i < response.length() ; i++){

            //create new model...
            HomeReceiptsModel home = new HomeReceiptsModel();
            JSONObject json;

            try {

                //get json

                json = response.getJSONObject(i);

                //Adding data to the bank
                home.setImage(json.getString("image"));
                home.setName(json.getString("name"));
                home.setNumber(json.getString("number"));
                home.setDesc(json.getString("desc"));
                home.setCategory(json.getString("category"));
                home.setDate(json.getString("date"));
                home.setTotal(json.getDouble("total"));
                home.setUuid(json.getString("uuid"));

            }catch (JSONException e){
                Log.d(TAG, e.getMessage());
            }

            //add to list
            homeReceiptsList.add(home);
        }

        homeReceiptAdapter = new HomeReceiptAdapter(HomeReceipts.this, homeReceiptsList);
        rv.setAdapter(homeReceiptAdapter);
        homeReceiptAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);
        MenuItem searchViewItem = menu.findItem(R.id.search);


        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchViewItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                //do something when collapsed...
                homeReceiptAdapter.setFilter(homeReceiptsList);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        final List<HomeReceiptsModel> filteredModelList = filter(homeReceiptsList, query);

        homeReceiptAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private List<HomeReceiptsModel> filter(List<HomeReceiptsModel> models, String query) {

        query = query.toLowerCase();

        final List<HomeReceiptsModel> filteredModelList = new ArrayList<>();

        for (HomeReceiptsModel model : models) {

            final String text = model.getName().toLowerCase();

            final String n = model.getNumber();

            final String total = String.valueOf(model.getTotal());

            if (text.contains(query) || n.contains(query) || total.contains(query)) {

                filteredModelList.add(model);

            }

        }
        return filteredModelList;
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        private GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)

                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
