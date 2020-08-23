package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SyncRequest;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import finalproj.dressapp.R;
import finalproj.dressapp.RecycleViewAdapter;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.RentProduct;
import finalproj.dressapp.httpclient.models.SearchObject;
import finalproj.dressapp.httpclient.models.WishlistProduct;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends DressAppActivity {
    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Product> products;
    private static SearchObject searchObject = new SearchObject("","","","","","",0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Utils.loadUserWishlistItems();
        fillSearchFields();

        AtomicLong minDateAvailable = new AtomicLong(System.currentTimeMillis());

        EditText minDate = findViewById(R.id.minDate);
        minDate.setOnClickListener((View.OnClickListener) view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LinearLayout dateContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.date_picker, null);
            builder.setView(dateContainer);

            final DatePicker date = dateContainer.findViewById(R.id.date);
            date.setMinDate(minDateAvailable.get());

            builder.setPositiveButton("OK", (dialog, which) -> {
                android.icu.util.Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                minDateAvailable.set(calendar.getTimeInMillis());
                String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1)
                        + "/" + (date.getYear() - 2000);
                minDate.setText(dateString);
            });
            builder.create().show();
        });

        EditText maxDate = findViewById(R.id.maxDate);
        maxDate.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LinearLayout dateContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.date_picker, null);
            builder.setView(dateContainer);

            final DatePicker date = dateContainer.findViewById(R.id.date);
            date.setMinDate(minDateAvailable.get());

            builder.setPositiveButton("OK", (dialog, which) -> {
                String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1)
                        + "/" + (date.getYear() - 2000);
                maxDate.setText(dateString);
            });
            builder.create().show();
        });

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getAllItems(searchObject);
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

        findViewById(R.id.applySearch).setOnClickListener(view -> {
            doSearch();
        });
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

    public void orderItem(final View view) {

        String fromDate = Utils.getFromDate();
        String toDate = Utils.getToDate();
        String productId = Utils.getProductId();
        RentProduct rentProduct = new RentProduct(productId, fromDate, toDate);

        if (fromDate == null || fromDate.isEmpty() ||
                toDate == null || toDate.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Please enter from and to dates", Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call <Boolean> call = apiInterface.rentItem(rentProduct);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.code() == 200) {
                        Utils.setProductId("");
                        Utils.setFromDate("");
                        Utils.setToDate("");
                    } else {
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Couldn't rent item")
                                .setMessage(response.message())
                                .show();
                    }
                }

                public void onFailure(Call<Boolean> call, Throwable t) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Couldn't rent item: " + productId)
                            .setMessage(t.getMessage())
                            .show();
                    Utils.setProductId("");
                    call.cancel();
                }
            });
        }
    }

    private void doSearch() {
        String name = ((TextView) findViewById(R.id.search)).getText().toString();
        int minRating = (int) ((RatingBar) findViewById(R.id.ratingBar)).getRating();
        String minPrice = ((TextView) findViewById(R.id.minPrice)).getText().toString();
        String maxPrice = ((TextView) findViewById(R.id.maxPrice)).getText().toString();
        String radius = ((TextView) findViewById(R.id.radius)).getText().toString();
        String fromDate = ((TextView) findViewById(R.id.minDate)).getText().toString();
        String toDate = ((TextView) findViewById(R.id.maxDate)).getText().toString();

        searchObject = new SearchObject(name, radius, minPrice, maxPrice, fromDate, toDate, minRating);

        HomeActivity.super.recreate();
    }

    private void fillSearchFields() {
        if (searchObject != null) {
            if (searchObject.name != null && !searchObject.name.isEmpty()) {
                ((TextView)findViewById(R.id.search)).setText(searchObject.name);
            }
            if (searchObject.fromDate != null && !searchObject.fromDate.isEmpty()) {
                ((TextView)findViewById(R.id.minDate)).setText(searchObject.fromDate);
            }
            if (searchObject.toDate != null && !searchObject.toDate.isEmpty()) {
                ((TextView)findViewById(R.id.maxDate)).setText(searchObject.toDate);
            }
            if (searchObject.minimumPrice != null && !searchObject.minimumPrice.isEmpty()) {
                ((TextView)findViewById(R.id.minPrice)).setText(searchObject.minimumPrice);
            }
            if (searchObject.maximumPrice != null && !searchObject.maximumPrice.isEmpty()) {
                ((TextView)findViewById(R.id.maxPrice)).setText(searchObject.maximumPrice);
            }
            if (searchObject.radius != null && !searchObject.radius.isEmpty()) {
                ((TextView)findViewById(R.id.radius)).setText(searchObject.radius);
            }
            if (searchObject.minimumRating != 0) {
                ((RatingBar)findViewById(R.id.ratingBar)).setRating(searchObject.minimumRating);
            }
        }
    }
}
