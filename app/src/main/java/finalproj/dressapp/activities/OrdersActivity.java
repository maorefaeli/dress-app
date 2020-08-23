package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import finalproj.dressapp.R;
import finalproj.dressapp.RecycleViewAdapter;
import finalproj.dressapp.Utils;
import finalproj.dressapp.fragments.CompleteOrderDialogFragment;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends DressAppActivity {
    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout ordersContainer;
    private List<Product> products = new ArrayList<>();
    private TextView noOrdersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        toggle = Utils.setNavigation(this,
                (DrawerLayout) findViewById(R.id.activity_orders),
                getSupportActionBar());

        ordersContainer = findViewById(R.id.ordersContainer);

        noOrdersText = findViewById(R.id.noOrders);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getRents();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    products = response.body();
                    showProducts();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                new AlertDialog.Builder(OrdersActivity.this)
                        .setTitle("failure")
                        .setMessage(t.getMessage())
                        .show();
                call.cancel();
            }
        });
    }

    private void showProducts() {
        noOrdersText.setVisibility(products.size() == 0 ? View.VISIBLE : View.GONE);
        for (final Product product : products) {
            LinearLayout productView = (LinearLayout) getLayoutInflater().inflate(R.layout.order_template, null);
            ((TextView)productView.findViewById(R.id.orderTitle)).setText(product.name);
            String dates = Utils.DateFormatToShow(product.fromdate) + "-" + Utils.DateFormatToShow(product.todate);
            ((TextView) productView.findViewById(R.id.dates)).setText(dates);
            ((TextView) productView.findViewById(R.id.address)).setText("address");
            productView.findViewById(R.id.finishOrder).setOnClickListener(view -> {
                CompleteOrderDialogFragment dialogFragment = new CompleteOrderDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", product.name);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "completeOrder");
            });

            ordersContainer.addView(productView);
        }
    }
}