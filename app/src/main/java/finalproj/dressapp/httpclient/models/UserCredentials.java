package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

public class UserCredentials {
    @SerializedName("username")
    public String username;

    @SerializedName("password") 
    public String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}