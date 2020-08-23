package finalproj.dressapp.httpclient.models;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import finalproj.dressapp.httpclient.models.UserRegistration;
import finalproj.dressapp.httpclient.models.Product;

import java.util.Date;

public class OrderReview {
    @SerializedName("rent")
    public String rent;

    @SerializedName("score")
    public int score;

    public OrderReview(String rent, int score) {
        this.rent = rent;
        this.score = score;
    }
}