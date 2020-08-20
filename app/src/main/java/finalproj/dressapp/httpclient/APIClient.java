
package finalproj.dressapp.httpclient;

import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        // OkHttpClient client = new OkHttpClient.Builder().cookieJar(new CookieJar(){
        //     @Override
        //     public void saveFromResponse(HttpUrl url, List<Cookie> cookies){
        //         Utils.setCookies(MyAppContext.getContext(), cookies);
        //     }

        //     @Override
        //     public List<Cookie> loadForRequest(HttpUrl url) {
        //         return Utils.getCookies(MyAppContext.getContext());
        //     }
        // }).build();

        if (retrofit == null) {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.cookieJar(cookieJar);

            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    //.baseUrl("http://192.168.1.14:3000/")
                    .baseUrl("https://dress-app.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

}
