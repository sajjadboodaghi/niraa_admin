package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
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

public class EditItemActivity extends AppCompatActivity {

    EditText titleEditText;
    EditText descriptionEditText;
    EditText priceEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.editItemToolbar);
        setSupportActionBar(toolbar);

        setupEditTexts();

    }
    private void setupEditTexts() {
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        titleEditText.setText(getIntent().getStringExtra("itemTitle"));

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        descriptionEditText.setText(getIntent().getStringExtra("itemDescription"));

        priceEditText = (EditText) findViewById(R.id.priceEditText);
        priceEditText.setText(getIntent().getStringExtra("itemPrice"));
    }
    public void updateItem(final View v) {
        String message = checkFieldsValues();
        if(message.length() > 0) {
            AlertHandler.alertNoticeMaker(this, message, new AlertNoticeCallback() {
                @Override
                public void okay() {

                }
            });
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(EditItemActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestHandler.makeRequest(EditItemActivity.this, "POST", Urls.UPDATE_ITEM, updateItemParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(EditItemActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        updateItem(v);
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
                    String message = jsonResponse.getString("message");
                    if(error) {
                        AlertHandler.alertProblemMaker(EditItemActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                updateItem(v);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    AlertHandler.alertNoticeMaker(EditItemActivity.this, message, new AlertNoticeCallback() {
                        @Override
                        public void okay() {
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(EditItemActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            updateItem(v);
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
                AlertHandler.alertProblemMaker(EditItemActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        updateItem(v);
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

    }
    private String checkFieldsValues() {
        String message = "";
        if(titleEditText.getText().toString().replace(" ", "").length() == 0) { message += "بخش عنوان را تکمیل نمایید!\n" ; }
        if(titleEditText.length() < 4 && titleEditText.length() > 0) { message += "عنوان طولانی تری انتخاب کنید!\n" ; }
        if(descriptionEditText.getText().toString().replace(" ", "").length() == 0) { message += "بخش توضیحات را تکمیل نمایید!\n"; }
        if(descriptionEditText.length() < 4 && descriptionEditText.length() > 0) { message += "توضیحات بیشتری بنویسید!\n"; }
        return message;
    }
    private Map<String, String> updateItemParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(EditItemActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(EditItemActivity.this).getPassword());
        params.put("item_id", String.valueOf(getIntent().getIntExtra("itemId",0)));
        params.put("item_title", titleEditText.getText().toString());
        params.put("item_description", descriptionEditText.getText().toString());
        params.put("item_price", priceEditText.getText().toString());
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
