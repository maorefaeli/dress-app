package finalproj.dressapp.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import finalproj.dressapp.R;
import finalproj.dressapp.Utils;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.UserRegistration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends DressAppActivity {

    static Pattern namePattern = Pattern.compile("^[a-zA-Z]{2,}$");
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mAddressView;
    private EditText mEmailView;
    private TextView mMoneyView;

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
        mMoneyView = findViewById(R.id.money);
        mMoneyView.setText("300");
        
        final String userEmail = Utils.getUserName(getApplicationContext());
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        final Call <UserRegistration> call = apiInterface.getCurrentUserDetails();
        call.enqueue(new Callback<UserRegistration>() {
            public void onResponse(Call <UserRegistration> call, Response <UserRegistration> response) {
                if (response.code() == 200) {

                    UserRegistration userDetails = response.body();
                    String upperString = userDetails.firstName.substring(0, 1).toUpperCase() + userDetails.firstName.substring(1).toLowerCase();
                    mFirstNameView.setText(upperString);
                    upperString = userDetails.lastName.substring(0, 1).toUpperCase() + userDetails.lastName.substring(1).toLowerCase();
                    mLastNameView.setText(upperString);
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

    public void updateUserDetails(View view) {
        Utils.showPopupProgressSpinner(this, true, "Just a moment...");
        
        Boolean cancel = false;
        View focusView = null;

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        
        String firstName = mFirstNameView.getText().toString().toLowerCase().trim();
        String lastName = mLastNameView.getText().toString().toLowerCase().trim();

        Matcher firstNameMatch = namePattern.matcher(firstName);
        Matcher lastNameMatch = namePattern.matcher(lastName);

        // Check for a valid first name.
        if (!firstNameMatch.find()) {
            mFirstNameView.setError("First name has to be at least 2 characters long and can't contain numbers, spaces or any special character.");
            focusView = mFirstNameView;
            cancel = true;
        };

        // Check for a valid last name.
        if (!lastNameMatch.find()) {
            mLastNameView.setError("Last name has to be at least 2 characters long and can't contain numbers, spaces or any special character.");
            focusView = mLastNameView;
            cancel = true;
        };

        if (cancel) {
            Utils.showPopupProgressSpinner( ProfileActivity.this, false, "");
            focusView.requestFocus();
        }
        else {
            UserRegistration userRegistration = new UserRegistration(firstName, lastName);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            
            Call<Boolean> call = apiInterface.doUpdateUser(userRegistration);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Utils.showPopupProgressSpinner( ProfileActivity.this, false, "");
                    if (response.code() == 200) {
                        Boolean didUpdate = response.body();
                        if (didUpdate) {
    
                        }
                    }
                }
    
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Utils.showPopupProgressSpinner( ProfileActivity.this, false, "");
                    new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Failed to update user")
                        .setMessage(t.getMessage())
                        .show();
                    call.cancel();
                }
            });
        }
    }
}