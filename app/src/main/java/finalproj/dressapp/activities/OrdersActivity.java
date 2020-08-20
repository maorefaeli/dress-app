package finalproj.dressapp.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import finalproj.dressapp.R;
import finalproj.dressapp.RecycleViewAdapter;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.models.Product;

public class OrdersActivity extends DressAppActivity {
    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        toggle = Utils.setNavigation(this,
                (DrawerLayout) findViewById(R.id.activity_orders),
                getSupportActionBar());

    }
}