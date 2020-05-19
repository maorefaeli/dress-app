package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

public class ServerCheck {
    @SerializedName("msg")
    public String msg;

    public ServerCheck(String msg) {
        this.msg = msg;
    }
}