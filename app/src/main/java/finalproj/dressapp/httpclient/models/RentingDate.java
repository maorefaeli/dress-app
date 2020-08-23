package finalproj.dressapp.httpclient.models;

import com.google.gson.annotations.SerializedName;

public class RentingDate {
    @SerializedName("fromdate")
    public String fromDate;

    @SerializedName("todate")
    public String toDate;
}
