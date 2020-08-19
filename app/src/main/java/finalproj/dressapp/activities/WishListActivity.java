package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.models.SuggestionInfo;
import okhttp3.internal.Util;

public class WishListActivity extends DressAppActivity {
    private List<Product> suggestions = new ArrayList<>();
    private LinearLayout suggestionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wish_list_activity);
        toggle = Utils.setNavigation(this, (DrawerLayout) findViewById(R.id.wish_list_activity), getSupportActionBar());
        suggestionsContainer = findViewById(R.id.suggestions);
        createMockSuggestions();
    }

    private void createMockSuggestions() {
        Product p1 = new Product("Dress", 100, "15.8.2020", "15.9.2020", "");
        suggestions.add(p1);
        updateSuggestions();
    }

    private void updateSuggestions() {
        if (suggestionsContainer != null && !suggestions.isEmpty()) {
            for (final Product suggestion : suggestions) {
                LinearLayout suggestionContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.wish_list_suggestion, null);
                ((TextView) suggestionContainer.findViewById(R.id.title)).setText(suggestion.name);
                ((ImageView) suggestionContainer.findViewById(R.id.image)).setImageURI(Uri.parse(suggestion.image));
                final EditText fromDate = suggestionContainer.findViewById(R.id.fromDate);
                final AtomicLong minDate = new AtomicLong(0);
                final AtomicLong maxDate = new AtomicLong(0);
                fromDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
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
                    }
                });

                final EditText toDate = suggestionContainer.findViewById(R.id.toDate);
                toDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                        LinearLayout dateContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.date_picker, null);
                        builder.setView(dateContainer);

                        final DatePicker date = dateContainer.findViewById(R.id.date);
                        date.setMinDate(minDate.get());
                        date.setMaxDate(maxDate.get());

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String dateString = date.getDayOfMonth() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() - 2000);
                                toDate.setText(dateString);
                            }
                        });
                        builder.create().show();
                    }
                });
                suggestionsContainer.addView(suggestionContainer);
            }
        }
    }
}