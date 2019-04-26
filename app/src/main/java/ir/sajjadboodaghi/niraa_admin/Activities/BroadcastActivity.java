package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertNoticeCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class BroadcastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        Toolbar toolbar = (Toolbar) findViewById(R.id.broadcastToolbar);
        setSupportActionBar(toolbar);

        Button sendBroadcastButton = (Button) findViewById(R.id.sendBroadcastButton);
        sendBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast();
            }
        });
    }

    public void sendBroadcast() {
        EditText broadcastTypeEditText = (EditText) findViewById(R.id.broadcastTypeEditText);
        EditText broadcastMessageEditText = (EditText) findViewById(R.id.broadcastMessageEditText);

        final String broadcastType = broadcastTypeEditText.getText().toString();
        final String broadcastMessage = broadcastMessageEditText.getText().toString();

        String message = "";
        if(broadcastType.trim().length() == 0) {
            message += "- " + getResources().getString(R.string.alert_dialog_message_write_broadcast_type) + "\n";
        }
        if(broadcastMessage.trim().length() < 2) {
            message += "- " + getResources().getString(R.string.alert_dialog_message_write_broadcast_message);
        }

        if(message.length() != 0) {
            AlertHandler.alertNoticeMaker(this, message, new AlertNoticeCallback() {
                @Override
                public void okay() {

                }
            });
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_title_danger));
        alertDialogBuilder.setMessage(getResources().getString(R.string.alert_dialog_message_sure_to_send_broadcast));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog progressDialog = new ProgressDialog(BroadcastActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
                progressDialog.setCancelable(false);
                progressDialog.show();

                RequestHandler.makeRequest(BroadcastActivity.this, "POST", Urls.SEND_BROADCAST, sendBroadcastParams(broadcastType, broadcastMessage), new RequestCallback() {
                    @Override
                    public void onNoInternetAccess() {
                        progressDialog.dismiss();
                        String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                        AlertHandler.alertProblemMaker(BroadcastActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                sendBroadcast();
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
                            String message = jsonResponse.getString("message");
                            AlertHandler.alertNoticeMaker(BroadcastActivity.this, message, new AlertNoticeCallback() {
                                @Override
                                public void okay() {
                                    finish();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                            AlertHandler.alertProblemMaker(BroadcastActivity.this, message, new AlertProblemCallback() {
                                @Override
                                public void tryAgain() {
                                    sendBroadcast();
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
                        AlertHandler.alertProblemMaker(BroadcastActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                sendBroadcast();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }
                });
            }
        });
        alertDialogBuilder.setNegativeButton("خیر، منصرف شدم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        alertDialogBuilder.show();
    }
    private Map<String, String> sendBroadcastParams(String broadcastType, String broadcastMessage) {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(BroadcastActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(BroadcastActivity.this).getPassword());
        params.put("broadcast_type", broadcastType);
        params.put("broadcast_message", broadcastMessage);
        return params;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuBack) {
            finish();
        }
        return true;
    }
}
