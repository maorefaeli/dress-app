package finalproj.dressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import finalproj.dressapp.activities.LoginActivity;
import finalproj.dressapp.R;
import finalproj.dressapp.Utils;
import finalproj.dressapp.fragments.ItemDialogFragment;
import finalproj.dressapp.models.Post;

public class HomeActivity extends AppCompatActivity {
    private ActionBarDrawerToggle toggle;
    private List<Post> posts = new ArrayList<>();
    private LinearLayout postsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toggle = Utils.setNavigation(this, getSupportActionBar());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 10);
        this.posts.add(new Post("Very nice dress", "this dress is very nice", "Shai",
                "/dress.jpg", "Bialik 126 Ramat Gan", System.currentTimeMillis(), calendar.getTimeInMillis(), 100));

        postsContainer = findViewById(R.id.postsContainer);

        for (final Post post : posts) {
            LinearLayout postContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.post_template, null);
            addPostData(postContainer, post);
            postsContainer.addView(postContainer);
            final HomeActivity activity = this;
            postContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemDialogFragment dialogFragment = new ItemDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("description", post.description);
                    bundle.putString("imgSrc", post.imageUrl);
                    bundle.putInt("cost", post.cost);
                    bundle.putLong("minDate", post.from);
                    bundle.putLong("maxDate", post.to);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(activity.getFragmentManager(), "ItemDialog");
                }
            });
        }

        // Will be moved to profile!
        Button mLogoutButton = (Button) findViewById(R.id.logoutButton);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Utils.getGuestStatus()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);        
        }
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void addPostData(LinearLayout post, Post postData) {
        ((TextView) post.findViewById(R.id.postTitle)).setText(postData.title);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

        String dates = dateFormat.format(postData.from) + " - " + dateFormat.format(postData.to);
        ((TextView) post.findViewById(R.id.dates)).setText(dates);
        ((TextView) post.findViewById(R.id.owner)).setText(postData.ownerName);
        ((TextView) post.findViewById(R.id.address)).setText(postData.address);
    }

    //Will be moved to profile!
    private void logOut() {
        Utils.clearUserName(this, getApplicationContext());
    }
}
