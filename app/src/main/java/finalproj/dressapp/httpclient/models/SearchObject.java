package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchObject {
    @SerializedName("name")
    public String name;

    @SerializedName("radius")
    public String radius;

    @SerializedName("minimumPrice")
    public String minimumPrice;

    @SerializedName("maximumPrice")
    public String maximumPrice;

    @SerializedName("minimumRating")
    public int minimumRating;

    @SerializedName("fromDate")
    public String fromDate;

    @SerializedName("toDate")
    public String toDate;

    public SearchObject(String name, String radius, String minimumPrice, String maximumPrice, String fromDate, String toDate, int minimumRating) {
        this.name = name;
        this.radius = radius;
        this.minimumPrice = minimumPrice; 
        this.maximumPrice = maximumPrice;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.minimumRating = minimumRating;
    }
}