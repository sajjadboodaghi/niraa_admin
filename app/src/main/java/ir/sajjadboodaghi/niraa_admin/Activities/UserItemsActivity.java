package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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
import ir.sajjadboodaghi.niraa_admin.Models.Item;
import ir.sajjadboodaghi.niraa_admin.Adapters.ItemAdapter;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.RequestCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.RequestHandler;
import ir.sajjadboodaghi.niraa_admin.Handlers.SharedPrefManager;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class UserItemsActivity extends AppCompatActivity {

    String phoneNumber;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Item> items;

    private final static float INCH_TO_CENTIMETER_RATE = 2.54f;
    private final static int MIN_ITEM_WIDTH_IN_CENTIMETER = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_items);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        Toolbar toolbar = (Toolbar) findViewById(R.id.userItemsToolbar);
        toolbar.setTitle("آگهی\u200Cهای کاربر");
        toolbar.setSubtitle(phoneNumber);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // we use this variable for getting width, height,...
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float widthInInch = metrics.widthPixels / (float) metrics.densityDpi;
        int widthInCentimeter = (int) (widthInInch * INCH_TO_CENTIMETER_RATE);
        int columnCount = widthInCentimeter / MIN_ITEM_WIDTH_IN_CENTIMETER;
        final GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(mGridLayoutManager);
        items = new ArrayList<>();
        adapter = new ItemAdapter(items, UserItemsActivity.this);
        recyclerView.setAdapter(adapter);

        loadUserItems();
    }

    private void loadUserItems() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestHandler.makeRequest(this, "POST", Urls.GET_USER_ITEMS, loadUserItemsParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        loadUserItems();
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
                        AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                loadUserItems();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    JSONArray newItems = jsonResponse.getJSONArray("items");
                    if(newItems.length() > 0) {
                        for(int i = 0; i < newItems.length(); i++) {
                            JSONObject object = newItems.getJSONObject(i);
                            Item item = new Item(
                                    object.getInt("id"),
                                    object.getString("phone_number"),
                                    object.getString("telegram_id"),
                                    object.getString("title"),
                                    object.getString("description"),
                                    object.getString("price"),
                                    object.getString("place"),
                                    object.getString("subcat_name"),
                                    object.getInt("subcat_id"),
                                    object.getString("shamsi"),
                                    object.getString("timestamp"),
                                    object.getInt("image_count"),
                                    object.getInt("verified")
                            );

                            items.add(item);
                        }

                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            loadUserItems();
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
                AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        loadUserItems();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

    }

    private Map<String, String> loadUserItemsParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(UserItemsActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(UserItemsActivity.this).getPassword());
        params.put("phone_number", phoneNumber);
        return params;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_items_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuBack) {
            finish();
        }
        if(item.getItemId() == R.id.menuBlock) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_title_danger));
            alertDialogBuilder.setMessage(getResources().getString(R.string.alert_dialog_message_block_user));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    blockUser();
                }
            });
            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.alert_dialog_button_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialogBuilder.show();
        }
        return true;
    }

    private void blockUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestHandler.makeRequest(this, "POST", Urls.BLOCK_USER, blockUserParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        blockUser();
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
                        AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                blockUser();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserItemsActivity.this);
                    alertDialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_title_message));
                    alertDialogBuilder.setMessage(message);
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setNeutralButton(getResources().getString(R.string.alert_dialog_button_understand), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialogBuilder.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            blockUser();
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
                AlertHandler.alertProblemMaker(UserItemsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        blockUser();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

    }

    private Map<String, String> blockUserParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(UserItemsActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(UserItemsActivity.this).getPassword());
        params.put("phone_number", phoneNumber);
        return params;
    }


}
