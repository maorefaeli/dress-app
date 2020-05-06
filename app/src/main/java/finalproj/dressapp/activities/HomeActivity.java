package finalproj.dressapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import finalproj.dressapp.R;
import finalproj.dressapp.models.Post;

public class HomeActivity extends AppCompatActivity {
    private List<Post> posts = new ArrayList<>();
    private LinearLayout postsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get from DB
        this.posts.add(new Post("Very nice dress", "this dress is very nice", "Shai",
                "/drss.jpg", new Date(2020, 6, 5), new Date(2020, 6, 29), 100));

        postsContainer = (LinearLayout) findViewById(R.id.postsContainer);

        for (Post post: posts) {
            LinearLayout postContainer = new LinearLayout(getApplicationContext());

        }
    }
}
