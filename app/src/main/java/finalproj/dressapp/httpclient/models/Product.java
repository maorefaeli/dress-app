package finalproj.dressapp.httpclient.models;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Product {
    @SerializedName("user")
    public String user;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("price")
    public Number price;

    @SerializedName("fromdate")
    public String fromdate;

    @SerializedName("todate")
    public String todate;

    @SerializedName("image")
    public String image;

    @SerializedName("isInWishlist")
    public Boolean isInWishlist;

    public Product(String productName, Number price, String fromDate, String toDate, String image) {
        this.name = productName;
        this.price = price;
        this.fromdate = fromDate;
        this.todate = toDate;    
        this.image = image;    
    }
}