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

import finalproj.dressapp.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPasswordView;
    private AutoCompleteTextView mEmailView;
    private TextView mLinkToLogin;
    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLastNameView = (EditText) findViewById(R.id.lastName);
        mPasswordView = (EditText) findViewById(R.id.password);
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
        
        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid first name.
        if (!TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError("First name can't be empty.");
            focusView = mFirstNameView;
            cancel = true;
        }
    }
}
