package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

public class UserRegistration {
    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    public UserRegistration(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;        
    }
}