package finalproj.dressapp.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;

public class WishListActivity extends DressAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        toggle = Utils.setNavigation(this, (DrawerLayout) findViewById(R.id.wish_list_activity), getSupportActionBar());
    }
}