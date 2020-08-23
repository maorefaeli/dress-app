package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.Wishlist;

public class UserRegistration {
    @SerializedName("id")
    public String id;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @SerializedName("address")
    public String address;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("wishlist")
    public List<Wishlist> wishlist;

    @SerializedName("reviewQuantity")
    public String reviewQuantity;

    @SerializedName("averageScore")
    public String averageScore;

    @SerializedName("coins")
    public String coins;

    public UserRegistration(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username; //email
        this.password = password;        
    }

    public UserRegistration(String firstName, String lastName, String address, String longitude, String latitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username; //email      
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public UserRegistration(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;  
    }
}