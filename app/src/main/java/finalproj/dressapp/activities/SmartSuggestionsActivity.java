package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.AddRent;
import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.RentProduct;
import finalproj.dressapp.httpclient.models.WishlistProduct;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartSuggestionsActivity extends DressAppActivity {
    private List<Product> suggestions = new ArrayList<>();
    private LinearLayout suggestionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_suggestions_activity);
        toggle = Utils.setNavigation(this, (DrawerLayout) findViewById(R.id.smart_suggestions_activity), getSupportActionBar());
        suggestionsContainer = findViewById(R.id.suggestions);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getSuggestions();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    suggestions = response.body();
                    updateSuggestions();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                new AlertDialog.Builder(SmartSuggestionsActivity.this)
                        .setTitle("Failed to get user's suggestions")
                        .setMessage(t.getMessage())
                        .show();
                call.cancel();
            }
        });
    }

    private void updateSuggestions() {
        if (suggestionsContainer != null && !suggestions.isEmpty()) {
            for (final Product suggestion : suggestions) {
                suggestionsContainer.removeAllViews();
                if (suggestion.name != null && !suggestion.name.isEmpty()){
                    LinearLayout suggestionContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.wish_list_suggestion, null);
                    ((TextView) suggestionContainer.findViewById(R.id.title)).setText(suggestion.name);
//                    ((ImageView) suggestionContainer.findViewById(R.id.image)).setImageURI(Uri.parse(suggestion.image));
                    final EditText fromDate = suggestionContainer.findViewById(R.id.fromDate);
                    final AtomicLong minDate = new AtomicLong(0);
                    final AtomicLong maxDate = new AtomicLong(0);
                    fromDate.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        LinearLayout dateContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.date_picker, null);
                        builder.setView(dateContainer);

                        final DatePicker date = dateContainer.findViewById(R.id.date);
                        minDate.set(Math.max(Utils.DateFormatToLong(suggestion.fromdate), System.currentTimeMillis()));
                        maxDate.set(Utils.DateFormatToLong(suggestion.todate));
                        date.setMinDate(minDate.get());
                        date.setMaxDate(maxDate.get());

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                                minDate.set(calendar.getTimeInMillis());
                                String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1)
                                        + "/" + (date.getYear() - 2000);
                                fromDate.setText(dateString);
                            }
                        });
                        builder.create().show();
                    });

                    final EditText toDate = suggestionContainer.findViewById(R.id.toDate);
                    toDate.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        LinearLayout dateContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.date_picker, null);
                        builder.setView(dateContainer);

                        final DatePicker date = dateContainer.findViewById(R.id.date);
                        date.setMinDate(minDate.get());
                        date.setMaxDate(maxDate.get());

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() - 2000);
                            toDate.setText(dateString);
                        });
                        builder.create().show();
                    });
                    suggestionContainer.findViewById(R.id.remove).setOnClickListener(view -> {
                        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                        WishlistProduct wishlistProduct = new WishlistProduct(suggestion.id);
                        Call<Boolean> call = apiInterface.removeFromWishlist(wishlistProduct);
                        call.enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(getBaseContext(), "Removed from wish list!", Toast.LENGTH_LONG).show();
                                    suggestions.remove(suggestion);
                                    suggestionsContainer.removeView(suggestionContainer);
                                    updateSuggestions();
                                }
                            }

                            public void onFailure(Call<Boolean> call, Throwable t) {
                                new AlertDialog.Builder(SmartSuggestionsActivity.this)
                                        .setTitle("Couldn't delete item: " + wishlistProduct.product + " from wishlist.")
                                        .setMessage(t.getMessage())
                                        .show();
                                call.cancel();
                            }
                        });
                    });

                    suggestionContainer.findViewById(R.id.placeRequest).setOnClickListener(view -> {
                        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                        AddRent rentProduct = new AddRent(suggestion.id, fromDate.getText().toString(), toDate.getText().toString());
                        Call <Boolean> call = apiInterface.requestSuggestion(rentProduct);
                        call.enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if (response.code() == 200) {
                                    Utils.setProductId("");
                                    Utils.setFromDate("");
                                    Utils.setToDate("");
                                    Toast.makeText(getApplicationContext(),
                                            "Order has been placed!", Toast.LENGTH_SHORT)
                                            .show();
                                    suggestionsContainer.removeView(suggestionContainer);
                                    updateSuggestions();
                                } else {
                                    new AlertDialog.Builder(SmartSuggestionsActivity.this)
                                            .setTitle("Couldn't rent item.")
                                            .setMessage(response.message())
                                            .show();
                                }
                            }

                            public void onFailure(Call<Boolean> call, Throwable t) {
                                new AlertDialog.Builder(SmartSuggestionsActivity.this)
                                        .setTitle("Couldn't rent item.")
                                        .setMessage(t.getMessage())
                                        .show();
                                Utils.setProductId("");
                                call.cancel();
                            }
                        });
                    });

                    suggestionsContainer.addView(suggestionContainer);
                }
            }
        } else if (suggestions.size() == 0) {
            findViewById(R.id.noSuggestions).setVisibility(View.VISIBLE);
        }
    }
}