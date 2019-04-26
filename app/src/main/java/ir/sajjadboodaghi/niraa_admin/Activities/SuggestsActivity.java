package ir.sajjadboodaghi.niraa_admin.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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
import ir.sajjadboodaghi.niraa_admin.Models.Suggest;
import ir.sajjadboodaghi.niraa_admin.Adapters.SuggestAdapter;
import ir.sajjadboodaghi.niraa_admin.Urls;

public class SuggestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SuggestAdapter suggestAdapter;
    List<Suggest> suggestsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggests);

        Toolbar toolbar = (Toolbar) findViewById(R.id.suggestsToolbar);
        setSupportActionBar(toolbar);

        suggestsList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestAdapter = new SuggestAdapter(suggestsList, this);
        recyclerView.setAdapter(suggestAdapter);
        getSuggests();
    }
    private void getSuggests() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestHandler.makeRequest(this, "POST", Urls.GET_SUGGESTS, getSuggestsParams(), new RequestCallback() {
            @Override
            public void onNoInternetAccess() {
                progressDialog.dismiss();
                String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        getSuggests();
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
                        AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                getSuggests();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        return;
                    }

                    JSONArray jsonArraySuggests = jsonResponse.getJSONArray("suggests");
                    if(jsonArraySuggests.length() > 0) {
                        for(int i = 0; i < jsonArraySuggests.length(); i++) {
                            JSONObject object = jsonArraySuggests.getJSONObject(i);
                            Suggest suggest = new Suggest(
                                    object.getInt("id"),
                                    object.getString("phone_number"),
                                    object.getString("niraa_version"),
                                    object.getString("android_version"),
                                    object.getString("description")
                            );

                            suggestsList.add(suggest);
                        }
                        suggestAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                    AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                        @Override
                        public void tryAgain() {
                            getSuggests();
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
                AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                    @Override
                    public void tryAgain() {
                        getSuggests();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

    }
    private Map<String, String> getSuggestsParams() {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(SuggestsActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(SuggestsActivity.this).getPassword());
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
    public void deleteSuggest(final View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_title_danger));
        alertDialogBuilder.setMessage(getResources().getString(R.string.alert_dialog_message_delete_suggest));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.alert_dialog_button_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int position = (int) v.getTag();

                final ProgressDialog progressDialog = new ProgressDialog(SuggestsActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.progress_dialog_connecting));
                progressDialog.setCancelable(false);
                progressDialog.show();

                RequestHandler.makeRequest(SuggestsActivity.this, "POST", Urls.DELETE_SUGGEST, deleteSuggestParams(position), new RequestCallback() {
                    @Override
                    public void onNoInternetAccess() {
                        progressDialog.dismiss();
                        String message = getResources().getString(R.string.alert_dialog_message_no_internet_access);
                        AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                deleteSuggest(v);
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
                            if (error) {
                                AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                                    @Override
                                    public void tryAgain() {
                                        deleteSuggest(v);
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                                return;
                            }

                            AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(SuggestsActivity.this);
                            alertDialogbuilder.setTitle(getResources().getString(R.string.alert_dialog_title_message));
                            alertDialogbuilder.setMessage(message);
                            alertDialogbuilder.setCancelable(false);
                            alertDialogbuilder.setNeutralButton(getResources().getString(R.string.alert_dialog_button_understand), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            alertDialogbuilder.show();

                            suggestsList.remove(position);
                            suggestAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String message = getResources().getString(R.string.alert_dialog_message_problem_in_server);
                            AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                                @Override
                                public void tryAgain() {
                                    deleteSuggest(v);
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
                        AlertHandler.alertProblemMaker(SuggestsActivity.this, message, new AlertProblemCallback() {
                            @Override
                            public void tryAgain() {
                                deleteSuggest(v);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }
                });

            }
        });
        alertDialogBuilder.show();
    }
    private Map<String, String> deleteSuggestParams(int position) {
        Map<String, String> params = new HashMap<>();
        params.put("username", SharedPrefManager.getInstance(SuggestsActivity.this).getUsername());
        params.put("password", SharedPrefManager.getInstance(SuggestsActivity.this).getPassword());
        params.put("suggest_id", String.valueOf(suggestsList.get(position).getId()));
        return params;
    }

}
