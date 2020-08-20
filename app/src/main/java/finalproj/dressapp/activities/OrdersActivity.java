package finalproj.dressapp.activities;

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
import finalproj.dressapp.httpclient.models.Product;

public class OrdersActivity extends DressAppActivity {
    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout ordersContainer;
    private List<Product> products = new ArrayList<>();
    private TextView noOrdersText;

    private void addMockProducts() {
        Product product = new Product("Dress",
                150, "1/1/2020", "10/01/2020", "./dress.png");
        products.add(product);
        showProducts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        toggle = Utils.setNavigation(this,
                (DrawerLayout) findViewById(R.id.activity_orders),
                getSupportActionBar());

        ordersContainer = findViewById(R.id.ordersContainer);

        noOrdersText = findViewById(R.id.noOrders);
        addMockProducts();
    }

    private void showProducts() {
        noOrdersText.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
        for (final Product product : products) {
            LinearLayout productView = (LinearLayout) getLayoutInflater().inflate(R.layout.order_template, null);
            ((TextView)productView.findViewById(R.id.orderTitle)).setText(product.name);
            String dates = product.fromdate + "-" + product.todate;
            ((TextView) productView.findViewById(R.id.dates)).setText(dates);
            ((TextView) productView.findViewById(R.id.address)).setText("address");
            ((TextView) productView.findViewById(R.id.owner)).setText(product.user);
            productView.findViewById(R.id.finishOrder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CompleteOrderDialogFragment dialogFragment = new CompleteOrderDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", product.name);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "completeOrder");
                }
            });

            ordersContainer.addView(productView);
        }
    }
}