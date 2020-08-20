
package finalproj.dressapp.httpclient;

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
        
        OkHttpClient client = new OkHttpClient.Builder().build();

        retrofit = new Retrofit.Builder()
                // .baseUrl("http://localhost:3000/")
                .baseUrl("https://dress-app.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

}
