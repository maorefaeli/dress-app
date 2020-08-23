package finalproj.dressapp.httpclient.models;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import finalproj.dressapp.httpclient.models.UserRegistration;
import finalproj.dressapp.httpclient.models.Product;

import java.util.Date;

public class AddRent {
    @SerializedName("product")
    public String product;

    @SerializedName("fromdate")
    public String fromdate;
    
    @SerializedName("todate")
    public String todate;

    public AddRent(String product, String fromdate, String todate) {
        this.product = product;
        this.fromdate = fromdate;
        this.todate = todate;
    }
}