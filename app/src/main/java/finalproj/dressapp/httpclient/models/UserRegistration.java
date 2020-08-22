package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import finalproj.dressapp.httpclient.models.Product;
import finalproj.dressapp.httpclient.models.Wishlist;

public class UserRegistration {
    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @SerializedName("wishlist")
    public List<Wishlist> wishlist;

    public UserRegistration(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username; //email
        this.password = password;        
    }

    public UserRegistration(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;  
    }
}