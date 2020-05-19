package finalproj.dressapp.httpclient;

import finalproj.dressapp.httpclient.models.ServerCheck;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface APIInterface {
    @GET("/")
    Call<ServerCheck> doServerCheck();
}