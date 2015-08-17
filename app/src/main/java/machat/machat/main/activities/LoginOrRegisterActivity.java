package machat.machat.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import machat.machat.R;

public class LoginOrRegisterActivity extends Activity {

    View.OnClickListener loginHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginOrRegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener registerHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginOrRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        findViewById(R.id.loginButton).setOnClickListener(loginHandler);

        findViewById(R.id.registerButton).setOnClickListener(registerHandler);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
