package finalproj.dressapp.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.app.AlertDialog;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.UserRegistration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends DressAppActivity {

    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mAddressView;
    private EditText mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toggle = Utils.setNavigation(this,
                (DrawerLayout) findViewById(R.id.activity_profile),
                getSupportActionBar());

        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLastNameView = (EditText) findViewById(R.id.lastName);
        mAddressView = (EditText) findViewById(R.id.address);
        mEmailView = (EditText) findViewById(R.id.email);
        
        final String userEmail = Utils.getUserName(getApplicationContext());
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        final Call <UserRegistration> call = apiInterface.getCurrentUserDetails();
        call.enqueue(new Callback<UserRegistration>() {
            public void onResponse(Call <UserRegistration> call, Response <UserRegistration> response) {
                if (response.code() == 200) {

                    UserRegistration userDetails = response.body();
                    mFirstNameView.setText(userDetails.firstName);
                    mLastNameView.setText(userDetails.lastName);
//                    mAddressView.setText(userDetails.address);
                    mEmailView.setText(userDetails.username);
                }
            }

            public void onFailure(Call<UserRegistration> call, Throwable t) {
                new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Could not get user details for: " + userEmail)
                    .setMessage(t.getMessage())
                    .show();
                call.cancel();
            }
        });
    }
}