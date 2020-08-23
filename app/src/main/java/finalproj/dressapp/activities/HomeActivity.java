package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import finalproj.dressapp.R;
import finalproj.dressapp.RecycleViewAdapter;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.WishlistProduct;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends DressAppActivity {
    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Utils.loadUserWishlistItems();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getAllItems();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {

                    recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

                    // Use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView.
                    recyclerView.setHasFixedSize(true);

                    // Use a linear layout manager.
                    layoutManager = new LinearLayoutManager(HomeActivity.this);
                    recyclerView.setLayoutManager(layoutManager);

                    products = response.body();

                    // Specify an adapter.
                    mAdapter = new RecycleViewAdapter(HomeActivity.this, HomeActivity.this, products);
                    recyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("failure")
                        .setMessage(t.getMessage())
                        .show();
                call.cancel();
            }
        });

        ((TextView) findViewById(R.id.current_money)).setText("300");
        toggle = Utils.setNavigation(this, (DrawerLayout) findViewById(R.id.activity_main), getSupportActionBar());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 10);
    }

    @Override
    public void onBackPressed() {
        // For guests, go back to login screen.
        if (Utils.getGuestStatus()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        // For users, go back to the android home screen.
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    public void onAddToWishlist(final View view) {
        // Only allow wishlist function for logged in users.
        if (!Utils.getGuestStatus()) {
            final TextView mWishlistIcon = view.findViewById(R.id.postTitleWishlistIcon);
            final WishlistProduct wishlistProduct = new WishlistProduct((String) ((View) view.getParent()).getTag());
            final Boolean isInWishlist = (Boolean) view.getTag();
            view.setTag(!isInWishlist);

            // Adding the item to the user's wishlist.
            if (!isInWishlist) {
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<Boolean> call = apiInterface.addToWishlist(wishlistProduct);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.code() == 200) {
                            mWishlistIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icons8_heart_26_full, 0);
                        }
                        Toast.makeText(getBaseContext(), "Added to wish list!", Toast.LENGTH_LONG).show();
                        Utils.loadUserWishlistItems();
                    }

                    public void onFailure(Call<Boolean> call, Throwable t) {
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Couldn't add item: " + wishlistProduct.product + " to wishlist.")
                                .setMessage(t.getMessage())
                                .show();
                        call.cancel();
                    }
                });
            }
            // Removing the item from the user's wishlist.
            else {
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<Boolean> call = apiInterface.removeFromWishlist(wishlistProduct);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.code() == 200) {
                            mWishlistIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icons8_heart_26, 0);
                            Toast.makeText(getBaseContext(), "Removed from wish list!", Toast.LENGTH_LONG).show();
                            Utils.loadUserWishlistItems();
                        }
                    }

                    public void onFailure(Call<Boolean> call, Throwable t) {
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Couldn't delete item: " + wishlistProduct.product + " from wishlist.")
                                .setMessage(t.getMessage())
                                .show();
                        call.cancel();
                    }
                });
            }
        }
    }
}
