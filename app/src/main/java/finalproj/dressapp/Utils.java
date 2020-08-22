package finalproj.dressapp;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import finalproj.dressapp.activities.HomeActivity;
import finalproj.dressapp.activities.LoginActivity;
import finalproj.dressapp.activities.MyClothesActivity;
import finalproj.dressapp.activities.OrdersActivity;
import finalproj.dressapp.activities.ProfileActivity;
import finalproj.dressapp.activities.RegisterActivity;
import finalproj.dressapp.activities.SmartSuggestionsActivity;
import finalproj.dressapp.activities.WishListActivity;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.MyAppContext;
import finalproj.dressapp.httpclient.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import finalproj.dressapp.httpclient.models.CookieJarList;
//import okhttp3.Cookie;

public class Utils {
    static SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    static Boolean isGuest;
    static ProgressDialog dialog = null;
    static List<Product> currentUserWishlistItems;
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_ID = "userid";
        
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
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }
    
    public static void setUserId(Context ctx, String userid) 
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, userid);
        editor.commit();
    }

    public static void loadUserWishlistItems()
    {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Product>> call = apiInterface.getWishlist();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    currentUserWishlistItems = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                new AlertDialog.Builder(MyAppContext.getContext())
                    .setTitle("Couldn't get current user's wishlist items")
                    .setMessage(t.getMessage())
                    .show();
                call.cancel();
            }
        });
    }

    public static List<Product> getCurrentUserWishlistItems() {
        return currentUserWishlistItems;
    }
//    public static List<Cookie> getCookies(Context ctx)
//    {
//        Gson gson = new Gson();
//        String json = getSharedPreferences(ctx).getString(PREF_COOKIES, "");
//
//        if (json == null || json.isEmpty()){
//            return new ArrayList<Cookie>();
//        }
//        else {
//            List<Cookie> cookies = gson.fromJson(json, ArrayList.class);
//            return cookies;
//        }
//    }
//
//    public static void setCookies(Context ctx, List<Cookie> cookies)
//    {
//        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(cookies);
//        editor.putString(PREF_COOKIES, json);
//        editor.commit();
//    }

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

    public static Long DateFormatToLong(String oldDate)
    {
        Long newDate;
        newDate = System.currentTimeMillis();
        try {
            Date dateFormat = dateformatYYYYMMDD.parse(oldDate);
            newDate = dateFormat.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    public static String DateFormatToShow(String oldDate)
    {
        String day = oldDate.substring(8, 10);
        String month = oldDate.substring(5, 7);
        String year = oldDate.substring(0, 4);

        return day + "." + month + "." + year;
    }

    public static ActionBarDrawerToggle setNavigation(final Activity activity, DrawerLayout dl, ActionBar actionBar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(toggle);
        toggle.syncState();

        actionBar.setDisplayHomeAsUpEnabled(true);

        NavigationView nv = activity.findViewById(R.id.nv);
        Menu menu = nv.getMenu();

        MenuItem currentItem;

        for (int i = 0; i < menu.size(); i++) {
            currentItem = menu.getItem(i);

            if (currentItem.getItemId() == R.id.myCloth ||
                currentItem.getItemId() == R.id.myOrders ||
                currentItem.getItemId() == R.id.home ||
                currentItem.getItemId() == R.id.profile ||
                currentItem.getItemId() == R.id.wishList ||
                currentItem.getItemId() == R.id.logout) {

                if (isGuest) {
                    currentItem.setVisible(false);
                }
            }
            else if (currentItem.getItemId() == R.id.login ||
                     currentItem.getItemId() == R.id.register) {
                if (!isGuest) {
                    currentItem.setVisible(false);
                }
            }
        }

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
                        intent = new Intent(activity.getApplicationContext(), OrdersActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.home:
                        intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.profile:
                        intent = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.wishList:
                        intent = new Intent(activity.getApplicationContext(), WishListActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.smartSuggestions:
                        intent = new Intent(activity.getApplicationContext(), SmartSuggestionsActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.logout:
                        clearUserName(activity);
                        return true;
                    case R.id.login:
                        intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                        activity.startActivity(intent);
                        return true;
                    case R.id.register:
                        intent = new Intent(activity.getApplicationContext(), RegisterActivity.class);
                        activity.startActivity(intent);
                        return true;
                }

                return true;
            }
        });

        return toggle;
    }
}