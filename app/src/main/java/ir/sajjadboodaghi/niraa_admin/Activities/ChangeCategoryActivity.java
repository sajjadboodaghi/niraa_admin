package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

public class ChangeCategoryActivity extends AppCompatActivity {

    Spinner categorySpinner;
    Spinner subCategorySpinner;
    JSONArray jsonCategoryArray;
    Button saveNewCategoryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.categoryToolbar);
        setSupportActionBar(toolbar);

        saveNewCategoryButton = (Button) findViewById(R.id.saveNewCategoryButton);

        setupCategory();
    }
    private void setupCategory() {
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        subCategorySpinner = (Spinner) findViewById(R.id.subCategorySpinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSubCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadCategories();
    }
    private void loadCategories() {
        RequestHandler.makeRequest(this, "GET", Urls.GET_CATEGORY, null, new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(ChangeCategoryActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        loadCategories();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }

            @Override
            public void onSuccess(String response) {
                try {
                    jsonCategoryArray = new JSONArray(response);
                    ArrayList<String> categoryList = new ArrayList<>();
                    for(int i = 0; i < jsonCategoryArray.length(); i++) {
                        categoryList.add(jsonCategoryArray.getJSONObject(i).getString("catname"));
                    }

                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(ChangeCategoryActivity.this, android.R.layout.simple_dropdown_item_1line, categoryList);
                    categorySpinner.setAdapter(categoryAdapter);
                    saveNewCategoryButton.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(ChangeCategoryActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            loadCategories();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }

            @Override
            public void onRequestError() {
                String message = getResources().getString(R.string.alert_dialog_message_problem_with_connecting);
                AlertHandler.alertProblemMaker(ChangeCategoryActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        loadCategories();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }
    private void setSubCategory(final int position) {
        try {
            JSONArray jsonSubCategoryArray = jsonCategoryArray.getJSONObject(position).getJSONArray("subcats");
            ArrayList<String> subCategoryList = new ArrayList<>();
            for(int i = 0; i < jsonSubCategoryArray.length(); i++) {
                subCategoryList.add(jsonSubCategoryArray.getJSONObject(i).getString("subcat_name"));
            }

            ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(ChangeCategoryActivity.this, android.R.layout.simple_dropdown_item_1line, subCategoryList);
            subCategorySpinner.setAdapter(subCategoryAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            String message = getResources().getString(R.string.alert_dialog_message_problem_subcategoy);
            AlertHandler.alertNoticeMaker(this, message, new AlertNoticeCallback() {
                @Override
                public void okay() {

                }
            });
        }

    }
    public void saveNewCategory(final View v) {
        final ProgressDialog progressDialog = new ProgressDialog(ChangeCategoryActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestHandler.makeRequest(this, "POST", Urls.SAVE_NEW_CATEGORY, setupParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(ChangeCategoryActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        saveNewCategory(v);
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
                        AlertHandler.alertNoticeMaker(ChangeCategoryActivity.this, message, new AlertNoticeCallback() {
                            @Override
                            public void okay() {

                            }
                        });
                        return;
                    }

                    AlertHandler.alertNoticeMaker(ChangeCategoryActivity.this, message, new AlertNoticeCallback() {
                        @Override
                        public void okay() {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertNoticeMaker(ChangeCategoryActivity.this, message, new AlertNoticeCallback() {
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
                AlertHandler.alertNoticeMaker(ChangeCategoryActivity.this, message, new AlertNoticeCallback() {
                    @Override
                    public void okay() {

                    }
                });
            }
        });
    }
    private Map<String, String> setupParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(ChangeCategoryActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(ChangeCategoryActivity.this).getPassword());
        params.put("item_id", String.valueOf(getIntent().getIntExtra("itemId",0)));
        params.put("new_subcat_name", subCategorySpinner.getSelectedItem().toString());

        int subcatId = 0;
        try {
            subcatId = jsonCategoryArray
                    .getJSONObject(categorySpinner.getSelectedItemPosition())
                    .getJSONArray("subcats")
                    .getJSONObject(subCategorySpinner.getSelectedItemPosition())
                    .getInt("subcat_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put("new_subcat_id", String.valueOf(subcatId));
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
