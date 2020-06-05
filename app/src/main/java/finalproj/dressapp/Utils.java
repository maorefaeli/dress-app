package finalproj.dressapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import finalproj.dressapp.activities.HomeActivity;

public class Utils {
    static ProgressDialog dialog = null;

    public static void showPopupProgressSpinner(Activity activity, Boolean isShowing) {
        if (isShowing) {
            dialog = ProgressDialog.show(activity, "", "Just a moment...", true);
        } else {
            dialog.dismiss();
        }

    }

    public static ActionBarDrawerToggle setNavigation(final Activity activity, ActionBar actionBar) {
        DrawerLayout dl = activity.findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(toggle);
        toggle.syncState();

        actionBar.setDisplayHomeAsUpEnabled(true);

        NavigationView nv = activity.findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch(id)
                {
                    case R.id.myCloth:
//                        Toast.makeText(HomeActivity.this, "My Account", Toast.LENGTH_SHORT).show();break;
                    case R.id.myOrders:
//                        Toast.makeText(HomeActivity.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.home:
                        intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
                        activity.startActivity(intent);
                    case R.id.profile:
//                        intent = new Intent(activity.getApplicationContext())
                    default:
                        return true;
                }
            }
        });

        return toggle;
    }
}