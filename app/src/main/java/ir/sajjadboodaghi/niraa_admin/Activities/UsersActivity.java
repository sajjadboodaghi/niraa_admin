package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.Urls;
import ir.sajjadboodaghi.niraa_admin.Models.User;
import ir.sajjadboodaghi.niraa_admin.Adapters.UsersAdapter;

public class UsersActivity extends AppCompatActivity {

    List<User> users;
    RecyclerView recyclerView;
    UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.usersToolbar);
        setSupportActionBar(toolbar);

        users = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        usersAdapter = new UsersAdapter(users, this);
        recyclerView.setAdapter(usersAdapter);
        getUsers();

    }

    private void getUsers() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestHandler.makeRequest(this, "POST", Urls.GET_USERS, getUsersParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(UsersActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        getUsers();
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
                        AlertHandler.alertProblemMaker(UsersActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                getUsers();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    JSONArray jsonArrayUsers = jsonResponse.getJSONArray("users");
                    if(jsonArrayUsers.length() > 0) {
                        for(int i = 0; i < jsonArrayUsers.length(); i++) {
                            JSONObject object = jsonArrayUsers.getJSONObject(i);
                            User user = new User(
                                    object.getInt("id"),
                                    object.getString("phone_number")
                            );

                            users.add(user);
                        }
                        usersAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(UsersActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            getUsers();
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
                AlertHandler.alertProblemMaker(UsersActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        getUsers();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }

    private Map<String, String> getUsersParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(UsersActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(UsersActivity.this).getPassword());
        return params;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.users_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuBack:
                finish();
                break;
            case R.id.broadcastMenu:
                startActivity(new Intent(this, BroadcastActivity.class));
                break;
        }
        return true;
    }
}
