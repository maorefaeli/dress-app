package finalproj.dressapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import finalproj.dressapp.R;
import finalproj.dressapp.RecycleViewAdapter;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.models.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends DressAppActivity {
    private RecycleViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Product> products;
    private List<Post> posts = new ArrayList<>();
    private LinearLayout postsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call <List<Product>> call = apiInterface.getAllItems();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {

                    recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    recyclerView.setHasFixedSize(true);

                    // use a linear layout manager
                    layoutManager = new LinearLayoutManager(HomeActivity.this);
                    recyclerView.setLayoutManager(layoutManager);

                    products = response.body();

                    //specify an adapter
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

        toggle = Utils.setNavigation(this, (DrawerLayout) findViewById(R.id.activity_main), getSupportActionBar());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 10);
    }

    @Override
    public void onBackPressed() {
        if (Utils.getGuestStatus()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);        
        }
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }
}
