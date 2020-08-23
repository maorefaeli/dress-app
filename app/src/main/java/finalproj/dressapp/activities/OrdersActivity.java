package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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
import finalproj.dressapp.httpclient.models.RentProduct;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class OrdersActivity extends DressAppActivity {
    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout ordersContainer;
    private List<RentProduct> products = new ArrayList<>();
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
        Call<List<RentProduct>> call = apiInterface.getRents();
        call.enqueue(new Callback<List<RentProduct>>() {
            @Override
            public void onResponse(Call<List<RentProduct>> call, Response<List<RentProduct>> response) {
                if (response.code() == 200) {
                    products = response.body();
                    showProducts();
                }
            }

            @Override
            public void onFailure(Call<List<RentProduct>> call, Throwable t) {
                new AlertDialog.Builder(OrdersActivity.this)
                        .setTitle("failure")
                        .setMessage(t.getMessage())
                        .show();
                call.cancel();
            }
        });
    }

    private void showProducts() {
        noOrdersText.setVisibility(products.size() == 0 ? View.VISIBLE : GONE);
        for (final RentProduct rentProduct : products) {
            LinearLayout productView = (LinearLayout) getLayoutInflater().inflate(R.layout.order_template, null);
            ((TextView)productView.findViewById(R.id.orderTitle)).setText(rentProduct.product.name);
            String dates = Utils.DateFormatToShow(rentProduct.fromdate) + "-" + Utils.DateFormatToShow(rentProduct.todate);
            ((TextView) productView.findViewById(R.id.dates)).setText(dates);
            ((TextView) productView.findViewById(R.id.address)).setText(rentProduct.user.address);
            String userFullName = rentProduct.user.firstName + " " + rentProduct.user.lastName;
            ((TextView) productView.findViewById(R.id.owner)).setText(userFullName);
            if (rentProduct.coins > 0) {
                TextView textNew = ((TextView) productView.findViewById(R.id.cost));
                textNew.setText(String.valueOf(rentProduct.coins));
            } else {
                productView.findViewById(R.id.cost).setVisibility(GONE);
                productView.findViewById(R.id.freeOrder).setVisibility(View.VISIBLE);
            }
            productView.findViewById(R.id.finishOrder).setOnClickListener(view -> {
                CompleteOrderDialogFragment dialogFragment = new CompleteOrderDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", rentProduct.product.name);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "completeOrder");
            });

            ordersContainer.addView(productView);
        }
    }
}