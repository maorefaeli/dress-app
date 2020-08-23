package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.Wishlist;

public class UserModel {
    @SerializedName("id")
    public String id;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("averageScore")
    public int averageScore;

    @SerializedName("reviewQuantity")
    public int reviewQuantity;

    @SerializedName("address")
    public String address;
}