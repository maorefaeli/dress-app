package finalproj.dressapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.app.AlertDialog;
import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import finalproj.dressapp.httpclient.models.UserCredentials;
import finalproj.dressapp.httpclient.models.UserRegistration;
import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import finalproj.dressapp.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPasswordView;
    private EditText mConfirmPassView;
    private AutoCompleteTextView mEmailView;
    private TextView mLinkToLogin;
    private Button mRegister;

    static Pattern namePattern = Pattern.compile("^[a-zA-Z]{2,}$");
    static Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,16}$");
    static Pattern emailPattern = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLastNameView = (EditText) findViewById(R.id.lastName);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPassView = (EditText) findViewById(R.id.confirmPassword);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mLinkToLogin = (TextView) findViewById(R.id.linkToLogin);

        mLinkToLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        mRegister = (Button) findViewById(R.id.btnRegister);
        mRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPassView.setError(null);
        
        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String confirmPass = mConfirmPassView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        Matcher firstNameMatch = namePattern.matcher(firstName);
        Matcher lastNameMatch = namePattern.matcher(lastName);
        Matcher emailMatcher = emailPattern.matcher(email);
        Matcher passwordMatcher = passwordPattern.matcher(password);

        // Check for a valid first name.
        if (!firstNameMatch.find()) {
            mFirstNameView.setError("First name has to be at least 2 characters long and can't contain numbers.");
            focusView = mFirstNameView;
            cancel = true;
        };

        // Check for a valid last name.
        if (!lastNameMatch.find()) {
            mLastNameView.setError("Last name has to be at least 2 characters long and can't contain numbers.");
            focusView = mLastNameView;
            cancel = true;
        };

        // Check for a valid email.
        if (!emailMatcher.find()) {
            mEmailView.setError("Email format is wrong.");
            focusView = mEmailView;
            cancel = true;
        };

        // Check for a valid password.
        if (!passwordMatcher.find()) {
            mPasswordView.setError("Password length needs to be between 6 to 16 characters, contain at least one upper case, one lower case and one number.");
            focusView = mPasswordView;
            cancel = true;
        }
        // Check if the passwords match (Only if the password is valid).
        else if (!password.equals(confirmPass)) {
            mConfirmPassView.setError("The passwords do not match.");
            focusView = mConfirmPassView;
            cancel = true;
        };

        if (cancel) {
            focusView.requestFocus();
        }
        else {

            firstName = firstName.toLowerCase();
            lastName = lastName.toLowerCase();
            email = email.toLowerCase();

            UserRegistration userRegistration = new UserRegistration(firstName, lastName, email, password);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

            Utils.showPopupProgressSpinner(this, true, "Juat a moment...");

            Call<Boolean> call = apiInterface.doRegister(userRegistration);
            final String finalEmail = email;
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    
                    Utils.showPopupProgressSpinner( RegisterActivity.this, false, "");

                    if (response.code() == 200) {
                        Boolean didRegister = response.body();
                        if (didRegister) {
                            UserCredentials userCredentials = new UserCredentials(finalEmail, password);
                            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                            
                            Utils.showPopupProgressSpinner(RegisterActivity.this, true, "Logging you in...");

                            Call<Boolean> callLogin = apiInterface.doLogin(userCredentials);
                            callLogin.enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    if (response.code() == 200) {
                                        Boolean didLogin = response.body();
                                        if (didLogin) {
                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                
                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("failure")
                                        .setMessage(t.getMessage())
                                        .show();
                                    call.cancel();
                                }
                            });
                        }
                    }
                    else if (response.code() == 403) {
                        mEmailView.setError("User already exists for the given email");
                        View focusView = mEmailView;
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("failure")
                        .setMessage(t.getMessage())
                        .show();
                    call.cancel();
                }
            });
        }
    }
}
