package finalproj.dressapp.httpclient;

import finalproj.dressapp.httpclient.models.ServerCheck;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpClientExample {
    
    APIInterface apiInterface;

    public static void testServer(Bundle savedInstanceState) {
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<ServerCheck> call = apiInterface.doServerCheck();
        call.enqueue(new Callback<ServerCheck>() {
            @Override
            public void onResponse(Call<ServerCheck> call, Response<ServerCheck> response) {
                ServerCheck serverCheck = response.body();
                Log.d("sucesss - msg is ", serverCheck.msg);
            }

            @Override
            public void onFailure(Call<ServerCheck> call, Throwable t) {
                Log.d("Fail");
                call.cancel();
            }
        });
    }
}
