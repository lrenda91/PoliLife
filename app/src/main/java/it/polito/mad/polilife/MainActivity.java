package it.polito.mad.polilife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.signup.SignUpActivity;

public class MainActivity extends AppCompatActivity implements DBCallbacks.UserLoginCallback {

    private ProgressBar progressBar;
    private EditText username, password;
    private Button loginButton, signUpButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MAIN_ACTIVITY", getIntent().hasExtra("json") ? getIntent().getStringExtra("json") : "null");

        username = (EditText) findViewById(R.id.email_editText);
        password = (EditText) findViewById(R.id.password_editText);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        loginButton = (Button) findViewById(R.id.login_button);
        signUpButton = (Button) findViewById(R.id.signup_loginpage_button);

        ParseUser loggedInUser = PoliLifeDB.tryLocalLogin();
        if (loggedInUser != null){
            onStudentLoginSuccess(loggedInUser);
        }



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!Utility.networkIsUp(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Please connect to any network",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                setUserInteractionEnabled(false);
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                PoliLifeDB.remoteLogIn(usernameText, passwordText, MainActivity.this);

            }
        });
    }


    private void setUserInteractionEnabled(boolean enabled){
        loginButton.setEnabled(enabled);
        signUpButton.setEnabled(enabled);
        username.setEnabled(enabled);
        password.setEnabled(enabled);
        progressBar.setVisibility(enabled ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onStudentLoginSuccess(ParseUser student) {
        setUserInteractionEnabled(true);
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginError(Exception exception) {
        setUserInteractionEnabled(true);
        Toast.makeText(getApplicationContext(),
                exception.getMessage(),
                Toast.LENGTH_LONG).show();
    }

}
