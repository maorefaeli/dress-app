package finalproj.dressapp.httpclient;

import finalproj.dressapp.httpclient.models.ServerCheck;
import finalproj.dressapp.httpclient.models.UserCredentials;
import finalproj.dressapp.httpclient.models.UserRegistration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
}