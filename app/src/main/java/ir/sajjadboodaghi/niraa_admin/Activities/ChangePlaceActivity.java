package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

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

public class ChangePlaceActivity extends AppCompatActivity {
    Button saveNewPlaceButton;
    SearchableSpinner placeSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_place);

        Toolbar toolbar = (Toolbar) findViewById(R.id.placeToolbar);
        setSupportActionBar(toolbar);

        saveNewPlaceButton = (Button) findViewById(R.id.saveNewPlaceButton);
        placeSpinner = (SearchableSpinner) findViewById(R.id.placeSpinner);
        loadPlaces();
    }
    private void loadPlaces() {
        ArrayAdapter<String> placeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.places));
        placeSpinner = (SearchableSpinner) findViewById(R.id.placeSpinner);
        placeSpinner.setTitle(getResources().getString(R.string.spinner_place_title));
        placeSpinner.setPositiveButton(getResources().getString(R.string.spinner_place_close_button));
        placeSpinner.setAdapter(placeAdapter);
    }
    public void saveNewPlace(final View v) {
        final ProgressDialog progressDialog = new ProgressDialog(ChangePlaceActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestHandler.makeRequest(this, "POST", Urls.SAVE_NEW_PLACE, setupParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(ChangePlaceActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        saveNewPlace(v);
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
                        AlertHandler.alertNoticeMaker(ChangePlaceActivity.this, message, new AlertNoticeCallback() {
                            @Override
                            public void okay() {

                            }
                        });
                        return;
                    }

                    AlertHandler.alertNoticeMaker(ChangePlaceActivity.this, message, new AlertNoticeCallback() {
                        @Override
                        public void okay() {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertNoticeMaker(ChangePlaceActivity.this, message, new AlertNoticeCallback() {
                        @Override
                        public void okay() {

                        }
                    });
                }
            }

            @Override
            public void onRequestError() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertNoticeMaker(ChangePlaceActivity.this, message, new AlertNoticeCallback() {
                    @Override
                    public void okay() {

                    }
                });
            }
        });
    }
    private Map<String, String> setupParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(ChangePlaceActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(ChangePlaceActivity.this).getPassword());
        params.put("item_id", String.valueOf(getIntent().getIntExtra("itemId",0)));
        params.put("new_place", placeSpinner.getSelectedItem().toString());
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
