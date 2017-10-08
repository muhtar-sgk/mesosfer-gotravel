package org.saungit.gotravel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.eyro.mesosfer.LogInCallback;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferUser;

import org.saungit.gotravel.app.Config;
import org.saungit.gotravel.app.SessionManager;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText textUsername;
    private TextInputEditText textPassword;
    private ProgressDialog loading;
    private AlertDialog dialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        textUsername = (TextInputEditText) findViewById(R.id.text_username);
        textPassword = (TextInputEditText) findViewById(R.id.text_password);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
    }

    public void handleLogin(View view) {
        final String username = textUsername.getText().toString();
        String password = textPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is empty", Toast.LENGTH_LONG).show();
            textUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show();
            textPassword.requestFocus();
            return;
        }

        loading.setMessage("Logging in...");
        loading.show();
        MesosferUser.logInAsync(username, password, new LogInCallback() {
            @Override
            public void done(MesosferUser user, MesosferException e) {
                loading.dismiss();
                if (e != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Login Failed");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );

                    dialog = builder.show();
                    return;
                }

                Toast.makeText(LoginActivity.this, "User logged in...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeAfterLoginActivity.class);
                startActivity(intent);

                session.createLoginSession(username);
                finish();
                //startActivity(new Intent(LoginActivity.this, CommentActivity.class));
            }
        });
    }

    public void handleRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, 1234);
    }
}
