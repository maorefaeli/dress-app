package finalproj.dressapp.httpclient.models;
import android.app.Application;
import android.content.Context;

public class MyAppContext extends Application {
    private static MyAppContext instance;

    public static MyAppContext getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}