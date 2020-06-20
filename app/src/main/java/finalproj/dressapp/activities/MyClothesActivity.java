package finalproj.dressapp.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;
import finalproj.dressapp.fragments.AddClothDialogFragment;

public class MyClothesActivity extends DressAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clothes);
        toggle = Utils.setNavigation(this,
                (DrawerLayout) findViewById(R.id.activity_my_clothes),
                getSupportActionBar());

        LinearLayout clothesContainer = findViewById(R.id.clothes);


        findViewById(R.id.addNewCloth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddClothDialogFragment dialogFragment = new AddClothDialogFragment();
                dialogFragment.show(getFragmentManager(), "ItemDialog");
            }
        });
    }
}