package com.example.toni.receipt.BankUi;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.toni.receipt.Adapter.BankReceiptsAdapter;
import com.example.toni.receipt.Helper.Global;
import com.example.toni.receipt.Model.BankReceipts;
import com.example.toni.receipt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BankViewReceiptsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String URL = "http://192.168.43.201:80/Receipts/get_receipts.php?uuid=";
    private static final String TAG = BankViewReceiptsActivity.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView _recyclerView;
    private List<BankReceipts> myList;
    private RequestQueue resRequestQueue;
    private BankReceiptsAdapter bankReceiptsAdapter;
    private BankReceipts bankReceipts;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_view_receipts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Receipts");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        id = getIntent().getExtras().getString("USER_ID");

        //views
        setupviews();

        //permissions
        enableRunTimePermissions();

        //initialize list..
        inititilize();

        //DATA
        if (Global.isConnected(this)) {

            getData();
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
                bankReceiptsAdapter.setFilter(myList);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void getData() {

        resRequestQueue.add(getDataFromServer(id));
    }

    private JsonArrayRequest getDataFromServer(String id) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Retrieving receipts");
        progressDialog.setMessage("Please wait a moment");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        myList.clear();

        return new JsonArrayRequest(URL + id,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        progressDialog.dismiss();
                        Log.d(TAG, String.valueOf(response));
                        parseData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error != null) {
                    Toast.makeText(BankViewReceiptsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        });
    }

    private void parseData(JSONArray response) {

        for (int i = 0; i < response.length(); i++) {
            //Creating the receipt object
            BankReceipts bankr = new BankReceipts();
            JSONObject json;
            try {

                //Getting json
                json = response.getJSONObject(i);

                //Adding data to the bank
                bankr.setImage(json.getString("image"));
                bankr.setName(json.getString("name"));
                bankr.setNumber(json.getString("number"));
                bankr.setDesc(json.getString("desc"));
                bankr.setCategory(json.getString("category"));
                bankr.setDate(json.getString("date"));
                bankr.setTotal(json.getDouble("total"));
                bankr.setUuid(json.getString("uuid"));

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }
            //Adding the superhero object to the list
            myList.add(bankr);
        }

        bankReceiptsAdapter = new BankReceiptsAdapter(getApplication(), myList);
        _recyclerView.setAdapter(bankReceiptsAdapter);
        bankReceiptsAdapter.notifyDataSetChanged();
    }

    private void inititilize() {

        myList = new ArrayList<>();
        resRequestQueue = Volley.newRequestQueue(this);

    }

    private void enableRunTimePermissions() {

    }

    private void setupviews() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        _recyclerView = (RecyclerView) findViewById(R.id.bank_receipt_rv);

        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 3);

        _recyclerView.setLayoutManager(lm);
        _recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(6), true));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initiateREfreshLayout() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

    }

    private void refreshData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getData();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, 1000);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        final List<BankReceipts> filteredModelList = filter(myList, query);

        bankReceiptsAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<BankReceipts> filter(List<BankReceipts> models, String query) {

        query = query.toLowerCase();

        final List<BankReceipts> filteredModelList = new ArrayList<>();

        for (BankReceipts model : models) {

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
    public boolean onQueryTextChange(String newText) {
        return false;
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
