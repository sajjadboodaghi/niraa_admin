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
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.sajjadboodaghi.niraa_admin.Handlers.AlertHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertNoticeCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class DeleteItemActivity extends AppCompatActivity {
    EditText messageEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.deleteItemToolbar);
        setSupportActionBar(toolbar);

        messageEditText = (EditText) findViewById(R.id.messageEditText);
    }
    public void deleteItem(final View view) {
        if(messageEditText.getText().toString().trim().length() < 2) {
            String message = getResources().getString(R.string.alert_dialog_message_make_a_note_for_user);
            AlertHandler.alertNoticeMaker(this, message, new AlertNoticeCallback() {
                @Override
                public void okay() {

                }
            });
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_title_danger));
        alertDialogBuilder.setMessage(getResources().getString(R.string.alert_dialog_message_want_to_delete_item));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog progressDialog = new ProgressDialog(DeleteItemActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
                progressDialog.setCancelable(false);
                progressDialog.show();

                RequestHandler.makeRequest(DeleteItemActivity.this, "POST", Urls.DELETE_ITEM, deleteItemParams(), new RequestCallback() {
                    @Override
                    public void onNoInternetAccess() {
                        progressDialog.dismiss();
                        String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                        AlertHandler.alertProblemMaker(DeleteItemActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                deleteItem(view);
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
                            AlertHandler.alertNoticeMaker(DeleteItemActivity.this, message, new AlertNoticeCallback() {
                                @Override
                                public void okay() {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                            AlertHandler.alertProblemMaker(DeleteItemActivity.this, message, new AlertProblemCallback() {
                                @Override
                                public void tryAgain() {
                                    deleteItem(view);
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
                        AlertHandler.alertProblemMaker(DeleteItemActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                deleteItem(view);
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
    private Map<String, String> deleteItemParams() {
        Intent intent = getIntent();
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(DeleteItemActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(DeleteItemActivity.this).getPassword());
        params.put("item_id", String.valueOf(intent.getIntExtra("itemId", 0)));
        params.put("item_phone_number", intent.getStringExtra("itemPhoneNumber"));
        params.put("item_title", intent.getStringExtra("itemTitle"));
        params.put("message_for_user", messageEditText.getText().toString());
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
