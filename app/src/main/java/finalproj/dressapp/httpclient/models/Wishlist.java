package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import finalproj.dressapp.httpclient.models.Product;

public class Wishlist {
    @SerializedName("user")
    public String user;

    @SerializedName("lastName")
    public List<Product> items;
}