package finalproj.dressapp.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;

public class ProfileActivity extends DressAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toggle = Utils.setNavigation(this,
                (DrawerLayout) findViewById(R.id.activity_profile),
                getSupportActionBar());
    }
}