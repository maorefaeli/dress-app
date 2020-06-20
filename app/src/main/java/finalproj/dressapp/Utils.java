package finalproj.dressapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import java.text.SimpleDateFormat;

import finalproj.dressapp.activities.HomeActivity;
import finalproj.dressapp.activities.LoginActivity;
import finalproj.dressapp.activities.MyClothesActivity;
import finalproj.dressapp.activities.ProfileActivity;

public class Utils {
    static SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    static Boolean isGuest;
    static ProgressDialog dialog = null;
    static final String PREF_USER_NAME = "username";
    static final String PREF_UDER_ID = "userid";
        
    public static void showPopupProgressSpinner(Activity activity, Boolean isShowing, String text) {

        if (isShowing) {
            dialog = ProgressDialog.show(activity, "", text, true);
        } else {
            dialog.dismiss();
        }

    }

    public static void setGuestStatus(Boolean setGuest) {
        isGuest = setGuest;
    }

    public static Boolean getGuestStatus() {
        return isGuest;
    }

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    //    return ctx.getSharedPreferences(PREF_USER_NAME, 0);
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    
    public static void setUserName(Context ctx, String userName) 
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_UDER_ID, "");
    }
    
    public static void setUserId(Context ctx, String userid) 
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_UDER_ID, userid);
        editor.commit();
    }
    public static void clearUserName(final Activity activity)
    {
        SharedPreferences.Editor editor = getSharedPreferences(activity.getApplicationContext()).edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public static String LongToDateFormat(Long oldDate)
    {
        StringBuilder newDate = new StringBuilder( dateformatYYYYMMDD.format( oldDate ) );
        return newDate.toString();
    }

    public static ActionBarDrawerToggle setNavigation(final Activity activity, DrawerLayout dl, ActionBar actionBar) {
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
                        intent = new Intent(activity.getApplicationContext(), MyClothesActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.myOrders:
                        intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
                        activity.startActivity(intent);
                        break;
                    case R.id.home:
                        intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.profile:
                        intent = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.logout:
                        clearUserName(activity);
                        return true;
                }

                return true;
            }
        });

        return toggle;
    }
}