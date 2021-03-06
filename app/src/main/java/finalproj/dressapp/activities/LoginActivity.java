package finalproj.dressapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import finalproj.dressapp.Utils;
import android.app.AlertDialog;

import java.util.List;

import finalproj.dressapp.httpclient.APIClient;
import finalproj.dressapp.httpclient.APIInterface;
import finalproj.dressapp.httpclient.models.UserCredentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import finalproj.dressapp.R;

public class LoginActivity extends AppCompatActivity {
    
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mRegister;
    private TextView mGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utils.getUserName(getApplicationContext()).length() != 0)
        {
            attemptLogin(Utils.getUserName(LoginActivity.this), Utils.getUserCookie(LoginActivity.this));
        }
        else
        {
            setContentView(R.layout.activity_login);
            mEmailView = findViewById(R.id.email);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            mGuest = (TextView) findViewById(R.id.continueAsGuest);
            mGuest.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    guestLogin();
                }
            });

            mRegister = (TextView) findViewById(R.id.register);
            mRegister.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
    }

    private void guestLogin() {
        Utils.setGuestStatus(true);
        showProgress(true);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString().toLowerCase();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            UserCredentials userCredentials = new UserCredentials(email, password);

            showProgress(true);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

            Call<Boolean> call = apiInterface.doLogin(userCredentials);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    showProgress(false);
                    if (response.code() == 200) {
                        Boolean didLogin = response.body();
                        if (didLogin) {
                            String userCookie = password;
                            Utils.setUserName(getApplicationContext(), email);
                            Utils.setUserCookie(getApplicationContext(), userCookie);
                            Utils.setGuestStatus(false);
                            goToHome();
                        }
                    }
                    else {
                        new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Could not log in.")
                        .setMessage("The E-mail and password combination was not found, please check your input.")
                        .show();
                        call.cancel();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    showProgress(false);
                    new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("failure")
                        .setMessage(t.getMessage())
                        .show();
                    call.cancel();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private void goToHome() {
        Utils.setGuestStatus(false);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptLogin(String userName, String userCookie) {

        // Store values at the time of the login attempt.
        final String email = userName;
        String cookie = userCookie;
        UserCredentials userCredentials = new UserCredentials(email, cookie);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<Boolean> call = apiInterface.doLogin(userCredentials);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.code() == 200) {
                    Boolean didLogin = response.body();
                    if (didLogin) {
                        Utils.loadUserWishlistItems();
                        String userCookie = cookie;
//                        List<String> Cookielist = response.headers().values("Set-Cookie");
//                        String userId = (Cookielist.get(0).split(";"))[0];
                        Utils.setUserName(getApplicationContext(), email);
//                        Utils.setUserId(getApplicationContext(), userId);
                        Utils.setUserCookie(getApplicationContext(), userCookie);
                        Utils.setGuestStatus(false);
                        goToHome();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("failure")
                        .setMessage(t.getMessage())
                        .show();
                call.cancel();
            }
        });
    }
}

