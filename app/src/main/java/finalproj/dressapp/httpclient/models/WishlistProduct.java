package finalproj.dressapp.httpclient.models;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class WishlistProduct {
    @SerializedName("product")
    public String product;

    public WishlistProduct(String product) {
        this.product = product;
    }
}