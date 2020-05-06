package finalproj.dressapp.models;

import java.util.Date;

/**
 * Created by Shai on 06/05/2020.
 */
public class Post {
    public String title;
    public String description;
    public String ownerName;
    public String imageUrl;
    public Date from;
    public Date to;
    public int cost;

    public Post(String title, String description, String ownerName,
                String imageUrl, Date from, Date to, int cost) {
        this.title = title;
        this.description = description;
        this.ownerName = ownerName;
        this.imageUrl = imageUrl;
        this.from = from;
        this.to = to;
        this.cost = cost;
    }
}
