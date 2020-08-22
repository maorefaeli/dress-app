package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import finalproj.dressapp.R;
import finalproj.dressapp.RecycleViewAdapter;
import finalproj.dressapp.Utils;
import finalproj.dressapp.fragments.AddClothDialogFragment;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishListActivity extends DressAppActivity {

    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        toggle = Utils.setNavigation(this, (DrawerLayout) findViewById(R.id.wish_list_activity), getSupportActionBar());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getWishlist();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {

                    recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

                    // Use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView.
                    recyclerView.setHasFixedSize(true);

                    // Use a linear layout manager.
                    layoutManager = new LinearLayoutManager(WishListActivity.this);
                    recyclerView.setLayoutManager(layoutManager);

                    products = response.body();

                    // Specify an adapter.
                    mAdapter = new RecycleViewAdapter(WishListActivity.this, WishListActivity.this, products);
                    recyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                new AlertDialog.Builder(WishListActivity.this)
                    .setTitle("failure")
                    .setMessage(t.getMessage())
                    .show();
                call.cancel();
            }
        });
    }
}