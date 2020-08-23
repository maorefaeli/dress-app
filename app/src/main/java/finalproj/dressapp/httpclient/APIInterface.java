package finalproj.dressapp.httpclient;

import java.util.List;

import finalproj.dressapp.httpclient.models.RentProduct;
import finalproj.dressapp.httpclient.models.ServerCheck;
import finalproj.dressapp.httpclient.models.UserCredentials;
import finalproj.dressapp.httpclient.models.UserRegistration;
import finalproj.dressapp.httpclient.models.SearchObject;
import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.WishlistProduct;
import finalproj.dressapp.Utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("check")
    Call<ServerCheck> doServerCheck();

    @GET("logout")
    Call<Boolean> doLogout();

    @POST("login")
    Call<Boolean> doLogin(@Body UserCredentials userCredentials);

    @POST("users/register")
    Call<Boolean> doRegister(@Body UserRegistration userRegistration);

    @POST("users/edit")
    Call<Boolean> doUpdateUser(@Body UserRegistration userRegistration);

    @POST("products/")
    Call<List<Product>> getAllItems(@Body SearchObject searchObject);

    @GET("products/user/me")
    Call<List<Product>> getAllMyItems();

    @GET("products/user/{userid}")
    Call<List<Product>> getAllSpecificUserItems(@Path("username") String userid);

    @POST("products/add")
    Call<Boolean> doAddItem(@Body Product product);

    @GET("products/close/{productid}")
    Call<Product> closeProduct(@Path("product") String userid);

    @GET("users/profile")
    Call<UserRegistration> getCurrentUserDetails();

    @GET("users/profile/{id}")
    Call<UserRegistration> getUserDetails(@Path("id") String userName);

    @POST("wishlist/add")
    Call<Boolean> addToWishlist(@Body WishlistProduct wishlistProduct);

    @HTTP(method = "DELETE", path = "wishlist/remove", hasBody = true)
    Call<Boolean> removeFromWishlist(@Body WishlistProduct wishlistProduct);

    @GET("wishlist")
    Call<List<Product>> getWishlist();

    @GET("suggestions")
    Call<List<Product>> getSuggestions();
    
    @POST("suggestions/request")
    Call<Boolean> requestSuggestion();

    @GET("rents")
    Call<List<Product>> getRents();

    @POST("rents/add")
    Call<Boolean> rentItem(@Body RentProduct rentProduct);
}