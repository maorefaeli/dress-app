package finalproj.dressapp.httpclient.models;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class RentProduct {
    @SerializedName("product")
    public String product;

    @SerializedName("fromdate")
    public String fromdate;
    
    @SerializedName("todate")
    public String todate;

    public RentProduct(String product, String fromdate, String todate) {
        this.product = product;
        this.fromdate = fromdate;
        this.todate = todate;
    }
}