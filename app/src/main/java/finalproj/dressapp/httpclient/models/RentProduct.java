package finalproj.dressapp.httpclient.models;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import finalproj.dressapp.httpclient.models.UserRegistration;
import finalproj.dressapp.httpclient.models.Product;

import java.util.Date;

public class RentProduct {
    @SerializedName("id")
    public String id;

    @SerializedName("product")
    public Product product;

    @SerializedName("user")
    public UserModel user;

    @SerializedName("fromdate")
    public String fromdate;
    
    @SerializedName("todate")
    public String todate;

    @SerializedName("coins")
    public int coins;

    @SerializedName("inDispute")
    public boolean inDispute;

    public RentProduct(String product, String fromdate, String todate) {
        this.product.id = product;
        this.fromdate = fromdate;
        this.todate = todate;
    }
}