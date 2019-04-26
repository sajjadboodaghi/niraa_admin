package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar loginToolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(loginToolbar);
    }

    public void login(final View v) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Map<String, String> params = loginParams();
        RequestHandler.makeRequest(this, "POST", Urls.LOGIN_ADMIN, params, new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(LoginActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        login(v);
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }

            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean error = jsonResponse.getBoolean("error");
                    if(error) {
                        String message = jsonResponse.getString("message");
                        AlertHandler.alertProblemMaker(LoginActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                login(v);
                            }

                            @Override
                            public void cancel() {
                            }
                        });
                        return;
                    }

                    SharedPrefManager.getInstance(LoginActivity.this).login(params.get("username"), params.get("password"));
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(LoginActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            login(v);
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }

            @Override
            public void onRequestError() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(LoginActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        login(v);
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

    }

    private Map<String, String> loginParams() {
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        return params;
    }

}
