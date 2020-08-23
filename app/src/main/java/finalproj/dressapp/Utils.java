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
import finalproj.dressapp.httpclient.models.UserRegistration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import finalproj.dressapp.httpclient.models.CookieJarList;
//import okhttp3.Cookie;

public class Utils {
    static SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    static String productId;
    static String fromDate;
    static String toDate;
    static Boolean isGuest;
    static Boolean isWishlistActivity = false;
    static ProgressDialog dialog = null;
    static List<Product> currentUserWishlistItems;
    static UserRegistration userReg;
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_COOKIE = "usercookie";
        
    public static void showPopupProgressSpinner(Activity activity, Boolean isShowing, String text) {

        if (isShowing) {
            dialog = ProgressDialog.show(activity, "", text, true);
        } else {
            dialog.dismiss();
        }

    }

    public static Boolean getGuestStatus() {
        return isGuest;
    }

    public static void setGuestStatus(Boolean setGuest) {
        isGuest = setGuest;
    }

    public static Boolean getWishlistActivity() {
        return isWishlistActivity;
    }

    public static void setWishlistActivity(Boolean setWishlistActivity) {
        isWishlistActivity = setWishlistActivity;
    }

    public static String getProductId() {
        return productId;
    }

    public static void setProductId(String newId) {
        productId = newId;
    }
    
    public static String getFromDate() {
        return fromDate;
    }

    public static void setFromDate(String newFrom) {
        fromDate = newFrom;
    }
    public static String getToDate() {
        return toDate;
    }

    public static void setToDate(String newTo) {
        toDate = newTo;
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

    // public static String getUserId(Context ctx)
    // {
    //     return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    // }
    
    // public static void setUserId(Context ctx, String userid) 
    // {
    //     SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
    //     editor.putString(PREF_USER_ID, userid);
    //     editor.commit();
    // }

    public static String getUserCookie(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_COOKIE, "");
    }

    public static void setUserCookie(Context ctx, String userCookie) 
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_COOKIE, userCookie);
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
                currentItem.getItemId() == R.id.logout ||
                currentItem.getItemId() == R.id.smartSuggestions) {

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
                        doLogOut();
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

    public static void doLogOut(){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<Boolean> call = apiInterface.doLogout();
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.code() == 200) {
                    Boolean didLogin = response.body();
                    if (didLogin) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                call.cancel();
            }
        });
    }
}